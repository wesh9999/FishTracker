package org.weshley.fishtracker;

import android.util.Log;

public class Logger
{
   static final String TAG = "Fish Tracker";

   static void error(String msg, Exception ex)
   {
      Log.e(TAG, msg + ": " + ex.getMessage());
      ex.printStackTrace();
   }

   static void error(String msg)
   {
      Log.e(TAG, msg);
   }

   static void warning(String msg)
   {
      Log.w(TAG, msg);
   }

   static void info(String msg)
   {
      Log.i(TAG, msg);
   }

}
