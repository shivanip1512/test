package stars.dr.hardware;


import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.LMHardwareClass;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.impl.StarsSearchDaoImpl;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.impl.InventoryBaseDaoImpl;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;
import com.cannontech.web.stars.dr.operator.hardware.service.impl.HardwareUiServiceImpl;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class HardwareServiceTest {

    private HardwareUiServiceImpl hardwareUiService;
    
    private InventoryBaseDao inventoryBaseDao;
    private StarsSearchDao starsSearchDao;
    
    @Before
    public void setUp() {
        
    	hardwareUiService = new HardwareUiServiceImpl() {
           @Override
           public HardwareDto getHardwareDto(int inventoryId, int energyCompanyId, int accountId) {
               HardwareDto dto = new HardwareDto();
               dto.setInventoryId(inventoryId);
               dto.setEnergyCompanyId(energyCompanyId);
               dto.setHardwareType(HardwareType.values()[inventoryId]);
               dto.setHardwareClass(HardwareType.values()[inventoryId].getLMHardwareClass());
               return dto;
           }
        };
        
        inventoryBaseDao = new InventoryBaseDaoImpl() {
            @Override
            public List<Integer> getInventoryIdsByAccountId(int accountId) {
                List<Integer> ids = Lists.newArrayList();
                /* Will end up creating a list of inventory, one per hardware type. */
                for (int i=0; i < HardwareType.values().length; i++) {
                    ids.add(i);
                }
                
                return ids;
            }
         };
         
         starsSearchDao = new StarsSearchDaoImpl() {
            @Override
            public LiteInventoryBase searchLMHardwareBySerialNumber(String serialNumber, int energyCompanyId) {
                LiteInventoryBase liteBase = new LiteInventoryBase();
                liteBase.setInventoryID(0);
                return liteBase;
            }
         };
         
         hardwareUiService.setInventoryBaseDao(inventoryBaseDao);
         hardwareUiService.setStarsSearchDao(starsSearchDao);
    }
    
    @Test
    public void testGetHardwareMapForAccount() {
        
        ListMultimap<LMHardwareClass, HardwareDto> map = hardwareUiService.getHardwareMapForAccount(0, 0);
        Assert.assertNotNull(map);
        Assert.assertTrue("Map should not be empty", !map.isEmpty());
        
        List<HardwareDto> switches = map.get(LMHardwareClass.SWITCH);
        List<HardwareDto> thermostats = map.get(LMHardwareClass.THERMOSTAT);
        List<HardwareDto> meters = map.get(LMHardwareClass.METER);
        
        Assert.assertEquals(switches.size(), HardwareType.getForClass(LMHardwareClass.SWITCH).size());
        Assert.assertEquals(thermostats.size(), HardwareType.getForClass(LMHardwareClass.THERMOSTAT).size());
        Assert.assertEquals(meters.size(), HardwareType.getForClass(LMHardwareClass.METER).size());
        
    }
    
    @Test
    public void testGetHardwareTypeById_ForYukonMeter() {
        HardwareType type = hardwareUiService.getHardwareTypeById(0);
        Assert.assertEquals(type, HardwareType.YUKON_METER);
    }
    
    @Test
    public void testCheckSerialNumber() {
        HardwareDto possibleDuplicate = new HardwareDto();
        possibleDuplicate.setInventoryId(1);
        
        boolean foundDuplicate = false;
        try {
        	hardwareUiService.checkSerialNumber(possibleDuplicate);
        } catch (ObjectInOtherEnergyCompanyException e) {/* Ignore */}
        catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            foundDuplicate = true;
        }
        Assert.assertTrue(foundDuplicate);
        
        possibleDuplicate.setInventoryId(0);
        foundDuplicate = false;
        
        try {
        	hardwareUiService.checkSerialNumber(possibleDuplicate);
        } catch (ObjectInOtherEnergyCompanyException e) {/* Ignore */}
        catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            foundDuplicate = true;
        }
        Assert.assertFalse(foundDuplicate);
    }
    
}