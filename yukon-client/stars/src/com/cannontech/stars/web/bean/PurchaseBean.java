package com.cannontech.stars.web.bean;

import java.text.SimpleDateFormat;
import java.util.*;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.stars.report.ServiceCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.database.db.stars.purchasing.*;
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
    private String currentPredictedShipDate;
    
    private List<Shipment> availableShipments;
    private Shipment currentShipment;
    private String currentShipDate;
    private String currentOrderingDate;
    private String currentReceivingDate;
    private String currentActualPricePerUnit;
    private String currentSalesTax;
    private String currentShippingCharges;
    private String currentOtherCharges;
    private String currentTotal;
    private String currentAmountPaid;
    
    private List<Invoice> availableInvoices;
    private Invoice currentInvoice;
    
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
        return AuthFuncs.checkRoleProperty(currentUser, AdministratorRole.ADMIN_MANAGE_MEMBERS) && (energyCompany.getChildren().size() > 0);
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
            availablePlans = PurchasePlan.getAllPurchasePlansForAllMembers(energyCompany.getEnergyCompanyID(), getAvailableMembers());
        else
            availablePlans = PurchasePlan.getAllPurchasePlans(energyCompany.getEnergyCompanyID());
        
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

    public String getCurrentPredictedShipDate() 
    {
        SimpleDateFormat datePart = new SimpleDateFormat("MM/dd/yyyy");
        currentPredictedShipDate = datePart.format(currentTimePeriod.getPredictedShipDate());
        return currentPredictedShipDate;
    }

    public void setCurrentPredictedShipDate(String currentPredictedShipDate) {
        this.currentPredictedShipDate = currentPredictedShipDate;
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
    
    public String getCurrentShipDate() 
    {
        SimpleDateFormat datePart = new SimpleDateFormat("MM/dd/yyyy");
        currentShipDate = datePart.format(currentShipment.getShipDate());
        return currentShipDate;
    }

    public List<Warehouse> getAvailableWarehouses()
    {
        if(availableWarehouses == null)
            availableWarehouses = energyCompany.getAllWarehousesDownward();
        return availableWarehouses;
    }
    
    public String getCurrentOrderingDate() 
    {
        SimpleDateFormat datePart = new SimpleDateFormat("MM/dd/yyyy");
        currentOrderingDate = datePart.format(currentShipment.getOrderedDate());
        return currentOrderingDate;
    }
    
    public String getCurrentReceivingDate() 
    {
        SimpleDateFormat datePart = new SimpleDateFormat("MM/dd/yyyy");
        currentReceivingDate = datePart.format(currentShipment.getReceivedDate());
        return currentReceivingDate;
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
    
    
}
