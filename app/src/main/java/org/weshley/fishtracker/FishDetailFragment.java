package org.weshley.fishtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FishDetailFragment
   extends AbstractFragment
   implements SelectedTripChangeListener, TripListChangeListener, SelectedFishChangeListener
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

      initEditors();
      initListeners();
      updateUi();

      return _rootView;
   }

   @Override
   public void selectedTripChanged(SelectedTripChangeEvent ev)
   {
      updateTripLabel();
   }

   @Override
   public void tripListChanged(TripListChangeEvent e)
   {
      // we can get this when an active trip is ended.  need to update end date/time
      if(e.getChangedTrip() == getSelectedTrip())
         updateTripLabel();
   }

   @Override
   public void selectedFishChanged(SelectedFishChangeEvent ev)
   {
      updateUi();
   }

   private void updateUi()
   {
      Trip t = getSelectedTrip();
      if(null != t)
         updateTripLabel();

      // TODO - update all the controls from the selected fish
      Fish f = getSelectedFish();
      if(null == f)
      {
         // TODO -- clearControls()
      }
      else
      {
         // TODO -- updateControls()
      }
   }

   private Fish getSelectedFish() { return getTripManager().getSelectedFish(); }

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

   private void initEditors()
   {
      // TODO - populate dropdowns, etc
   }

   private void initListeners()
   {
      getTripManager().addSelectedTripChangeListener(this);
      getTripManager().addTripListChangeListener(this);
      getTripManager().addSelectedFishChangeListener(this);

      // TODO - add listeners for controls
   }

   private TextView getTitleView()
   {
      return (TextView) _rootView.findViewById(R.id.fish_detail_title);
   }
}
