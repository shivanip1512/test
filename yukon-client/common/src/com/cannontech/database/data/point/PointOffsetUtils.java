package com.cannontech.database.data.point;

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

    public static int getValidPointOffset(Integer paoId, PointType type) {
        return YukonSpringHook.getBean(PointDao.class).getNextOffsetByPaoObjectIdAndPointType(paoId, type);
    }

}
