package org.weshley.fishtracker;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.lang.reflect.Method;

public class PagerAdapter extends FragmentPagerAdapter
{

   static final Class[] FRAGMENT_LAYOUTS =
   {
      TripsFragment.class, TripDetailFragment.class, FishListFragment.class,
      FishDetailFragment.class, BluetoothTestFragment.class
   };

   private String[] _fragmentLabelCache = null;

   public PagerAdapter(FragmentManager fm)
   {
      // TODO:  The documentation says to use the following but I can't find the BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT constant???
      //super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
      super(fm);
   }

   public int getFragmentIndex(Class fragmentClass)
   {
      for(int i = 0; i < FRAGMENT_LAYOUTS.length; ++i)
      {
         if(FRAGMENT_LAYOUTS[i] == fragmentClass)
            return i;
      }
      return -1;
   }

   @Override
   public int getCount()
   {
      return FRAGMENT_LAYOUTS.length;
   }

   @Override
   public Fragment getItem(int position)
   {
       if(position < FRAGMENT_LAYOUTS.length)
       {
          try
          {
             return (Fragment) FRAGMENT_LAYOUTS[position].newInstance();
          }
          catch(Exception ex)
          {
             Logger.error("cannot instantiate page #" + position, ex);
          }
       }
       return new MissingFragment();
   }

   @Override
   public CharSequence getPageTitle(int position)
   {
      initFragmentLabelCache();
      if(position < _fragmentLabelCache.length)
         return _fragmentLabelCache[position];
      return MissingFragment.getLabel();
   }

   private void initFragmentLabelCache()
   {
      if(null != _fragmentLabelCache)
         return;

      _fragmentLabelCache = new String[FRAGMENT_LAYOUTS.length];
      for(int i = 0; i < FRAGMENT_LAYOUTS.length; ++i)
      {
         Class cl = FRAGMENT_LAYOUTS[i];
         _fragmentLabelCache[i] = getFragmentLabel(cl);
      }
   }

   private String getFragmentLabel(Class cl)
   {
      try
      {
         Method m = cl.getDeclaredMethod("getLabel");
         return (String) m.invoke(null);
      }
      catch (Exception e)
      {
         Logger.error("cannot get fragment label", e);
         return MissingFragment.getLabel();
      }
   }

}
