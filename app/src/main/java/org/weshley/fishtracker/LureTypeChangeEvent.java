package org.weshley.fishtracker;

public class LureTypeChangeEvent
{
   private String _type = null;

   public LureTypeChangeEvent(String s)
   {
      _type = s;
   }

   public String getType()
   {
      return _type;
   }
}