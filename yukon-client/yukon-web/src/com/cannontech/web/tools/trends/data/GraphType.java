
package com.cannontech.web.tools.trends.data;

import com.cannontech.common.i18n.DisplayableEnum;

public enum GraphType implements DisplayableEnum{
   
   BASIC_GRAPH_TYPE (0x0009),
   USAGE_GRAPH_TYPE (0x0005), 
   PEAK_GRAPH_TYPE (0x0011),   //PEAK + GRAPH
   YESTERDAY_GRAPH_TYPE (0x0021),  //YESTERDAY + GRAPH
   DATE_GRAPH_TYPE (0x0081),   //DATE + GRAPH
   MARKER_GRAPH_TYPE(0x0041),
   GRAPH_TYPE (0x0001),
   PRIMARY_TYPE (0x0002),
   USAGE_TYPE(0x0004),
   BASIC_TYPE(0x0008),
   PEAK_TYPE(0x0010),
   YESTERDAY_TYPE(0x0020),
   MARKER_TYPE(0x0041),
   DATE_TYPE(0x0080);
   
   private int value;
   private GraphType(int hexval)
   {
       this.value = hexval;
   }
   public int getValue()
   {
       return this.value;
   }
   public static GraphType getByType(int type) {
       for (GraphType graphType : values()) {
           if (graphType.value == type) return graphType;
       }
       throw new IllegalArgumentException("GraphType has no such type " + type);
   }

    @Override
    public String getFormatKey() {
        // TODO Auto-generated method stub
        return "yukon.web.modules.tools.trend.graphType." + name();
    }
   
}

