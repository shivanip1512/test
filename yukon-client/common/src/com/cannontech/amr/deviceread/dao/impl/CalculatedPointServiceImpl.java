package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collections;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.deviceread.CalculatedPointResults;
import com.cannontech.amr.deviceread.dao.CalculatedPointService;
import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * @author mkruse
 * 
 */
public class CalculatedPointServiceImpl implements CalculatedPointService {

	private Logger logger = YukonLogManager
			.getLogger(CalculatedPointServiceImpl.class);

	AttributeService attributeService;
	MeterReadService meterReadService;
	PeakReportService peakReportService;

	/**
	 * This calculates a point in the past from the current usage.
	 * 
	 * @param device
	 * @param beginDate
	 */
	public CalculatedPointResults calculatePoint(Meter meter, Date beginDate, LiteYukonUser liteYukonUser) {

        CalculatedPointResults results = new CalculatedPointResults();

		// Makes a call to the meter to find the usage,
		// which we use to calculate the point
		logger.info("Starting meter read for " + meter.toString());
		CommandResultHolder meterReadResults = meterReadService.readMeter(
				meter, Collections.singleton(BuiltInAttribute.ENERGY),
				liteYukonUser);

        PointValueHolder currentPVH = null;
		if (meterReadResults.isErrorsExist()) {
            results.setErrors(meterReadResults.getErrors());
			return results;
		} else {
			LitePoint lp = attributeService.getPointForAttribute(meter,
					BuiltInAttribute.ENERGY);
			for (PointValueHolder pvh : meterReadResults.getValues()) {
				if (pvh.getId() == lp.getLiteID()) {
					currentPVH = pvh;
				}
			}
		}

		// profile peak calculating out the average usage
		logger.info("Starting peak report request for " + meter.toString());
		PeakReportResult peakReportResults = peakReportService
				.requestPeakReport(meter.getDeviceId(), PeakReportPeakType.DAY,
						PeakReportRunType.PRE, 1, beginDate, new Date(), false,
						liteYukonUser);

		double calculatedDifferenceUsage = 0;
		if (peakReportResults.getErrors().isEmpty()) {
			calculatedDifferenceUsage = peakReportResults.getTotalUsage();
		} else {
            results.setErrors(peakReportResults.getErrors());
			return results;
		}

		// Check the relative position of the wanted move out
		// reading in comparison with the previous reading
		double calculatedUsageValue = 0;
		calculatedUsageValue = currentPVH.getValue() - calculatedDifferenceUsage;

		SimplePointValue calculatedPVH = new SimplePointValue(currentPVH.getId(),
														 beginDate, 
														 currentPVH.getType(), 
														 calculatedUsageValue);
        
        SimplePointValue differencePVH = new SimplePointValue(currentPVH.getId(),
                                                         currentPVH.getPointDataTimeStamp(),
                                                         currentPVH.getType(),
                                                         calculatedDifferenceUsage);

        results.setErrors(peakReportResults.getErrors());
        results.setCurrentPVH(currentPVH);
        results.setDifferencePVH(differencePVH);
        results.setCalculatedPVH(calculatedPVH);
        
        return results;
	}

	@Required
	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	@Required
	public void setMeterReadService(MeterReadService meterReadService) {
		this.meterReadService = meterReadService;
	}

	@Required
	public void setPeakReportService(PeakReportService peakReportService) {
		this.peakReportService = peakReportService;
	}
}