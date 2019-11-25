package org.weshley.fishtracker;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends FragmentActivity
{
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
   }

   private void registerEventHandlers()
   {
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
      if(getTripManager().hasCurrentTrip())
         endTrip();
      else
         startTrip();
      updateButtonState();
   }

   private void updateButtonState()
   {
      if(getTripManager().hasCurrentTrip())
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
      UiManager.instance().showPage(TripDetailFragment.class);
   }

   private void endTrip()
   {
      getTripManager().endTrip();
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

   private void handleFishButtonClick(Button b)
   {
      // TODO - needs to be implemented
   }

   private Button getTripButton()
   {
      return (Button) findViewById(R.id.trip_button);
   }

   private Button getFishButton()
   {
      return (Button) findViewById(R.id.fish_button);
   }


}
