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
import com.cannontech.msp.beans.v5.enumerations.PhoneTypeKind;
import com.cannontech.msp.beans.v5.multispeak.BillingStatusInformation;
import com.cannontech.msp.beans.v5.multispeak.ContactInfo;
import com.cannontech.msp.beans.v5.multispeak.Customer;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.service.v5.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;

public class MspAccountInformationV5 implements MspAccountInformation {

    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private MultispeakCustomerInfoService multispeakCustomerInfoService;
    @Autowired private MultispeakFuncs multispeakFuncs;

    public ModelAndView getMspInformation(YukonMeter meter, MultispeakVendor mspVendor, ModelAndView mav,
            YukonUserContext userContext) {

        Customer mspCustomer = mspObjectDao.getMspCustomer(meter, mspVendor);
        ServiceLocation mspServLoc = mspObjectDao.getMspServiceLocation(meter, mspVendor);

        mav.addObject("mspCustomer", mspCustomer);
        mav.addObject("mspServLoc", mspServLoc);
        if (mspServLoc != null && mspServLoc.getContactInfo() != null
            && mspServLoc.getContactInfo().getAddressItems() != null) {
            List<Address> servLocAddresses = multispeakFuncs.getAddressList(mspServLoc.getContactInfo().getAddressItems());
            mav.addObject("servLocAddresses", servLocAddresses.get(0));
        }
        Map<PhoneTypeKind, String> primaryContact = multispeakFuncs.getPrimaryContacts(mspCustomer);
        mav.addObject("homePhone", primaryContact.get(PhoneTypeKind.HOME));
        mav.addObject("dayPhone", primaryContact.get(PhoneTypeKind.BUSINESS));

        if (mspCustomer.getContactInfo() != null && mspCustomer.getContactInfo().getEMailAddresses() != null) {
            List<String> emailAddresses = multispeakCustomerInfoService.getEmailAddresses(
                mspCustomer.getContactInfo().getEMailAddresses(), userContext);
            mav.addObject("mspEmailAddresses", emailAddresses);
        }

        if (mspCustomer.getContactInfo() != null && mspCustomer.getContactInfo().getAddressItems() != null) {
            List<Address> custAddressInfo = multispeakFuncs.getAddressList(mspCustomer.getContactInfo().getAddressItems());
            mav.addObject("custAddressInfo", custAddressInfo.get(0));
        }
        // Customer Basic Information
        List<Info> custBasicsInfo = getCustomerBasicsInfo(mspCustomer, userContext);
        mav.addObject("custBasicsInfo", custBasicsInfo);

        List<Contact> custBasicContactInfo = getCustomerBasicContactInfo(mspCustomer, userContext);
        mav.addObject("custBasicContactInfo", custBasicContactInfo);

        if (mspCustomer.getCustomerHazards() != null) {
            List<Info> custHazardInfo = getCustomerHazardInfo(mspCustomer, userContext);
            mav.addObject("custHazardInfo", custHazardInfo);
        }
        if (mspCustomer.getAccounts() != null) {
            List<Info> custAccountInfo = getCustomerAccountInfo(mspCustomer, userContext);
            mav.addObject("custAccountInfo", custAccountInfo);

            Map<String, List<Info>> custAccountReceivableInfo =
                getCustomerAccountReceivableInfo(mspCustomer, userContext);
            mav.addObject("custReceivableInfo", custAccountReceivableInfo);

            Map<String, List<Info>> custAcountPriorityInfo = getCustomerAccountPriorityInfo(mspCustomer, userContext);
            mav.addObject("custPriorityInfo", custAcountPriorityInfo);

        }
        if (mspCustomer.getAlternateContacts() != null) {
            List<Contact> custAlternateContactInfo = getCustomerAlternateContactsInfo(mspCustomer, userContext);
            if (custAlternateContactInfo != null) {
                mav.addObject("custAlternateContactInfo", custAlternateContactInfo);
            }
        }

        // Service Location Information
        List<Info> servLocBasicsInfo = getServLocBasicsInfo(mspServLoc, userContext);
        mav.addObject("servLocBasicsInfo", servLocBasicsInfo);
        
        if (mspServLoc != null && mspServLoc.getContactInfo() != null
                && mspServLoc.getContactInfo().getAddressItems() != null) {
                List<Address> servLocAddresses = multispeakFuncs.getAddressList(mspServLoc.getContactInfo().getAddressItems());
                mav.addObject("servLocAddressesList", servLocAddresses);
        }

        if (mspServLoc.getServiceHazards() != null) {
            Map<AtomicInteger, List<Info>> servLocHazardInfo = getServiceLocationHazardInfo(mspServLoc, userContext);
            mav.addObject("servLocHazardInfo", servLocHazardInfo);
        }

        if (mspServLoc.getElectricServicePoints() != null) {
            List<Info> electricServicePointsInfo = getElectricServicePointInfo(mspServLoc, userContext);
            mav.addObject("electricServicePointsInfo", electricServicePointsInfo);
        }
        if (mspServLoc.getGasServicePoints() != null) {
            List<Info> gasServicePointsInfo = getGasServicePointInfo(mspServLoc, userContext);
            mav.addObject("gasServicePointsInfo", gasServicePointsInfo);
        }

        if (mspServLoc.getWaterServicePoints() != null) {
            List<Info> waterServicePointsInfo = getWaterServicePointInfo(mspServLoc, userContext);
            mav.addObject("waterServicePointsInfo", waterServicePointsInfo);
        }
        if (mspServLoc.getPropaneServicePoints() != null) {
            List<Info> propaneServicePointsInfo = getPropaneServicePointInfo(mspServLoc, userContext);
            mav.addObject("propaneServicePointsInfo", propaneServicePointsInfo);
        }

        return mav;
    }

