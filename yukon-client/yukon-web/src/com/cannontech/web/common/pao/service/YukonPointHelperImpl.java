package com.cannontech.web.common.pao.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class YukonPointHelperImpl implements YukonPointHelper {
    
    @Autowired private PointDao pointDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Override
    public List<LiteYukonPoint> getYukonPoints(final YukonPao pao, SortingParameters sorting) {
        
        Function<LitePoint, LiteYukonPoint> pointFunction = new Function<LitePoint, LiteYukonPoint>() {
            @Override
            public LiteYukonPoint apply(LitePoint lp) {
                PointIdentifier pointId = new PointIdentifier(lp.getPointTypeEnum(), lp.getPointOffset());
                PaoType paoType = pao.getPaoIdentifier().getPaoType();
                BuiltInAttribute attribute = paoDefinitionDao.findOneAttributeForPaoTypeAndPoint(PaoTypePointIdentifier.of(paoType, pointId));
                return LiteYukonPoint.of(new PaoPointIdentifier(pao.getPaoIdentifier(), pointId), attribute, lp.getPointName(), lp.getPointID());
            }
        };
        
        List<LitePoint> points = pointDao.getLitePointsByPaObjectId(pao.getPaoIdentifier().getPaoId());
        
        List<LiteYukonPoint> liteYukonPoints = new ArrayList<>(Lists.transform(points, pointFunction));
        PointSortField sortField = PointSortField.valueOf(sorting.getSort());
        Collections.sort(liteYukonPoints, LiteYukonPoint.getComparatorForSortField(sortField));
        
        if (sorting.getDirection() == Direction.desc) {
            liteYukonPoints = Lists.reverse(liteYukonPoints);
        }
        
        return liteYukonPoints;
    }
    
    @Override
    public List<LiteYukonPoint> getYukonPoints(final YukonPao pao) {
        return getYukonPoints(pao, SortingParameters.of(PointSortField.POINTNAME.name(), Direction.asc));
    }
}