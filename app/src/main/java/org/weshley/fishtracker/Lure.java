package org.weshley.fishtracker;

import java.util.Set;
import java.util.TreeSet;

public class Lure
{
   private String _type = null;
   private String _color = null;
   private String _size = null;

   public Lure()
   {
   }

   public Lure(String type, String color, String size)
   {
      setType(type);
      setColor(color);
      setSize(size);
   }

   public void setType(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _type = null;
      }
      else
      {
         _type = s;
         getTripManager().addLureType(s);
      }
   }

   public String getType()
   {
      return _type;
   }

   public void setColor(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _type = null;
      }
      else
      {
         _color = s;
         getTripManager().addLureColor(s);
      }
   }

   public String getColor()
   {
      return _color;
   }

   public void setSize(String s)
   {
      if((null == s) || s.isEmpty())
      {
         _type = null;
      }
      else
      {
         _size = s;
         getTripManager().addLureSize(s);
      }
   }

   public String getSize()
   {
      return _size;
   }



   private TripManager getTripManager()
   {
      return TripManager.instance();
   }


}
