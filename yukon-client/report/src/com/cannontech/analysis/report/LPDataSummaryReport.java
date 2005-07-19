/*
 * Created on Jul 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.report;

import java.awt.print.PageFormat;

import com.cannontech.analysis.tablemodel.PointDataSummaryModel;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LPDataSummaryReport extends PointDataSummaryReport
{

    /**
     * 
     */
    public LPDataSummaryReport() {
        this( new PointDataSummaryModel());
    }
    /**
     * @param model_
     */
    public LPDataSummaryReport(PointDataSummaryModel model_) {
        super(model_);
        setPageOrientation(PageFormat.PORTRAIT);
        model_.setPointType(PointDataSummaryModel.LOAD_PROFILE_POINT_TYPE);
		setModel(model_);
    }
}
