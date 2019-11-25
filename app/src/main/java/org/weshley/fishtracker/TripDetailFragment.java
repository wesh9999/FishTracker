package org.weshley.fishtracker;

import androidx.fragment.app.Fragment;

public class TripDetailFragment extends MissingFragment
{
   public static String getLabel()
   {
      return MainActivity.getAppResources().getString(R.string.trip_detail_label);
   }

}
