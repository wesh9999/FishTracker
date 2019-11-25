package org.weshley.fishtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TripsFragment extends AbstractFragment
{
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
      TripListAdapter adapter = new TripListAdapter();
      TripManager.instance().addTripListChangeListener(adapter);
      recyclerView.setAdapter(adapter);
      return rootView;
   }

}
