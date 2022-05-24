package com.cannontech.web.widget.accountInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.Contact;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.msp.beans.v4.ArrayOfAddressItem;
import com.cannontech.msp.beans.v4.ArrayOfBillingStatusItem;
import com.cannontech.msp.beans.v4.ArrayOfServiceLocation1;
import com.cannontech.msp.beans.v4.ContactInfo;
import com.cannontech.msp.beans.v4.Customer;
import com.cannontech.msp.beans.v4.EMailAddress;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.PhoneType;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.service.v4.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;

public class MspAccountInformationV4 implements MspAccountInformation {

    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private MultispeakCustomerInfoService multispeakCustomerInfoService;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @Override
    public ModelAndView getMspInformation(YukonMeter meter, MultispeakVendor mspVendor, ModelAndView mav,
            YukonUserContext userContext) {

        String meterNumber = meter.getMeterNumber();
        Customer mspCustomer = mspObjectDao.getMspCustomer(meter, mspVendor);
        ArrayOfServiceLocation1 arrayOfmspServLoc = mspObjectDao.getMspServiceLocation(meterNumber, mspVendor);
        Meters mspMeter = mspObjectDao.getMspMeter(meter, mspVendor);
        
        List<ServiceLocation> serviceLocationList = arrayOfmspServLoc.getServiceLocation();
        
        ServiceLocation mspServLoc = serviceLocationList != null?serviceLocationList.get(0):null;
        
        mav.addObject("mspCustomer", mspCustomer);
        mav.addObject("mspServLoc", mspServLoc);
        if (mspServLoc != null && mspServLoc != null
            && mspServLoc.getContactInfo().getAddressList().getAddressItem() != null) {
            ArrayOfAddressItem addressList = mspServLoc.getContactInfo().getAddressList();
            List<Address> servLocAddresses = multispeakFuncs.getAddressList(addressList.getAddressItem());
            mav.addObject("servLocAddresses", servLocAddresses.get(0));
        }
        Map<PhoneType, String> primaryContact = multispeakFuncs.getPrimaryContacts(mspCustomer);
        mav.addObject("homePhone", primaryContact.get(PhoneType.HOME));
        mav.addObject("dayPhone", primaryContact.get(PhoneType.BUSINESS));

        ContactInfo contactInfo = mspCustomer.getContactInfo();
        if (contactInfo != null && contactInfo.getEMailList() != null) {
            List<EMailAddress> eMailAddress = contactInfo.getEMailList().getEMailAddress();
            List<String> emailAddresses = multispeakCustomerInfoService.getEmailAddresses(contactInfo, userContext);
                
            mav.addObject("mspEmailAddresses", emailAddresses);
        }

        if (contactInfo != null && contactInfo.getAddressList() != null) {
            List<Address> custAddressInfo = multispeakFuncs.getAddressList(contactInfo.getAddressList().getAddressItem());
            mav.addObject("custAddressInfo", custAddressInfo.get(0));
        }
        // Customer Basic Information
        List<Info> custBasicsInfo = getCustomerBasicsInfo(mspCustomer, userContext);
        mav.addObject("custBasicsInfo", custBasicsInfo);

        List<Contact> custBasicContactInfo = getCustomerBasicContactInfo(mspCustomer, userContext);
        mav.addObject("custBasicContactInfo", custBasicContactInfo);

        if (mspCustomer.getAccounts() != null) {
            List<Info> custAccountInfo = getCustomerAccountInfo(mspCustomer, userContext);
            mav.addObject("custAccountInfo", custAccountInfo);

            Map<String, List<Info>> custAccountReceivableInfo =
                getCustomerAccountReceivableInfo(mspCustomer, userContext);
            mav.addObject("custReceivableInfo", custAccountReceivableInfo);

        }
        if (mspCustomer.getAlternateContactList().getAlternateContact() != null) {
            List<Contact> custAlternateContactInfo = getCustomerAlternateContactsInfo(mspCustomer, userContext);
            if (custAlternateContactInfo != null) {
                mav.addObject("custAlternateContactInfo", custAlternateContactInfo);
            }
        }

        // Service Location Information
        List<Info> servLocBasicsInfo = getServLocBasicsInfo(mspServLoc, userContext);
        mav.addObject("servLocBasicsInfo", servLocBasicsInfo);
        
        if (mspServLoc != null && mspServLoc.getContactInfo() != null
                && mspServLoc.getContactInfo().getAddressList().getAddressItem() != null) {
                List<Address> servLocAddresses = multispeakFuncs.getAddressList(mspServLoc.getContactInfo().getAddressList().getAddressItem());
                mav.addObject("servLocAddressesList", servLocAddresses);
        }

//        if (mspServLoc.getServiceHazards() != null) {
//            Map<AtomicInteger, List<Info>> servLocHazardInfo = getServiceLocationHazardInfo(mspServLoc, userContext);
//            mav.addObject("servLocHazardInfo", servLocHazardInfo);
//        }
        
        if (mspServLoc.getElectricServiceList().getElectricService() != null) {
            List<Info> electricServiceListInfo = getElectricServiceListInfo(mspServLoc, userContext);
            mav.addObject("electricServiceListInfo", electricServiceListInfo);
        }
        if (mspServLoc.getGasServiceList().getGasService() != null) {
            List<Info> gasServiceListInfo = getGasServicePointInfo(mspServLoc, userContext);
            mav.addObject("gasServiceListInfo", gasServiceListInfo);
        }

        if (mspServLoc.getWaterServiceList().getWaterService() != null) {
            List<Info> waterServiceListInfo = getWaterServiceListInfo(mspServLoc, userContext);
            mav.addObject("waterServiceListInfo", waterServiceListInfo);
        }
//        if (mspServLoc.getPropaneServicePoints() != null) {
//            List<Info> propaneServicePointsInfo = getPropaneServicePointInfo(mspServLoc, userContext);
//            mav.addObject("propaneServicePointsInfo", propaneServicePointsInfo);
//        }

        return mav;
    }

