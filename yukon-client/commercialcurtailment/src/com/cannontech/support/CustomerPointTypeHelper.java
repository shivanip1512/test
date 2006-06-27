package com.cannontech.support;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.CICustomerPointData;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.VirtualDevice;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.database.db.point.Point;

public class CustomerPointTypeHelper {
    private CustomerPointTypeLookup pointTypeLookup;
    private String customerDevicePrefix = "";
    private String customerDeviceSuffix = " Point Data";
    private CustomerStubDao customerStubDao;
    private PointDao pointDao;
    private DeviceDao deviceDao;
    private PaoDao paoDao;
    private DBPersistentDao dbPersistentDao;

    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public CustomerPointTypeHelper() {
        super();
    }
    
    /**
     * @param customer
     * @param type
     * @return
     * @throws NoPointException
     */
    public LitePoint getPoint(CICustomerStub customer, CICustomerPointType type) throws NoPointException {
        Validate.notNull(customer);
        CICustomerPointData data = customer.getPointData().get(type);
        if (data == null) {
            throw new NoPointException(customer.getId(), type);
        }
        LitePoint litePoint = pointDao.getLitePoint(data.getPointId());
        return litePoint;
    }
    
    public boolean doRequiredPointsExist(CICustomerStub customer) {
        LiteCICustomer liteCICustomer = customer.getLite();
        Collection<CICustomerPointType> applicablePoints = pointTypeLookup.getApplicablePoints(liteCICustomer);
        return customer.getPointData().keySet().containsAll(applicablePoints);
    }
    
    public boolean isPointGroupSatisfied(CICustomerStub customer, String pointGroup) {
        Collection<CICustomerPointType> applicablePoints = pointTypeLookup.getApplicablePoints(pointGroup);
        
        return customer.getPointData().keySet().containsAll(applicablePoints);
    }
    
    public Set<String> getSatisfiedPointGroups(CICustomerStub customer) {
        Set<String> result = new TreeSet<String>();
        Set<CICustomerPointType> currentPoints = customer.getPointData().keySet();
        Collection<String> typeGroups = pointTypeLookup.getPointTypeGroups(customer.getLite());
        for (String typeGroup : typeGroups) {
            if (isPointGroupSatisfied(customer, typeGroup)) {
                result.add(typeGroup);
            }
        }
        return result;
    }
    
    public Set<String> getSatisfiedPointGroups(Collection<CICustomerStub> customers) {
        Set<String> result = null;
        for (CICustomerStub customer : customers) {
            if (result == null) {
                result = getSatisfiedPointGroups(customer);
            } else {
                Set<String> temp = getSatisfiedPointGroups(customer);
                result.retainAll(temp);
            }
        }
        return result;
    }
    
    /**
     * @param customer
     * @param type
     */
    public void createPoint(CICustomerStub customer, CICustomerPointType type) {
        LiteYukonPAObject customerDevice = getCustomerDevice(customer);
        String pointName = customer.getCompanyName() + "-" + type;
        int pointId = 0;
        boolean found = false;
        // see if point already exists
        LitePoint[] litePointsForPAObject = 
            paoDao.getLitePointsForPAObject(customerDevice.getLiteID());
        for (int i = 0; i < litePointsForPAObject.length; i++) {
            LitePoint point = litePointsForPAObject[i];
            if (point.getPointName().equals(pointName)) {
                pointId = point.getPointID();
                found = true;
                break;
            }
        }
        if (!found) {
            pointId = Point.getNextPointID();
            PointBase point = PointFactory.createAnalogPoint(pointName, 
                                                             customerDevice.getYukonID(), 
                                                             pointId, 
                                                             0, 
                                                             PointUnits.UOMID_UNDEF);
            point.getPoint().setArchiveType(PointTypes.ARCHIVE_ON_TIMER_OR_UPDATE);
            point.getPoint().setArchiveInterval(7*24*60*60); // 1 week as seconds
            dbPersistentDao.performDBChange(point, Transaction.INSERT);
        }
        CICustomerPointData customerPoint = new CICustomerPointData();
        customerPoint.setType(type);
        customerPoint.setOptionalLabel(type.getLabel());
        customerPoint.setPointId(pointId);
        customer.addPoint(customerPoint);
        
        customerStubDao.save(customer);
    }
    
    protected LiteYukonPAObject getCustomerDevice(CICustomerStub customer) {
        final String generatedDeviceCategory = PAOGroups.STRING_CAT_DEVICE;
        final String generatedDeviceClass = DeviceClasses.STRING_CLASS_VIRTUAL;
        final String generatedDeviceType = PAOGroups.getPAOTypeString(DeviceTypes.VIRTUAL_SYSTEM);
        String deviceName = customerDevicePrefix + customer.getCompanyName() + customerDeviceSuffix;
        
        // returns null if not found
        //TODO create new search function that looks at category, class, type, and name
        LiteYukonPAObject yukonPAObject = deviceDao.getLiteYukonPAObject(deviceName,
                                                                           generatedDeviceCategory, 
                                                                           generatedDeviceClass,
                                                                           generatedDeviceType);
        
        if (yukonPAObject == null) {
            VirtualDevice device;
            // create device
            device = new VirtualDevice();
            device.setPAOName(deviceName);
            device.setPAOCategory(generatedDeviceCategory);
            device.setPAOClass(generatedDeviceClass);
            device.setDeviceType(generatedDeviceType);
            
            dbPersistentDao.performDBChange(device, Transaction.INSERT);
            yukonPAObject = (LiteYukonPAObject) LiteFactory.createLite(device);
        }
        
        return yukonPAObject;
    }
    
    public void setPointTypeLookup(CustomerPointTypeLookup pointTypeLookup) {
        this.pointTypeLookup = pointTypeLookup;
    }

    public void setCustomerDao(CustomerStubDao customerDao) {
        this.customerStubDao = customerDao;
    }

    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }


    

}
