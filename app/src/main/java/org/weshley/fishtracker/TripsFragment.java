package org.weshley.fishtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TripsFragment extends AbstractFragment
{
   private TripListAdapter _adapter = null;

   public static String getLabel()
   {
      return MainActivity.getAppResources().getString(R.string.trips_label);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
         R.layout.trips_fragment, container, false);

      // set up the trips list view with a simple linear layout and a custom adapter
      RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.trips_list);
      recyclerView.setHasFixedSize(true);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
      recyclerView.setLayoutManager(layoutManager);
      _adapter = new TripListAdapter();
      TripManager.instance().addTripListChangeListener(_adapter);
      recyclerView.setAdapter(_adapter);

      // support context menus when long pressing on trips list
      registerForContextMenu(recyclerView);
      return rootView;
   }

   @Override
   public boolean onContextItemSelected(MenuItem item)
   {
      Trip trip = null;
      if((null != _adapter) && (null != _adapter.getSelectedHolder()))
         trip = _adapter.getSelectedHolder().getTrip();

      switch (item.getItemId())
      {
        case R.id.trips_menu_resume_id:
            resumeOldTrip(trip);
            break;
        case R.id.trips_menu_delete_id:
            deleteTrip(trip);
            break;
      }

      return super.onContextItemSelected(item);
   }

   private void deleteTrip(final Trip trip)
   {
      new AlertDialog.Builder(getContext())
         .setTitle("Delete Trip?")
         .setMessage("Permanently destroy all data associated with trip '" + trip.getLabel() + "'?")
         .setIcon(android.R.drawable.ic_dialog_alert)
         .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
               public void onClick(DialogInterface dialog, int whichButton)
               {
                  getTripManager().deleteTrip(trip);
               }
            })
         .setNegativeButton(android.R.string.no, null).show();
   }

   private void resumeOldTrip(final Trip trip)
   {
      String msgPrefix = "Resume";
      if(getTripManager().hasActiveTrip())
         msgPrefix = "End current trip and resume";
      new AlertDialog.Builder(getContext())
         .setTitle("Resume Old Trip?")
         .setMessage(msgPrefix + " old trip '" + trip.getLabel() + "'?")
         .setIcon(android.R.drawable.ic_dialog_alert)
         .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
               public void onClick(DialogInterface dialog, int whichButton)
               {
                  getTripManager().resumeTrip(trip);
               }
            })
         .setNegativeButton(android.R.string.no, null).show();
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

}
