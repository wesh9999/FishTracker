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

public class BluetoothTestFragment
   extends AbstractFragment
{

   private ViewGroup _rootView = null;
   private BluetoothAdapter _btAdapter = null;
   private BroadcastReceiver _broadcastReceiver = null;
   private Map<String,String> _devices = null;
      // maps MAC addr to device name
   private boolean _scanning = false;

   public static String getLabel()
   {
      return MainActivity.getAppResources().getString(R.string.bluetooth_testing_label);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      _rootView = (ViewGroup) inflater.inflate(
         R.layout.bluetooth_test_fragment, container, false);

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
               if(device.getBondState() != BluetoothDevice.BOND_BONDED)
                  _devices.put(macAddress, deviceName);
               logMessage("Discovered device '" + deviceName + "'");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
               logMessage("++++ Discovery started");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
               logMessage("++++ Discovery finished");
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

      Spinner spinner = getPairedDevicesList();
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
         this.getContext(), R.layout.spinner_item, pairedList);
      spinner.setAdapter(spinnerArrayAdapter);
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
      if(!hasBluetoothSupport())
      {
         getScanButton().setEnabled(false);
         getScanLabel().setText("");
         return;
      }

      getScanButton().setEnabled(true);
      if(_scanning)
      {
         getScanButton().setText(R.string.bluetooth_cancel_scan_label);
         getScanLabel().setText("Scanning...");
      }
      else
      {
         getScanButton().setText(R.string.bluetooth_scan_label);
         getScanLabel().setText("");
      }
   }

   private void toggleScanning()
   {
      if(!hasBluetoothSupport())
         return;

      if(_scanning)
      {
         if(!_btAdapter.cancelDiscovery())
            logMessage("cancel discovery failed");
         else
            _scanning = false;
      }
      else
      {
//         if(!_btAdapter.startDiscovery())
//            logMessage("start discovery failed");
//         else
//            _scanning = true;
         _btAdapter.cancelDiscovery();
         _btAdapter.startDiscovery();
         _scanning = true;
      }
      initScanControls();
   }

   private Button getScanButton()
   {
      return (Button) _rootView.findViewById(R.id.bluetooth_scan_button);
   }

   private TextView getScanLabel()
   {
      return (TextView) _rootView.findViewById(R.id.bluetooth_scan_label);
   }

   private TextView getBluetoothStatusText()
   {
      return (TextView) _rootView.findViewById(R.id.bluetooth_status_message);
   }

   private EditText getNotesField()
   {
      return (EditText) _rootView.findViewById(R.id.bluetooth_notes_field);
   }

   private Spinner getPairedDevicesList()
   {
      return (Spinner) _rootView.findViewById(R.id.bluetooth_pairs_list);
   }
}
