package com.cannontech.database.data.point;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.spring.YukonSpringHook;

public class PointOffsetUtils {

    public static boolean isValidPointOffset(int offset, Integer paoId, int type) {
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(paoId);
        for (LitePoint point : points) {
            if (point.getPointType() == type) {
                if (offset == point.getPointOffset()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getValidPointOffset(Integer paoId) {
        return getMaxPointOffsetForDevice(paoId) + 1;
    }

    public static int getMaxPointOffsetForDevice(Integer paoId) {
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(paoId);
        if (points.isEmpty()) {
            return 0;
        } else {
            LitePoint maxPoint = Collections.max(points, new Comparator<LitePoint>() {
                public int compare(LitePoint p1, LitePoint p2) {
                    return p1.getPointOffset() - p2.getPointOffset();
                }
            });
            return maxPoint.getPointOffset();
        }
    }
}
