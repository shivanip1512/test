package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteMeterHardwareBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.MeterHardwareBase;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.util.InventoryUtils;

public class StarsInventoryBaseDaoImpl implements StarsInventoryBaseDao {
    private final Logger log = YukonLogManager.getLogger(getClass());
    private StarsDatabaseCache starsDatabaseCache;
    
    @Override
    @Transactional(readOnly = true)
    public LiteInventoryBase getById(int inventoryId, int energyCompanyId) {
        return getById(inventoryId, energyCompanyId, false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LiteInventoryBase getById(final int inventoryId, final int energyCompanyId,
            final boolean brief) {
        LiteInventoryBase liteInv = null;
        
        try {
            com.cannontech.database.db.stars.hardware.InventoryBase invDB =
                    new com.cannontech.database.db.stars.hardware.InventoryBase();
            invDB.setInventoryID(inventoryId);
            invDB = Transaction.createTransaction( Transaction.RETRIEVE, invDB ).execute();
            
            if (InventoryUtils.isLMHardware( invDB.getCategoryID().intValue() )) {
                com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB =
                        new com.cannontech.database.db.stars.hardware.LMHardwareBase();
                hwDB.setInventoryID( invDB.getInventoryID() );
                
                hwDB = Transaction.createTransaction( Transaction.RETRIEVE, hwDB ).execute();
                
                com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
                        new com.cannontech.database.data.stars.hardware.LMHardwareBase();
                hardware.setInventoryBase( invDB );
                hardware.setLMHardwareBase( hwDB );
                
                liteInv = new LiteStarsLMHardware();
                StarsLiteFactory.setLiteStarsLMHardware( (LiteStarsLMHardware)liteInv, hardware );
            }
            /*
             * Should always get here for a non yukon meter.  They are not loaded into cache
             * with other inventory.
             */
            else if (InventoryUtils.isNonYukonMeter( invDB.getCategoryID().intValue()))
            {
                MeterHardwareBase mhbDB = new MeterHardwareBase();
                mhbDB.setInventoryID( invDB.getInventoryID() );
                
                mhbDB = Transaction.createTransaction( Transaction.RETRIEVE, mhbDB ).execute();
                
                com.cannontech.database.data.stars.hardware.MeterHardwareBase hardware =
                        new com.cannontech.database.data.stars.hardware.MeterHardwareBase();
                hardware.setInventoryBase( invDB );
                hardware.setMeterHardwareBase( mhbDB );
                
                liteInv = new LiteMeterHardwareBase();
                StarsLiteFactory.setLiteMeterHardwareBase( (LiteMeterHardwareBase)liteInv, hardware );
            }
            else {
                liteInv = new LiteInventoryBase();
                StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
            }
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);
        }
        
        if (liteInv == null) return null;
        
        if (!brief && !liteInv.isExtended()) {
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
            StarsLiteFactory.extendLiteInventoryBase( liteInv, energyCompany);
        }
        
        return liteInv;
    }

    @Override
    public List<LiteInventoryBase> getByIds(Set<Integer> inventoryIds,
            int energyCompanyId) {
        return getByIds(inventoryIds, energyCompanyId, false);
    }
    
    @Override
    public List<LiteInventoryBase> getByIds(final Set<Integer> inventoryIds, final int energyCompanyId,
            final boolean brief) {
        final List<LiteInventoryBase> resultList = new ArrayList<LiteInventoryBase>(inventoryIds.size());

        for (final Integer inventoryId : inventoryIds) {
            resultList.add(getById(inventoryId, energyCompanyId, brief));
        }
        
        return resultList;
    }
    
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

}
