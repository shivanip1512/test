package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.deploy.service.ContactInfo;
import com.cannontech.multispeak.deploy.service.CoordType;
import com.cannontech.multispeak.deploy.service.EMailAddress;
import com.cannontech.multispeak.deploy.service.Nameplate;
import com.cannontech.multispeak.deploy.service.Network;
import com.cannontech.multispeak.deploy.service.PhoneNumber;
import com.cannontech.multispeak.deploy.service.UtilityInfo;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class AccountInformationWidget extends WidgetControllerBase{
    
    private MspObjectDao mspObjectDao;
    public MultispeakFuncs multispeakFuncs;
    public MultispeakDao multispeakDao;
    public MeterDao meterDao;
    
    
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("accountInformationWidget/accountInfo.jsp");
        int deviceId = WidgetParameterHelper.getIntParameter(request, "deviceId");
        
        Meter meter = meterDao.getForId(deviceId);
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        
        com.cannontech.multispeak.deploy.service.Customer mspCustomer = mspObjectDao.getMspCustomer(meter, mspVendor);
        com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc = mspObjectDao.getMspServiceLocation(meter, mspVendor);
        com.cannontech.multispeak.deploy.service.Meter mspMeter = mspObjectDao.getMspMeter(meter, mspVendor);
        
        mav.addObject("mspCustomer", mspCustomer);
        mav.addObject("mspServLoc", mspServLoc);
        mav.addObject("mspMeter", mspMeter);
       
        // cust info detail
        List<Info> custBasicsInfo = getCustomerBasicsInfo(mspCustomer);
        Map<String, String> custAddressInfo = getCustomerAddressInfo(mspCustomer);
        mav.addObject("custBasicsInfo", custBasicsInfo);
        mav.addObject("custAddressInfo", custAddressInfo);
        if (mspCustomer.getContactInfo() != null) {
            List<Info> custContactInfo = getCustomerContactInfo(mspCustomer);
            if (custContactInfo != null) {
                mav.addObject("custContactInfo", custContactInfo);
            }
        }
        
        // serv loc info detail
        List<Info> servLocBasicsInfo = getServLocBasicsInfo(mspServLoc);
        Map<String, String> servLocAddressInfo = getServLocAddressInfo(mspServLoc);
        List<Info> servLocNetworkInfo = getServLocNetworkInfo(mspServLoc);
        mav.addObject("servLocBasicsInfo", servLocBasicsInfo);
        mav.addObject("servLocAddressInfo", servLocAddressInfo);
        mav.addObject("servLocNetworkInfo", servLocNetworkInfo);
        
        // meter info detail
        List<Info> meterBasicsInfo = getMeterBasicsInfo(mspMeter);
        List<Info> meterNameplateInfo = getMeterNameplateInfo(mspMeter);
        List<Info> meterUtilInfoInfo = getMeterUtilInfo(mspMeter);
        mav.addObject("meterBasicsInfo", meterBasicsInfo);
        mav.addObject("meterNameplateInfo", meterNameplateInfo);
        mav.addObject("meterUtilInfoInfo", meterUtilInfoInfo);
        
        return mav;
    }
    
    
    
    
    
    
    
    
    
    
    
    // CUST INFO
    private List<Info> getCustomerBasicsInfo(com.cannontech.multispeak.deploy.service.Customer mspCustomer) {
        
        List<Info> infoList = new ArrayList<Info>();
        add("Last Name", mspCustomer.getLastName(), false, infoList);
        add("First Name", mspCustomer.getFirstName(), false, infoList);
        add("Middle Name", mspCustomer.getMName(), true, infoList);
        add("DBA", mspCustomer.getDBAName(), false, infoList);
        add("Home Phone", formatPhone(mspCustomer.getHomeAc(), mspCustomer.getHomePhone()), true, infoList);
        add("Day Phone", formatPhone(mspCustomer.getDayAc(), mspCustomer.getDayPhone()), true, infoList);
        add("Utility", mspCustomer.getUtility(), true, infoList);
        add("Comments", mspCustomer.getComments(), true, infoList);
        add("Error String", mspCustomer.getErrorString(), true, infoList);
        return infoList;
    }
    
    private List<Info> getCustomerContactInfo(com.cannontech.multispeak.deploy.service.Customer mspCustomer) {
        
        List<Info> infoList = new ArrayList<Info>();
        ContactInfo contactInfo = mspCustomer.getContactInfo();
        
        boolean hasPhones = false;
        List<String> phones = new ArrayList<String>();
        if (contactInfo.getPhoneList() != null) {
            for (PhoneNumber p : contactInfo.getPhoneList()) {
                String fullPhone = "";
                if (p.getPhone() != null) {
                    fullPhone += p.getPhone();
                }
                if (p.getPhoneType() != null && p.getPhoneType().getValue() != null) {
                    fullPhone += " " + p.getPhoneType().getValue();
                }
                if (!StringUtils.isBlank(fullPhone)) {
                    phones.add(fullPhone);
                    hasPhones = true;
                }
            }
        }
        
        boolean hasEmails = false;
        List<String> emails = new ArrayList<String>();
        if (contactInfo.getEMailList() != null) {
            for (EMailAddress e : contactInfo.getEMailList()) {
                String fullEmail = "";
                if (e.getEMail() != null) {
                    fullEmail += e.getEMail();
                }
                if (!StringUtils.isBlank(fullEmail)) {
                    emails.add(fullEmail);
                    hasEmails = true;
                }
            }
        }
        
        if (!hasPhones && !hasEmails) {
            return null;
        }
        
        add("Phone Number", StringUtils.join(phones, ", "), true, infoList);
        add("Email Address", StringUtils.join(emails, ", "), true, infoList);
        return infoList;
    }
    
    private Map<String, String> getCustomerAddressInfo(com.cannontech.multispeak.deploy.service.Customer mspCustomer) {
        
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("Address 1", mspCustomer.getBillAddr1());
        m.put("Address 2", mspCustomer.getBillAddr2());
        m.put("City", mspCustomer.getBillCity());
        m.put("State", mspCustomer.getBillState());
        m.put("Zip", mspCustomer.getBillZip());
        return m;
    }
    
    // SERV LOC INFO
    private List<Info> getServLocBasicsInfo(com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc) {
        
        List<Info> infoList = new ArrayList<Info>();
        add("Customer ID", mspServLoc.getCustID(), false, infoList);
        add("Account Number", mspServLoc.getAccountNumber(), false, infoList);
        add("Facility ID", mspServLoc.getFacilityID(), false, infoList);
        add("Facility Name", mspServLoc.getFacilityName(), false, infoList);
        add("Site ID", mspServLoc.getSiteID(), false, infoList);
        add("Utility", mspServLoc.getUtility(), true, infoList);
        add("Type", mspServLoc.getServType(), false, infoList);
        add("Revenue Class", mspServLoc.getRevenueClass(), true, infoList);
        add("Status", mspServLoc.getServStatus(), true, infoList);
        add("Outage Status", mspServLoc.getOutageStatus(), true, infoList);
        add("Billing Cycle", mspServLoc.getBillingCycle(), true, infoList);
        add("Route", mspServLoc.getRoute(), true, infoList);
        add("Special Needs", mspServLoc.getSpecialNeeds(), true, infoList);
        add("Load Mgmt", mspServLoc.getLoadMgmt(), true, infoList);
        add("Budget Bill Code", mspServLoc.getBudgBill(), true, infoList);
        add("Total accounts receivable balance.", mspServLoc.getAcRecvBal(), true, infoList);
        add("Current accounts receivable balance", mspServLoc.getAcRecvCur(), true, infoList);
        add("30-day accounts receivable balance", mspServLoc.getAcRecv30(), true, infoList);
        add("60-day accounts receivable balance", mspServLoc.getAcRecv60(), true, infoList);
        add("90-day accounts receivable balance", mspServLoc.getAcRecv90(), true, infoList);
        add("Payment Due Date", mspServLoc.getPaymentDueDate(), true, infoList);
        add("Last Payment Date", mspServLoc.getLastPaymentDate(), true, infoList);
        add("Last Payment Amount", mspServLoc.getLastPaymentAmount(), true, infoList);
        add("Bill Date", mspServLoc.getBillDate(), true, infoList);
        add("Shut Off Date", mspServLoc.getShutOffDate(), true, infoList);
        add("Connection", mspServLoc.getConnection(), true, infoList);
        add("Connect Date", mspServLoc.getConnectDate(), true, infoList);
        add("Disconnect Date", mspServLoc.getDisconnectDate(), true, infoList);
        add("SIC", mspServLoc.getSIC(), true, infoList);
        add("Is Cogeneration Site", mspServLoc.getIsCogenerationSite(), true, infoList);
        add("Work order number", mspServLoc.getWoNumber(), true, infoList);
        add("Service order number", mspServLoc.getSoNumber(), true, infoList);
        if (mspServLoc.getPhaseCode() != null) {
            add("Phasing code", mspServLoc.getPhaseCode().getValue(), true, infoList);
        } else {
            add("Phasing code", null, true, infoList);
        }
        if (mspServLoc.getMapLocation() != null && mspServLoc.getMapLocation().getCoord() != null) {
            String xyz = makeMapLocation(mspServLoc.getMapLocation().getCoord());
            add("Map Location", xyz, false, infoList);
        } else {
            add("Map Location", null, false, infoList);
        }
        add("Grid Location", mspServLoc.getGridLocation(), false, infoList);
        add("Rotation", mspServLoc.getRotation(), true, infoList);
        if (mspServLoc.getFromNodeID() != null) {
            add("From Node ID", mspServLoc.getFromNodeID().getName(), true, infoList);
        } else {
            add("From Node ID", null, true, infoList);
        }
        if (mspServLoc.getToNodeID() != null) {
            add("To Node ID", mspServLoc.getToNodeID().getName(), true, infoList);
        } else {
            add("To Node ID", null, true, infoList);
        }
        add("Section ID", mspServLoc.getSectionID(), true, infoList);
        if (mspServLoc.getParentSectionID() != null) {
            add("Parent Section ID", mspServLoc.getParentSectionID().getName(), true, infoList);
        } else {
            add("Parent Section ID", null, true, infoList);
        }
        add("Comments", mspServLoc.getComments(), true, infoList);
        add("Error String", mspServLoc.getErrorString(), true, infoList);
        
        return infoList;
    }
    
    private Map<String, String> getServLocAddressInfo(com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc) {
        
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("Address 1", mspServLoc.getServAddr1());
        m.put("Address 2", mspServLoc.getServAddr2());
        m.put("City", mspServLoc.getServCity());
        m.put("State", mspServLoc.getServState());
        m.put("Zip", mspServLoc.getServZip());
        return m;
    }   
    
    private List<Info> getServLocNetworkInfo(com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc) {
        
        List<Info> infoList = new ArrayList<Info>();
        Network n = mspServLoc.getNetwork();
        if (n != null) {
            add("Board District", n.getBoardDist(), true, infoList);
            add("Tax District", n.getTaxDist(), true, infoList);
            add("Francise District", n.getFranchiseDist(), true, infoList);
            add("School District", n.getSchoolDist(), true, infoList);
            add("District", n.getDistrict(), true, infoList);
            add("County", n.getCounty(), true, infoList);
            add("City Code", n.getCityCode(), true, infoList);
            add("Substation Code", n.getSubstationCode(), true, infoList);
            add("Feeder", n.getFeeder(), true, infoList);
            add("Phasing code", n.getPhaseCd(), true, infoList);
            add("Engineering analysis location", n.getEaLoc(), false, infoList);
            add("Pole Number", n.getPoleNo(), true, infoList);
            add("Section", n.getSection(), true, infoList);
            add("Township", n.getTownship(), true, infoList);
            add("Range", n.getRange(), true, infoList);
            add("Subdivision", n.getSubdivision(), true, infoList);
            add("Block", n.getBlock(), true, infoList);
            add("Lot", n.getLot(), true, infoList);
            if (n.getLinkedTransformer() != null && n.getLinkedTransformer().getBankID() != null) {
                add("Linked Transformer", "Bank ID: " + n.getLinkedTransformer().getBankID(), true, infoList);
            } else {
                add("Linked Transformer", null, true, infoList);
            }
            add("Lineman Service Area", n.getLinemanServiceArea(), true, infoList);
        }
        return infoList;
    }   
    
    // METER INFO
    private List<Info> getMeterBasicsInfo(com.cannontech.multispeak.deploy.service.Meter mspMeter) {
        
        List<Info> infoList = new ArrayList<Info>();
        add("Meter Number", mspMeter.getMeterNo(), false, infoList);
        add("Meter Type", mspMeter.getMeterType(), false, infoList);
        if (mspMeter.getSealNumberList() != null) {
            add("Seal Numbers", StringUtils.join(mspMeter.getSealNumberList(), ", "), true, infoList);
        } else {
            add("Seal Numbers", null, true, infoList);
        }
        add("AMR Type", mspMeter.getAMRType(), false, infoList);
        add("AMR Device Type", mspMeter.getAMRDeviceType(), false, infoList);
        add("AMR Vendor", mspMeter.getAMRVendor(), false, infoList);
        
        add("In Service Date", mspMeter.getInServiceDate(), false, infoList);
        add("Out Service Date", mspMeter.getOutServiceDate(), false, infoList);
        add("Serial Number", mspMeter.getSerialNumber(), true, infoList);
        add("Device Class", mspMeter.getDeviceClass(), true, infoList);
        add("Manufacturer", mspMeter.getManufacturer(), true, infoList);
        add("Facility ID", mspMeter.getFacilityID(), true, infoList);
        add("Utility", mspMeter.getUtility(), true, infoList);
        add("Comments", mspMeter.getComments(), true, infoList);
        add("Error String", mspMeter.getErrorString(), true, infoList);
        return infoList;
    }
    
    private List<Info> getMeterNameplateInfo(com.cannontech.multispeak.deploy.service.Meter mspMeter) {
        
        List<Info> infoList = new ArrayList<Info>();
        Nameplate np = mspMeter.getNameplate();
        if (np != null) {
            
            add("Meter kh (watthour) constant", np.getKh(), true, infoList);
            add("Watthour meter register constant", np.getKr(), true, infoList);
            add("Frequency", np.getFrequency(), true, infoList);
            add("Number Of Element", np.getNumberOfElements(), true, infoList);
            add("Base Type", np.getBaseType(), true, infoList);
            add("Accuracy Class", np.getAccuracyClass(), true, infoList);
            add("Element Voltage", np.getElementsVoltage(), true, infoList);
            add("Supply Voltage", np.getSupplyVoltage(), true, infoList);
            add("Max Amperage", np.getMaxAmperage(), true, infoList);
            add("Test Amperage", np.getTestAmperage(), true, infoList);
            add("Reg Ratio", np.getRegRatio(), true, infoList);
            add("Phases", np.getPhases(), true, infoList);
            add("Wires", np.getWires(), true, infoList);
            add("Dials", np.getDials(), true, infoList);
            add("Form", np.getForm(), true, infoList);
            add("Multiplier", np.getMultiplier(), false, infoList);
            add("Demand Multiplier", np.getDemandMult(), false, infoList);
            add("Transponder ID", np.getTransponderID(), false, infoList);
        }
        return infoList;
    }
    
    private List<Info> getMeterUtilInfo(com.cannontech.multispeak.deploy.service.Meter mspMeter) {
        
        List<Info> infoList = new ArrayList<Info>();
        UtilityInfo u = mspMeter.getUtilityInfo();
        if (u != null) {
            
            add("Owner", u.getOwner(), true, infoList);
            add("District", u.getDistrict(), true, infoList);
            add("Service Location", u.getServLoc(), false, infoList);
            add("Account Number", u.getAccountNumber(), false, infoList);
            add("Customer ID", u.getCustID(), false, infoList);
            add("Substation Code", u.getSubstationCode(), false, infoList);
            add("Substation Name", u.getSubstationName(), false, infoList);
            add("Feeder", u.getFeeder(), true, infoList);
            add("Bus", u.getBus(), true, infoList);
            if (u.getPhaseCd() != null) {
                add("Phasing code", u.getPhaseCd().getValue(), true, infoList);
            } else {
                add("Phasing code", null, true, infoList);
            }
            if (u.getEaLoc() != null) {
                add("Engineering analysis location", u.getEaLoc().getName(), true, infoList);
            } else {
                add("Engineering analysis location", null, true, infoList);
            }
            add("Transformer Bank ID", u.getTransformerBankID(), true, infoList);
            add("Meter Base ID", u.getMeterBaseID(), true, infoList);
            if (u.getMapLocation() != null && u.getMapLocation().getCoord() != null) {
                String xyz = makeMapLocation(u.getMapLocation().getCoord());
                add("Map Location", xyz, true, infoList);
            } else {
                add("Map Location", null, true, infoList);
            }
            
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
    
    private void add(String label, Object value, Boolean hideIfBlank, List<Info> list) {
        
        Info info = new Info(label, value);
        if (!(info.isBlank() && hideIfBlank)) {
            list.add(info);
        }
    }
    
    public class Info {
        
        private String label = "";
        private String value = "";
        
        public Info(String label, Object value) {
            
            this.label = label;
            if (value != null) {
                this.value = value.toString();
            }
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
            return this.value == null || StringUtils.isBlank(this.value);
        }
    }
    
//    private String getMapURL(Address address) {
//        if (address == null ||
//                address.getLocationAddress1() == null ||
//                address.getCityName() == null ||
//                address.getStateCode() == null ||
//                address.getZipCode() == null) {
//            return null;
//        }
//
//        String[] split = StringUtils.split(address.getLocationAddress1(), ' ');
//        String join = StringUtils.join(split, '+');
//
//        final StringBuilder sb = new StringBuilder();
//        sb.append("http://maps.google.com/maps?f=q&hl=en&q=");
//        sb.append(join);
//        sb.append("+");
//        sb.append(address.getCityName());
//        sb.append(",+");
//        sb.append(address.getStateCode());
//        sb.append("+");
//        sb.append(address.getZipCode());
//        sb.append("&ie=UTF8&om=1&z=16&iwloc=addr&t=h");
//        return sb.toString();
//    }
    
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
        this.mspObjectDao = mspObjectDao;
    }
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

}
