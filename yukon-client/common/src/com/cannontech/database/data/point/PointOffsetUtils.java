package com.cannontech.database.data.point;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LitePoint;

public class PointOffsetUtils {

    public PointOffsetUtils() {
        super();
     
    }

    public static boolean isValidPointOffset(int offset, Integer paoId, int type){
        if (getMaxPointOffsetForDevice(paoId, type) > offset){
            return false;
        }
        return true;
    }
    
    public static int getValidPointOffset(Integer paoId, int type) {
        return getMaxPointOffsetForDevice (paoId, type) + 1;  
    }
    
    public static int getMaxPointOffsetForDevice(Integer paoId, int pointType) {
        LitePoint[] points = PAOFuncs.getLitePointsForPAObject(paoId);
        int maxOffset = 0;
        for (int i = 0; i < points.length; i++) {
            LitePoint point = points[i];
            if (point.getPointType() == pointType ) {
                if (maxOffset < point.getPointOffset()) {
                    maxOffset = point.getPointOffset();
                }
            }
        }
        return maxOffset;
    }

}

