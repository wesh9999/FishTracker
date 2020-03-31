package org.weshley.fishtracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

// TODO: need a UI to edit these values, maybe some way to switch between
//       named configs, persist & load them, etc
public class Config
{
   private static final String DATE_FORMAT = "yyyy/MM/dd";
   private static final String TIME_FORMAT = "h:mm a";

   public static final String OTHER_LOCATION_LABEL = "-- Other --";

   public static DateFormat getDateTimeFormat()
   {
      return new SimpleDateFormat(DATE_FORMAT + " " + TIME_FORMAT);
   }

   public static DateFormat getDateFormat()
   {
      return new SimpleDateFormat(DATE_FORMAT);
   }

   public static DateFormat getTimeFormat()
   {
      return new SimpleDateFormat(TIME_FORMAT);
   }

   public static Temperature.Units getDefaultTempUnits() { return Temperature.Units.F; }
}
