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
import com.cannontech.multispeak.deploy.service.Nameplate;
import com.cannontech.multispeak.deploy.service.Network;
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
        add("Middle Name", mspCustomer.getMName(), false, infoList);
        add("Home Phone", formatPhone(mspCustomer.getHomeAc(), mspCustomer.getHomePhone()), false, infoList);
        add("Day Phone", formatPhone(mspCustomer.getDayAc(), mspCustomer.getDayPhone()), false, infoList);
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
        add("Facility Name", mspServLoc.getFacilityName(), false, infoList);
        add("Site ID", mspServLoc.getSiteID(), false, infoList);
        add("Type", mspServLoc.getServType(), false, infoList);
        add("Revenue Class", mspServLoc.getRevenueClass(), false, infoList);
        add("Status", mspServLoc.getServStatus(), false, infoList);
        add("Outage Status", mspServLoc.getOutageStatus(), false, infoList);
        add("Billing Cycle", mspServLoc.getBillingCycle(), false, infoList);
        add("Route", mspServLoc.getRoute(), false, infoList);
        add("Special Needs", mspServLoc.getSpecialNeeds(), false, infoList);
        add("Load Mgmt", mspServLoc.getLoadMgmt(), false, infoList);
        add("Budget Bill Code", mspServLoc.getBudgBill(), false, infoList);
        add("Total accounts receivable balance.", mspServLoc.getAcRecvBal(), false, infoList);
        add("Current accounts receivable balance", mspServLoc.getAcRecvCur(), false, infoList);
        add("30-day accounts receivable balance", mspServLoc.getAcRecv30(), false, infoList);
        add("60-day accounts receivable balance", mspServLoc.getAcRecv60(), false, infoList);
        add("90-day accounts receivable balance", mspServLoc.getAcRecv90(), false, infoList);
        add("Payment Due Date", mspServLoc.getPaymentDueDate(), false, infoList);
        add("Last Payment Date", mspServLoc.getLastPaymentDate(), false, infoList);
        add("Last Payment Amount", mspServLoc.getLastPaymentAmount(), false, infoList);
        add("Bill Date", mspServLoc.getBillDate(), false, infoList);
        add("Shut Off Date", mspServLoc.getShutOffDate(), false, infoList);
        add("Connection", mspServLoc.getConnection(), false, infoList);
        add("Connect Date", mspServLoc.getConnectDate(), false, infoList);
        add("Disconnect Date", mspServLoc.getDisconnectDate(), false, infoList);
        add("SIC", mspServLoc.getSIC(), false, infoList);
        add("Work order number", mspServLoc.getWoNumber(), false, infoList);
        add("Service order number", mspServLoc.getSoNumber(), false, infoList);
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
            add("District", n.getDistrict(), false, infoList);
            add("Board District", n.getBoardDist(), false, infoList);
            add("Tax District", n.getTaxDist(), false, infoList);
            add("Francise District", n.getFranchiseDist(), false, infoList);
            add("School District", n.getSchoolDist(), false, infoList);
            add("County", n.getCounty(), false, infoList);
            add("City Code", n.getCityCode(), false, infoList);
            add("Substation Code", n.getSubstationCode(), false, infoList);
            add("Feeder", n.getFeeder(), false, infoList);
            add("Phasing code", n.getPhaseCd(), false, infoList);
            add("Engineering analysis location", n.getEaLoc(), false, infoList);
            add("Pole Number", n.getPoleNo(), false, infoList);
            add("Section", n.getSection(), false, infoList);
            add("Township", n.getTownship(), false, infoList);
            add("Range", n.getRange(), false, infoList);
            add("Subdivision", n.getSubdivision(), false, infoList);
            add("Block", n.getBlock(), false, infoList);
            add("Lot", n.getLot(), false, infoList);
            if (n.getLinkedTransformer() != null) {
                String bankId = n.getLinkedTransformer().getBankID() == null ? "" : n.getLinkedTransformer().getBankID();
                String unitList = n.getLinkedTransformer().getUnitList() == null ? "" : StringUtils.join(n.getLinkedTransformer().getUnitList(), ", ");
                add("Linked Transformer", "Bank ID: " + bankId + " Unit List: " + unitList, false, infoList);
                
            } else {
                add("Linked Transformer", null, false, infoList);
            }
            add("Lineman Service Area", n.getLinemanServiceArea(), false, infoList);
        }
        return infoList;
    }   
    
    // METER INFO
    private List<Info> getMeterBasicsInfo(com.cannontech.multispeak.deploy.service.Meter mspMeter) {
        
        List<Info> infoList = new ArrayList<Info>();
        add("Meter Number", mspMeter.getMeterNo(), false, infoList);
        add("Meter Type", mspMeter.getMeterType(), false, infoList);
        if (mspMeter.getSealNumberList() != null) {
            add("Seal Numbers", StringUtils.join(mspMeter.getSealNumberList(), ", "), false, infoList);
        } else {
            add("Seal Numbers", null, false, infoList);
        }
        add("AMR Type", mspMeter.getAMRType(), false, infoList);
        add("AMR Device Type", mspMeter.getAMRDeviceType(), false, infoList);
        add("AMR Vendor", mspMeter.getAMRVendor(), false, infoList);
        return infoList;
    }
    
    private List<Info> getMeterNameplateInfo(com.cannontech.multispeak.deploy.service.Meter mspMeter) {
        
        List<Info> infoList = new ArrayList<Info>();
        Nameplate np = mspMeter.getNameplate();
        if (np != null) {
            
            add("Meter kh (watthour) constant", np.getKh(), false, infoList);
            add("Watthour meter register constant", np.getKr(), false, infoList);
            add("Frequency", np.getFrequency(), false, infoList);
            add("Number Of Element", np.getNumberOfElements(), false, infoList);
            add("Base Type", np.getBaseType(), false, infoList);
            add("Accuracy Class", np.getAccuracyClass(), false, infoList);
            add("Element Voltage", np.getElementsVoltage(), false, infoList);
            add("Supply Voltage", np.getSupplyVoltage(), false, infoList);
            add("Max Amperage", np.getMaxAmperage(), false, infoList);
            add("Test Amperage", np.getTestAmperage(), false, infoList);
            add("Reg Ratio", np.getRegRatio(), false, infoList);
            add("Phases", np.getPhases(), false, infoList);
            add("Wires", np.getWires(), false, infoList);
            add("Dials", np.getDials(), false, infoList);
            add("Form", np.getForm(), false, infoList);
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
            
            add("Owner", u.getOwner(), false, infoList);
            add("District", u.getDistrict(), false, infoList);
            add("Service Location", u.getServLoc(), false, infoList);
            add("Account Number", u.getAccountNumber(), false, infoList);
            add("Customer ID", u.getCustID(), false, infoList);
            add("Substation Code", u.getSubstationCode(), false, infoList);
            add("Substation Name", u.getSubstationName(), false, infoList);
            add("Feeder", u.getFeeder(), false, infoList);
            add("Bus", u.getBus(), false, infoList);
            if (u.getPhaseCd() != null) {
                add("Phasing code", u.getPhaseCd().getValue(), false, infoList);
            } else {
                add("Phasing code", null, false, infoList);
            }
            if (u.getEaLoc() != null) {
                add("Engineering analysis location", u.getEaLoc().getName(), false, infoList);
            } else {
                add("Engineering analysis location", null, false, infoList);
            }
            add("Transformer Bank ID", u.getTransformerBankID(), false, infoList);
            add("Meter Base ID", u.getMeterBaseID(), false, infoList);
            if (u.getMapLocation() != null && u.getMapLocation().getCoord() != null) {
                String xyz = "";
                xyz += "X=" + u.getMapLocation().getCoord().getX();
                xyz += " Y=" + u.getMapLocation().getCoord().getY();
                xyz += " Z=" + u.getMapLocation().getCoord().getZ();
                xyz += " Bulge=" + u.getMapLocation().getCoord().getBulge();
                add("Map Location", xyz, false, infoList);
            } else {
                add("Map Location", null, false, infoList);
            }
            
        }
        
        return infoList;
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
