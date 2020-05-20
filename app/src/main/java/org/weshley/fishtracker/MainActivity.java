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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity
   extends FragmentActivity
   implements TripListChangeListener
{
   private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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

   private boolean checkLocationPermission()
   {
      if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
      {
         // Should we show an explanation?
         if (ActivityCompat.shouldShowRequestPermissionRationale(
               this, Manifest.permission.ACCESS_FINE_LOCATION))
         {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission")
                    .setMessage("Grant location permissions?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i)
                       {
                          //Prompt the user once explanation has been shown
                          ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                       }
                    })
                    .create()
                    .show();
         }
         else
         {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(
             this,
             new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
             MY_PERMISSIONS_REQUEST_LOCATION);
         }
         return false;
       }
      else
      {
        return true;
      }
   }

   @Override
   public void onRequestPermissionsResult(
      int requestCode, String permissions[], int[] grantResults)
   {
      switch(requestCode)
      {
         case MY_PERMISSIONS_REQUEST_LOCATION:
         {
            // If request is cancelled, the result arrays are empty.
            if((grantResults.length > 0)
                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
               // permission was granted, yay! Do the
               // location-related task you need to do.
               if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
               {
//                    //Request location updates:
//                    locationManager.requestLocationUpdates(provider, 400, 1, this);
               }
            }
            else
            {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
         }
      }
   }
}
