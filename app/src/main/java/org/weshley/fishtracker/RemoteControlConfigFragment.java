package org.weshley.fishtracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RemoteControlConfigFragment
   extends AbstractFragment
{
   private ViewGroup _rootView = null;
   private BluetoothAdapter _btAdapter = null;
   private BroadcastReceiver _broadcastReceiver = null;
   private Map<String,String> _devices = null;
      // maps MAC addr to device name
   private boolean _scanning = false;
   private Map<String,Integer> _devicesPositionMap = null;

   public static String getLabel()
   {
      return MainActivity.getAppResources().getString(R.string.remote_control_config_label);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      _rootView = (ViewGroup) inflater.inflate(
         R.layout.remote_control_config_fragment, container, false);

      initBluetoothState();
      initUi();
      initDeviceDiscovery();
      initControllers();
      return _rootView;
   }

   @Override
   public void onDestroy()
   {
      super.onDestroy();
      stopBroadcastReceiver();
   }

   @Override
   public void onResume()
   {
      super.onResume();
      initDeviceDiscovery();
   }

   @Override
   public void onPause()
   {
      super.onPause();
      stopBroadcastReceiver();
   }

   private void stopBroadcastReceiver()
   {
      if(null != _btAdapter)
         _btAdapter.cancelDiscovery();

      if(null != _broadcastReceiver)
         getContext().unregisterReceiver(_broadcastReceiver);
      _broadcastReceiver = null;
   }

   private void initUi()
   {
      initPairedDevicesList();
      initScanControls();
   }

   private void initControllers()
   {
      getScanButton().setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            toggleScanning();
         }
      });
   }

   private void initDeviceDiscovery()
   {
      if(!hasBluetoothSupport())
         return;

      // Create a BroadcastReceiver for ACTION_FOUND.
      _broadcastReceiver = new BroadcastReceiver()
      {
         public void onReceive(Context context, Intent intent)
         {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
               BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               // If it's already paired, skip it, because it's been listed already
               String deviceName = device.getName();
               String macAddress = device.getAddress();
               if((null != deviceName) && (device.getBondState() != BluetoothDevice.BOND_BONDED))
                  addNewDevice(macAddress, deviceName);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
               _scanning = true;
               initScanControls();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
               _scanning = false;
               initScanControls();
            }
         }
      };

      // Register for broadcasts when a device is discovered.
      IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
      filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
      filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
      getContext().registerReceiver(_broadcastReceiver, filter);
   }

   private void logMessage(String msg)
   {
      EditText notes = getNotesField();
      notes.setText(notes.getText() + "\n" + msg);
   }

   private void initBluetoothState()
   {
      _btAdapter = BluetoothAdapter.getDefaultAdapter();
      if(null == _btAdapter)
      {
         getBluetoothStatusText().setText("Bluetooth NOT SUPPORTED!");
         return;
      }

      if(_btAdapter.isEnabled())
      {
         getBluetoothStatusText().setText("Bluetooth enabled");
      }
      else
      {
         _btAdapter = null;
         getBluetoothStatusText().setText("Bluetooth disabled");
// TODO - figure out how to ask for bluetooth to be enabled and monitor bluetooth status changes
      }
   }

   private boolean hasBluetoothSupport()
   {
      return null != _btAdapter;
   }

   private void initPairedDevicesList()
   {
      // sort the names
      Set<String> deviceNames = new TreeSet<String>();
      deviceNames.addAll(getPairedDevices().values());

      List<String> pairedList = new ArrayList<>();
      pairedList.addAll(deviceNames);
      _devicesPositionMap = new HashMap<String,Integer>();
      for(int i = 0; i < pairedList.size(); ++i)
         _devicesPositionMap.put(pairedList.get(i), i);

      Spinner spinner = getPairedDevicesList();
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, pairedList);
      spinner.setAdapter(spinnerArrayAdapter);
   }

   private void addNewDevice(String macAddress, String deviceName)
   {
      if(!_devices.containsKey(macAddress))
      {
         _devices.put(macAddress, deviceName);
         initPairedDevicesList();
         Spinner spinner = getPairedDevicesList();
         int pos = _devicesPositionMap.get(deviceName);
         spinner.setSelection(pos);
      }
   }

   private Map<String,String> getPairedDevices()
   {
      if(null != _devices)
         return _devices;
      _devices = new HashMap<>();
      if(hasBluetoothSupport())
      {
         Set<BluetoothDevice> pairedDevices = _btAdapter.getBondedDevices();
         for (BluetoothDevice device : pairedDevices)
         {
            String deviceName = device.getName();
            String macAddress = device.getAddress();
            _devices.put(macAddress, deviceName);
         }
      }
      return _devices;
   }

   private void initScanControls()
   {
      if(!hasBluetoothSupport() || !UiManager.instance().isLocationPermissionGranted())
      {
         getScanButton().setEnabled(false);
         getScanLabel().setText("");
         return;
      }

      getScanButton().setEnabled(true);
      if(_scanning)
      {
         getScanButton().setText(R.string.remote_control_config_cancel_scan_label);
         getScanLabel().setText("Scanning...");
      }
      else
      {
         getScanButton().setText(R.string.remote_control_config_scan_label);
         getScanLabel().setText("");
      }
   }

   private void toggleScanning()
   {
      if(!hasBluetoothSupport() || !UiManager.instance().isLocationPermissionGranted())
         return;

      if(_scanning)
      {
         if(!_btAdapter.cancelDiscovery())
            logMessage("cancel discovery failed");
      }
      else
      {
//         _btAdapter.cancelDiscovery();
         if(!_btAdapter.startDiscovery())
            logMessage("start discovery failed");
      }
//      initScanControls();
   }

   private Button getScanButton()
   {
      return (Button) _rootView.findViewById(R.id.remote_control_config_scan_button);
   }

   private TextView getScanLabel()
   {
      return (TextView) _rootView.findViewById(R.id.remote_control_config_bt_scan_label);
   }

   private TextView getBluetoothStatusText()
   {
      return (TextView) _rootView.findViewById(R.id.remote_control_config_bt_status_message);
   }

   private EditText getNotesField()
   {
      return (EditText) _rootView.findViewById(R.id.remote_control_config_notes_field);
   }

   private Spinner getPairedDevicesList()
   {
      return (Spinner) _rootView.findViewById(R.id.remote_control_config_devices_list);
   }
}
