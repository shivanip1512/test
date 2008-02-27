package com.cannontech.database.data.point;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;

public class PointOffsetUtils {

    public PointOffsetUtils() {
        super();
     
    }

    public static boolean isValidPointOffset(int offset, Integer paoId, int type){
        List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(paoId);
        for (LitePoint point : points) {
		    if (point.getPointType() == type ) {
		        if (offset == point.getPointOffset()) {
		        	return false;
		        }
		    }
		}
		return true;
    }
    
    public static int getValidPointOffset(Integer paoId, int type) {
        return getMaxPointOffsetForDevice (paoId, type) + 1;  
    }
    
    public static int getMaxPointOffsetForDevice(Integer paoId, int pointType) {        
        List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(paoId);
        if(points.isEmpty()) {
            return 0;
        }else {
            LitePoint maxPoint = Collections.max(points, new Comparator<LitePoint>() {
                public int compare(LitePoint p1, LitePoint p2) {
                    return p1.getPointOffset() - p2.getPointOffset();
                }
            });        
            return maxPoint.getPointOffset();
        }
    }
}

