package org.weshley.fishtracker;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TripListAdapter
   extends RecyclerView.Adapter<TripListAdapter.ViewHolder>
   implements TripListChangeListener
{
   private List<Trip> _sortedTrips = null;
   private ViewHolder _selectedHolder = null;

   void setSelectedHolder(ViewHolder vh)
   {
      _selectedHolder = vh;
   }

   ViewHolder getSelectedHolder()
   {
      return _selectedHolder;
   }

   // the view holders in the list just have a single text item
   public class ViewHolder
      extends RecyclerView.ViewHolder
      implements View.OnCreateContextMenuListener
   {
      private TripListAdapter _adapter = null;
      private TextView _textView = null;
      private Trip _trip = null;

      public ViewHolder(View v, TripListAdapter adapter)
      {
         super(v);
         _textView = (TextView) v.findViewById(R.id.textView);
         _adapter = adapter;
         v.setOnCreateContextMenuListener(this);
      }

      Trip getTrip()
      {
         return _trip;
      }

      void bind(Trip t)
      {
         _trip = t;
         _textView.setText(getTripLabel(t));
         ClickListener listener = new ClickListener(this);
         _textView.setOnClickListener(listener);
         _textView.setOnLongClickListener(listener);
      }

      void unbind()
      {
         _textView.setOnClickListener(null);
         _textView.setOnLongClickListener(null);
         _textView = null;
         _trip = null;
      }

      void setSelectedHolder()
      {
         _adapter.setSelectedHolder(this);
      }

      @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
      {
         menu.add(Menu.NONE, R.id.trips_menu_resume_id, Menu.NONE, R.string.trips_menu_resume_label);
         menu.add(Menu.NONE, R.id.trips_menu_delete_id, Menu.NONE, R.string.trips_menu_delete_label);
      }
   }

   public static class ClickListener
      implements View.OnClickListener, View.OnLongClickListener
   {
      private ViewHolder _holder = null;

      public ClickListener(ViewHolder holder)
      {
         _holder = holder;
      }

      public boolean onLongClick(View v)
      {
         _holder.setSelectedHolder();
         return false;
      }

      public void onClick(View v)
      {
         // TODO - set a selectedTrip in the TripManager and update trip detail UI
         UiManager.instance().showPage(TripDetailFragment.class);
      }

   }


   public TripListAdapter()
   {
   }

   public void tripListChanged(TripListChangeEvent ev)
   {
      // TODO - May want to refine this to only update the one item that has changed?
      //        Be aware that we may be changing one trip, adding new trip, or deleting a trip
      //        See https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter.html
      //          and notify methods in https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter.html
      //          and a comment here https://developer.android.com/guide/topics/ui/layout/recyclerview
      _sortedTrips = null;
      this.notifyDataSetChanged();
   }

   // Create new views (invoked by the layout manager)
   @Override
   public TripListAdapter.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType)
   {
      // create a new view
      View v = LayoutInflater.from(parent.getContext()).inflate(
         R.layout.trips_list_item, parent, false);
      ViewHolder vh = new ViewHolder(v, this);
      return vh;
   }

   // Replace the contents of a view (invoked by the layout manager)
   @Override
   public void onBindViewHolder(ViewHolder holder, int position)
   {
      Trip t = getSortedTrips().get(position);
      holder.bind(t);
   }

   @Override
   public void onViewRecycled(ViewHolder holder)
   {
      holder.unbind();
      super.onViewRecycled(holder);
   }

   // Return the size of your dataset (invoked by the layout manager)
   @Override
   public int getItemCount()
   {
      return getSortedTrips().size();
   }

   private String getTripLabel(Trip t)
   {
      String tripLabel = t.getLabel();
      if(getTripManager().isCurrentTrip(t))
         tripLabel = "----> " + tripLabel + " <----";
      return tripLabel;
   }

   // Reverse sort the trips by start date
   private List<Trip> getSortedTrips()
   {
      if(null == _sortedTrips)
      {
         // TODO: sort this first by date (most recent first) and then by location (alphabetical)
         Map<Date,Trip> sortMap = new TreeMap<Date,Trip>();
         for(Trip t : getTripManager().getTrips())
            sortMap.put(t.getStart(), t);
         _sortedTrips = new ArrayList<Trip>(sortMap.size());
         for(int i = 0; i < sortMap.size(); ++i)
            _sortedTrips.add(null);
         int idx = sortMap.size() - 1;
         for(Date start : sortMap.keySet())
            _sortedTrips.set(idx--, sortMap.get(start));
      }

      return _sortedTrips;
   }

   private TripManager getTripManager()
   {
      return TripManager.instance();
   }

}
