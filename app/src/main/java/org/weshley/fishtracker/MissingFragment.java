package org.weshley.fishtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MissingFragment extends AbstractFragment
{
   public static String getLabel()
   {
      return MainActivity.getAppResources().getString(R.string.missing_label);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
         R.layout.missing_fragment, container, false);

      return rootView;
   }
}
