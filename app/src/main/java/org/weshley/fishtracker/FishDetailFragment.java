package org.weshley.fishtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

public class FishDetailFragment
   extends AbstractFragment
   implements SelectedTripChangeListener, TripListChangeListener
{
   private ViewGroup _rootView = null;

   public static String getLabel()
   {
      return MainActivity.getAppResources().getString(R.string.fish_detail_label);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      _rootView = (ViewGroup) inflater.inflate(
         R.layout.fish_detail_fragment, container, false);

      updateUi();

      return _rootView;
   }

   @Override
   public void selectedTripChanged(SelectedTripChangeEvent ev)
   {
      updateUi();
   }

   @Override
   public void tripListChanged(TripListChangeEvent e)
   {
      // we can get this when an active trip is ended.  need to update end date/time
      if(e.getChangedTrip() == getSelectedTrip())
         updateTripLabel();
   }

   private void updateUi()
   {
      Trip t = getSelectedTrip();
      if(null == t)
         return;

      updateTripLabel();
   }

   private Trip getSelectedTrip()
   {
      return getTripManager().getSelectedTrip();
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

   private void updateTripLabel()
   {
      Trip t = getSelectedTrip();
      if(null != t)
         getTitleView().setText(getTripManager().getDisplayableTripLabel(t));
   }

   private TextView getTitleView()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_title);
   }
}
