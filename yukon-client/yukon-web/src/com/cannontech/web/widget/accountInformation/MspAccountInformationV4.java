package com.cannontech.web.widget.accountInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
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
        ServiceLocation mspServLoc = mspObjectDao.getServiceLocationByMeterNo(meterNumber, mspVendor);
        Meters mspMeter = mspObjectDao.getMspMeter(meter, mspVendor);
        
        mav.addObject("mspCustomer", mspCustomer);
        mav.addObject("mspServLoc", mspServLoc);
        if (mspServLoc != null && mspServLoc.getContactInfo() != null && mspServLoc.getContactInfo().getAddressList() != null
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
        if (mspCustomer.getAlternateContactList() != null && mspCustomer.getAlternateContactList().getAlternateContact() != null) {
            List<Contact> custAlternateContactInfo = getCustomerAlternateContactsInfo(mspCustomer, userContext);
            if (custAlternateContactInfo != null) {
                mav.addObject("custAlternateContactInfo", custAlternateContactInfo);
            }
        }

        // Service Location Information
        List<Info> servLocBasicsInfo = getServLocBasicsInfo(mspServLoc, userContext);
        mav.addObject("servLocBasicsInfo", servLocBasicsInfo);
        
        if (mspServLoc != null) {
            if (mspServLoc.getContactInfo() != null && mspServLoc.getContactInfo().getAddressList() != null
                    && mspServLoc.getContactInfo().getAddressList().getAddressItem() != null) {
                List<Address> servLocAddresses = multispeakFuncs
                        .getAddressList(mspServLoc.getContactInfo().getAddressList().getAddressItem());
                mav.addObject("servLocAddressesList", servLocAddresses);
            }

            if (mspServLoc.getElectricServiceList() != null && mspServLoc.getElectricServiceList().getElectricService() != null) {
                List<Info> electricServiceListInfo = getElectricServiceListInfo(mspServLoc, userContext);
                mav.addObject("electricServicePointsInfo", electricServiceListInfo);
            }

            if (mspServLoc.getGasServiceList() != null && mspServLoc.getGasServiceList().getGasService() != null) {
                List<Info> gasServiceListInfo = getGasServicePointInfo(mspServLoc, userContext);
                mav.addObject("gasServiceListInfo", gasServiceListInfo);
            }

            if (mspServLoc.getWaterServiceList() != null && mspServLoc.getWaterServiceList().getWaterService() != null) {
                List<Info> waterServiceListInfo = getWaterServiceListInfo(mspServLoc, userContext);
                mav.addObject("waterServiceListInfo", waterServiceListInfo);
            }
        }
        return mav;
    }

    // Customer Basic Information
    private List<Info> getCustomerBasicsInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Info> infoList = new ArrayList<Info>();

        Map<PhoneType, String> primaryContact = multispeakFuncs.getPrimaryContacts(mspCustomer);

        add("Object ID", mspCustomer.getObjectID(), false, infoList, userContext);
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
                add("Last Payment Amount", accountInfo.getLastPaymentAmount().getValue(), true, info, userContext);
                add("Bill Date", accountInfo.getBillDate(), true, info, userContext);
                add("Shutoff Date", accountInfo.getShutOffDate(), true, info, userContext);
                add("Prepay", accountInfo.isIsPrePay(), true, info, userContext);
                add("Billing Term", accountInfo.getBillingTerms(), true, info, userContext);
                add("Calculated Current Bill Amount", accountInfo.getCalculatedCurrentBillAmount().getValue(), true, info,
                    userContext);
                add("Calculated Current Bill Date", accountInfo.getCalculatedCurrentBillDateTime(), true, info,
                    userContext);
                add("Last Bill Amount", accountInfo.getLastBillAmount().getValue(), true, info, userContext);
                add("Calculated Used Yesterday", accountInfo.getCalculatedUsedYesterday().getValue(), true, info, userContext);

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
                            add("Receivable Amount", acctsReceivable.getReceivableAmount().getValue(), true, info, userContext);
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
                
                List<String> phoneNumbers =
                    multispeakCustomerInfoService.getPhoneNumbers(contactInfo, userContext);
                List<String> emailAddresses =
                    multispeakCustomerInfoService.getEmailAddresses(contactInfo, userContext);
                Contact contact =
                    new Contact(alternateContact.getFirstName(), alternateContact.getMName(),
                        alternateContact.getLastName(), phoneNumbers, emailAddresses,contactInfo.getAddressList() == null?null:
                        (contactInfo.getAddressList().getAddressItem() != null ? multispeakFuncs.getAddressList(contactInfo.getAddressList().getAddressItem()) : null));
                info.add(contact);

            });
        return info;
    }

    // Service location basic information
    private List<Info> getServLocBasicsInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> infoList = new ArrayList<Info>();

        add("Object ID", mspServLoc.getObjectID(), false, infoList, userContext);
        add("Description", mspServLoc.getDescription(), true, infoList, userContext);
        add("Facility ID", mspServLoc.getFacilityID(), false, infoList, userContext);
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

        }

        return infoList;
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
                
                if (electricService.getAccountNumber() != null) {
                    add("meterAccNo", electricService.getAccountNumber(), false,
                        info, userContext);
                }
                
                add("Meter Base ID", electricService.getMeterBaseID(), false, info, userContext);
                
                
            if (electricService.getMeterBase() != null && electricService.getMeterBase().getElectricMeter() != null) {
                if (electricService.getMeterBase() != null && electricService.getMeterBase().getElectricMeter() != null
                        && electricService.getMeterBase().getElectricMeter().getMeterConnectionStatus() != null) {
                    add("Connection Status",
                            electricService.getMeterBase().getElectricMeter().getMeterConnectionStatus(), true, info,
                            userContext);

                    add("Metrology Firmware Version",
                            electricService.getMeterBase().getElectricMeter().getMetrologyFirmwareVersion(), true, info,
                            userContext);
                    add("Metrology Firmware Revision",
                            electricService.getMeterBase().getElectricMeter().getMetrologyFirmwareRevision(), true, info,
                            userContext);
                    add("AMI Device Type", electricService.getMeterBase().getElectricMeter().getAMRDeviceType(), false, info,
                            userContext);
                    add("AMI Vendor", electricService.getMeterBase().getElectricMeter().getAMRVendor(), false, info, userContext);
                    add("Billing Cycle", electricService.getMeterBase().getElectricMeter().getBillingCycle(), true, info,
                            userContext);
                }
                if (electricService.getMeterBase().getElectricMeter().getUtilityInfo() != null) {
                    add("Owner", electricService.getMeterBase().getElectricMeter().getUtilityInfo().getOwner(), true, info,
                            userContext);
                    if (electricService.getMeterBase().getElectricMeter().getUtilityInfo().getServiceID() != null) {
                        add("Service Point Value",
                                electricService.getMeterBase().getElectricMeter().getUtilityInfo().getServiceID().getValue(),
                                true, info, userContext);
                        add("Service Point Type",
                                electricService.getMeterBase().getElectricMeter().getUtilityInfo().getServiceID()
                                        .getServiceType(),
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
                    electricService.getMeterBase().getElectricMeter().getConfiguredReadingTypes().getConfiguredReadingType()
                            .forEach(
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
                            && electricService.getMeterBase().getElectricMeter().getElectricNameplate().getFrequency()
                                    .getUnits() != null) {
                        add("Frequency",
                                electricService.getMeterBase().getElectricMeter().getElectricNameplate().getFrequency().getValue()
                                        + StringUtils.SPACE
                                        + electricService.getMeterBase().getElectricMeter().getElectricNameplate().getFrequency()
                                                .getUnits().value(),
                                true, info, userContext);
                    }
                    add("Number of elements",
                            electricService.getMeterBase().getElectricMeter().getElectricNameplate().getNumberOfElements(), true,
                            info, userContext);
                    add("Base Type", electricService.getMeterBase().getElectricMeter().getElectricNameplate().getBaseType(),
                            true, info, userContext);
                    add("Accuracy Class ",
                            electricService.getMeterBase().getElectricMeter().getElectricNameplate().getAccuracyClass(), true,
                            info,
                            userContext);

                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getElementsVoltage() != null) {
                        add("Element Voltage",
                                electricService.getMeterBase().getElectricMeter().getElectricNameplate().getElementsVoltage(),
                                true, info, userContext);
                    }

                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getSupplyVoltage() != null) {
                        add("Supply Voltage",
                                electricService.getMeterBase().getElectricMeter().getElectricNameplate().getSupplyVoltage(),
                                true, info, userContext);
                    }
                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getMaxAmperage() != null) {
                        add("Max Amp",
                                electricService.getMeterBase().getElectricMeter().getElectricNameplate().getMaxAmperage(),
                                true, info, userContext);
                    }
                    if (electricService.getMeterBase().getElectricMeter().getElectricNameplate().getTestAmperage() != null) {
                        add("Test Amp",
                                electricService.getMeterBase().getElectricMeter().getElectricNameplate().getTestAmperage(),
                                true, info, userContext);
                    }
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
            }
            

            add("Service Sub Type", electricService.getServiceSubType(), true, info, userContext);
            if (electricService.getOutageStatus() != null) {
                add("Outage Status", electricService.getOutageStatus().value(), true, info, userContext);
            }
            add("Cogeneration Site", electricService.isIsCogenerationSite(), true, info, userContext);
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
                }

                add("Bus", electricService.getElectricLocationFields().getBus(), true, info, userContext);

                add("Phase Code", electricService.getElectricLocationFields().getPhaseCode(), true, info,
                        userContext);
                add("Linemen Service Area", electricService.getElectricLocationFields().getLinemenServiceArea(),
                        true, info, userContext);
                add("Pole Number", electricService.getElectricLocationFields().getPoleNo(), true, info,
                        userContext);
            }
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
                add("Service Order ID", gasServicePoint.getObjectID(), true, info, userContext);
                add("Revenue Class", gasServicePoint.getRevenueClass(), true, info, userContext);
                add("Rate Code", gasServicePoint.getRateCode(), true, info, userContext);
                add("Service Sub Type", gasServicePoint.getServiceSubType(), true, info, userContext);
                add("Service Point Status - Connectivity Status", gasServicePoint.getServiceStatus(), true, info, userContext);
                
                // Billing Information
                add(null, "Billing Information", true, info, userContext);
                if (gasServicePoint.getGasMeter() != null && gasServicePoint.getGasMeter().getBillingStatusInformation() != null) {
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
                if(waterServicePoint.getWaterMeter() != null) {
                ArrayOfBillingStatusItem billingStatusInformation = waterServicePoint.getWaterMeter().getBillingStatusInformation();
                    if (billingStatusInformation != null) {
                        makeBillingInformation(billingStatusInformation, info, userContext);
                    }
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
                MspAccountInformationInfoV4 accountInfo =
                    Arrays.asList(MspAccountInformationInfoV4.values()).stream().filter(
                        objType -> objType.isInstance(value)).findFirst().orElse(MspAccountInformationInfoV4.OTHER);

                if (MspAccountInformationInfoV4.DATE.isInstance(value)) {
                    this.value = formatDate((Date) value, userContext);
                } else if (MspAccountInformationInfoV4.CALENDAR.isInstance(value)) {
                    this.value = formatDate(((Calendar) value).getTime(), userContext);
                } else if (MspAccountInformationInfoV4.XMLGREGORIANCALENDAR.isInstance(value)) {
                    this.value = dateFormattingService.format(value, DateFormattingService.DateFormatEnum.DATE, userContext);
                } else if (MspAccountInformationInfoV4.BOOLEAN.isInstance(value)) {
                    this.value = MspAccountInformationInfoV4.BOOLEAN.getValue(value);
                } else if (MspAccountInformationInfoV4.GPSLOCATION.isInstance(value)) {
                    this.value = MspAccountInformationInfoV4.GPSLOCATION.getValue(value);
                } else if (MspAccountInformationInfoV4.POINTTYPE.isInstance(value)) {
                    this.value = MspAccountInformationInfoV4.POINTTYPE.getValue(value);
                }else if (MspAccountInformationInfoV4.METERID.isInstance(value)) {
                    this.value = MspAccountInformationInfoV4.METERID.getValue(value);
                }else {
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
