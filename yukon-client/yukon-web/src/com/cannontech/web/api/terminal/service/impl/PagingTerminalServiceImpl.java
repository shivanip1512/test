package com.cannontech.web.api.terminal.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.TerminalCopy;
import com.cannontech.common.exception.DeletionFailureException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.api.terminal.service.PagingTerminalService;
import com.cannontech.yukon.IDatabaseCache;

public class PagingTerminalServiceImpl implements PagingTerminalService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private IDatabaseCache cache;
    @Autowired private PointDao pointDao;
    @Autowired private DbChangeManager dbChangeManager;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public TerminalBase create(TerminalBase terminalBase) {
        IEDBase iedBase = TerminalBaseFactory.getIEDBase(terminalBase.getType());
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

    @Override
    @Transactional
    public int delete(int id) {
        LiteYukonPAObject terminal = cache.getAllPaosMap().get(id);
        if (terminal == null || !(terminal.getPaoType() == PaoType.SNPP_TERMINAL || terminal.getPaoType() == PaoType.TAPTERMINAL
                || terminal.getPaoType() == PaoType.TNPP_TERMINAL || terminal.getPaoType() == PaoType.WCTP_TERMINAL)) {
            throw new NotFoundException("Terminal Id not found");
        }

        String routeName = DeviceBase.hasRoute(terminal.getLiteID());
        if (StringUtils.isNotBlank(routeName)) {
            throw new DeletionFailureException(
                    "You cannot delete the terminal '" + terminal.getPaoName() + "' because it is utilized by the route named '"
                            + routeName + "'");
        }
        YukonPAObject deleteTerminal = (YukonPAObject) LiteFactory.createDBPersistent(terminal);
        dbPersistentDao.performDBChange(deleteTerminal, TransactionType.DELETE);
        return deleteTerminal.getPAObjectID();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public TerminalBase<?> retrieve(int id) {
        LiteYukonPAObject terminal = cache.getAllPaosMap().get(id);
        if (terminal == null || !(terminal.getPaoType() == PaoType.SNPP_TERMINAL || terminal.getPaoType() == PaoType.TAPTERMINAL
                || terminal.getPaoType() == PaoType.TNPP_TERMINAL || terminal.getPaoType() == PaoType.WCTP_TERMINAL)) {
            throw new NotFoundException("Terminal Id not found");
        }

        IEDBase iedBase = (IEDBase) dbPersistentDao.retrieveDBPersistent(terminal);
        TerminalBase terminalBase = TerminalBaseFactory.getTerminalBase(iedBase.getPaoType());
        terminalBase.buildModel(iedBase);
        terminalBase.getCommChannel().setName(cache.getAllPaosMap().get(terminalBase.getCommChannel().getId()).getPaoName());
        return terminalBase;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<TerminalBase> retrieveAll() {
        List<LiteYukonPAObject> liteObjectList = cache.getAllPaosMap().values().stream()
                .filter(pao -> pao.getPaoType() == PaoType.SNPP_TERMINAL || pao.getPaoType() == PaoType.TAPTERMINAL
                        || pao.getPaoType() == PaoType.TNPP_TERMINAL || pao.getPaoType() == PaoType.WCTP_TERMINAL)
                .collect(Collectors.toList());
        List<TerminalBase> terminalList = new ArrayList<TerminalBase>();
        if (CollectionUtils.isNotEmpty(liteObjectList)) {
            liteObjectList.forEach(liteObject -> {
                IEDBase iedBase = (IEDBase) dbPersistentDao.retrieveDBPersistent(liteObject);
                TerminalBase terminalBase = new TerminalBase<IEDBase>(iedBase.getPAObjectID(), iedBase.getPAOName(),
                        iedBase.getPaoType(),
                        !iedBase.isDisabled());
                terminalList.add(terminalBase);
            });

        }
        return terminalList;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public TerminalBase<?> copy(int id, TerminalCopy terminalCopy) {
        LiteYukonPAObject terminal = cache.getAllPaosMap().get(id);
        if (terminal == null || !(terminal.getPaoType() == PaoType.SNPP_TERMINAL || terminal.getPaoType() == PaoType.TAPTERMINAL
                || terminal.getPaoType() == PaoType.TNPP_TERMINAL || terminal.getPaoType() == PaoType.WCTP_TERMINAL)) {
            throw new NotFoundException("Terminal Id not found");
        }
        IEDBase iedBase = (IEDBase) dbPersistentDao.retrieveDBPersistent(terminal);
        terminalCopy.buildDBPersistent(iedBase);
        iedBase.setDeviceID(null);
        dbPersistentDao.performDBChange(iedBase, TransactionType.INSERT);
        if (terminalCopy.getCopyPoints() != null) {
            // Copy points if true
            if (terminalCopy.getCopyPoints()) {
                List<PointBase> points = pointDao.getPointsForPao(id);
                SimpleDevice device = SimpleDevice.of(iedBase.getPAObjectID(), iedBase.getPaoType());
                paoCreationHelper.applyPoints(device, points);
                dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
            }
        }
        // Create Route
        createRoute(iedBase);
        TerminalBase terminalBase = TerminalBaseFactory.getTerminalBase(iedBase.getPaoType());
        // Build model object
        terminalBase.buildModel(iedBase);
        terminalBase.getCommChannel().setName(cache.getAllPaosMap().get(terminalBase.getCommChannel().getId()).getPaoName());
        return terminalBase;
    }

}