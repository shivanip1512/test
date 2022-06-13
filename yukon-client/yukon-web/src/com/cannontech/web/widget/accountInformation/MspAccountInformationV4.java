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
import com.cannontech.msp.beans.v4.ElectricLocationFields;
import com.cannontech.msp.beans.v4.ElectricMeter;
import com.cannontech.msp.beans.v4.ElectricNameplate;
import com.cannontech.msp.beans.v4.GasNameplate;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.Network;
import com.cannontech.msp.beans.v4.PhoneType;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.msp.beans.v4.WaterNameplate;
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
            if(CollectionUtils.isNotEmpty(custAddressInfo)) {
                mav.addObject("custAddressInfo", custAddressInfo.get(0));
                }
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
        if (mspServLoc != null) {
            List<Info> servLocBasicsInfo = getServLocBasicsInfo(mspServLoc, userContext);
            mav.addObject("servLocBasicsInfo", servLocBasicsInfo);
            
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
            if (contactInfo.getAddressList() != null && contactInfo.getAddressList().getAddressItem() != null) {
                contact.setAddresses(multispeakFuncs.getAddressList(contactInfo.getAddressList().getAddressItem()));
            }
            info.add(contact);
        }
        return info;

    }

    // Customer Account Information
    private List<Info> getCustomerAccountInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();
        if(mspCustomer.getAccounts() != null && mspCustomer.getAccounts().getAccount() !=null) {
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
        };
        return info;
    }

    // Customer Account Receivable Information
    private Map<String, List<Info>> getCustomerAccountReceivableInfo(Customer mspCustomer, YukonUserContext userContext) {

        Map<String, List<Info>> customerAccountReceivableMap = new HashMap<>();
        if (mspCustomer.getAccounts() != null && mspCustomer.getAccounts().getAccount() != null) {
            mspCustomer.getAccounts().getAccount().forEach(
                    accountInfo -> {
                        if (accountInfo.getAccountsReceivable() != null
                                && accountInfo.getAccountsReceivable().getAcctsReceivable() != null) {
                            List<Info> info = new ArrayList<>();
                            accountInfo.getAccountsReceivable().getAcctsReceivable().forEach(
                                    acctsReceivable -> {
                                        add("Receivable Amount", acctsReceivable.getReceivableAmount().getValue(), true, info,
                                                userContext);
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
        };
        return customerAccountReceivableMap;
    }

    // Get the customer alternate contact information
    private List<Contact> getCustomerAlternateContactsInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Contact> info = new ArrayList<>();

        mspCustomer.getAlternateContactList().getAlternateContact().forEach(
                alternateContact -> {
                    ContactInfo contactInfo = alternateContact.getContactInfo();

                    List<String> phoneNumbers = multispeakCustomerInfoService.getPhoneNumbers(contactInfo, userContext);
                    List<String> emailAddresses = multispeakCustomerInfoService.getEmailAddresses(contactInfo, userContext);
                    Contact contact = new Contact(alternateContact.getFirstName(), alternateContact.getMName(),
                            alternateContact.getLastName(), phoneNumbers, emailAddresses,
                            contactInfo.getAddressList() == null ? null : (contactInfo.getAddressList()
                                    .getAddressItem() != null ? multispeakFuncs
                                            .getAddressList(contactInfo.getAddressList().getAddressItem()) : null));
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
        
        if (mspServLoc.getServiceOrderList() != null && 
                CollectionUtils.isNotEmpty(mspServLoc.getServiceOrderList().getServiceOrder()) && 
                mspServLoc.getServiceOrderList().getServiceOrder().get(0) != null) {
            
            add("ServiceOrder ID", mspServLoc.getServiceOrderList().getServiceOrder().get(0).getObjectID(),
                    false, infoList,
                    userContext);
        }
        
        add("GPS Location ", mspServLoc.getGPSLocation(), false, infoList, userContext);
        add("GML Location ", mspServLoc.getGMLLocation(), false, infoList, userContext);
        add("Grid Location ", mspServLoc.getGridLocation(), false, infoList, userContext);
        if (mspServLoc.getRotation() != null) {
            add("Rotation ", mspServLoc.getRotation(), true, infoList, userContext);
        }
        // Location Information
        Network network = mspServLoc.getNetwork();
        if (network != null) {
            add("City ", network.getCity(), true, infoList, userContext);
            add("County ", network.getCountry(), true, infoList, userContext);
            add("Township Name ", network.getTownship(), true, infoList, userContext);
            add("Sub Division ", network.getSubdivision(), true, infoList, userContext);
            add("Block ", network.getBlock(), true, infoList, userContext);
            add("Lot ", network.getLot(), true, infoList, userContext);
            add("State ", network.getState(), true, infoList, userContext);

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
                
                
            ElectricMeter electricMeter = electricService.getMeterBase().getElectricMeter();
            if (electricService.getMeterBase() != null && electricMeter != null) {
                if (electricMeter.getMeterConnectionStatus() != null) {
                    add("Connection Status",
                            electricMeter.getMeterConnectionStatus(), true, info,
                            userContext);

                    add("Metrology Firmware Version",
                            electricMeter.getMetrologyFirmwareVersion(), true, info,
                            userContext);
                    add("Metrology Firmware Revision",
                            electricMeter.getMetrologyFirmwareRevision(), true, info,
                            userContext);
                    add("AMI Device Type", electricMeter.getAMRDeviceType(), false, info,
                            userContext);
                    add("AMI Vendor", electricMeter.getAMRVendor(), false, info, userContext);
                    add("Billing Cycle", electricMeter.getBillingCycle(), true, info,
                            userContext);
                }
                if (electricMeter.getUtilityInfo() != null) {
                    add("Owner", electricMeter.getUtilityInfo().getOwner(), true, info,
                            userContext);
                    if (electricMeter.getUtilityInfo().getServiceID() != null) {
                        add("Service Point Value",
                                electricMeter.getUtilityInfo().getServiceID().getValue(),
                                true, info, userContext);
                        add("Service Point Type",
                                electricMeter.getUtilityInfo().getServiceID()
                                        .getServiceType(),
                                true, info, userContext);
                    }

                }
                if (electricMeter.getParentMeterList() != null && electricMeter.getParentMeterList().getMeterID() != null) {
                    electricMeter.getParentMeterList().getMeterID().forEach(meterID -> {
                        add("Parent Meter", meterID, true, info, userContext);
                    });
                }
                if (electricMeter.getSubMeterList() != null && electricMeter.getSubMeterList().getMeterID() != null) {
                    electricMeter.getSubMeterList().getMeterID().forEach(meterID -> {
                        add("Sub Meter", meterID, true, info, userContext);
                    });
                }
                if (electricMeter.getModuleList() != null && electricMeter.getModuleList().getModule() != null) {
                    electricMeter.getModuleList().getModule().forEach(module -> {
                        add("Module", module.getDescription(), true, info, userContext);
                    });
                }

                if (electricMeter.getConfiguredReadingTypes() != null
                        && electricMeter.getConfiguredReadingTypes().getConfiguredReadingType() != null) {
                    electricMeter.getConfiguredReadingTypes().getConfiguredReadingType()
                            .forEach(
                                    configuredReadingType -> {
                                        add("Configured Reading Type", configuredReadingType, true, info, userContext);
                                    });
                }

                if (electricMeter.getMeterCommAddress() != null) {
                    add("Communication Address",
                            electricMeter.getMeterCommAddress(), true, info,
                            userContext);
                }

                ElectricNameplate electricNameplate = electricMeter.getElectricNameplate();
                if (electricMeter != null
                        && electricNameplate != null) {
                    add(null, "Nameplate Information", true, info, userContext);
                    add("kh", electricNameplate.getKh(), true, info,
                            userContext);
                    add("kr", electricNameplate.getKr(), true, info,
                            userContext);
                    if (electricNameplate.getFrequency() != null
                            && electricNameplate.getFrequency()
                                    .getUnits() != null) {
                        add("Frequency",
                                electricNameplate.getFrequency().getValue()
                                        + StringUtils.SPACE
                                        + electricNameplate.getFrequency()
                                                .getUnits().value(),
                                true, info, userContext);
                    }
                    add("Number of elements",
                            electricNameplate.getNumberOfElements(), true,
                            info, userContext);
                    add("Base Type", electricNameplate.getBaseType(),
                            true, info, userContext);
                    add("Accuracy Class ",
                            electricNameplate.getAccuracyClass(), true,
                            info,
                            userContext);

                    if (electricNameplate.getElementsVoltage() != null) {
                        add("Element Voltage",
                                electricNameplate.getElementsVoltage(),
                                true, info, userContext);
                    }

                    if (electricNameplate.getSupplyVoltage() != null) {
                        add("Supply Voltage",
                                electricNameplate.getSupplyVoltage(),
                                true, info, userContext);
                    }
                    if (electricNameplate.getMaxAmperage() != null) {
                        add("Max Amp",
                                electricNameplate.getMaxAmperage(),
                                true, info, userContext);
                    }
                    if (electricNameplate.getTestAmperage() != null) {
                        add("Test Amp",
                                electricNameplate.getTestAmperage(),
                                true, info, userContext);
                    }
                    add("Register Ratio",
                            electricNameplate.getRegRatio(), true, info,
                            userContext);
                    add("Phases", electricNameplate.getPhases(), true,
                            info, userContext);
                    add("Wire", electricNameplate.getWires(), true, info,
                            userContext);
                    add("Dials", electricNameplate.getDials(), true,
                            info, userContext);
                    add("Form", electricNameplate.getForm(), true, info,
                            userContext);
                    add("Multiplier", electricNameplate.getMultiplier(),
                            false, info, userContext);
                    add("Demand Multiplier",
                            electricNameplate.getDemandMult(), false,
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
            if (electricMeter.getBillingStatusInformation() != null) {
                add(null, "Billing Information", true, info, userContext);
                makeBillingInformation(electricMeter.getBillingStatusInformation(), info, userContext);
            }

            // Electric Location Fields
            add(null, "Utility Information", true, info, userContext);
            ElectricLocationFields electricLocationFields = electricService.getElectricLocationFields();
            if (electricLocationFields != null) {
                add("Substation Ref - Code", electricLocationFields.getSubstationCode(), false,
                        info, userContext);
                add("Substation Ref - Name ", electricLocationFields.getSubstationName(), false,
                        info, userContext);
                add("Feeder Ref - Code", electricLocationFields.getFeederCode(),
                        true, info, userContext);

                add("Bus", electricLocationFields.getBus(), true, info, userContext);

                add("Phase Code", electricLocationFields.getPhaseCode(), true, info,
                        userContext);
                add("Linemen Service Area", electricLocationFields.getLinemenServiceArea(),
                        true, info, userContext);
                add("Pole Number", electricLocationFields.getPoleNo(), true, info,
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
                GasNameplate gasNameplate = gasServicePoint.getGasMeter().getGasNameplate();
                if (gasServicePoint.getGasMeter() != null && gasNameplate != null) {
                    add(null, "Nameplate Information", true, info, userContext);
                    if (gasNameplate.getMechanicalForm() != null) {
                        add("Mechanical Form",
                            gasNameplate.getMechanicalForm().value(), true, info,
                            userContext);
                    }
                    if (gasNameplate.getMeasurementSystem() != null) {
                        add("Measurement System",
                            gasNameplate.getMeasurementSystem().value(), true,
                            info, userContext);
                    }
                    if (gasNameplate.getGasPressure() != null
                        && gasNameplate.getGasPressure().getMaxPressureUOM() != null) {
                        add("Gas Pressure",
                            gasNameplate.getGasPressure().getValue()
                                + StringUtils.SPACE
                                + gasNameplate.getGasPressure().getMaxPressureUOM().value(),
                            true, info, userContext);
                    }
                    if (gasNameplate.getGasFlow() != null
                        && gasNameplate.getGasFlow().getMaxFlowRateUOM() != null) {
                        add("Gas Flow", gasNameplate.getGasFlow().getValue()
                            + StringUtils.SPACE
                            + gasNameplate.getGasFlow().getMaxFlowRateUOM().value(),
                            true, info, userContext);
                    }
                    if (gasNameplate.getGearDriveSize() != null) {
                        add("Gas Drive Size",
                            gasNameplate.getGearDriveSize().value(), true, info,
                            userContext);
                        add("Gas Internal Pipe Diameter",
                            gasNameplate.getInternalPipeDiameter().value(), true, info,
                            userContext);
                        add("Gas Temperature Compensation Type",
                            gasNameplate.getTemperatureCompensationType().value(), true, info,
                            userContext);
                        add("Gas Pressure Compensation Type",
                            gasNameplate.getPressureCompensationType().value(), true, info,
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
                    WaterNameplate fetchWaterNamplate = waterServicePoint.getWaterMeter().getWaterNameplate();
                    add(null, "Nameplate Information", true, info, userContext);
                    if (fetchWaterNamplate.getInstallType() != null) {
                        add("Install Type",
                                fetchWaterNamplate.getInstallType().value(), true,
                            info, userContext);
                    }
                    if (fetchWaterNamplate.getFluidType() != null) {
                        add("Fluid Type", fetchWaterNamplate.getFluidType().value(),
                            true, info, userContext);
                    }
                    if (waterServicePoint.getWaterMeter().getWaterNameplate().getDriveType() != null) {
                        add("Drive Type", fetchWaterNamplate.getDriveType().value(),
                            true, info, userContext);
                    }
                    if(fetchWaterNamplate.getPipeSize() != null) {
                        add("Pipe Size", fetchWaterNamplate.getPipeSize().value(), true, info,
                                userContext);
                    }
                    
                }

            });

        return info;
    }

    private void makeBillingInformation(ArrayOfBillingStatusItem billingStatusItem, List<Info> info,
            YukonUserContext userContext) {
        if(billingStatusItem.getBillingStatusItem() != null) {
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
