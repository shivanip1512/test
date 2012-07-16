package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.LiteStarsLMHardwareRowMapper;
import com.cannontech.stars.core.dao.SmartLiteInventoryBaseRowMapper;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.web.bean.InventoryBean;
import com.google.common.collect.Lists;

public class StarsSearchDaoImpl implements StarsSearchDao {
    private static final YukonRowMapper<LiteInventoryBase> inventoryRowMapper = new SmartLiteInventoryBaseRowMapper();
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private InventoryBaseDao inventoryBaseDao;

    @Override
    public LiteLmHardwareBase searchLmHardwareBySerialNumber(String serialNumber, YukonEnergyCompany energyCompany) throws ObjectInOtherEnergyCompanyException {
        return searchLmHardwareBySerialNumber(serialNumber, energyCompany.getEnergyCompanyId());
    }

    @Override
    public LiteLmHardwareBase searchLmHardwareBySerialNumber(String serialNumber, int energyCompanyId) throws ObjectInOtherEnergyCompanyException {
        
        SqlStatementBuilder idSql = new SqlStatementBuilder();
        idSql.append("SELECT inventoryId");
        idSql.append("FROM LMHardwareBase");
        idSql.append("WHERE UPPER(ManufacturerSerialNumber) = UPPER(").appendArgument(serialNumber).append(")");
        
        List<Integer> hardwareIds = yukonJdbcTemplate.query(idSql, new IntegerRowMapper());
        
        List<LiteLmHardwareBase> liteHardwareList = new ArrayList<LiteLmHardwareBase>();
        if(hardwareIds.size() > 0) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ib.*, lhb.*, etim.EnergyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
            sql.append("FROM InventoryBase ib, LMHardwareBase lhb, ECToInventoryMapping etim, YukonListEntry yle");
            sql.append("WHERE lhb.InventoryId = ib.InventoryId");
            sql.append("AND etim.InventoryId = ib.InventoryId");
            sql.append("AND yle.EntryId = ib.CategoryId");
            sql.append("AND ib.InventoryId").in(hardwareIds);
    
            liteHardwareList = yukonJdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());
        }
        
        if(liteHardwareList.size() == 0) {
            return null;
        }
        
        LiteLmHardwareBase lmHardwareInOtherEnergyCompany = null;
        for (LiteLmHardwareBase liteLmHardwareBase : liteHardwareList) {
            if(energyCompanyId == liteLmHardwareBase.getEnergyCompanyId()) {
                //found _a_ match on same energy company, let's use that one.
                return liteLmHardwareBase;
            } else {    //found a match on different energy company
                lmHardwareInOtherEnergyCompany = liteLmHardwareBase;
            }
        }
        
        // If we get to here, we didn't find the serial number for the energyCompany provided.  Throw exception.
        YukonEnergyCompany otherEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByInventoryId(lmHardwareInOtherEnergyCompany.getInventoryID());
        throw new ObjectInOtherEnergyCompanyException(lmHardwareInOtherEnergyCompany, otherEnergyCompany);
    }

    @Override
    public List<LiteInventoryBase> searchLMHardwareBySerialNumber(String serialNumber,
            Collection<YukonEnergyCompany> yecList) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany yec : yecList) {
            ecIdList.add(yec.getEnergyCompanyId());
        }

        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib, LMHardwareBase lhb, ECToInventoryMapping etim, YukonListEntry yle");
        sql.append("WHERE lhb.InventoryId = ib.InventoryId");
        sql.append("AND etim.inventoryId = lhb.InventoryId");
        sql.append("AND yle.EntryId = ib.CategoryId");
        sql.append("AND UPPER(lhb.ManufacturerSerialNumber) = UPPER(").appendArgument(serialNumber).append(")");
        sql.append("AND etim.energyCompanyId").in(ecIdList);
        
        
        List<LiteInventoryBase> hardwareList = yukonJdbcTemplate.query(sql, inventoryRowMapper);
        
        return hardwareList;
    }

    @Override
    public LiteInventoryBase searchForDevice(int categoryID, String deviceName, YukonEnergyCompany yec)
    throws ObjectInOtherEnergyCompanyException {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, mhb.*, ypo.paoname, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
        sql.append("WHERE ib.DeviceID > 0");
        sql.append("AND ib.CategoryID").eq(categoryID);
        sql.append("AND UPPER(ypo.paoname) LIKE UPPER(").appendArgument(deviceName + "%").append(")");
        
        List<LiteInventoryBase> inventoryBaseList = yukonJdbcTemplate.query(sql, inventoryRowMapper);
        
        if (inventoryBaseList.size() == 0) {
            return null;
        }
        
        LiteInventoryBase inventoryBase = inventoryBaseList.get(0);
        if(!inventoryBase.getEnergyCompanyId().equals(yec.getEnergyCompanyId())) {
            throw new ObjectInOtherEnergyCompanyException(inventoryBase, yec);
        }
        
        return inventoryBase;
    }
    

    @Override
    public List<LiteInventoryBase> searchInventoryByDeviceName(String deviceName, Collection<YukonEnergyCompany> yecList) {

        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany yec : yecList) {
            ecIdList.add(yec.getEnergyCompanyId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, mhb.*, ypo.paoname, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectId = ib.DeviceId");
        sql.append("WHERE ib.DeviceID > 0");
        sql.append("AND etim.energyCompanyId").in(ecIdList);
        sql.append("AND UPPER(ypo.paoname) LIKE UPPER(").appendArgument(deviceName + "%").append(")");
        
        List<LiteInventoryBase> inventoryList = yukonJdbcTemplate.query(sql, inventoryRowMapper);
        
        return inventoryList;
    }

    @Override
    public LiteInventoryBase getByDeviceId(int deviceId, YukonEnergyCompany energyCompany)
            throws ObjectInOtherEnergyCompanyException {

        //Throws NotFoundException
        LiteInventoryBase inventoryBase = inventoryBaseDao.getByDeviceId(deviceId);
        
        return verifyInventoryInEnergyCompany(inventoryBase,energyCompany);
    }

    @Override
    public LiteInventoryBase getById(int inventoryId, YukonEnergyCompany energyCompany)
            throws ObjectInOtherEnergyCompanyException {

        LiteInventoryBase inventoryBase = inventoryBaseDao.getByInventoryId(inventoryId);
        return verifyInventoryInEnergyCompany(inventoryBase,energyCompany);
    }
    
    private LiteInventoryBase verifyInventoryInEnergyCompany(LiteInventoryBase invBase, YukonEnergyCompany energyCompany) throws ObjectInOtherEnergyCompanyException {
        if(!invBase.getEnergyCompanyId().equals(energyCompany.getEnergyCompanyId())) {
            throw new ObjectInOtherEnergyCompanyException(invBase, energyCompany);
        }
        
        return invBase;
    }
    
    @Override
    public List<LiteInventoryBase> searchInventoryByAltTrackNumber(String altTrackNumber, Collection<YukonEnergyCompany> yecList) {

        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany yec : yecList) {
            ecIdList.add(yec.getEnergyCompanyId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE ib.InventoryId >= 0");
        sql.append("AND UPPER(ib.AlternateTrackingNumber) = UPPER(").appendArgument(altTrackNumber).append(")");
        sql.append("AND etim.energyCompanyId").in(ecIdList);
        
        List<LiteInventoryBase> inventoryBaseList = yukonJdbcTemplate.query(sql, inventoryRowMapper);
        
        return inventoryBaseList;
    }

    @Override
    public List<LiteInventoryBase> searchInventoryByInstallationCompany(
            int installationCompanyId,
            Collection<YukonEnergyCompany> yecList) {

        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany yec : yecList) {
            ecIdList.add(yec.getEnergyCompanyId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ib.*, lhb.*, mhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("LEFT OUTER JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("LEFT OUTER JOIN MeterHardwareBase mhb ON mhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("WHERE ib.InventoryId >= 0");
        sql.append("AND ib.InstallationCompanyId").eq(installationCompanyId);
        sql.append("AND etim.energyCompanyId").in(ecIdList);
        
        List<LiteInventoryBase> inventoryBaseList = yukonJdbcTemplate.query(sql, inventoryRowMapper);
        
        return inventoryBaseList;
    }

    @Override
    public List<LiteLmHardwareBase> searchLMHardwareByRoute(int routeId,
            Collection<YukonEnergyCompany> yecList) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany yec : yecList) {
            ecIdList.add(yec.getEnergyCompanyId());
        }

        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.inventoryId = lhb.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("AND lhb.RouteId").eq(routeId);
        sql.append("AND etim.EnergyCompanyId").in(ecIdList);
        
        
        List<LiteLmHardwareBase> hardwareList = yukonJdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());
        
        return hardwareList;
    }

    @Override
    public List<LiteLmHardwareBase> searchLMHardwareBySerialNumberRange(
            long startSerialNumber, long endSerialNumber,
            int deviceTypeDefinitionId,
            Collection<YukonEnergyCompany> yecList) throws PersistenceException {


        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        List<Integer> ecIdList = Lists.newArrayList();
        for (YukonEnergyCompany yec : yecList) {
            ecIdList.add(yec.getEnergyCompanyId());
        }

        sql.append("SELECT ib.*, lhb.*, etim.energyCompanyId, yle.YukonDefinitionId AS CategoryDefId");
        sql.append("FROM InventoryBase ib");
        sql.append("JOIN LMHardwareBase lhb ON lhb.InventoryId = ib.InventoryId");
        sql.append("JOIN ECToInventoryMapping etim ON etim.inventoryId = lhb.InventoryId");
        sql.append("JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId");
        sql.append("JOIN YukonListEntry yle2 ON yle2.EntryId = lhb.LMHardwareTypeId");
        sql.append("AND yle2.YukonDefinitionId").eq(deviceTypeDefinitionId);
        sql.append("AND CAST(lhb.ManufacturerSerialNumber AS NUMERIC)").gte(startSerialNumber);
        sql.append("AND CAST(lhb.ManufacturerSerialNumber AS NUMERIC)").lte(endSerialNumber);
        sql.append("AND etim.EnergyCompanyId").in(ecIdList);
        
        List<LiteLmHardwareBase> hardwareList = null;
        try {
            hardwareList = yukonJdbcTemplate.query(sql, new LiteStarsLMHardwareRowMapper());
            
        // Oracle seems to throw SQLSyntaxErrorException whereas MS-SQL throws SQLException, 
        // that is the root of discrepancy in the Spring SQL error exception translator to throw
        // BadSqlGrammarException vs. DataIntegrityViolationException
        } catch (BadSqlGrammarException e){
            throw new PersistenceException(InventoryBean.INVENTORY_SQL_ERROR_FUNCTION, e);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException(InventoryBean.INVENTORY_SQL_ERROR_FUNCTION, e);
        }
        
        return hardwareList;
    }

}