package com.cannontech.web.widget;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.model.Address;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.deploy.service.CoordType;
import com.cannontech.multispeak.deploy.service.LinkedTransformer;
import com.cannontech.multispeak.deploy.service.Nameplate;
import com.cannontech.multispeak.deploy.service.Network;
import com.cannontech.multispeak.deploy.service.NodeIdentifier;
import com.cannontech.multispeak.deploy.service.ObjectRef;
import com.cannontech.multispeak.deploy.service.PointType;
import com.cannontech.multispeak.deploy.service.UtilityInfo;
import com.cannontech.multispeak.service.MultispeakCustomerInfoService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.Lists;

@CheckRoleProperty(YukonRoleProperty.CIS_DETAIL_WIDGET_ENABLED)
public class AccountInformationWidget extends WidgetControllerBase{
    
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MeterDao meterDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private MultispeakCustomerInfoService multispeakCustomerInfoService;
    @Autowired private YukonSettingsDao yukonSettingsDao;

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("accountInformationWidget/accountInfo.jsp");
        int deviceId = WidgetParameterHelper.getIntParameter(request, "deviceId");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        int vendorId = yukonSettingsDao.getSettingIntegerValue(YukonSetting.MSP_PRIMARY_CB_VENDORID);
        if (vendorId <= 0) {
        	mav.addObject("hasVendorId", false);
        	return mav;
        }
        mav.addObject("hasVendorId", true);
        
        Meter meter = meterDao.getForId(deviceId);
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        
        com.cannontech.multispeak.deploy.service.Customer mspCustomer = mspObjectDao.getMspCustomer(meter, mspVendor);
        com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc = mspObjectDao.getMspServiceLocation(meter, mspVendor);
        com.cannontech.multispeak.deploy.service.Meter mspMeter = mspObjectDao.getMspMeter(meter, mspVendor);
        
        mav.addObject("mspCustomer", mspCustomer);
        mav.addObject("mspServLoc", mspServLoc);
        mav.addObject("mspMeter", mspMeter);
       
        // cust info detail
        List<Info> custBasicsInfo = getCustomerBasicsInfo(mspCustomer, userContext);
        Address custAddress = getCustomerAddressInfo(mspCustomer);
        mav.addObject("custBasicsInfo", custBasicsInfo);
        mav.addObject("custAddress", custAddress);
        if (mspCustomer.getContactInfo() != null) {
            List<Info> custContactInfo = getCustomerContactInfo(mspCustomer, userContext);
            if (custContactInfo != null) {
                mav.addObject("custContactInfo", custContactInfo);
            }
        }
        
        // serv loc info detail
        List<Info> servLocBasicsInfo = getServLocBasicsInfo(mspServLoc, userContext);
        Address servLocAddress = getServLocAddressInfo(mspServLoc);
        List<Info> servLocNetworkInfo = getServLocNetworkInfo(mspServLoc, userContext);
        mav.addObject("servLocBasicsInfo", servLocBasicsInfo);
        mav.addObject("servLocAddress", servLocAddress);
        mav.addObject("servLocNetworkInfo", servLocNetworkInfo);
        
        // meter info detail
        List<Info> meterBasicsInfo = getMeterBasicsInfo(mspMeter, userContext);
        List<Info> meterNameplateInfo = getMeterNameplateInfo(mspMeter, userContext);
        List<Info> meterUtilInfoInfo = getMeterUtilInfo(mspMeter, userContext);
        mav.addObject("meterBasicsInfo", meterBasicsInfo);
        mav.addObject("meterNameplateInfo", meterNameplateInfo);
        mav.addObject("meterUtilInfoInfo", meterUtilInfoInfo);
        
