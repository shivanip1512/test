package com.cannontech.multispeak.service.impl.v4;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.service.impl.MultispeakLMServiceBase;
import com.cannontech.multispeak.service.v4.MultispeakLMService;

public class MultispeakLMServiceImpl extends MultispeakLMServiceBase implements MultispeakLMService {

    @Autowired private FdrTranslationDao fdrTranslationDao;
    @Autowired private SimplePointAccessDao simplePointAccessDao;
    @Autowired private MspObjectDao mspObjectDao;

    @Override
    public PointData buildPointData(int pointId, ScadaAnalog scadaAnalog, String userName) {
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setValue(scadaAnalog.getValue().getValue());
        // pointData.setPointQuality(getPointQuality(scadaAnalog.getQuality()));
        pointData.setType(PointType.Analog.getPointTypeId());
        pointData.setStr("MultiSpeak ScadaAnalog Analog point update.");
        pointData.setUserName(userName);
        if (scadaAnalog.getTimeStamp() != null) {
            pointData.setTime(scadaAnalog.getTimeStamp().toGregorianCalendar().getTime());
        }
        return pointData;
    }

    @Override
    public ErrorObject writeAnalogPointData(ScadaAnalog scadaAnalog, LiteYukonUser liteYukonUser) {
        String objectId = scadaAnalog.getObjectID().trim();
        String translationStr = buildFdrMultispeakLMTranslation(objectId);
        List<FdrTranslation> fdrTranslations = fdrTranslationDao.getByInterfaceTypeAndTranslation(FdrInterfaceType.MULTISPEAK_LM,
                translationStr);
        if (!fdrTranslations.isEmpty()) {
            for (FdrTranslation fdrTranslation : fdrTranslations) {
                if (fdrTranslation.getDirection() == FdrDirection.RECEIVE) {
                    PointData pointData = buildPointData(fdrTranslation.getPointId(), scadaAnalog, liteYukonUser.getUsername());
                    simplePointAccessDao.writePointData(pointData);
                    CTILogger.debug("PointData update sent to Dispatch (" + pointData.toString() + ")");
                }
            }
        } else {
            return mspObjectDao.getErrorObject(objectId,
                    "No point mapping found in Yukon for objectId:" + objectId,
                    "ScadaAnalog", "writeAnalogPointData", liteYukonUser.getUsername());
        }
        return null;
    }

}
