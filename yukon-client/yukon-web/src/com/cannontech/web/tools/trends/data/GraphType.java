
package com.cannontech.web.tools.trends.data;

public enum GraphType {
   BASIC_GRAPH_TYPE (0x0009),
   USAGE_GRAPH_TYPE (0x0005), 
   PEAK_GRAPH_TYPE (0x0011),   //PEAK + GRAPH
   YESTERDAY_GRAPH_TYPE (0x0021),  //YESTERDAY + GRAPH
   DATE_GRAPH_TYPE (0x0081);   //DATE + GRAPH
   private int value;
   private GraphType(int hexval)
   {
       this.value = hexval;
   }
   public int getValue()
   {
       return this.value;
   }
   
}

