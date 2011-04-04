package com.cannontech.stars.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.service.DefaultRouteService;

public class DefaultRouteServiceImpl implements DefaultRouteService {
    private static final Logger log = YukonLogManager.getLogger(DefaultRouteServiceImpl.class);
    
    private DBPersistentDao dbPersistentDao;
    private StarsEventLogService starsEventLogService;
    private PaoPermissionService paoPermissionService;
    private YukonJdbcOperations yukonJdbcTemplate;
    
    public int getDefaultRoute(LiteStarsEnergyCompany energyCompany) {
        Set<Integer> permittedPaoIds = paoPermissionService.getPaoIdsForUserPermission(energyCompany.getEnergyCompanyUser(), Permission.DEFAULT_ROUTE);
        if(permittedPaoIds.isEmpty()) {
            log.info("no permitted PAOs found for default route for energy company " + energyCompany.getName());
            return LiteStarsEnergyCompany.INVALID_ROUTE_ID;
        }

        List<Integer> serialGroupIds = getPermittedSerialGroupIds(permittedPaoIds);

        // get a serial group whose serial number is set to 0, the route id of this group is the default route id
        if (serialGroupIds.isEmpty()) {
            log.info("no serial group IDs for default route found for energy company " + energyCompany.getName());
            return LiteStarsEnergyCompany.INVALID_ROUTE_ID;
        }

        // get versacom serial groups
        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("SELECT LMGV.RouteId");
        sql2.append("FROM YukonPAObject PAO");
        sql2.append("  JOIN LMGroupVersacom LMGV ON PAO.PAObjectId = LMGV.DeviceId");
        sql2.append("WHERE LMGV.DeviceId").in(serialGroupIds);

        List<Integer> versacomDefaultRouteIds = yukonJdbcTemplate.query(sql2, new IntegerRowMapper());
        if (versacomDefaultRouteIds.size() > 0) {
            return versacomDefaultRouteIds.get(0);
        }

        // get expresscom serial groups 
        SqlStatementBuilder sql3 = new SqlStatementBuilder();
        sql3.append("SELECT LMGE.RouteId");
        sql3.append("FROM YukonPAObject PAO");
        sql3.append("  JOIN LMGroupExpresscom LMGE ON PAO.PAObjectId = LMGE.LMGroupId");
        sql3.append("WHERE LMGE.LmGroupId").in(serialGroupIds);

        List<Integer> expresscomDefaultRouteIds = yukonJdbcTemplate.query(sql3, new IntegerRowMapper());
        if (expresscomDefaultRouteIds.size() > 0) {
            return expresscomDefaultRouteIds.get(0);
        }

        log.info("no default route id found for energy company " + energyCompany.getName());
        return LiteStarsEnergyCompany.INVALID_ROUTE_ID;

    }
    
    public void updateDefaultRoute(LiteStarsEnergyCompany energyCompany, int routeId, LiteYukonUser user) {
        
        int previousRouteId = energyCompany.getDefaultRouteId();
        if (previousRouteId != routeId) {
            if (routeId == LiteStarsEnergyCompany.INVALID_ROUTE_ID) {
                removeDefaultRoute(energyCompany);
            } else if (previousRouteId == LiteStarsEnergyCompany.INVALID_ROUTE_ID) {
                setupNewDefaultRoute(energyCompany, routeId);
            } else if (routeId > 0 || previousRouteId > 0) {
                if (routeId < 0) routeId = 0;
                updateExistingDefaultRoute(energyCompany, routeId);
            }
            
            // Sending out DB change to notify possible other servers.
            dbPersistentDao.processDatabaseChange(DbChangeType.UPDATE, 
                                                  DbChangeCategory.ENERGY_COMPANY_ROUTE, 
                                                  energyCompany.getEnergyCompanyId());

            // Logging Default Route Id
            starsEventLogService.energyCompanyDefaultRouteChanged(user, energyCompany.getName(),
                                                                  previousRouteId, routeId);
        }
    }

