package org.weshley.fishtracker;

public class LureColorChangeEvent
{
   private String _color = null;

   public LureColorChangeEvent(String s)
   {
      _color = s;
   }

   public String getColor()
   {
      return _color;
   }
}