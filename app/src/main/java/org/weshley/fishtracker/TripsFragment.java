package org.weshley.fishtracker;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
System.out.println("+++++++++++++++++++++++ RESUME " + trip.getLabel());
// TODO:  prompt for confirmation if a trip is active, end active trip, resume selected trip
            break;
        case R.id.trips_menu_delete_id:
System.out.println("+++++++++++++++++++++++ DELETE " + trip.getLabel());
// TODO:  prompt for confirmation then delete the trip.  make sure ui updates.  clear current trip if it is the one being deleted
            break;
      }

      return super.onContextItemSelected(item);
   }

}
