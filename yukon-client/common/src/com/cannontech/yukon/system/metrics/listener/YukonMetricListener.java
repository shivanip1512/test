package com.cannontech.yukon.system.metrics.listener;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.loader.jaxb.Point;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class YukonMetricListener implements MessageListener {
    private static final Logger log = YukonLogManager.getLogger(YukonMetricListener.class);

    @Autowired private PointDao pointDao;
    @Autowired private SimplePointAccessDao pointAccessDao;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private PaoDefinitionDao definitionDao;
    @Autowired private DBPersistentDao dbPersistentDao;

    private Gson gson;

    @PostConstruct
    public void init() {
        gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                .create();
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            String textMessage = null;
            try {
                textMessage = ((TextMessage) message).getText();
                YukonMetric yukonMetric = gson.fromJson(textMessage, YukonMetric.class);
                if (YukonMetricPointDataType.isPointData(yukonMetric.getPointInfo())) {
                    log.info("Received Yukon Metric data " + yukonMetric);
                    YukonMetricPointDataType pointDataType = YukonMetricPointDataType.getForPointInfo(yukonMetric.getPointInfo());
                    PointIdentifier pointIdentifier = new PointIdentifier(pointDataType.getType(), pointDataType.getOffset());
                    PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(PaoUtils.SYSTEM_PAOIDENTIFIER,
                            pointIdentifier);
                    LitePoint litePoint = null;
                    try {
                        litePoint = pointDao.getLitePoint(paoPointIdentifier);
                    } catch (NotFoundException e) {
                        Map<String, Point> systemDevicePoints = definitionDao.getSystemDevicePoints();
                        if (isPointDefined(systemDevicePoints, pointDataType.getOffset(), pointDataType.getType())) {
                            litePoint = createPoint(systemDevicePoints.get(pointDataType.getName()));
                        } else {
                            throw new NotFoundException(
                                    "Point definition not found for " + pointDataType.getName() + " in SYSTEM.xml file");
                        }
                    }
                    pointAccessDao.setPointValue(litePoint, yukonMetric.getTimestamp().toInstant(),
                            Double.valueOf(yukonMetric.getValue().toString()));
                }
            } catch (Exception e) {
                log.error("Error occurred while generating point data.", e);
            }
        }
    }

    /**
     * Check whether point is defined in SSYSTEM.xml by comparing offset value and pointType.
     */
    private boolean isPointDefined(Map<String, Point> systemDevicePoints, Integer offset, PointType type) {
        return systemDevicePoints.values().stream()
                .filter(point -> point.getOffset() == offset && PointType.valueOf(point.getType()) == type).findAny().isPresent();
    }

    /**
     * Create the specified Point and return LitePoint object for the created point.
     */
    private LitePoint createPoint(Point point) throws SQLException {
        StatusControlType controlType = point.getControlType() == null ? StatusControlType.NONE : StatusControlType
                .valueOf(point.getControlType().getValue().name());
        PointArchiveType pointArchiveType = point.getArchive() == null
                || point.getArchive().getType() == null ? PointArchiveType.NONE : PointArchiveType
                        .valueOf(point.getArchive().getType());
        PointArchiveInterval pointArchiveInterval = point.getArchive() == null
                || point.getArchive().getInterval() == null ? PointArchiveInterval.ZERO : PointArchiveInterval
                        .valueOf(point.getArchive().getInterval());
        PointBase pointBase = pointCreationService.createPoint(PointType.Analog.getPointTypeId(),
                point.getName(), PaoUtils.SYSTEM_PAOIDENTIFIER,
                point.getOffset(), point.getMultiplier().getValue().doubleValue(),
                UnitOfMeasure.valueOf(point.getUnitofmeasure().getValue().name()).getId(), StateGroupUtils.DEFAULT_STATE, 0,
                point.getDecimalplaces().getValue(), controlType,
                pointArchiveType, pointArchiveInterval);
        dbPersistentDao.performDBChange(pointBase, TransactionType.INSERT);
        LitePoint litePoint = pointDao.getLitePoint(pointBase.getPoint().getPointID());
        return litePoint;

    }

}
