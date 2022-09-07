package com.cannontech.multispeak.dao.impl.v4;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.msp.beans.v4.QualityDescription;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.msp.beans.v4.Value;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.ScadaAnalogProcessingService;

public class ScadaAnalogProcessingServiceImpl implements ScadaAnalogProcessingService {

    @Override
    public ScadaAnalog createScadaAnalog(LiteYukonPAObject litePAObject, LitePoint litePoint,
            PointValueQualityHolder pointValue) {

        ScadaAnalog scadaAnalog = new ScadaAnalog();

        // objectId identifier is limited to 52 characters by customer system, so take first
        // 50 characters of pao name plus '.#' where # is the point offset, 1-4.
        String paoName = litePAObject.getPaoName();
        String objectId = StringUtils.left(paoName, 50) + "." + litePoint.getPointOffset();
        scadaAnalog.setObjectID(objectId);
        // Comments
        String comments = "ProgramName: " + paoName + "; PointName: " + litePoint.getPointName();
        scadaAnalog.setComments(comments);
        // Quality
        scadaAnalog.setQuality(getQualityForPointValue(pointValue));
        // Timestamp
        Calendar cal = new GregorianCalendar();
        cal.setTime(pointValue.getPointDataTimeStamp());
        scadaAnalog.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(cal));
        // Point Value
        Value value = new Value();
        value.setValue(new Float(pointValue.getValue()));
        scadaAnalog.setValue(value);

        return scadaAnalog;
    }

    /**
     * Convert Yukon point quality to a MultiSpeak QualityDescription.
     * Non-Update -> Failed
     * Else -> Measured ("normal")
     */
    private QualityDescription getQualityForPointValue(PointValueQualityHolder pointValue) {
        if (pointValue.getPointQuality() == PointQuality.NonUpdated) {
            return QualityDescription.FAILED;
        }
        return QualityDescription.MEASURED; // Corresponds to PointQuality.NORMAL.
    }

}
