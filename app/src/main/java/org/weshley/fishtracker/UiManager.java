package org.weshley.fishtracker;

import androidx.viewpager.widget.ViewPager;

public class UiManager
{
   private static UiManager INSTANCE = new UiManager();

   private ViewPager _pager = null;
   private PagerAdapter _pagerAdapter = null;

   public static UiManager instance()
   {
      return INSTANCE;
   }

   public void setPager(ViewPager pager, PagerAdapter adapter)
   {
      _pager = pager;
      _pagerAdapter = adapter;
   }

   public void showPage(Class fragmentClass)
   {
     _pager.setCurrentItem(_pagerAdapter.getFragmentIndex(fragmentClass));
   }
}