    private void updateExistingDefaultRoute(LiteStarsEnergyCompany energyCompany, int routeID) {
        Set<Integer> permittedPaoIds = paoPermissionService.getPaoIdsForUserPermission(energyCompany.getUser(), Permission.DEFAULT_ROUTE);
        if (!permittedPaoIds.isEmpty()) {
            List<Integer> routeGroupIds = getPermittedSerialGroupIds(permittedPaoIds);
            if (routeGroupIds.size() == 0) {
                throw new RuntimeException("Not able to find the default route group");
            }

            if (routeGroupIds.size() > 1) {
                log.warn("Expected one group ID, got multiple, continuing with first: " + routeGroupIds);
            }

            int groupId = routeGroupIds.get(0);
            LMGroupExpressCom expresscomGroup = (LMGroupExpressCom)LMFactory.createLoadManagement(PaoType.LM_GROUP_EXPRESSCOMM.getDeviceTypeId());
            expresscomGroup.setLMGroupID(groupId);
            dbPersistentDao.performDBChange(expresscomGroup, TransactionType.RETRIEVE);

            com.cannontech.database.db.device.lm.LMGroupExpressCom expressComGroupDb = expresscomGroup.getLMGroupExpressComm();
            expressComGroupDb.setRouteID(routeID);
            dbPersistentDao.performDBChange(expresscomGroup, TransactionType.UPDATE);
        }
    }

    private List<Integer> getPermittedSerialGroupIds(Set<Integer> permittedPaoIDs) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT exc.LMGroupID");
        sql.append("FROM LMGroupExpressCom exc");
        sql.append("  JOIN GenericMacro macro ON macro.ChildID = exc.LMGroupID");
        sql.append("WHERE macro.MacroType").eq(MacroTypes.GROUP);
        sql.append("  AND exc.SerialNumber = '0'");
        sql.append("  AND macro.OwnerID").in(permittedPaoIDs);
        
