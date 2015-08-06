
package com.cannontech.web.tools.trends.data;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.web.tools.trends.TrendDataController;


public class GraphDataProvider implements IGraphDataProvider {
    private static final Logger log = YukonLogManager.getLogger(TrendDataController.class);
    private GraphType graphType;
    @Override
    public List<Object[]> getGraphData(List<PointValueHolder> data) {

        return null;

    }

    @Override
    public List<Object[]> getDateRangeGraphData(List<PointValueHolder> data, Date date) {

        return null;

    }

    @Override
    public List<Object[]> getYesterdayGraphData(List<PointValueHolder> data) {

        return null;

    }

    @Override
    public void setGraphType(GraphType graphType) {
        this.graphType = graphType;
    }

}

