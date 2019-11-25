package org.weshley.fishtracker;

import java.util.Set;
import java.util.TreeSet;

public class Lure
{
   // TODO: add more default lure types, colors, sizes
   private static final String[] DEFAULT_LURE_TYPES = {"Tube", "Rip Stop"};
   private static final String[] DEFAULT_LURE_COLORS = {"Black/White", "Blue/Orange", "Shad"};
   private static final String[] DEFAULT_LURE_SIZES = {"3in", "4in"};

   private static Set<String> _allTypes = null;
   private static Set<String> _allColors = null;
   private static Set<String> _allSizes = null;

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
      _type = s;
      Lure.addToAllTypes(s);
   }

   public String getType()
   {
      return _type;
   }

   public void setColor(String s)
   {
      _color = s;
      Lure.addToAllColors(s);
   }

   public String getColor()
   {
      return _color;
   }

   public void setSize(String s)
   {
      _size = s;
      Lure.addToAllSizes(s);
   }

   public String getSize()
   {
      return _size;
   }

   private static Set<String> getAllTypes()
   {
      if(null == _allTypes)
      {
         _allTypes = new TreeSet<String>();
         // TODO: read persisted list and add values to _allTypes
         for(String s : DEFAULT_LURE_TYPES)
            _allTypes.add(s);
      }
      return _allTypes;
   }

   private static void addToAllTypes(String s)
   {
      // TODO: persist _allTypes when changed
      if((null != s) && !s.isEmpty())
         getAllTypes().add(s);
   }


   private static Set<String> getAllColors()
   {
      if(null == _allColors)
      {
         _allColors = new TreeSet<String>();
         // TODO: read persisted list and add values to _allColors
         for(String s : DEFAULT_LURE_COLORS)
            _allColors.add(s);
      }
      return _allColors;
   }

   private static void addToAllColors(String s)
   {
      // TODO: persist _allColors when changed
      if((null != s) && !s.isEmpty())
         getAllColors().add(s);
   }

   private static Set<String> getAllSizes()
   {
      if(null == _allSizes)
      {
         _allSizes = new TreeSet<String>();
         // TODO: read persisted list and add values to _allSizes
         for(String s : DEFAULT_LURE_SIZES)
            _allSizes.add(s);
      }
      return _allSizes;
   }

   private static void addToAllSizes(String s)
   {
      // TODO: persist _allSizes when changed
      if((null != s) && !s.isEmpty())
         getAllSizes().add(s);
   }


}