    // Customer Basic Information
    private List<Info> getCustomerBasicsInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Info> infoList = new ArrayList<Info>();

        Map<PhoneType, String> primaryContact = multispeakFuncs.getPrimaryContacts(mspCustomer);

        add("Identifier", mspCustomer.getObjectID(), false, infoList, userContext);
        add("Last Name", mspCustomer.getLastName(), false, infoList, userContext);
        add("First Name", mspCustomer.getFirstName(), false, infoList, userContext);
        add("Middle Name", mspCustomer.getMName(), true, infoList, userContext);
        add("DBA", mspCustomer.getDBAName(), false, infoList, userContext);
        add("Home Phone", primaryContact.get(PhoneType.HOME), true, infoList, userContext);
        add("Day Phone", primaryContact.get(PhoneType.BUSINESS), true, infoList, userContext);
        add("Utility", mspCustomer.getUtility(), true, infoList, userContext);
        add("Comments", mspCustomer.getComments(), true, infoList, userContext);
        add("Government ID", mspCustomer.getGovernmentID(), true, infoList, userContext);
        return infoList;
    }

    // Customer Basic Contact Information
    private List<Contact> getCustomerBasicContactInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Contact> info = new ArrayList<>();

        ContactInfo contactInfo = mspCustomer.getContactInfo();
        if (contactInfo != null) {
            Contact contact = new Contact();
            List<String> phoneNumbers =
                multispeakCustomerInfoService.getPhoneNumbers(contactInfo, userContext);
            contact.setPhoneNumbers(phoneNumbers);

            List<String> emailAddresses =
                multispeakCustomerInfoService.getEmailAddresses(contactInfo, userContext);
            contact.setEmailAddresses(emailAddresses);
            if (contactInfo.getAddressList().getAddressItem() != null) {
                contact.setAddresses(multispeakFuncs.getAddressList(contactInfo.getAddressList().getAddressItem()));
            }
            info.add(contact);
        }
        return info;

    }

    // Customer Account Information
    private List<Info> getCustomerAccountInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();

        mspCustomer.getAccounts().getAccount().forEach(
            accountInfo -> {
                add("Account ID", accountInfo.getObjectID(), false, info, userContext);
                add("Billing Cycle", accountInfo.getBillingCycle(), true, info, userContext);
                add("Budget Bill", accountInfo.getBudgetBill(), true, info, userContext);
                add("Payment Due Date", accountInfo.getPaymentDueDate(), true, info, userContext);
                add("Last Payment Date", accountInfo.getLastPaymentDate(), true, info, userContext);
                add("Last Payment Amount", accountInfo.getLastPaymentAmount(), true, info, userContext);
                add("Bill Date", accountInfo.getBillDate(), true, info, userContext);
                add("Shutoff Date", accountInfo.getShutOffDate(), true, info, userContext);
                add("Prepay", accountInfo.isIsPrePay(), true, info, userContext);
                add("Billing Term", accountInfo.getBillingTerms(), true, info, userContext);
                add("Calculated Current Bill Amount", accountInfo.getCalculatedCurrentBillAmount(), true, info,
                    userContext);
                add("Calculated Current Bill Date", accountInfo.getCalculatedCurrentBillDateTime(), true, info,
                    userContext);
                add("Last Bill Amount", accountInfo.getLastBillAmount(), true, info, userContext);
                add("Calculated Used Yesterday", accountInfo.getCalculatedUsedYesterday(), true, info, userContext);

            });
        return info;
    }

    // Customer Account Receivable Information
    private Map<String, List<Info>> getCustomerAccountReceivableInfo(Customer mspCustomer, YukonUserContext userContext) {

        Map<String, List<Info>> customerAccountReceivableMap = new HashMap<>();

        mspCustomer.getAccounts().getAccount().forEach(
            accountInfo -> {
                if (accountInfo.getAccountsReceivable() != null) {
                    List<Info> info = new ArrayList<>();
                    accountInfo.getAccountsReceivable().getAcctsReceivable().forEach(
                        acctsReceivable -> {
                            add("Receivable Amount", acctsReceivable.getReceivableAmount(), true, info, userContext);
                            add("Receivable Type", acctsReceivable.getReceivableType().value(), true, info,
                                userContext);
                            add("Description", acctsReceivable.getDescription(), true, info, userContext);
                            add("Due Date", acctsReceivable.getDueDate(), true, info, userContext);
                            if (acctsReceivable.getServiceType() != null) {
                                add("Service Type", acctsReceivable.getServiceType(), true, info,
                                    userContext);
                            }
                        });
                    customerAccountReceivableMap.put(accountInfo.getObjectID(), info);
                }
            });
        return customerAccountReceivableMap;
    }

    // Get the customer alternate contact information
    private List<Contact> getCustomerAlternateContactsInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Contact> info = new ArrayList<>();

        mspCustomer.getAlternateContactList().getAlternateContact().forEach(
            alternateContact -> {
                ContactInfo contactInfo = alternateContact.getContactInfo();
                List<EMailAddress> eMailAddress = mspCustomer.getContactInfo().getEMailList().getEMailAddress();
                
                List<String> phoneNumbers =
                    multispeakCustomerInfoService.getPhoneNumbers(contactInfo, userContext);
                List<String> emailAddresses =
                    multispeakCustomerInfoService.getEmailAddresses(contactInfo, userContext);
                Contact contact =
                    new Contact(alternateContact.getFirstName(), alternateContact.getMName(),
                        alternateContact.getLastName(), phoneNumbers, emailAddresses,contactInfo.getAddressList() == null?null:
                        (contactInfo.getAddressList().getAddressItem() != null ? multispeakFuncs.getAddressList(null) : null));
                info.add(contact);

            });
        return info;
    }

    // Service location basic information
    private List<Info> getServLocBasicsInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> infoList = new ArrayList<Info>();

        add("Primary Identifier ", mspServLoc.getObjectID(), false, infoList, userContext);
        add("Description", mspServLoc.getDescription(), true, infoList, userContext);
        add("Facility Name", mspServLoc.getFacilityName(), false, infoList, userContext);
        add("Account ID", mspServLoc.getObjectID(), false, infoList, userContext);
        add("SIC", mspServLoc.getSIC(), false, infoList, userContext);
        add("Site ID", mspServLoc.getSiteID(), false, infoList, userContext);
        
        if(mspServLoc.getServiceOrderList() !=null && mspServLoc.getServiceOrderList().getServiceOrder() !=null && mspServLoc.getServiceOrderList().getServiceOrder().get(0) !=null) {
        add("ServiceOrder ID", mspServLoc.getServiceOrderList().getServiceOrder().get(0).getObjectID(), false, infoList, userContext);
        }
        
        add("GPS Location ", mspServLoc.getGPSLocation(), false, infoList, userContext);
        add("GML Location ", mspServLoc.getGMLLocation(), false, infoList, userContext);
        add("Grid Location ", mspServLoc.getGridLocation(), false, infoList, userContext);
        if (mspServLoc.getRotation() != null) {
            add("Rotation ", mspServLoc.getRotation() + StringUtils.SPACE
                + (mspServLoc.getRotation() != null ? mspServLoc.getRotation() : ""), true,
                infoList, userContext);
        }
        // Location Information
        if (mspServLoc.getNetwork() != null) {
            add("City ", mspServLoc.getNetwork().getCity(), true, infoList, userContext);
            add("County ", mspServLoc.getNetwork().getCountry(), true, infoList, userContext);
            add("Township Name ", mspServLoc.getNetwork().getTownship(), true, infoList, userContext);
            add("Sub Division ", mspServLoc.getNetwork().getSubdivision(), true, infoList, userContext);
            add("Block ", mspServLoc.getNetwork().getBlock(), true, infoList, userContext);
            add("Lot ", mspServLoc.getNetwork().getLot(), true, infoList, userContext);
            add("State ", mspServLoc.getNetwork().getState(), true, infoList, userContext);
//            if (mspServLoc.getLocationInformation().getTRSQ() != null) {
//                add("Section ", mspServLoc.getLocationInformation().getTRSQ().getSection(), true, infoList,
//                    userContext);
//                add("Quater Section ", mspServLoc.getLocationInformation().getTRSQ().getQuarterSection(), true,
//                    infoList, userContext);
//                add("Range ", mspServLoc.getLocationInformation().getTRSQ().getRange(), true, infoList, userContext);
//                add("Township ", mspServLoc.getLocationInformation().getTRSQ().getTownship(), true, infoList,
//                    userContext);
//            }
//            if (mspServLoc.getLocationInformation().getTimeZone() != null) {
//                add("Time Zone - Name ", mspServLoc.getLocationInformation().getTimeZone().getName(), true, infoList,
//                    userContext);
//                add("Time Zone - Value ", mspServLoc.getLocationInformation().getTimeZone().getValue(), true,
//                    infoList, userContext);
//                add("Time Zone - UTC offset ", mspServLoc.getLocationInformation().getTimeZone().getUTCOffset(), true,
//                    infoList, userContext);
//                add("Time Zone - Comment ", mspServLoc.getLocationInformation().getTimeZone().getComment(), true,
//                    infoList, userContext);
//            }
        }

        return infoList;
    }

    // Service Location Hazards Information
    private Map<AtomicInteger, List<Info>> getServiceLocationHazardInfo(ServiceLocation mspServLoc,
            YukonUserContext userContext) {

        Map<AtomicInteger, List<Info>> locationHazardMap = new HashMap<>();
        List<Info> info = new ArrayList<>();
        AtomicInteger hazardID = new AtomicInteger();

//        mspServLoc.getServiceHazards().getServiceHazard().forEach(locationHazard -> {
//            add("Type", locationHazard.getLocationHazardType(), true, info, userContext);
//            add("Sub Type", locationHazard.getLocationHazardSubType(), true, info, userContext);
//            add("Text", locationHazard.getHazardText(), true, info, userContext);
//            hazardID.incrementAndGet();
//            locationHazardMap.put(hazardID, info);
//        });

        return locationHazardMap;
    }

    // Service Location - Electric Service Point
    private List<Info> getElectricServiceListInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        // Map<String, List<Info>> electricServicePointMap = new HashMap<>();
        List<Info> info = new ArrayList<>();

        mspServLoc.getElectricServiceList().getElectricService().forEach(electricService -> {
            add(null, "Meter Information", true, info, userContext);
            // Electric Meter
            
                if (electricService.getElectricMeterID() != null) {
                    add("Meter ID", electricService.getElectricMeterID(), false,
                        info, userContext);
                }
                
                add("Meter Base ID", electricService.getMeterBaseID(), false, info, userContext);
                
                if (electricService.getMeterBase().getElectricMeter().getMeterConnectionStatus() != null) {
                    add("Connection Status",
                        electricService.getMeterBase().getElectricMeter().getMeterConnectionStatus(), true, info,
                        userContext);
                }
                add("Metrology Firmware Version",
                    electricService.getMeterBase().getElectricMeter().getMetrologyFirmwareVersion(), true, info, userContext);
                add("Metrology Firmware Revision",
                    electricService.getMeterBase().getElectricMeter().getMetrologyFirmwareRevision(), true, info, userContext);
                add("AMI Device Type", electricService.getMeterBase().getElectricMeter().getAMRDeviceType(), false, info,
                    userContext);
                add("AMI Vendor", electricService.getMeterBase().getElectricMeter().getAMRVendor(), false, info, userContext);
                add("Billing Cycle", electricService.getMeterBase().getElectricMeter().getBillingCycle(), true, info,
                    userContext);

                if (electricService.getMeterBase().getElectricMeter().getUtilityInfo() != null) {
                    add("Owner", electricService.getMeterBase().getElectricMeter().getUtilityInfo().getOwner(), true, info,
                        userContext);
                    if (electricService.getMeterBase().getElectricMeter().getUtilityInfo().getServiceID() != null) {
                        add("Service Point Value",
                            electricService.getMeterBase().getElectricMeter().getUtilityInfo().getServiceID().getValue(),
                            true, info, userContext);
                        add("Service Point Type",
                            electricService.getMeterBase().getElectricMeter().getUtilityInfo().getServiceID().getServiceType(),
                            true, info, userContext);
                    }

                }
                if (electricService.getMeterBase().getElectricMeter().getParentMeterList() != null) {
                    electricService.getMeterBase().getElectricMeter().getParentMeterList().getMeterID().forEach(meterID -> {
                        add("Parent Meter", meterID, true, info, userContext);
                    });
                }
                if (electricService.getMeterBase().getElectricMeter().getSubMeterList() != null) {
                    electricService.getMeterBase().getElectricMeter().getSubMeterList().getMeterID().forEach(meterID -> {
                        add("Sub Meter", meterID, true, info, userContext);
                    });
                }
                if (electricService.getMeterBase().getElectricMeter().getModuleList() != null) {
                    electricService.getMeterBase().getElectricMeter().getModuleList().getModule().forEach(module -> {
                        add("Module", module.getDescription(), true, info, userContext);
                    });
                }

                if (electricService.getMeterBase().getElectricMeter().getConfiguredReadingTypes() != null) {
                    electricService.getMeterBase().getElectricMeter().getConfiguredReadingTypes().getConfiguredReadingType().forEach(
                        configuredReadingType -> {
                            add("Configured Reading Type", configuredReadingType, true, info, userContext);
                        });
                }

                if (electricService.getMeterBase().getElectricMeter().getMeterCommAddress() != null) {
                    add("Communication Address",
                        electricService.getMeterBase().getElectricMeter().getMeterCommAddress(), true, info,
                        userContext);
                }

                if (electricService.getMeterBase().getElectricMeter() != null
                    && electricService.getMeterBase().getElectricMeter().getElectricNameplate() != null) {
                    add(null, "Nameplate Information", true, info, userContext);
                    add("kh", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getKh(), true, info,
                        userContext);
                    add("kr", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getKr(), true, info,
                        userContext);
                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getFrequency() != null
                        && electricService.getMeterBase().getElectricMeter().getElectricNameplate().getFrequency().getUnits() != null) {
                        add("Frequency",
                            electricService.getMeterBase().getElectricMeter().getElectricNameplate().getFrequency().getValue()
                                + StringUtils.SPACE
                                + electricService.getMeterBase().getElectricMeter().getElectricNameplate().getFrequency().getUnits().value(),
                            true, info, userContext);
                    }
                    add("Number of elements",
                        electricService.getMeterBase().getElectricMeter().getElectricNameplate().getNumberOfElements(), true,
                        info, userContext);
                    add("Base Type", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getBaseType(),
                        true, info, userContext);
                    add("Accuracy Class ",
                        electricService.getMeterBase().getElectricMeter().getElectricNameplate().getAccuracyClass(), true, info,
                        userContext);
//                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getElementsVoltage() != null
//                        && electricService.getMeterBase().getElectricMeter().getElectricNameplate().getElementsVoltage().getUnits() != null) {
//                        add("Element Voltage",
//                            electricService.getMeterBase().getElectricMeter().getElectricNameplate().getElementsVoltage().getValue()
//                                + StringUtils.SPACE
//                                + electricService.getMeterBase().getElectricMeter().getElectricNameplate().getElementsVoltage().getUnits().value(),
//                            true, info, userContext);
//                    }
//                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getSupplyVoltage() != null
//                        && electricService.getMeterBase().getElectricMeter().getElectricNameplate().getSupplyVoltage().getUnits() != null) {
//                        add("Supply Voltage",
//                            electricService.getMeterBase().getElectricMeter().getElectricNameplate().getSupplyVoltage().getValue()
//                                + StringUtils.SPACE
//                                + electricService.getMeterBase().getElectricMeter().getElectricNameplate().getSupplyVoltage().getUnits().value(),
//                            true, info, userContext);
//                    }
//                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getMaxAmperage() != null) {
//                        add("Max Amp",
//                            electricService.getMeterBase().getElectricMeter().getElectricNameplate().getMaxAmperage()
//                                + StringUtils.SPACE
//                                + electricService.getMeterBase().getElectricMeter().getElectricNameplate().getMaxAmperage().getUnits().value(),
//                            true, info, userContext);
//                    }
//                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getTestAmperage() != null
//                        && electricService.getMeterBase().getElectricMeter().getElectricNameplate().getTestAmperage().getUnits() != null) {
//                        add("Test Amp",
//                            electricService.getMeterBase().getElectricMeter().getElectricNameplate().getTestAmperage().getValue()
//                                + StringUtils.SPACE
//                                + electricService.getMeterBase().getElectricMeter().getElectricNameplate().getTestAmperage().getUnits().value(),
//                            true, info, userContext);
//                    }
                    add("Register Ratio",
                        electricService.getMeterBase().getElectricMeter().getElectricNameplate().getRegRatio(), true, info,
                        userContext);
                    add("Phases", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getPhases(), true,
                        info, userContext);
                    add("Wire", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getWires(), true, info,
                        userContext);
                    add("Dials", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getDials(), true,
                        info, userContext);
                    add("Form", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getForm(), true, info,
                        userContext);
                    add("Multiplier", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getMultiplier(),
                        false, info, userContext);
                    add("Demand Multiplier",
                        electricService.getMeterBase().getElectricMeter().getElectricNameplate().getDemandMult(), false,
                        info, userContext);

                }
            

            add("Service Sub Type", electricService.getServiceSubType(), true, info, userContext);
            if (electricService.getOutageStatus() != null) {
                add("Outage Status", electricService.getOutageStatus().value(), true, info, userContext);
            }
            add("Cogeneration Site", electricService.isIsCogenerationSite(), true, info, userContext);
            //add("Service Order ID", electricService.getServiceOrderID(), true, info, userContext);

            add("Billing Cycle", electricService.getBillingCycle(), true, info, userContext);
            add("Route", electricService.getRoute(), true, info, userContext);
            add("Shutoff Date", electricService.getShutOffDate(), false, info, userContext);
            add("Connect Date", electricService.getConnectDate(), false, info, userContext);
            add("Disconnect Date", electricService.getDisconnectDate(), true, info, userContext);

            if (electricService.getServiceStatus() != null) {
                add("Service Point Status - Service Status",
                    electricService.getServiceStatus(), true, info,
                    userContext);
            }

            // Billing Information
            if (electricService.getMeterBase().getElectricMeter().getBillingStatusInformation() != null) {
                add(null, "Billing Information", true, info, userContext);
                makeBillingInformation(electricService.getMeterBase().getElectricMeter().getBillingStatusInformation(), info, userContext);
            }

            // Electric Location Fields
            add(null, "Utility Information", true, info, userContext);
            if (electricService.getElectricLocationFields() != null) {
                if (electricService.getElectricLocationFields() != null) {
                    add("Substation Ref - Code",
                        electricService.getElectricLocationFields().getSubstationCode(), false,
                        info, userContext);
                    add("Substation Ref - Name ",
                        electricService.getElectricLocationFields().getSubstationName(), false,
                        info, userContext);
                }
                if (electricService.getElectricLocationFields() != null) {
                    add("Feeder Ref - Code",
                        electricService.getElectricLocationFields().getFeederCode(),
                        true, info, userContext);
//                    add("Feeder Ref - Noun", electricService.getElectricLocationFields().getFeederRef().getNoun(),
//                        true, info, userContext);
                }

                add("Bus", electricService.getElectricLocationFields().getBus(), true, info, userContext);

                add("Phase Code", electricService.getElectricLocationFields().getPhaseCode(), true, info,
                    userContext);
                add("Linemen Service Area", electricService.getElectricLocationFields().getLinemenServiceArea(),
                    true, info, userContext);
                add("Pole Number", electricService.getElectricLocationFields().getPoleNo(), true, info,
                    userContext);

                add("Service Location ID", electricService.getServiceLocationID(),
                    false, info, userContext);
//                add("Service Point ID", electricService.getElectricLocationFields().getElectricServicePointID(),
//                    true, info, userContext);
//
//                if (electricService.getElectricLocationFields().getNetworkModelRef() != null) {
//                    add("Network Model Ref - Code",
//                        electricService.getElectricLocationFields().getNetworkModelRef().getPrimaryIdentifierValue(),
//                        true, info, userContext);
//                    add("Network Model Ref - Noun",
//                        electricService.getElectricLocationFields().getNetworkModelRef().getNoun(), true, info,
//                        userContext);
//
//                }
            }

            // Electric Rating
//            if (electricService.getElectricalRatings() != null) {
//                if (electricService.getElectricalRatings().getRatedCurrent() != null
//                    && electricService.getElectricalRatings().getRatedCurrent().getUnits() != null) {
//                    add("Rated Current", electricService.getElectricalRatings().getRatedCurrent().getValue()
//                        + StringUtils.SPACE
//                        + electricService.getElectricalRatings().getRatedCurrent().getUnits().value(), true,
//                        info, userContext);
//                }
//                if (electricService.getElectricalRatings().getRatedVoltage() != null
//                    && electricService.getElectricalRatings().getRatedVoltage().getUnits() != null) {
//                    add("Rated Voltage", electricService.getElectricalRatings().getRatedVoltage().getValue()
//                        + StringUtils.SPACE
//                        + electricService.getElectricalRatings().getRatedVoltage().getUnits().value(), true,
//                        info, userContext);
//                }
//                if (electricService.getElectricalRatings().getRatedPower() != null
//                    && electricService.getElectricalRatings().getRatedPower().getUnits() != null) {
//                    add("Rated Power", electricService.getElectricalRatings().getRatedPower().getValue()
//                        + StringUtils.SPACE
//                        + electricService.getElectricalRatings().getRatedPower().getUnits().value(), true, info,
//                        userContext);
//                }
//            }
        });
        return info;
    }

    // Service Location - Gas Service Point
    private List<Info> getGasServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();

        mspServLoc.getGasServiceList().getGasService().forEach(
            gasServicePoint -> {
                add(null, "Meter Information", true, info, userContext);
                add("Gas Meter ID", gasServicePoint.getGasMeterID(), true, info, userContext);
                add("Service Order ID", mspServLoc.getServiceOrderList().getServiceOrder().get(0).getObjectID(), true, info, userContext);
                add("Revenue Class", gasServicePoint.getRevenueClass(), true, info, userContext);
                add("Rate Code", gasServicePoint.getRateCode(), true, info, userContext);
                add("Service Sub Type", gasServicePoint.getServiceSubType(), true, info, userContext);
                if (gasServicePoint.getServiceStatus() != null) {
                    add("Service Point Status - Service Status",
                        gasServicePoint.getServiceStatus(), true, info, userContext);
//                    add("Service Point Status - Account Status",
//                        gasServicePoint..getAccountStatus(), true, info, userContext);
                    add("Service Point Status - Connectivity Status",
                        gasServicePoint.getGasMeter().getMeterConnectionStatus().value(), true, info,
                        userContext);
                }
                // Billing Information
                add(null, "Billing Information", true, info, userContext);
                if (gasServicePoint.getGasMeter().getBillingStatusInformation() != null) {
                    makeBillingInformation(gasServicePoint.getGasMeter().getBillingStatusInformation(), info, userContext);
                }
                add("Billing Cycle", gasServicePoint.getBillingCycle(), true, info, userContext);
                add("Route", gasServicePoint.getRoute(), true, info, userContext);
                add("Buget Bill", gasServicePoint.getBudgetBill(), true, info, userContext);
                add("Shutoff Date", gasServicePoint.getShutOffDate(), true, info, userContext);
                add("Connect Date", gasServicePoint.getConnectDate(), true, info, userContext);
                add("Disconnect Date", gasServicePoint.getDisconnectDate(), true, info, userContext);

                // Gas Meter
                if (gasServicePoint.getGasMeter() != null && gasServicePoint.getGasMeter().getGasNameplate() != null) {
                    add(null, "Nameplate Information", true, info, userContext);
                    if (gasServicePoint.getGasMeter().getGasNameplate().getMechanicalForm() != null) {
                        add("Mechanical Form",
                            gasServicePoint.getGasMeter().getGasNameplate().getMechanicalForm().value(), true, info,
                            userContext);
                    }
                    if (gasServicePoint.getGasMeter().getGasNameplate().getMeasurementSystem() != null) {
                        add("Measurement System",
                            gasServicePoint.getGasMeter().getGasNameplate().getMeasurementSystem().value(), true,
                            info, userContext);
                    }
                    if (gasServicePoint.getGasMeter().getGasNameplate().getGasPressure() != null
                        && gasServicePoint.getGasMeter().getGasNameplate().getGasPressure().getMaxPressureUOM() != null) {
                        add("Gas Pressure",
                            gasServicePoint.getGasMeter().getGasNameplate().getGasPressure().getValue()
                                + StringUtils.SPACE
                                + gasServicePoint.getGasMeter().getGasNameplate().getGasPressure().getMaxPressureUOM().value(),
                            true, info, userContext);
                    }
                    if (gasServicePoint.getGasMeter().getGasNameplate().getGasFlow() != null
                        && gasServicePoint.getGasMeter().getGasNameplate().getGasFlow().getMaxFlowRateUOM() != null) {
                        add("Gas Flow", gasServicePoint.getGasMeter().getGasNameplate().getGasFlow().getValue()
                            + StringUtils.SPACE
                            + gasServicePoint.getGasMeter().getGasNameplate().getGasFlow().getMaxFlowRateUOM().value(),
                            true, info, userContext);
                    }
                    if (gasServicePoint.getGasMeter().getGasNameplate().getGearDriveSize() != null) {
                        add("Gas Drive Size",
                            gasServicePoint.getGasMeter().getGasNameplate().getGearDriveSize().value(), true, info,
                            userContext);
                        add("Gas Internal Pipe Diameter",
                            gasServicePoint.getGasMeter().getGasNameplate().getInternalPipeDiameter().value(), true, info,
                            userContext);
                        add("Gas Temperature Compensation Type",
                            gasServicePoint.getGasMeter().getGasNameplate().getTemperatureCompensationType().value(), true, info,
                            userContext);
                        add("Gas Pressure Compensation Type",
                            gasServicePoint.getGasMeter().getGasNameplate().getPressureCompensationType().value(), true, info,
                            userContext);
                    }

                }

            });
        return info;
    }

    // Service Location - Propane Service Point
