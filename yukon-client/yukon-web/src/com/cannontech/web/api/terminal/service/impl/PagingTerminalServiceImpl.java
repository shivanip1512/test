package com.cannontech.web.api.terminal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.web.api.terminal.service.PagingTerminalService;
import com.cannontech.yukon.IDatabaseCache;

public class PagingTerminalServiceImpl implements PagingTerminalService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private IDatabaseCache cache;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public TerminalBase create(TerminalBase terminalBase) {
        IEDBase iedBase = TerminalBaseFactory.getTerminalBase(terminalBase.getType());
        terminalBase.buildDBPersistent(iedBase);
        dbPersistentDao.performDBChange(iedBase, TransactionType.INSERT);
        // Add default points
        SimpleDevice device = SimpleDevice.of(iedBase.getPAObjectID(), iedBase.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);
        // Create Route
        createRoute(iedBase);
        // Build model object
        terminalBase.buildModel(iedBase);
        terminalBase.getCommChannel().setName(cache.getAllPaosMap().get(terminalBase.getCommChannel().getId()).getPaoName());
        return terminalBase;
    }

    /**
     * Create a route for the paging terminal.
     */
    private void createRoute(IEDBase iedBase) {
        PaoType paoType = iedBase.getPaoType();
        if (paoType.isTransmitter()) {

            PaoType routeType = null;
            if (paoType == PaoType.TAPTERMINAL) {
                routeType = PaoType.ROUTE_TAP_PAGING;
            } else if (paoType == PaoType.TNPP_TERMINAL) {
                routeType = PaoType.ROUTE_TNPP_TERMINAL;
            } else if (paoType == PaoType.WCTP_TERMINAL) {
                routeType = PaoType.ROUTE_WCTP_TERMINAL;
            } else if (paoType == PaoType.SNPP_TERMINAL) {
                routeType = PaoType.ROUTE_SNPP_TERMINAL;
            }

            // A route is automatically added to each transmitter create new route to be added
            RouteBase route = RouteFactory.createRoute(routeType);
            route.setRouteName(iedBase.getPAOName());
            route.setDeviceID(((DeviceBase) iedBase).getDevice().getDeviceID());
            route.setDefaultRoute(CtiUtilities.getTrueCharacter().toString());
            dbPersistentDao.performDBChange(route, TransactionType.INSERT);
        }
    }
}