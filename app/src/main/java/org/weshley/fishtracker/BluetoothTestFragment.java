package org.weshley.fishtracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Set;

public class BluetoothTestFragment
   extends AbstractFragment
{
   private ViewGroup _rootView = null;
   private BluetoothAdapter _btAdapter = null;

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

      initUi();
      return _rootView;
   }

   private void initUi()
   {
      initBluetoothState();
      listPairedDevices();
   }

   private void listPairedDevices()
   {
      String pairedData = "";
      if(null == _btAdapter)
      {
         pairedData = "No Bluetooth";
      }
      else
      {
         Set<BluetoothDevice> pairedDevices = _btAdapter.getBondedDevices();
         for (BluetoothDevice device : pairedDevices)
         {
            String deviceName = device.getName();
            //String deviceHardwareAddress = device.getAddress(); // MAC address
            pairedData = pairedData + deviceName + "\n";
         }
      }
      getNotesField().setText(pairedData);
   }

   private void initBluetoothState()
   {
      _btAdapter = getBluetoothAdapter();
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

   private BluetoothAdapter getBluetoothAdapter()
   {
      return BluetoothAdapter.getDefaultAdapter();
   }

   private TextView getBluetoothStatusText()
   {
      return (TextView) _rootView.findViewById(R.id.bluetooth_status_message);
   }

   private EditText getNotesField()
   {
      return (EditText) _rootView.findViewById(R.id.bluetooth_notes_field);
   }
}
