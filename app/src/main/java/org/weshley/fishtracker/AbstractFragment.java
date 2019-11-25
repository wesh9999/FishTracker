package org.weshley.fishtracker;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public abstract class AbstractFragment extends Fragment
{
   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
   {
      TabLayout tabLayout = view.getRootView().findViewById(R.id.tab_layout);
      tabLayout.setupWithViewPager((ViewPager)view.getRootView().findViewById(R.id.pager));
   }
}