        List<Integer> routeGroupIds = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        return routeGroupIds;
    }

    private void setupNewDefaultRoute(LiteStarsEnergyCompany energyCompany, int routeID) {
        // Assign the default route to the energy company
        // Checks to see if the LMGroupExpressCom exists
        String nameOfDefaultRoute = energyCompany.getName() + " Default Route";
        List<Integer> paoObjectIds = getDeviceGroupByName(nameOfDefaultRoute);
        LMGroupExpressCom expresscomGroup;
        if (paoObjectIds.size() == 0) {
            //  Creates the expresscom if it doesn't exist.
            expresscomGroup = (LMGroupExpressCom) LMFactory.createLoadManagement(PaoType.LM_GROUP_EXPRESSCOMM.getDeviceTypeId());
            expresscomGroup.setPAOName(nameOfDefaultRoute);  
            expresscomGroup.setRouteID(routeID);
            dbPersistentDao.performDBChange(expresscomGroup, TransactionType.INSERT);
        } else {
            if (paoObjectIds.size() > 1) {
                log.warn("Expected one group ID, got multiple, continuing with first: " + paoObjectIds);
            }
            expresscomGroup = new LMGroupExpressCom();
            
            int deviceID = paoObjectIds.get(0);
            expresscomGroup.setDeviceID(deviceID);
            dbPersistentDao.performDBChange(expresscomGroup, TransactionType.RETRIEVE);
        }

        // Checks to see if the MacroGroup Exists
        String nameOfSerialGroup = energyCompany.getName() + " Serial Group";
        List<Integer> macroGroupIds = getDeviceGroupByName(nameOfSerialGroup);
        MacroGroup serialGroup;
        PaoType macroGroupType = PaoType.MACRO_GROUP;
        if (macroGroupIds.size() == 0) {
            // Creates a macrogroup if it doesn't exist.
            serialGroup = (MacroGroup) LMFactory.createLoadManagement(macroGroupType.getDeviceTypeId());  
            serialGroup.setPAOName(nameOfSerialGroup);
            GenericMacro macro = new GenericMacro();
            macro.setChildID(expresscomGroup.getPAObjectID());  
            macro.setChildOrder(0);
            macro.setMacroType( MacroTypes.GROUP );
            serialGroup.getMacroGroupVector().add( macro );
            dbPersistentDao.performDBChange(serialGroup, TransactionType.INSERT);
        } else {
            if (macroGroupIds.size() > 1) {
                log.warn("Expected one group ID, got multiple, continuing with first: " + macroGroupIds);
            }
            int deviceId = macroGroupIds.get(0);
            serialGroup = new MacroGroup();
            serialGroup.setDeviceID(deviceId);
            dbPersistentDao.performDBChange(serialGroup, TransactionType.RETRIEVE);
        }
        
        PaoType paoType = macroGroupType;
        PaoIdentifier pao = new PaoIdentifier(serialGroup.getPAObjectID(), paoType);
        paoPermissionService.addPermission(energyCompany.getUser(), pao, Permission.DEFAULT_ROUTE, true);
    }

    private List<Integer> getDeviceGroupByName(String nameOfDefaultRoute) {
        SqlStatementBuilder sql1 = new SqlStatementBuilder();
        sql1.append("SELECT PAO.PAObjectId");
        sql1.append("FROM YukonPAObject PAO");
        sql1.append("WHERE PAO.Category").eq_k(PaoCategory.DEVICE);
        sql1.append(  "AND PAO.PAOClass").eq_k(PaoClass.GROUP);
        sql1.append(  "AND PAO.PAOName").eq(nameOfDefaultRoute);

        List<Integer> paoObjectIds = yukonJdbcTemplate.query(sql1, new IntegerRowMapper());
        return paoObjectIds;
    }
    
    public void removeDefaultRoute(final LiteStarsEnergyCompany energyCompany) {
        Set<Integer> permittedPaoIds = paoPermissionService.getPaoIdsForUserPermission(energyCompany.getUser(), Permission.DEFAULT_ROUTE);
        if (!permittedPaoIds.isEmpty()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT exc.LMGroupID, macro.OwnerID");
            sql.append("FROM LMGroupExpressCom exc");
            sql.append(  "JOIN GenericMacro macro on macro.ChildID = exc.LMGroupID");
            sql.append("WHERE macro.MacroType").eq(MacroTypes.GROUP);
            sql.append(  "AND exc.SerialNumber = '0'");
            sql.append(  "AND macro.OwnerID").in(permittedPaoIds);
            
            yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
                
                @Override
                public void processRow(YukonResultSet rs) throws SQLException {
                    int defaultRouteGroupId = rs.getInt("LMGroupID");
                    int serialGroupId = rs.getInt("OwnerId");
                    /*Load groups are only assigned to users, so for now we only have to worry about removing from
                     * the user.
                     */
                    PaoType paoType = PaoType.MACRO_GROUP;
                    PaoIdentifier pao = new PaoIdentifier(serialGroupId, paoType);
                    paoPermissionService.removePermission(energyCompany.getUser(), pao, Permission.DEFAULT_ROUTE);
                    
                    MacroGroup grpSerial = (MacroGroup) LMFactory.createLoadManagement(PaoType.MACRO_GROUP.getDeviceTypeId());
                    grpSerial.setDeviceID(serialGroupId);
                    dbPersistentDao.performDBChange(grpSerial, TransactionType.DELETE);
                    
                    LMGroupExpressCom grpDftRoute = (LMGroupExpressCom) LMFactory.createLoadManagement(PaoType.LM_GROUP_EXPRESSCOMM.getDeviceTypeId());
                    grpDftRoute.setLMGroupID(defaultRouteGroupId);
                    dbPersistentDao.performDBChange(grpDftRoute, TransactionType.DELETE);
                }
            });
        }
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Autowired
    public void setStarsEventLogService(StarsEventLogService starsEventLogService) {
        this.starsEventLogService = starsEventLogService;
    }

    @Autowired
    public void setPaoPermissionService(PaoPermissionService paoPermissionService) {
        this.paoPermissionService = paoPermissionService;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcOperations yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
