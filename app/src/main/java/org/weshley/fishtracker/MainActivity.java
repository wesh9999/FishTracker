package org.weshley.fishtracker;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity
   extends FragmentActivity
   implements TripListChangeListener
{
   private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

   private static Resources _resources = null;
   private static Context _context = null;

   protected static Resources getAppResources()
   {
      return _resources;
   }

   protected static Context getContext()
   {
      return _context;
   }

   public void tripListChanged(TripListChangeEvent ev)
   {
      updateButtonState();
   }

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      _resources = getResources();
      _context = this;

      PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
      ViewPager pager = (ViewPager)findViewById(R.id.pager);
      pager.setAdapter(adapter);
      UiManager.instance().setPager(pager, adapter);

      registerEventHandlers();
      checkLocationPermission();
   }

   private void registerEventHandlers()
   {
      // want to know when trips have changed so we can update the
      // EndTrip/NewTrip/CaughtFish buttons if necessary
      TripManager.instance().addTripListChangeListener(this);

      getTripButton().setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
            { handleTripButtonClick((Button) v); }
      });

      getFishButton().setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
            { handleFishButtonClick((Button) v); }
      });
      updateButtonState();
   }

   private void handleTripButtonClick(Button b)
   {
      if(getTripManager().hasActiveTrip())
         confirmAndEndTrip();
      else
         startTrip();
   }

   private void updateButtonState()
   {
      if(getTripManager().hasActiveTrip())
      {
        getTripButton().setText(getText(R.string.end_trip));
        getFishButton().setEnabled(true);
      }
      else
      {
        getTripButton().setText(getText(R.string.new_trip));
        getFishButton().setEnabled(false);
      }
   }

   private void startTrip()
   {
      getTripManager().startTrip();
      updateButtonState();
      UiManager.instance().showPage(TripDetailFragment.class);
   }

   private void confirmAndEndTrip()
   {
      new AlertDialog.Builder(getContext())
         .setTitle("End Current Trip?")
         .setMessage("End the current trip?")
         .setIcon(android.R.drawable.ic_dialog_alert)
         .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
               public void onClick(DialogInterface dialog, int whichButton)
               {
                  endTrip();
               }
            })
         .setNegativeButton(android.R.string.no, null).show();

   }

   private void endTrip()
   {
      getTripManager().endTrip();
      updateButtonState();
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

   private void handleFishButtonClick(Button b)
   {
      getTripManager().caughtNewFish();
      UiManager.instance().showPage(FishDetailFragment.class);
   }

   private Button getTripButton()
   {
      return (Button) findViewById(R.id.trip_button);
   }

   private Button getFishButton()
   {
      return (Button) findViewById(R.id.fish_button);
   }

   // this is required to get GPS positions and for bluetooth device discovery
   private void checkLocationPermission()
   {
      // tell the UI that we don't have location permission yet
      UiManager.instance().setLocationPermissionGranted(false);

      // do we already have location permission?
      if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
      {
         UiManager.instance().setLocationPermissionGranted(true);
         return;
      }

      // Does this device require that we prompt the user for permission?
      if (ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_FINE_LOCATION))
      {
         // Present the user with a dialog they can use to grant permissions.  The
         // requestPermissions() call will result in a callback to
         // onRequestPermissionsResult().
         new AlertDialog.Builder(this)
              .setTitle("Permission Request")
              .setMessage("This application needs permission to access location data.")
              .setPositiveButton("OK", new DialogInterface.OnClickListener()
              {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i)
                 {
                    ActivityCompat.requestPermissions(
                       MainActivity.this,
                       new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                       LOCATION_PERMISSION_REQUEST_CODE);
                 }
              })
               .create()
               .show();
      }
      else
      {
         // Request permission without prompting.  onRequestPermissionsResult() will
         // be invoked if permission granted or denied
         ActivityCompat.requestPermissions(
          this,
          new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
          LOCATION_PERMISSION_REQUEST_CODE);
      }
   }

   @Override
   public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
   {
      if(requestCode == LOCATION_PERMISSION_REQUEST_CODE)
      {
         // If request is cancelled, the result arrays are empty.
         if((grantResults.length > 0)
                 && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
         {
            // permission should be granted, check again just to confirm then store the fact
            // that we're allowed to access location data so the UI can be updated as
            // appropriate
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                  == PackageManager.PERMISSION_GRANTED)
            {
               UiManager.instance().setLocationPermissionGranted(true);
            }
         }
         else
         {
            UiManager.instance().setLocationPermissionGranted(false);
         }
      }
   }
}
