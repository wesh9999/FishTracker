package org.weshley.fishtracker;

public class CoverChangeEvent
{
   private String _cover = null;

   public CoverChangeEvent(String c)
   {
      _cover = c;
   }

   public String getCover()
   {
      return _cover;
   }

}
