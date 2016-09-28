package com.cannontech.web.tools.mapping.service;

import java.util.List;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.web.tools.mapping.model.Neighbor;
import com.cannontech.web.tools.mapping.model.Parent;
import com.cannontech.web.tools.mapping.model.RouteInfo;


public interface NmNetworkTestService {

    Parent getParent(int deviceId);

    List<RouteInfo> getRoute(int deviceId, MessageSourceAccessor accessor);

    List<Neighbor> getNeighbors(int deviceId, MessageSourceAccessor accessor);
}
