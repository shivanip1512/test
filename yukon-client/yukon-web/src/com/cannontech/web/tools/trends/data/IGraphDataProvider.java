
package com.cannontech.web.tools.trends.data;

import java.util.Date;
import java.util.List;

import com.cannontech.core.dynamic.PointValueHolder;

public interface IGraphDataProvider {
    List<Object[]> getGraphData(List<PointValueHolder> data);
    List<Object[]> getDateRangeGraphData(List<PointValueHolder> data, Date date);
    List<Object[]> getYesterdayGraphData(List<PointValueHolder> data);
    void setGraphType(GraphType graphType);
}