//    private List<Info> getPropaneServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {
//
//        List<Info> info = new ArrayList<>();
//
//        mspServLoc.getPropaneServicePoints().getPropaneServicePoint().forEach(
//            propaneServicePoint -> {
//                add(null, "Meter Information", true, info, userContext);
//                add("Propane Meter ID", propaneServicePoint.getPropaneMeterID(), true, info, userContext);
//                add("Service Order ID", propaneServicePoint.getServiceOrderID(), true, info, userContext);
//                add("Revenue Class", propaneServicePoint.getRevenueClass(), true, info, userContext);
//                add("Rate Code", propaneServicePoint.getRateCode(), true, info, userContext);
//                add("Service Sub Type", propaneServicePoint.getServiceSubType(), true, info, userContext);
//
//                if (propaneServicePoint.getServicePointStatus() != null
//                    && propaneServicePoint.getServicePointStatus().getServiceStatus() != null) {
//                    add("Service Point Status - Service Status",
//                        propaneServicePoint.getServicePointStatus().getServiceStatus().getValue(), true, info,
//                        userContext);
//                    add("Service Point Status - Account Status",
//                        propaneServicePoint.getServicePointStatus().getAccountStatus().getValue(), true, info,
//                        userContext);
//                    add("Service Point Status - Connectivity Status",
//                        propaneServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), true, info,
//                        userContext);
//                }
//                // Billing Information
//                add(null, "Billing Information", true, info, userContext);
//                if (propaneServicePoint.getBillingStatusInformation() != null) {
//                    makeBillingInformation(propaneServicePoint.getBillingStatusInformation(), info, userContext);
//                }
//                add("Billing Cycle", propaneServicePoint.getBillingCycle(), true, info, userContext);
//                add("Route", propaneServicePoint.getRoute(), true, info, userContext);
//                add("Buget Bill", propaneServicePoint.getBudgetBill(), true, info, userContext);
//                add("Shutoff Date", propaneServicePoint.getShutOffDate(), true, info, userContext);
//                add("Connect Date", propaneServicePoint.getConnectDate(), true, info, userContext);
//                add("Disconnect Date", propaneServicePoint.getDisconnectDate(), true, info, userContext);
//
//                // Gas Meter
//                if (propaneServicePoint.getPropaneMeter() != null
//                    && propaneServicePoint.getPropaneMeter().getPropaneNameplate() != null) {
//                    add(null, "Nameplate Information", true, info, userContext);
//                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMechanicalForm() != null) {
//                        add("Mechanical Form",
//                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMechanicalForm().value(),
//                            true, info, userContext);
//                    }
//                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMeasurementSystem() != null) {
//                        add("Measurement System",
//                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMeasurementSystem().value(),
//                            true, info, userContext);
//                    }
//                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure() != null
//                        && propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getMaxPressureUOM() != null) {
//                        add("Gas Pressure",
//                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getValue()
//                                + StringUtils.SPACE
//                                + propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getMaxPressureUOM().value(),
//                            true, info, userContext);
//                    }
//                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow() != null
//                        && propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getMaxFlowRateUOM() != null) {
//                        add("Gas Flow",
//                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getValue()
//                                + StringUtils.SPACE
//                                + propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getMaxFlowRateUOM().value(),
//                            true, info, userContext);
//                    }
//                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGearDriveSize() != null) {
//                        add("Gas Drive Size",
//                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGearDriveSize().value(),
//                            true, info, userContext);
//                    }
//                    add("Gas Internal Pipe Diameter",
//                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getInternalPipeDiameter().value(), true,
//                        info, userContext);
//                    add("Gas Temperature Compensation Type",
//                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getTemperatureCompensationType().value(),
//                        true, info, userContext);
//                    add("Gas Pressure Compensation Type",
//                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getPressureCompensationType().value(),
//                        true, info, userContext);
//                }
//
//            });
//        return info;
//    }

    // Service Location - Water Service Point
    private List<Info> getWaterServiceListInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();
        
        mspServLoc.getWaterServiceList().getWaterService().forEach(
            waterServicePoint -> {
                add(null, "Meter Information", true, info, userContext);
                add("Meter ID", waterServicePoint.getWaterMeterID(), true, info, userContext);
                add("Service Order ID", waterServicePoint.getObjectID(), true, info, userContext);
                add("Revenue Class", waterServicePoint.getRevenueClass(), true, info, userContext);
                add("Rate Code", waterServicePoint.getRateCode(), true, info, userContext);
                add("Service Sub Type", waterServicePoint.getServiceSubType(), true, info, userContext);

                // Billing Information
                add(null, "Billing Information", true, info, userContext);
                ArrayOfBillingStatusItem billingStatusInformation = waterServicePoint.getWaterMeter().getBillingStatusInformation();
                if (billingStatusInformation != null) {
                    makeBillingInformation(billingStatusInformation, info, userContext);
                }
                add("Billing Cycle", waterServicePoint.getBillingCycle(), true, info, userContext);
                add("Route", waterServicePoint.getRoute(), true, info, userContext);
                add("Budget Bill", waterServicePoint.getBudgetbill(), true, info, userContext);
                add("Shutoff Date", waterServicePoint.getShutOffDate(), true, info, userContext);
                add("Connect Date", waterServicePoint.getConnectDate(), true, info, userContext);
                add("Disconnect Date", waterServicePoint.getDisconnectDate(), true, info, userContext);

                // Water Meter
                if (waterServicePoint.getWaterMeter() != null
                    && waterServicePoint.getWaterMeter().getWaterNameplate() != null) {
                    add(null, "Nameplate Information", true, info, userContext);
                    if (waterServicePoint.getWaterMeter().getWaterNameplate().getInstallType() != null) {
                        add("Install Type",
                            waterServicePoint.getWaterMeter().getWaterNameplate().getInstallType().value(), true,
                            info, userContext);
                    }
                    if (waterServicePoint.getWaterMeter().getWaterNameplate().getFluidType() != null) {
                        add("Fluid Type", waterServicePoint.getWaterMeter().getWaterNameplate().getFluidType().value(),
                            true, info, userContext);
                    }
                    if (waterServicePoint.getWaterMeter().getWaterNameplate().getDriveType() != null) {
                        add("Drive Type", waterServicePoint.getWaterMeter().getWaterNameplate().getDriveType().value(),
                            true, info, userContext);
                    }
                    add("Pipe Size", waterServicePoint.getWaterMeter().getWaterNameplate().getPipeSize().value(), true, info,
                        userContext);
                }

            });

        return info;
    }

    private void makeBillingInformation(ArrayOfBillingStatusItem billingStatusItem, List<Info> info,
            YukonUserContext userContext) {

        billingStatusItem.getBillingStatusItem().forEach(
            billingItem -> {
                add("Account Receivable", billingItem.getAccountsReceivable(), true, info, userContext);
                add("Account Receivable this Period", billingItem.getAccountsReceivableThisPeriod(), true, info,
                    userContext);
                if (billingItem.getBillingItemsType() != null) {
                    add("Billing Type", billingItem.getBillingItemsType(), true, info, userContext);
                }
            });
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
                MspAccountInformationInfo accountInfo =
                    Arrays.asList(MspAccountInformationInfo.values()).stream().filter(
                        objType -> objType.isInstance(value)).findFirst().orElse(MspAccountInformationInfo.OTHER);

                if (MspAccountInformationInfo.DATE.isInstance(value)) {
                    this.value = formatDate((Date) value, userContext);
                } else if (MspAccountInformationInfo.CALENDAR.isInstance(value)) {
                    this.value = formatDate(((Calendar) value).getTime(), userContext);
                } else if (MspAccountInformationInfo.XMLGREGORIANCALENDAR.isInstance(value)) {
                    this.value = dateFormattingService.format(value, DateFormattingService.DateFormatEnum.DATE, userContext);
                } else if (MspAccountInformationInfo.BOOLEAN.isInstance(value)) {
                    this.value = MspAccountInformationInfo.BOOLEAN.getValue(value);
                } else {
                    this.value = accountInfo.getValue(value);
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
            return StringUtils.isBlank(value);
        }
    }
}