        return mav;
    }
    
    // CUST INFO
    private List<Info> getCustomerBasicsInfo(com.cannontech.multispeak.deploy.service.Customer mspCustomer, YukonUserContext userContext) {
        
        List<Info> infoList = new ArrayList<Info>();
        add("Last Name", mspCustomer.getLastName(), false, infoList, userContext);
        add("First Name", mspCustomer.getFirstName(), false, infoList, userContext);
        add("Middle Name", mspCustomer.getMName(), true, infoList, userContext);
        add("DBA", mspCustomer.getDBAName(), false, infoList, userContext);
        add("Home Phone", formatPhone(mspCustomer.getHomeAc(), mspCustomer.getHomePhone()), true, infoList, userContext);
        add("Day Phone", formatPhone(mspCustomer.getDayAc(), mspCustomer.getDayPhone()), true, infoList, userContext);
        add("Utility", mspCustomer.getUtility(), true, infoList, userContext);
        add("Comments", mspCustomer.getComments(), true, infoList, userContext);
        add("Error String", mspCustomer.getErrorString(), true, infoList, userContext);
        return infoList;
    }
    
    private List<Info> getCustomerContactInfo(com.cannontech.multispeak.deploy.service.Customer mspCustomer, YukonUserContext userContext) {
        
        
        List<String> phoneNumbers = multispeakCustomerInfoService.getPhoneNumbers(mspCustomer, userContext);
        boolean hasPhones = phoneNumbers.size() > 0;
        
        List<String> emailAddresses = multispeakCustomerInfoService.getEmailAddresses(mspCustomer, userContext);
        boolean hasEmails = emailAddresses.size() > 0;
        
        if (!hasPhones && !hasEmails) {
            return null;
        }
        
        List<Info> infoList = Lists.newArrayList();
        add("Phone Number", StringUtils.join(phoneNumbers, ", "), true, infoList, userContext);
        add("Email Address", StringUtils.join(emailAddresses, ", "), true, infoList, userContext);
        return infoList;
    }
    
    private Address getCustomerAddressInfo(com.cannontech.multispeak.deploy.service.Customer mspCustomer) {
        
        Address address = new Address();
        address.setLocationAddress1(mspCustomer.getBillAddr1());
        address.setLocationAddress2(mspCustomer.getBillAddr2());
        address.setCityName(mspCustomer.getBillCity());
        address.setStateCode(mspCustomer.getBillState());
        address.setZipCode(mspCustomer.getBillZip());
        
        return address;
    }
    
    // SERV LOC INFO
    private List<Info> getServLocBasicsInfo(com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc, YukonUserContext userContext) {
        
        List<Info> infoList = new ArrayList<Info>();
        add("Service Location", mspServLoc.getObjectID(), false, infoList, userContext);
        add("Customer ID", mspServLoc.getCustID(), false, infoList, userContext);
        add("Account Number", mspServLoc.getAccountNumber(), false, infoList, userContext);
        add("Facility ID", mspServLoc.getFacilityID(), false, infoList, userContext);
        add("Facility Name", mspServLoc.getFacilityName(), false, infoList, userContext);
        add("Site ID", mspServLoc.getSiteID(), false, infoList, userContext);
        add("Utility", mspServLoc.getUtility(), true, infoList, userContext);
        add("Type", mspServLoc.getServType(), false, infoList, userContext);
        add("Revenue Class", mspServLoc.getRevenueClass(), true, infoList, userContext);
        add("Status", mspServLoc.getServStatus(), true, infoList, userContext);
        add("Outage Status", mspServLoc.getOutageStatus(), true, infoList, userContext);
        add("Billing Cycle", mspServLoc.getBillingCycle(), true, infoList, userContext);
        add("Route", mspServLoc.getRoute(), true, infoList, userContext);
        add("Special Needs", mspServLoc.getSpecialNeeds(), true, infoList, userContext);
        add("Load Mgmt", mspServLoc.getLoadMgmt(), true, infoList, userContext);
        add("Budget Bill Code", mspServLoc.getBudgBill(), true, infoList, userContext);
        add("Total accounts receivable balance.", mspServLoc.getAcRecvBal(), true, infoList, userContext);
        add("Current accounts receivable balance", mspServLoc.getAcRecvCur(), true, infoList, userContext);
        add("30-day accounts receivable balance", mspServLoc.getAcRecv30(), true, infoList, userContext);
        add("60-day accounts receivable balance", mspServLoc.getAcRecv60(), true, infoList, userContext);
        add("90-day accounts receivable balance", mspServLoc.getAcRecv90(), true, infoList, userContext);
        add("Payment Due Date", mspServLoc.getPaymentDueDate(), true, infoList, userContext);
        add("Last Payment Date", mspServLoc.getLastPaymentDate(), true, infoList, userContext);
        add("Last Payment Amount", mspServLoc.getLastPaymentAmount(), true, infoList, userContext);
        add("Bill Date", mspServLoc.getBillDate(), true, infoList, userContext);
        add("Shut Off Date", mspServLoc.getShutOffDate(), true, infoList, userContext);
        add("Connection", mspServLoc.getConnection(), true, infoList, userContext);
        add("Connect Date", mspServLoc.getConnectDate(), true, infoList, userContext);
        add("Disconnect Date", mspServLoc.getDisconnectDate(), true, infoList, userContext);
        add("SIC", mspServLoc.getSIC(), true, infoList, userContext);
        add("Is Cogeneration Site", mspServLoc.getIsCogenerationSite(), true, infoList, userContext);
        add("Work order number", mspServLoc.getWoNumber(), true, infoList, userContext);
        add("Service order number", mspServLoc.getSoNumber(), true, infoList, userContext);
        add("Phasing code", mspServLoc.getPhaseCode(), true, infoList, userContext);
        add("Map Location", mspServLoc.getMapLocation(), false, infoList, userContext);
        add("Grid Location", mspServLoc.getGridLocation(), false, infoList, userContext);
        add("Rotation", mspServLoc.getRotation(), true, infoList, userContext);
        add("From Node ID", mspServLoc.getFromNodeID(), true, infoList, userContext);
        add("To Node ID", mspServLoc.getToNodeID(), true, infoList, userContext);
        add("Section ID", mspServLoc.getSectionID(), true, infoList, userContext);
        add("Parent Section ID", mspServLoc.getParentSectionID(), true, infoList, userContext);
        add("Comments", mspServLoc.getComments(), true, infoList, userContext);
        add("Error String", mspServLoc.getErrorString(), true, infoList, userContext);
        
        return infoList;
    }
    
    private Address getServLocAddressInfo(com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc) {
        
        Address address = new Address();
        address.setLocationAddress1(mspServLoc.getServAddr1());
        address.setLocationAddress2(mspServLoc.getServAddr2());
        address.setCityName(mspServLoc.getServCity());
        address.setStateCode(mspServLoc.getServState());
        address.setZipCode(mspServLoc.getServZip());
        
        return address;
    }   
    
    private List<Info> getServLocNetworkInfo(com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc, YukonUserContext userContext) {
        
        List<Info> infoList = new ArrayList<Info>();
        Network n = mspServLoc.getNetwork();
        if (n != null) {
            add("Board District", n.getBoardDist(), true, infoList, userContext);
            add("Tax District", n.getTaxDist(), true, infoList, userContext);
            add("Francise District", n.getFranchiseDist(), true, infoList, userContext);
            add("School District", n.getSchoolDist(), true, infoList, userContext);
            add("District", n.getDistrict(), true, infoList, userContext);
            add("County", n.getCounty(), true, infoList, userContext);
            add("City Code", n.getCityCode(), true, infoList, userContext);
            add("Substation Code", n.getSubstationCode(), true, infoList, userContext);
            add("Feeder", n.getFeeder(), true, infoList, userContext);
            add("Phasing code", n.getPhaseCd(), true, infoList, userContext);
            add("Engineering analysis location", n.getEaLoc(), true, infoList, userContext);
            add("Pole Number", n.getPoleNo(), true, infoList, userContext);
            add("Section", n.getSection(), true, infoList, userContext);
            add("Township", n.getTownship(), true, infoList, userContext);
            add("Range", n.getRange(), true, infoList, userContext);
            add("Subdivision", n.getSubdivision(), true, infoList, userContext);
            add("Block", n.getBlock(), true, infoList, userContext);
            add("Lot", n.getLot(), true, infoList, userContext);
            add("Linked Transformer", n.getLinkedTransformer(), true, infoList, userContext);
            add("Lineman Service Area", n.getLinemanServiceArea(), true, infoList, userContext);
        }
        return infoList;
    }   
    
    // METER INFO
    private List<Info> getMeterBasicsInfo(com.cannontech.multispeak.deploy.service.Meter mspMeter, YukonUserContext userContext) {
        
        List<Info> infoList = new ArrayList<Info>();
        add("Meter Number", mspMeter.getMeterNo(), false, infoList, userContext);
        add("Meter Type", mspMeter.getMeterType(), false, infoList, userContext);
        add("Seal Numbers", mspMeter.getSealNumberList(), true, infoList, userContext);
        add("AMR Type", mspMeter.getAMRType(), false, infoList, userContext);
        add("AMR Device Type", mspMeter.getAMRDeviceType(), false, infoList, userContext);
        add("AMR Vendor", mspMeter.getAMRVendor(), false, infoList, userContext);
        add("In Service Date", mspMeter.getInServiceDate(), false, infoList, userContext);
        add("Out Service Date", mspMeter.getOutServiceDate(), false, infoList, userContext);
        add("Serial Number", mspMeter.getSerialNumber(), true, infoList, userContext);
        add("Device Class", mspMeter.getDeviceClass(), true, infoList, userContext);
        add("Manufacturer", mspMeter.getManufacturer(), true, infoList, userContext);
        add("Facility ID", mspMeter.getFacilityID(), true, infoList, userContext);
        add("Utility", mspMeter.getUtility(), true, infoList, userContext);
        add("Comments", mspMeter.getComments(), true, infoList, userContext);
        add("Error String", mspMeter.getErrorString(), true, infoList, userContext);
        return infoList;
    }
    
    private List<Info> getMeterNameplateInfo(com.cannontech.multispeak.deploy.service.Meter mspMeter, YukonUserContext userContext) {
        
        List<Info> infoList = new ArrayList<Info>();
        Nameplate np = mspMeter.getNameplate();
        if (np != null) {
            
            add("Meter kh (watthour) constant", np.getKh(), true, infoList, userContext);
            add("Watthour meter register constant", np.getKr(), true, infoList, userContext);
            add("Frequency", np.getFrequency(), true, infoList, userContext);
            add("Number Of Element", np.getNumberOfElements(), true, infoList, userContext);
            add("Base Type", np.getBaseType(), true, infoList, userContext);
            add("Accuracy Class", np.getAccuracyClass(), true, infoList, userContext);
            add("Element Voltage", np.getElementsVoltage(), true, infoList, userContext);
            add("Supply Voltage", np.getSupplyVoltage(), true, infoList, userContext);
            add("Max Amperage", np.getMaxAmperage(), true, infoList, userContext);
            add("Test Amperage", np.getTestAmperage(), true, infoList, userContext);
            add("Reg Ratio", np.getRegRatio(), true, infoList, userContext);
            add("Phases", np.getPhases(), true, infoList, userContext);
            add("Wires", np.getWires(), true, infoList, userContext);
            add("Dials", np.getDials(), true, infoList, userContext);
            add("Form", np.getForm(), true, infoList, userContext);
            add("Multiplier", np.getMultiplier(), false, infoList, userContext);
            add("Demand Multiplier", np.getDemandMult(), false, infoList, userContext);
            add("Transponder ID", np.getTransponderID(), false, infoList, userContext);
        }
        return infoList;
    }
    
    private List<Info> getMeterUtilInfo(com.cannontech.multispeak.deploy.service.Meter mspMeter, YukonUserContext userContext) {
        
        List<Info> infoList = new ArrayList<Info>();
        UtilityInfo u = mspMeter.getUtilityInfo();
        if (u != null) {
            
            add("Owner", u.getOwner(), true, infoList, userContext);
            add("District", u.getDistrict(), true, infoList, userContext);
            add("Service Location", u.getServLoc(), false, infoList, userContext);
            add("Account Number", u.getAccountNumber(), false, infoList, userContext);
            add("Customer ID", u.getCustID(), false, infoList, userContext);
            add("Substation Code", u.getSubstationCode(), false, infoList, userContext);
            add("Substation Name", u.getSubstationName(), false, infoList, userContext);
            add("Feeder", u.getFeeder(), true, infoList, userContext);
            add("Bus", u.getBus(), true, infoList, userContext);
            add("Phasing code", u.getPhaseCd(), true, infoList, userContext);
            add("Engineering analysis location", u.getEaLoc(), true, infoList, userContext);
            add("Transformer Bank ID", u.getTransformerBankID(), true, infoList, userContext);
            add("Meter Base ID", u.getMeterBaseID(), true, infoList, userContext);
            add("Map Location", u.getMapLocation(), true, infoList, userContext);
        }
        
        return infoList;
    }
    
    private String makeMapLocation(CoordType coordType) {
        
        String xyz = "";
        xyz += "X=" + coordType.getX();
        xyz += " Y=" + coordType.getY();
        xyz += " Z=" + coordType.getZ();
        xyz += " Bulge=" + coordType.getBulge();
        return xyz;
    }
    
    private String formatPhone(String areaCode, String phone) {
        String formattedPhone = "";
        if (!StringUtils.isBlank(areaCode)) {
            formattedPhone += "(" + areaCode + ") ";
        }
        if (!StringUtils.isBlank(phone)) {
            formattedPhone += phone;
        }
        return formattedPhone;
    }
    
    private void add(String label, Object value, Boolean hideIfBlank, List<Info> list, YukonUserContext userContext) {
        
        Info info = new Info(label, value, userContext);
        if (!(info.isBlank() && hideIfBlank)) {
            list.add(info);
        }
    }
    
    public class Info {
        
        private String label = "";
        private String value = "";
        
        public Info(String label, Object value, YukonUserContext userContext) {
            
            this.label = label;
            if (value != null) {
                
                if (value instanceof Integer || value instanceof Long) {
                    this.value = value.toString();
                } else if (value instanceof Number) {
                    DecimalFormat formatter = new DecimalFormat("#.###");
                    this.value = formatter.format(value);
                } else if (value instanceof Date) {
                    this.value = formatDate((Date)value, userContext);
                } else if (value instanceof Calendar) {
                    this.value = formatDate(((Calendar)value).getTime(), userContext);
                } else if (value instanceof ObjectRef) {
                    this.value = ((ObjectRef)value).getName();
                } else if (value instanceof NodeIdentifier) {
                    this.value = ((NodeIdentifier)value).getName();
                } else if (value instanceof LinkedTransformer) {
                    
                    LinkedTransformer linkedTransformer = (LinkedTransformer)value;
                    String linkedTransformerStr = "";
                    if (linkedTransformer.getBankID() != null) {
                        linkedTransformerStr += "Bank ID: " + linkedTransformer.getBankID();
                    }
                    if (linkedTransformer.getUnitList() != null && linkedTransformer.getUnitList().length > 0) {
                        linkedTransformerStr += "Unit List: " + StringUtils.join(linkedTransformer.getUnitList(), ", ");
                    }
                    this.value = linkedTransformerStr;
                } else if (value instanceof PointType) {
                    
                    PointType pointType = (PointType)value;
                    if (pointType.getCoord() != null) {
                        this.value = makeMapLocation(pointType.getCoord());
                    }
                } else if (value instanceof String[]) {
                    this.value = StringUtils.join((String[])value, ", ");
                } else {
                    this.value = value.toString();
                }
            }
        }
        
        private String formatDate(Date date, YukonUserContext userContext) {
            return dateFormattingService.format(date, DateFormattingService.DateFormatEnum.DATE, userContext);
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean isBlank() {
            return StringUtils.isBlank(this.value);
        }
    }

}