    // Customer Basic Information
    private List<Info> getCustomerBasicsInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Info> infoList = new ArrayList<Info>();

        Map<PhoneTypeKind, String> primaryContact = multispeakFuncs.getPrimaryContacts(mspCustomer);

        add("Identifier", mspCustomer.getPrimaryIdentifier(), false, infoList, userContext);
        add("Last Name", mspCustomer.getLastName(), false, infoList, userContext);
        add("First Name", mspCustomer.getFirstName(), false, infoList, userContext);
        add("Middle Name", mspCustomer.getMiddleName(), true, infoList, userContext);
        add("DBA", mspCustomer.getDBAName(), false, infoList, userContext);
        add("Home Phone", primaryContact.get(PhoneTypeKind.HOME), true, infoList, userContext);
        add("Day Phone", primaryContact.get(PhoneTypeKind.BUSINESS), true, infoList, userContext);
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
                multispeakCustomerInfoService.getPhoneNumbers(contactInfo.getPhoneNumbers(), userContext);
            contact.setPhoneNumbers(phoneNumbers);

            List<String> emailAddresses =
                multispeakCustomerInfoService.getEmailAddresses(contactInfo.getEMailAddresses(), userContext);
            contact.setEmailAddresses(emailAddresses);
            if (contactInfo.getAddressItems() != null) {
                contact.setAddresses(multispeakFuncs.getAddressList(contactInfo.getAddressItems()));
            }
            info.add(contact);
        }
        return info;

    }

    // Customer Hazards Information
    private List<Info> getCustomerHazardInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();

        mspCustomer.getCustomerHazards().getCustomerHazard().forEach(customerHazard -> {
            add("Type", customerHazard.getCustomerHazardType(), true, info, userContext);
            add("Sub Type", customerHazard.getCustomerHazardSubType(), true, info, userContext);
            add("Text", customerHazard.getHazardText(), true, info, userContext);
        });
        return info;
    }

    // Customer Account Information
    private List<Info> getCustomerAccountInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();

        mspCustomer.getAccounts().getAccount().forEach(
            accountInfo -> {
                add("Account ID", accountInfo.getPrimaryIdentifier(), false, info, userContext);
                if (accountInfo.getAccountStatus() != null) {
                    add("Account Status", accountInfo.getAccountStatus().getValue(), true, info, userContext);
                }
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
                            add("Receivable Type", acctsReceivable.getReceivableType().getValue().value(), true, info,
                                userContext);
                            add("Description", acctsReceivable.getDescription(), true, info, userContext);
                            add("Due Date", acctsReceivable.getDueDate(), true, info, userContext);
                            if (acctsReceivable.getServiceType() != null) {
                                add("Service Type", acctsReceivable.getServiceType().getValue(), true, info,
                                    userContext);
                            }
                        });
                    customerAccountReceivableMap.put(accountInfo.getPrimaryIdentifier().getValue(), info);
                }
            });
        return customerAccountReceivableMap;
    }

    // Customer Account Priority Information
    private Map<String, List<Info>> getCustomerAccountPriorityInfo(Customer mspCustomer, YukonUserContext userContext) {

        Map<String, List<Info>> customerAccountPriorityMap = new HashMap<>();

        mspCustomer.getAccounts().getAccount().forEach(
            accountInfo -> {
                if (accountInfo.getAccountPriorities() != null) {
                    List<Info> info = new ArrayList<>();
                    accountInfo.getAccountPriorities().getAccountPriority().forEach(
                        servicePriority -> {
                            if (servicePriority.getServicePriorityType() != null) {
                                add("Priority Type", servicePriority.getServicePriorityType().getValue(), true, info,
                                    userContext);
                            }
                            add("Priority SubType", servicePriority.getServicePrioritySubtype(), true, info,
                                userContext);
                            add("Description", servicePriority.getDescription(), true, info, userContext);
                        });
                    customerAccountPriorityMap.put(accountInfo.getPrimaryIdentifier().getValue(), info);
                }
            });
        return customerAccountPriorityMap;
    }

    // Get the customer alternate contact information
    private List<Contact> getCustomerAlternateContactsInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Contact> info = new ArrayList<>();

        mspCustomer.getAlternateContacts().getAlternateContact().forEach(
            alternateContact -> {
                ContactInfo contactInfo = alternateContact.getContactInfo();
                List<String> phoneNumbers =
                    multispeakCustomerInfoService.getPhoneNumbers(contactInfo.getPhoneNumbers(), userContext);
                List<String> emailAddresses =
                    multispeakCustomerInfoService.getEmailAddresses(contactInfo.getEMailAddresses(), userContext);
                Contact contact =
                    new Contact(alternateContact.getFirstName(), alternateContact.getMName(),
                        alternateContact.getLastName(), phoneNumbers, emailAddresses,
                        contactInfo.getAddressItems() != null ? multispeakFuncs.getAddressList(contactInfo.getAddressItems()) : null);
                info.add(contact);

            });
        return info;
    }

    // Service location basic information
    private List<Info> getServLocBasicsInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> infoList = new ArrayList<Info>();

        add("Primary Identifier ", mspServLoc.getPrimaryIdentifier(), false, infoList, userContext);
        add("Description", mspServLoc.getDescription(), true, infoList, userContext);
        add("Facility Name", mspServLoc.getFacilityName(), false, infoList, userContext);
        add("Account ID", mspServLoc.getAccountID(), false, infoList, userContext);
        add("SIC", mspServLoc.getSIC(), false, infoList, userContext);
        add("Site ID", mspServLoc.getSiteID(), false, infoList, userContext);

        add("GPS Location ", mspServLoc.getGPSLocation(), false, infoList, userContext);
        add("GML Location ", mspServLoc.getGMLLocation(), false, infoList, userContext);
        add("Grid Location ", mspServLoc.getGridLocation(), false, infoList, userContext);
        if (mspServLoc.getRotation() != null) {
            add("Rotation ", mspServLoc.getRotation().getValue() + StringUtils.SPACE
                + (mspServLoc.getRotation().getUnits() != null ? mspServLoc.getRotation().getUnits() : ""), true,
                infoList, userContext);
        }
        // Location Information
        if (mspServLoc.getLocationInformation() != null) {
            add("City ", mspServLoc.getLocationInformation().getCity(), true, infoList, userContext);
            add("County ", mspServLoc.getLocationInformation().getCounty(), true, infoList, userContext);
            add("Township Name ", mspServLoc.getLocationInformation().getTownshipName(), true, infoList, userContext);
            add("Sub Division ", mspServLoc.getLocationInformation().getSubdivision(), true, infoList, userContext);
            add("Block ", mspServLoc.getLocationInformation().getBlock(), true, infoList, userContext);
            add("Lot ", mspServLoc.getLocationInformation().getLot(), true, infoList, userContext);
            add("State ", mspServLoc.getLocationInformation().getState(), true, infoList, userContext);
            if (mspServLoc.getLocationInformation().getTRSQ() != null) {
                add("Section ", mspServLoc.getLocationInformation().getTRSQ().getSection(), true, infoList,
                    userContext);
                add("Quater Section ", mspServLoc.getLocationInformation().getTRSQ().getQuarterSection(), true,
                    infoList, userContext);
                add("Range ", mspServLoc.getLocationInformation().getTRSQ().getRange(), true, infoList, userContext);
                add("Township ", mspServLoc.getLocationInformation().getTRSQ().getTownship(), true, infoList,
                    userContext);
            }
            if (mspServLoc.getLocationInformation().getTimeZone() != null) {
                add("Time Zone - Name ", mspServLoc.getLocationInformation().getTimeZone().getName(), true, infoList,
                    userContext);
                add("Time Zone - Value ", mspServLoc.getLocationInformation().getTimeZone().getValue(), true,
                    infoList, userContext);
                add("Time Zone - UTC offset ", mspServLoc.getLocationInformation().getTimeZone().getUTCOffset(), true,
                    infoList, userContext);
                add("Time Zone - Comment ", mspServLoc.getLocationInformation().getTimeZone().getComment(), true,
                    infoList, userContext);
            }
        }

        return infoList;
    }

    // Service Location Hazards Information
    private Map<AtomicInteger, List<Info>> getServiceLocationHazardInfo(ServiceLocation mspServLoc,
            YukonUserContext userContext) {

        Map<AtomicInteger, List<Info>> locationHazardMap = new HashMap<>();
        List<Info> info = new ArrayList<>();
        AtomicInteger hazardID = new AtomicInteger();

        mspServLoc.getServiceHazards().getServiceHazard().forEach(locationHazard -> {
            add("Type", locationHazard.getLocationHazardType(), true, info, userContext);
            add("Sub Type", locationHazard.getLocationHazardSubType(), true, info, userContext);
            add("Text", locationHazard.getHazardText(), true, info, userContext);
            hazardID.incrementAndGet();
            locationHazardMap.put(hazardID, info);
        });

        return locationHazardMap;
    }

    // Service Location - Electric Service Point
    private List<Info> getElectricServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        // Map<String, List<Info>> electricServicePointMap = new HashMap<>();
        List<Info> info = new ArrayList<>();

        mspServLoc.getElectricServicePoints().getElectricServicePoint().forEach(electricServicePoint -> {
            add(null, "Meter Information", true, info, userContext);
            // Electric Meter
            if (electricServicePoint.getElectricMeter() != null) {
                if (electricServicePoint.getElectricMeterID() != null
                    && electricServicePoint.getElectricMeterID().getPrimaryIdentifier() != null) {
                    add("Meter ID", electricServicePoint.getElectricMeterID().getPrimaryIdentifier().getValue(), false,
                        info, userContext);
                }
                add("Meter Base ID", electricServicePoint.getElectricMeter().getMeterBaseID(), false, info, userContext);
                if (electricServicePoint.getElectricMeter().getMeterConnectionStatus() != null) {
                    add("Connection Status",
                        electricServicePoint.getElectricMeter().getMeterConnectionStatus().getValue(), true, info,
                        userContext);
                }
                add("Metrology Firmware Version",
                    electricServicePoint.getElectricMeter().getMetrologyFirmwareVersion(), true, info, userContext);
                add("Metrology Firmware Revision",
                    electricServicePoint.getElectricMeter().getMetrologyFirmwareRevision(), true, info, userContext);
                add("AMI Device Type", electricServicePoint.getElectricMeter().getAMIDeviceType(), false, info,
                    userContext);
                add("AMI Vendor", electricServicePoint.getElectricMeter().getAMIVendor(), false, info, userContext);
                add("Billing Cycle", electricServicePoint.getElectricMeter().getBillingCycle(), true, info,
                    userContext);

                if (electricServicePoint.getElectricMeter().getUtilityInfo() != null) {
                    add("Owner", electricServicePoint.getElectricMeter().getUtilityInfo().getOwner(), true, info,
                        userContext);
                    if (electricServicePoint.getElectricMeter().getUtilityInfo().getServicePointID() != null) {
                        add("Service Point Value",
                            electricServicePoint.getElectricMeter().getUtilityInfo().getServicePointID().getValue(),
                            true, info, userContext);
                        add("Service Point Type",
                            electricServicePoint.getElectricMeter().getUtilityInfo().getServicePointID().getServiceType(),
                            true, info, userContext);
                    }

                }
                if (electricServicePoint.getElectricMeter().getParentMeters() != null) {
                    electricServicePoint.getElectricMeter().getParentMeters().getMeterID().forEach(meterID -> {
                        add("Parent Meter", meterID, true, info, userContext);
                    });
                }
                if (electricServicePoint.getElectricMeter().getSubMeters() != null) {
                    electricServicePoint.getElectricMeter().getSubMeters().getMeterID().forEach(meterID -> {
                        add("Sub Meter", meterID, true, info, userContext);
                    });
                }
                if (electricServicePoint.getElectricMeter().getModules() != null) {
                    electricServicePoint.getElectricMeter().getModules().getModule().forEach(module -> {
                        add("Module", module.getDescription(), true, info, userContext);
                    });
                }

                if (electricServicePoint.getElectricMeter().getConfiguredReadingTypes() != null) {
                    electricServicePoint.getElectricMeter().getConfiguredReadingTypes().getConfiguredReadingType().forEach(
                        configuredReadingType -> {
                            add("Configured Reading Type", configuredReadingType, true, info, userContext);
                        });
                }

                if (electricServicePoint.getElectricMeter().getCommunicationsAddress() != null) {
                    add("Communication Address",
                        electricServicePoint.getElectricMeter().getCommunicationsAddress().getValue(), true, info,
                        userContext);
                    add("Communication Port",
                        electricServicePoint.getElectricMeter().getCommunicationsAddress().getCommunicationsPort(),
                        true, info, userContext);
                }

                if (electricServicePoint.getElectricMeter() != null
                    && electricServicePoint.getElectricMeter().getElectricNameplate() != null) {
                    add(null, "Nameplate Information", true, info, userContext);
                    add("kh", electricServicePoint.getElectricMeter().getElectricNameplate().getKh(), true, info,
                        userContext);
                    add("kr", electricServicePoint.getElectricMeter().getElectricNameplate().getKr(), true, info,
                        userContext);
                    if (electricServicePoint.getElectricMeter().getElectricNameplate().getFrequency() != null
                        && electricServicePoint.getElectricMeter().getElectricNameplate().getFrequency().getUnits() != null) {
                        add("Frequency",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getFrequency().getValue()
                                + StringUtils.SPACE
                                + electricServicePoint.getElectricMeter().getElectricNameplate().getFrequency().getUnits().value(),
                            true, info, userContext);
                    }
                    add("Number of elements",
                        electricServicePoint.getElectricMeter().getElectricNameplate().getNumberOfElements(), true,
                        info, userContext);
                    add("Base Type", electricServicePoint.getElectricMeter().getElectricNameplate().getBaseType(),
                        true, info, userContext);
                    add("Accuracy Class ",
                        electricServicePoint.getElectricMeter().getElectricNameplate().getAccuracyClass(), true, info,
                        userContext);
                    if (electricServicePoint.getElectricMeter().getElectricNameplate().getElementsVoltage() != null
                        && electricServicePoint.getElectricMeter().getElectricNameplate().getElementsVoltage().getUnits() != null) {
                        add("Element Voltage",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getElementsVoltage().getValue()
                                + StringUtils.SPACE
                                + electricServicePoint.getElectricMeter().getElectricNameplate().getElementsVoltage().getUnits().value(),
                            true, info, userContext);
                    }
                    if (electricServicePoint.getElectricMeter().getElectricNameplate().getSupplyVoltage() != null
                        && electricServicePoint.getElectricMeter().getElectricNameplate().getSupplyVoltage().getUnits() != null) {
                        add("Supply Voltage",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getSupplyVoltage().getValue()
                                + StringUtils.SPACE
                                + electricServicePoint.getElectricMeter().getElectricNameplate().getSupplyVoltage().getUnits().value(),
                            true, info, userContext);
                    }
                    if (electricServicePoint.getElectricMeter().getElectricNameplate().getMaxAmperage() != null
                        && electricServicePoint.getElectricMeter().getElectricNameplate().getMaxAmperage().getUnits() != null) {
                        add("Max Amp",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getMaxAmperage().getValue()
                                + StringUtils.SPACE
                                + electricServicePoint.getElectricMeter().getElectricNameplate().getMaxAmperage().getUnits().value(),
                            true, info, userContext);
                    }
                    if (electricServicePoint.getElectricMeter().getElectricNameplate().getTestAmperage() != null
                        && electricServicePoint.getElectricMeter().getElectricNameplate().getTestAmperage().getUnits() != null) {
                        add("Test Amp",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getTestAmperage().getValue()
                                + StringUtils.SPACE
                                + electricServicePoint.getElectricMeter().getElectricNameplate().getTestAmperage().getUnits().value(),
                            true, info, userContext);
                    }
                    add("Register Ratio",
                        electricServicePoint.getElectricMeter().getElectricNameplate().getRegisterRatio(), true, info,
                        userContext);
                    add("Phases", electricServicePoint.getElectricMeter().getElectricNameplate().getPhases(), true,
                        info, userContext);
                    add("Wire", electricServicePoint.getElectricMeter().getElectricNameplate().getWires(), true, info,
                        userContext);
                    add("Dials", electricServicePoint.getElectricMeter().getElectricNameplate().getDials(), true,
                        info, userContext);
                    add("Form", electricServicePoint.getElectricMeter().getElectricNameplate().getForm(), true, info,
                        userContext);
                    add("Multiplier", electricServicePoint.getElectricMeter().getElectricNameplate().getMultiplier(),
                        false, info, userContext);
                    add("Demand Multiplier",
                        electricServicePoint.getElectricMeter().getElectricNameplate().getDemandMultiplier(), false,
                        info, userContext);

                }
            }

            add("Service Sub Type", electricServicePoint.getServiceSubType(), true, info, userContext);
            if (electricServicePoint.getOutageStatus() != null) {
                add("Outage Status", electricServicePoint.getOutageStatus().getValue(), true, info, userContext);
            }
            add("Cogeneration Site", electricServicePoint.isIsCogenerationSite(), true, info, userContext);
            add("Service Order ID", electricServicePoint.getServiceOrderID(), true, info, userContext);

            add("Billing Cycle", electricServicePoint.getBillingCycle(), true, info, userContext);
            add("Route", electricServicePoint.getRoute(), true, info, userContext);
            add("Shutoff Date", electricServicePoint.getShutOffDate(), false, info, userContext);
            add("Connect Date", electricServicePoint.getConnectDate(), false, info, userContext);
            add("Disconnect Date", electricServicePoint.getDisconnectDate(), true, info, userContext);

            if (electricServicePoint.getServicePointStatus() != null
                && electricServicePoint.getServicePointStatus().getServiceStatus() != null) {
                add("Service Point Status - Service Status",
                    electricServicePoint.getServicePointStatus().getServiceStatus().getValue(), true, info,
                    userContext);
                add("Service Point Status - Account Status",
                    electricServicePoint.getServicePointStatus().getAccountStatus().getValue(), true, info,
                    userContext);
                add("Service Point Status - Connectivity Status",
                    electricServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), true, info,
                    userContext);
            }

            // Billing Information
            if (electricServicePoint.getBillingStatusInformation() != null) {
                add(null, "Billing Information", true, info, userContext);
                makeBillingInformation(electricServicePoint.getBillingStatusInformation(), info, userContext);
            }

            // Electric Location Fields
            add(null, "Utility Information", true, info, userContext);
            if (electricServicePoint.getElectricLocationFields() != null) {
                if (electricServicePoint.getElectricLocationFields().getSubstationRef() != null) {
                    add("Substation Ref - Code",
                        electricServicePoint.getElectricLocationFields().getSubstationRef().getSubstationCode(), false,
                        info, userContext);
                    add("Substation Ref - Name ",
                        electricServicePoint.getElectricLocationFields().getSubstationRef().getSubstationName(), false,
                        info, userContext);
                }
                if (electricServicePoint.getElectricLocationFields().getFeederRef() != null) {
                    add("Feeder Ref - Code",
                        electricServicePoint.getElectricLocationFields().getFeederRef().getPrimaryIdentifierValue(),
                        true, info, userContext);
                    add("Feeder Ref - Noun", electricServicePoint.getElectricLocationFields().getFeederRef().getNoun(),
                        true, info, userContext);
                }

                add("Bus", electricServicePoint.getElectricLocationFields().getBus(), true, info, userContext);

                add("Phase Code", electricServicePoint.getElectricLocationFields().getPhaseCode(), true, info,
                    userContext);
                add("Linemen Service Area", electricServicePoint.getElectricLocationFields().getLinemenServiceArea(),
                    true, info, userContext);
                add("Pole Number", electricServicePoint.getElectricLocationFields().getPoleNumber(), true, info,
                    userContext);

                add("Service Location ID", electricServicePoint.getElectricLocationFields().getServiceLocationID(),
                    false, info, userContext);
                add("Service Point ID", electricServicePoint.getElectricLocationFields().getElectricServicePointID(),
                    true, info, userContext);

                if (electricServicePoint.getElectricLocationFields().getNetworkModelRef() != null) {
                    add("Network Model Ref - Code",
                        electricServicePoint.getElectricLocationFields().getNetworkModelRef().getPrimaryIdentifierValue(),
                        true, info, userContext);
                    add("Network Model Ref - Noun",
                        electricServicePoint.getElectricLocationFields().getNetworkModelRef().getNoun(), true, info,
                        userContext);

                }
            }

            // Electric Rating
            if (electricServicePoint.getElectricalRatings() != null) {
                if (electricServicePoint.getElectricalRatings().getRatedCurrent() != null
                    && electricServicePoint.getElectricalRatings().getRatedCurrent().getUnits() != null) {
                    add("Rated Current", electricServicePoint.getElectricalRatings().getRatedCurrent().getValue()
                        + StringUtils.SPACE
                        + electricServicePoint.getElectricalRatings().getRatedCurrent().getUnits().value(), true,
                        info, userContext);
                }
                if (electricServicePoint.getElectricalRatings().getRatedVoltage() != null
                    && electricServicePoint.getElectricalRatings().getRatedVoltage().getUnits() != null) {
                    add("Rated Voltage", electricServicePoint.getElectricalRatings().getRatedVoltage().getValue()
                        + StringUtils.SPACE
                        + electricServicePoint.getElectricalRatings().getRatedVoltage().getUnits().value(), true,
                        info, userContext);
                }
                if (electricServicePoint.getElectricalRatings().getRatedPower() != null
                    && electricServicePoint.getElectricalRatings().getRatedPower().getUnits() != null) {
                    add("Rated Power", electricServicePoint.getElectricalRatings().getRatedPower().getValue()
                        + StringUtils.SPACE
                        + electricServicePoint.getElectricalRatings().getRatedPower().getUnits().value(), true, info,
                        userContext);
                }
            }
        });
        return info;
    }

    // Service Location - Gas Service Point
    private List<Info> getGasServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();

        mspServLoc.getGasServicePoints().getGasServicePoint().forEach(
            gasServicePoint -> {
                add(null, "Meter Information", true, info, userContext);
                add("Gas Meter ID", gasServicePoint.getGasMeterID(), true, info, userContext);
                add("Service Order ID", gasServicePoint.getServiceOrderID(), true, info, userContext);
                add("Revenue Class", gasServicePoint.getRevenueClass(), true, info, userContext);
                add("Rate Code", gasServicePoint.getRateCode(), true, info, userContext);
                add("Service Sub Type", gasServicePoint.getServiceSubType(), true, info, userContext);
                if (gasServicePoint.getServicePointStatus() != null
                    && gasServicePoint.getServicePointStatus().getServiceStatus() != null) {
                    add("Service Point Status - Service Status",
                        gasServicePoint.getServicePointStatus().getServiceStatus().getValue(), true, info, userContext);
                    add("Service Point Status - Account Status",
                        gasServicePoint.getServicePointStatus().getAccountStatus().getValue(), true, info, userContext);
                    add("Service Point Status - Connectivity Status",
                        gasServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), true, info,
                        userContext);
                }
                // Billing Information
                add(null, "Billing Information", true, info, userContext);
                if (gasServicePoint.getBillingStatusInformation() != null) {
                    makeBillingInformation(gasServicePoint.getBillingStatusInformation(), info, userContext);
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
    private List<Info> getPropaneServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();

        mspServLoc.getPropaneServicePoints().getPropaneServicePoint().forEach(
            propaneServicePoint -> {
                add(null, "Meter Information", true, info, userContext);
                add("Propane Meter ID", propaneServicePoint.getPropaneMeterID(), true, info, userContext);
                add("Service Order ID", propaneServicePoint.getServiceOrderID(), true, info, userContext);
                add("Revenue Class", propaneServicePoint.getRevenueClass(), true, info, userContext);
                add("Rate Code", propaneServicePoint.getRateCode(), true, info, userContext);
                add("Service Sub Type", propaneServicePoint.getServiceSubType(), true, info, userContext);

                if (propaneServicePoint.getServicePointStatus() != null
                    && propaneServicePoint.getServicePointStatus().getServiceStatus() != null) {
                    add("Service Point Status - Service Status",
                        propaneServicePoint.getServicePointStatus().getServiceStatus().getValue(), true, info,
                        userContext);
                    add("Service Point Status - Account Status",
                        propaneServicePoint.getServicePointStatus().getAccountStatus().getValue(), true, info,
                        userContext);
                    add("Service Point Status - Connectivity Status",
                        propaneServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), true, info,
                        userContext);
                }
                // Billing Information
                add(null, "Billing Information", true, info, userContext);
                if (propaneServicePoint.getBillingStatusInformation() != null) {
                    makeBillingInformation(propaneServicePoint.getBillingStatusInformation(), info, userContext);
                }
                add("Billing Cycle", propaneServicePoint.getBillingCycle(), true, info, userContext);
                add("Route", propaneServicePoint.getRoute(), true, info, userContext);
                add("Buget Bill", propaneServicePoint.getBudgetBill(), true, info, userContext);
                add("Shutoff Date", propaneServicePoint.getShutOffDate(), true, info, userContext);
                add("Connect Date", propaneServicePoint.getConnectDate(), true, info, userContext);
                add("Disconnect Date", propaneServicePoint.getDisconnectDate(), true, info, userContext);

                // Gas Meter
                if (propaneServicePoint.getPropaneMeter() != null
                    && propaneServicePoint.getPropaneMeter().getPropaneNameplate() != null) {
                    add(null, "Nameplate Information", true, info, userContext);
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMechanicalForm() != null) {
                        add("Mechanical Form",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMechanicalForm().value(),
                            true, info, userContext);
                    }
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMeasurementSystem() != null) {
                        add("Measurement System",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMeasurementSystem().value(),
                            true, info, userContext);
                    }
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure() != null
                        && propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getMaxPressureUOM() != null) {
                        add("Gas Pressure",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getValue()
                                + StringUtils.SPACE
                                + propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getMaxPressureUOM().value(),
                            true, info, userContext);
                    }
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow() != null
                        && propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getMaxFlowRateUOM() != null) {
                        add("Gas Flow",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getValue()
                                + StringUtils.SPACE
                                + propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getMaxFlowRateUOM().value(),
                            true, info, userContext);
                    }
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGearDriveSize() != null) {
                        add("Gas Drive Size",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGearDriveSize().value(),
                            true, info, userContext);
                    }
                    add("Gas Internal Pipe Diameter",
                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getInternalPipeDiameter().value(), true,
                        info, userContext);
                    add("Gas Temperature Compensation Type",
                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getTemperatureCompensationType().value(),
                        true, info, userContext);
                    add("Gas Pressure Compensation Type",
                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getPressureCompensationType().value(),
                        true, info, userContext);
                }

            });
        return info;
    }

    // Service Location - Water Service Point
    private List<Info> getWaterServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();

        mspServLoc.getWaterServicePoints().getWaterServicePoint().forEach(
            waterServicePoint -> {
                add(null, "Meter Information", true, info, userContext);
                add("Meter ID", waterServicePoint.getWaterMeterID(), true, info, userContext);
                add("Service Order ID", waterServicePoint.getServiceOrderID(), true, info, userContext);
                add("Revenue Class", waterServicePoint.getRevenueClass(), true, info, userContext);
                add("Rate Code", waterServicePoint.getRateCode(), true, info, userContext);
                add("Service Sub Type", waterServicePoint.getServiceSubType(), true, info, userContext);
                if (waterServicePoint.getServicePointStatus() != null
                    && waterServicePoint.getServicePointStatus().getServiceStatus() != null) {
                    add("Service Point Status - Service Status",
                        waterServicePoint.getServicePointStatus().getServiceStatus().getValue(), true, info,
                        userContext);
                    add("Service Point Status - Account Status",
                        waterServicePoint.getServicePointStatus().getAccountStatus().getValue(), true, info,
                        userContext);
                    add("Service Point Status - Connectivity Status",
                        waterServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), true, info,
                        userContext);
                }
                // Billing Information
                add(null, "Billing Information", true, info, userContext);
                if (waterServicePoint.getBillingStatusInformation() != null) {
                    makeBillingInformation(waterServicePoint.getBillingStatusInformation(), info, userContext);
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

    private void makeBillingInformation(BillingStatusInformation billingInfo, List<Info> info,
            YukonUserContext userContext) {

        billingInfo.getBillingStatusItem().forEach(
            billingItem -> {
                add("Account Receivable", billingItem.getAccountsReceivable(), true, info, userContext);
                add("Account Receivable this Period", billingItem.getAccountsReceivableThisPeriod(), true, info,
                    userContext);
                if (billingItem.getBillingItemsType() != null) {
                    add("Billing Type", billingItem.getBillingItemsType().getValue(), true, info, userContext);
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
