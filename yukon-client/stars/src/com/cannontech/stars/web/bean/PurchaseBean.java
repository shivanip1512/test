package com.cannontech.stars.web.bean;

import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.database.db.stars.purchasing.DeliverySchedule;
import com.cannontech.database.db.stars.purchasing.Invoice;
import com.cannontech.database.db.stars.purchasing.PurchasePlan;
import com.cannontech.database.db.stars.purchasing.ScheduleTimePeriod;
import com.cannontech.database.db.stars.purchasing.Shipment;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;


public class PurchaseBean 
{
    private LiteStarsEnergyCompany energyCompany = null;
    private LiteYukonUser currentUser = null;
    private List<LiteStarsEnergyCompany> availableMembers;
    private YukonSelectionList availableDeviceTypes;
    private List<Warehouse> availableWarehouses;
    
    private List<PurchasePlan> availablePlans;
    private int numberOfPlans;
    private PurchasePlan currentPlan;
    
    private List<DeliverySchedule> availableSchedules;
    private DeliverySchedule currentSchedule;
    private String currentQuotedPricePerUnit;
    
    private List<ScheduleTimePeriod> availableTimePeriods;
    private ScheduleTimePeriod currentTimePeriod;
    
    private List<Shipment> availableShipments;
    private Shipment currentShipment;
    private String currentActualPricePerUnit;
    private String currentSalesTax;
    private String currentShippingCharges;
    private String currentOtherCharges;
    private String currentTotal;
    private String currentAmountPaid;
    private boolean currentSerialNumberError;
    private LiteStarsEnergyCompany serialNumberMember; 
    private Warehouse serialNumberWarehouse;
    private YukonListEntry serialNumberDeviceState;
    private YukonListEntry serialNumberDeviceType;
    
    private List<Invoice> availableInvoices;
    private List<Shipment> assignedInvoiceShipments;
    private List<Shipment> allUnassignedInvoiceShipments;
    private Invoice currentInvoice;
    private boolean isCurrentlyAuthorized;
    private boolean hasCurrentlyPaid;
    
    public LiteStarsEnergyCompany getEnergyCompany()
    {
        return energyCompany;
    }
    
    public void setEnergyCompany(LiteStarsEnergyCompany company)
    {
        energyCompany = company;
    }
    
    public LiteYukonUser getCurrentUser()
    {
        return currentUser;
    }
    
    public void setCurrentUser(LiteYukonUser user)
    {
        currentUser = user;
    }
    
    public boolean getManageMembers()
    {
        return DaoFactory.getAuthDao().checkRoleProperty(currentUser, AdministratorRole.ADMIN_MANAGE_MEMBERS) && (energyCompany.hasChildEnergyCompanies());
    }
    
    public List<LiteStarsEnergyCompany> getAvailableMembers()
    {
        if(availableMembers == null)
            availableMembers = ECUtils.getAllDescendants(energyCompany);
        return availableMembers;
    }
     
