package org.weshley.fishtracker;

// TODO: how do i want to represent this
public class AudioNote
{
   private String _label = null;
   private byte[] _data = null;

   public AudioNote(String label, byte[] data)
   {
      _label = label;
      _data = data;
   }

   public String getLabel()
   {
      return _label;
   }

   public void setLabel(String s)
   {
      _label = s;
   }
}
