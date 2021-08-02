package stars.dr.hardware;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.impl.InventoryBaseDaoImpl;
import com.cannontech.stars.core.dao.impl.StarsSearchDaoImpl;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.service.impl.HardwareUiServiceImpl;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class HardwareServiceTest {

    private HardwareUiServiceImpl hardwareUiService;
    
    private InventoryBaseDaoImpl inventoryBaseDao;
    private StarsSearchDao starsSearchDao;
    
    @BeforeEach
    public void setUp() {
        
    	hardwareUiService = new HardwareUiServiceImpl() {
           @Override
           public Hardware getHardware(int inventoryId) {
               Hardware dto = new Hardware();
               dto.setInventoryId(inventoryId);
               dto.setEnergyCompanyId(0);
               dto.setHardwareType(HardwareType.values()[inventoryId]);
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
            public LiteLmHardwareBase searchLmHardwareBySerialNumber(String serialNumber, int energyCompanyId) {
                LiteLmHardwareBase liteBase = new LiteLmHardwareBase();
                liteBase.setInventoryID(0);
                return liteBase;
            }
         };
         
         hardwareUiService.setInventoryBaseDao(inventoryBaseDao);
         hardwareUiService.setStarsSearchDao(starsSearchDao);
    }
    
    @Test
    public void testGetHardwareMapForAccount() {
        
        ListMultimap<HardwareClass, Hardware> map = hardwareUiService.getHardwareMapForAccount(0);
        assertNotNull(map);
        assertTrue(!map.isEmpty(), "Map should not be empty");
        
        List<Hardware> switches = map.get(HardwareClass.SWITCH);
        List<Hardware> thermostats = map.get(HardwareClass.THERMOSTAT);
        List<Hardware> meters = map.get(HardwareClass.METER);
        
        assertEquals(switches.size(), HardwareType.getForClass(HardwareClass.SWITCH).size());
        assertEquals(thermostats.size(), HardwareType.getForClass(HardwareClass.THERMOSTAT).size());
        assertEquals(meters.size(), HardwareType.getForClass(HardwareClass.METER).size());
        
    }
    
    @Test
    public void testCheckSerialNumber() {
        Hardware possibleDuplicate = new Hardware();
        possibleDuplicate.setInventoryId(1);
        
        boolean foundDuplicate = false;
        try {
        	hardwareUiService.checkSerialNumber(possibleDuplicate);
        } catch (ObjectInOtherEnergyCompanyException e) {/* Ignore */}
        catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            foundDuplicate = true;
        }
        assertTrue(foundDuplicate);
        
        possibleDuplicate.setInventoryId(0);
        foundDuplicate = false;
        
        try {
        	hardwareUiService.checkSerialNumber(possibleDuplicate);
        } catch (ObjectInOtherEnergyCompanyException e) {/* Ignore */}
        catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            foundDuplicate = true;
        }
        assertFalse(foundDuplicate);
    }
    
}