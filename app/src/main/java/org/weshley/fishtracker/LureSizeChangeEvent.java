package org.weshley.fishtracker;

public class LureSizeChangeEvent
{
   private String _size = null;

   public LureSizeChangeEvent(String s)
   {
      _size = s;
   }

   public String getSize()
   {
      return _size;
   }
}