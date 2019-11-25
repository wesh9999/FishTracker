package org.weshley.fishtracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

// TODO: need a UI to edit these values, maybe some way to switch between
//       named configs, persist & load them, etc
public class Config
{
   public static DateFormat getDateFormat()
   {
      return new SimpleDateFormat("yyyy/MM/dd h:mm a");
   }
}
