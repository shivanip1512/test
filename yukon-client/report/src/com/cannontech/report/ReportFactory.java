package com.cannontech.report;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:08:41 PM)
 * @author: 
 */

import com.cannontech.report.loadmanagement.*;
public final class ReportFactory
{
/**
 * Insert the method's description here.
 * Creation date: (5/18/00 3:50:38 PM)
 */
public final static ReportBase createReport(int type)
{
	ReportBase retRB = null;
	switch(type)
	{
		case ReportTypes.LOADS_SHED:
			retRB =  new LoadsShedReport();
			break;

		case ReportTypes.DAILY_PEAKS:
			retRB =  new DailyPeaksReport();
			break;

		default: //this is bad
			throw new Error("ReportFactory::createReport - Unrecognized report type");
	}
	return retRB;
}
}