    public YukonSelectionList getAvailableDeviceTypes()
    {
        if(availableDeviceTypes == null)
            availableDeviceTypes = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, true, true);
        return availableDeviceTypes;
    }

    public List<PurchasePlan> getAvailablePlans() 
    {
        if(getManageMembers())
            availablePlans = PurchasePlan.getAllPurchasePlansForAllMembers(energyCompany.getEnergyCompanyId(), getAvailableMembers());
        else
            availablePlans = PurchasePlan.getAllPurchasePlans(energyCompany.getEnergyCompanyId());
        
        return availablePlans;
    }

    public void setAvailablePlans(List<PurchasePlan> availablePlans) 
    {
        this.availablePlans = availablePlans;
    }

    public PurchasePlan getCurrentPlan() 
    {
        /**
         * Usually the most recently created plan in the db.
         */
        if(currentPlan == null)
        {
            if(getAvailablePlans().size() > 0)
                currentPlan = availablePlans.get(0);
            else
                currentPlan = new PurchasePlan();
        }
        return currentPlan;
    }

    public void setCurrentPlan(PurchasePlan currentPlan) 
    {
        this.currentPlan = currentPlan;
    }

    public DeliverySchedule getCurrentSchedule() {
        return currentSchedule;
    }

    public void setCurrentSchedule(DeliverySchedule currentSchedule) {
        this.currentSchedule = currentSchedule;
    }

    public int getNumberOfPlans() {
        numberOfPlans = availablePlans.size();
        return numberOfPlans;
    }

    public List<DeliverySchedule> getAvailableSchedules() 
    {
        availableSchedules = DeliverySchedule.getAllDeliverySchedulesForAPlan(currentPlan.getPurchaseID());
        return availableSchedules;
    }

    public void setAvailableSchedules(List<DeliverySchedule> availableSchedules) {
        this.availableSchedules = availableSchedules;
    }

    public List<ScheduleTimePeriod> getAvailableTimePeriods() 
    {
        availableTimePeriods = ScheduleTimePeriod.getAllTimePeriodsForDeliverySchedule(currentSchedule.getScheduleID());
        return availableTimePeriods;
    }

    public void setAvailableTimePeriods(List<ScheduleTimePeriod> availableTimePeriods) 
    {
        this.availableTimePeriods = availableTimePeriods;
    }

    public ScheduleTimePeriod getCurrentTimePeriod() 
    {
        return currentTimePeriod;
    }

    public void setCurrentTimePeriod(ScheduleTimePeriod currentTimePeriod) 
    {
        this.currentTimePeriod = currentTimePeriod;
    }

    public List<Shipment> getAvailableShipments() {
        availableShipments = Shipment.getAllShipmentsForDeliverySchedule(currentSchedule.getScheduleID());
        return availableShipments;
    }

    public void setAvailableShipments(List<Shipment> availableShipments) {
        this.availableShipments = availableShipments;
    }

    public Shipment getCurrentShipment() {
        return currentShipment;
    }

    public void setCurrentShipment(Shipment currentShipment) {
        this.currentShipment = currentShipment;
    }

    public String getCurrentQuotedPricePerUnit() {
        currentQuotedPricePerUnit = String.valueOf(currentSchedule.getQuotedPricePerUnit());
        return currentQuotedPricePerUnit;
    }
    
    public List<Warehouse> getAvailableWarehouses()
    {
        availableWarehouses = energyCompany.getAllWarehousesDownward();
        return availableWarehouses;
    }
    
    public String getCurrentActualPricePerUnit() {
        currentActualPricePerUnit = String.valueOf(currentShipment.getActualPricePerUnit());
        return currentActualPricePerUnit;
    }
    
    public String getCurrentSalesTax() {
        currentSalesTax = String.valueOf(currentShipment.getSalesTax());
        return currentSalesTax;
    }
    
    public String getCurrentShippingCharges() {
        currentShippingCharges = String.valueOf(currentShipment.getShippingCharges());
        return currentShippingCharges;
    }
    
    public String getCurrentOtherCharges() {
        currentOtherCharges = String.valueOf(currentShipment.getOtherCharges());
        return currentOtherCharges;
    }
    
    public String getCurrentTotal() {
        currentTotal = String.valueOf(currentShipment.getSalesTotal());
        return currentTotal;
    }
    
    public String getCurrentAmountPaid() {
        currentAmountPaid = String.valueOf(currentShipment.getAmountPaid());
        return currentAmountPaid;
    }

    public Invoice getCurrentInvoice() {
        return currentInvoice;
    }

    public void setCurrentInvoice(Invoice currentInvoice) {
        this.currentInvoice = currentInvoice;
    }

    public List<Invoice> getAvailableInvoices() {
        availableInvoices = Invoice.getAllInvoicesForPurchasePlan(currentPlan.getPurchaseID());
        return availableInvoices;
    }

    public boolean getIsCurrentlyAuthorized() {
        isCurrentlyAuthorized = currentInvoice.getAuthorized().compareTo("Y") == 0;
        return isCurrentlyAuthorized;
    }

    public boolean getHasCurrentlyPaid() {
        hasCurrentlyPaid = currentInvoice.getHasPaid().compareTo("Y") == 0;
        return hasCurrentlyPaid;
    }

    public boolean isAllowSerialNumberInput() 
    {
        return (currentShipment.getShipmentID() == null || currentSerialNumberError) ? true : false;
    }

    public boolean isCurrentSerialNumberError() {
        return currentSerialNumberError;
    }

    public void setCurrentSerialNumberError(boolean currentSerialNumberError) {
        this.currentSerialNumberError = currentSerialNumberError;
    }

    public LiteStarsEnergyCompany getSerialNumberMember() {
        if(currentShipment.getWarehouseID().intValue() <= 0)
        {
            serialNumberMember = energyCompany;
            return serialNumberMember;
        }
        
        Integer ecID = Warehouse.getEnergyCompanyIDFromWarehouseID(currentShipment.getWarehouseID());
        
        if(ecID.compareTo(energyCompany.getEnergyCompanyId()) == 0)
            serialNumberMember = energyCompany;
        
        for(int i = 0; i < getAvailableMembers().size(); i++)
        {
            if(getAvailableMembers().get(i).getEnergyCompanyID().compareTo(ecID) == 0)
                serialNumberMember = getAvailableMembers().get(i);
        }
        
        return serialNumberMember;
    }

    public Warehouse getSerialNumberWarehouse() {
        
        for(int i = 0; i < getAvailableWarehouses().size(); i++)
        {
            if(getAvailableWarehouses().get(i).getWarehouseID().compareTo(currentShipment.getWarehouseID()) == 0)
                serialNumberWarehouse = getAvailableWarehouses().get(i);
        }
        
        return serialNumberWarehouse;
    }

    public YukonListEntry getSerialNumberDeviceState() {
        
        List<YukonListEntry> deviceStates = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, true, true).getYukonListEntries();
        for(int i = 0; i < deviceStates.size(); i++)
        {
            if(((YukonListEntry)deviceStates.get(i)).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_ORDERED)
                serialNumberDeviceState = ((YukonListEntry)deviceStates.get(i));
        }
        return serialNumberDeviceState;
    }
    
    public YukonListEntry getSerialNumberDeviceType() {
        
        List<YukonListEntry> deviceTypes = getAvailableDeviceTypes().getYukonListEntries();
        for(int i = 0; i < deviceTypes.size(); i++)
        {
            if(((YukonListEntry)deviceTypes.get(i)).getEntryID() == currentSchedule.getModelID())
                serialNumberDeviceType = ((YukonListEntry)deviceTypes.get(i));
        }
        return serialNumberDeviceType;
    }
    
    public List<Shipment> getAssignedInvoiceShipments() {
        assignedInvoiceShipments = Shipment.getAllShipmentsForInvoice(currentInvoice.getInvoiceID());
        return assignedInvoiceShipments;
    }

    public List<Shipment> getAllUnassignedInvoiceShipments() {
        allUnassignedInvoiceShipments = Shipment.getAllUnassignedShipmentsForInvoiceUse(currentPlan.getPurchaseID());
        return allUnassignedInvoiceShipments;
    }
}
