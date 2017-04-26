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
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.msp.beans.v5.commontypes.AddressItems;
import com.cannontech.msp.beans.v5.commontypes.PhoneNumber;
import com.cannontech.msp.beans.v5.enumerations.PhoneTypeKind;
import com.cannontech.msp.beans.v5.multispeak.BillingStatusInformation;
import com.cannontech.msp.beans.v5.multispeak.ContactInfo;
import com.cannontech.msp.beans.v5.multispeak.Customer;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.service.v5.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;

public class MspAccountInformationV5 implements MspAccountInformation {

    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private MultispeakCustomerInfoService multispeakCustomerInfoService;
    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;

    public ModelAndView getMspInformation(YukonMeter meter, MultispeakVendor mspVendor, ModelAndView mav,
            YukonUserContext userContext) {

        Customer mspCustomer = mspObjectDao.getMspCustomer(meter, mspVendor);
        ServiceLocation mspServLoc = mspObjectDao.getMspServiceLocation(meter, mspVendor);

        mav.addObject("mspCustomer", mspCustomer);
        mav.addObject("mspServLoc", mspServLoc);

        Map<PhoneTypeKind, String> primaryContact = getPrimaryContacts(mspCustomer);
        mav.addObject("homePhone", primaryContact.get(PhoneTypeKind.HOME));
        mav.addObject("dayPhone", primaryContact.get(PhoneTypeKind.BUSINESS));

        if (mspCustomer.getContactInfo() != null) {
            List<Address> custAddressInfo = getAddressList(mspCustomer.getContactInfo().getAddressItems());
            mav.addObject("custAddressInfo", custAddressInfo);
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

        Map<PhoneTypeKind, String> primaryContact = getPrimaryContacts(mspCustomer);

        add("Identifier", mspCustomer.getPrimaryIdentifier(), true, infoList, userContext);
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
        Contact contact = new Contact();
        List<String> phoneNumbers =
            multispeakCustomerInfoService.getPhoneNumbers(contactInfo.getPhoneNumbers(), userContext);
        contact.setPhoneNumbers(phoneNumbers);

        List<String> emailAddresses =
            multispeakCustomerInfoService.getEmailAddresses(contactInfo.getEMailAddresses(), userContext);
        contact.setEmailAddresses(emailAddresses);
        contact.setAddresses(getAddressList(contactInfo.getAddressItems()));
        info.add(contact);

        return info;

    }

    // Customer Hazards Information
    private List<Info> getCustomerHazardInfo(Customer mspCustomer, YukonUserContext userContext) {

        List<Info> info = new ArrayList<>();

        mspCustomer.getCustomerHazards().getCustomerHazard().forEach(customerHazard -> {
            add("Type", customerHazard.getCustomerHazardType(), false, info, userContext);
            add("Sub Type", customerHazard.getCustomerHazardSubType(), false, info, userContext);
            add("Text", customerHazard.getHazardText(), false, info, userContext);
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
                    add("Account Status", accountInfo.getAccountStatus().getValue(), false, info, userContext);
                }
                add("Billing Cycle", accountInfo.getBillingCycle(), false, info, userContext);
                add("Budget Bill", accountInfo.getBudgetBill(), false, info, userContext);
                add("Payment Due Date", accountInfo.getPaymentDueDate(), false, info, userContext);
                add("Last Payment Date", accountInfo.getLastPaymentDate(), false, info, userContext);
                add("Last Payment Amount", accountInfo.getLastPaymentAmount(), false, info, userContext);
                add("Bill Date", accountInfo.getBillDate(), false, info, userContext);
                add("Shutoff Date", accountInfo.getShutOffDate(), false, info, userContext);
                add("Prepay", accountInfo.isIsPrePay(), false, info, userContext);
                add("Billing Term", accountInfo.getBillingTerms(), false, info, userContext);
                add("Calculated Current Bill Amount", accountInfo.getCalculatedCurrentBillAmount(), false, info,
                    userContext);
                add("Calculated Current Bill Date", accountInfo.getCalculatedCurrentBillDateTime(), false, info,
                    userContext);
                add("Last Bill Amount", accountInfo.getLastBillAmount(), false, info, userContext);
                add("Calculated Used Yesterday", accountInfo.getCalculatedUsedYesterday(), false, info, userContext);

            });
        return info;
    }

    // Customer Account Receivable Information
    private Map<String, List<Info>> getCustomerAccountReceivableInfo(Customer mspCustomer, YukonUserContext userContext) {

        Map<String, List<Info>> customerAccountReceivableMap = new HashMap<>();
        List<Info> info = new ArrayList<>();

        mspCustomer.getAccounts().getAccount().forEach(accountInfo -> {
            if (accountInfo.getAccountsReceivable() != null) {
                accountInfo.getAccountsReceivable().getAcctsReceivable().forEach(acctsReceivable -> {
                    add("Receivable Amount", acctsReceivable.getReceivableAmount(), false, info, userContext);
                    add("Receivable Type", acctsReceivable.getReceivableType(), false, info, userContext);
                    add("Description", acctsReceivable.getDescription(), false, info, userContext);
                    add("Due Date", acctsReceivable.getDueDate(), false, info, userContext);
                    if (acctsReceivable.getServiceType() != null) {
                        add("Service Type", acctsReceivable.getServiceType().getValue(), false, info, userContext);
                    }
                    customerAccountReceivableMap.put(accountInfo.getPrimaryIdentifier().getValue(), info);

                });
            }
        });
        return customerAccountReceivableMap;
    }

    // Customer Account Priority Information
    private Map<String, List<Info>> getCustomerAccountPriorityInfo(Customer mspCustomer, YukonUserContext userContext) {

        Map<String, List<Info>> customerAccountPriorityMap = new HashMap<>();
        List<Info> info = new ArrayList<>();

        mspCustomer.getAccounts().getAccount().forEach(
            accountInfo -> {
                if (accountInfo.getAccountPriorities() != null) {
                    accountInfo.getAccountPriorities().getAccountPriority().forEach(
                        servicePriority -> {
                            if (servicePriority.getServicePriorityType() != null) {
                                add("Priority Type", servicePriority.getServicePriorityType().getValue(), false, info,
                                    userContext);
                            }
                            add("Priority SubType", servicePriority.getServicePrioritySubtype(), false, info,
                                userContext);
                            add("Description", servicePriority.getDescription(), false, info, userContext);
                            customerAccountPriorityMap.put(accountInfo.getPrimaryIdentifier().getValue(), info);
                        });
                }
            });
        return customerAccountPriorityMap;
    }

    // Returns phone number (Home and Business) of the primary contact
    private Map<PhoneTypeKind, String> getPrimaryContacts(Customer mspCustomer) {
        Map<PhoneTypeKind, String> allPhoneNumbers = new HashMap<>();

        List<PhoneNumber> phoneNumber = new ArrayList<>();
        if (mspCustomer.getContactInfo() != null && mspCustomer.getContactInfo().getPhoneNumbers() != null) {
            phoneNumber = mspCustomer.getContactInfo().getPhoneNumbers().getPhoneNumber();

            phoneNumber.forEach(phNo -> {
                if (phNo.getPhoneType() != null) {
                    if (phNo.getPhoneType().getValue() == PhoneTypeKind.HOME
                        || phNo.getPhoneType().getValue() == PhoneTypeKind.BUSINESS) {

                        allPhoneNumbers.put(phNo.getPhoneType().getValue(), phoneNumberFormattingService.formatPhone(
                            phNo.getPhone().getAreaCode(), phNo.getPhone().getLocalNumber()));
                    }
                }
            });
        }
        return allPhoneNumbers;
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
                        getAddressList(contactInfo.getAddressItems()));
                info.add(contact);

            });
        return info;
    }

    private List<Address> getAddressList(AddressItems addressItems) {
        List<Address> addressList = new ArrayList<>();
        addressItems.getAddressItem().forEach(addressItem -> {
            if (addressItem.getAddress() != null) {
                Address address = new Address();
                address.setLocationAddress1(addressItem.getAddress().getAddress1());
                address.setLocationAddress2(addressItem.getAddress().getAddress2());
                address.setCityName(addressItem.getAddress().getCity());
                address.setStateCode(addressItem.getAddress().getState());
                address.setZipCode(addressItem.getAddress().getPostalCode());
                addressList.add(address);
            }
        });
        return addressList;
    }

    // Service location basic information
    private List<Info> getServLocBasicsInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        List<Info> infoList = new ArrayList<Info>();

        add("Primary Identifier ", mspServLoc.getPrimaryIdentifier(), false, infoList, userContext);
        add("Description", mspServLoc.getDescription(), false, infoList, userContext);
        add("Facility Name", mspServLoc.getFacilityName(), false, infoList, userContext);
        add("Account ID", mspServLoc.getAccountID(), false, infoList, userContext);
        add("SIC", mspServLoc.getSIC(), false, infoList, userContext);
        add("Site ID", mspServLoc.getSiteID(), false, infoList, userContext);

        add("GPS Location ", mspServLoc.getGPSLocation(), false, infoList, userContext);
        add("GML Location ", mspServLoc.getGMLLocation(), false, infoList, userContext);
        add("Grid Location ", mspServLoc.getGridLocation(), false, infoList, userContext);
        add("Rotation ", mspServLoc.getRotation(), false, infoList, userContext);

        // Location Information
        if (mspServLoc.getLocationInformation() != null) {
            add("City ", mspServLoc.getLocationInformation().getCity(), false, infoList, userContext);
            add("County ", mspServLoc.getLocationInformation().getCounty(), false, infoList, userContext);
            add("Township Name ", mspServLoc.getLocationInformation().getTownshipName(), false, infoList, userContext);
            add("Sub Division ", mspServLoc.getLocationInformation().getSubdivision(), false, infoList, userContext);
            add("Block ", mspServLoc.getLocationInformation().getBlock(), false, infoList, userContext);
            add("Lot ", mspServLoc.getLocationInformation().getLot(), false, infoList, userContext);
            add("State ", mspServLoc.getLocationInformation().getState(), false, infoList, userContext);
            if (mspServLoc.getLocationInformation().getTRSQ() != null) {
                add("Section ", mspServLoc.getLocationInformation().getTRSQ().getSection(), false, infoList,
                    userContext);
                add("Quater Section ", mspServLoc.getLocationInformation().getTRSQ().getQuarterSection(), false,
                    infoList, userContext);
                add("Range ", mspServLoc.getLocationInformation().getTRSQ().getRange(), false, infoList, userContext);
                add("Township ", mspServLoc.getLocationInformation().getTRSQ().getTownship(), false, infoList,
                    userContext);
            }
            if (mspServLoc.getLocationInformation().getTimeZone() != null) {
                add("Time Zone - Name ", mspServLoc.getLocationInformation().getTimeZone().getName(), false, infoList,
                    userContext);
                add("Time Zone - Value ", mspServLoc.getLocationInformation().getTimeZone().getValue(), false,
                    infoList, userContext);
                add("Time Zone - UTC offset ", mspServLoc.getLocationInformation().getTimeZone().getUTCOffset(), false,
                    infoList, userContext);
                add("Time Zone - Comment ", mspServLoc.getLocationInformation().getTimeZone().getComment(), false,
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
            add("Type", locationHazard.getLocationHazardType(), false, info, userContext);
            add("Sub Type", locationHazard.getLocationHazardSubType(), false, info, userContext);
            add("Text", locationHazard.getHazardText(), false, info, userContext);
            hazardID.incrementAndGet();
            locationHazardMap.put(hazardID, info);
        });

        return locationHazardMap;
    }

    // Service Location - Electric Service Point
    private List<Info> getElectricServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        // Map<String, List<Info>> electricServicePointMap = new HashMap<>();
        List<Info> info = new ArrayList<>();

        mspServLoc.getElectricServicePoints().getElectricServicePoint().forEach(
            electricServicePoint -> {
                add("Service Sub Type", electricServicePoint.getServiceSubType(), false, info, userContext);
                if (electricServicePoint.getOutageStatus() != null) {
                    add("Outage Status", electricServicePoint.getOutageStatus().getValue(), false, info, userContext);
                }
                add("Cogeneration Site", electricServicePoint.isIsCogenerationSite(), false, info, userContext);
                add("Service Order ID", electricServicePoint.getServiceOrderID(), false, info, userContext);

                add("Billing Cycle", electricServicePoint.getBillingCycle(), false, info, userContext);
                add("Route", electricServicePoint.getRoute(), false, info, userContext);
                add("Shutoff Date", electricServicePoint.getShutOffDate(), false, info, userContext);
                add("Connect Date", electricServicePoint.getConnectDate(), false, info, userContext);
                add("Disconnect Date", electricServicePoint.getDisconnectDate(), false, info, userContext);

                if (electricServicePoint.getServicePointStatus() != null
                    && electricServicePoint.getServicePointStatus().getServiceStatus() != null) {
                    add("Service Point Status - Service Status",
                        electricServicePoint.getServicePointStatus().getServiceStatus().getValue(), false, info,
                        userContext);
                    add("Service Point Status - Account Status",
                        electricServicePoint.getServicePointStatus().getAccountStatus().getValue(), false, info,
                        userContext);
                    add("Service Point Status - Connectivity Status",
                        electricServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), false, info,
                        userContext);
                }

                // Billing Information
                if (electricServicePoint.getBillingStatusInformation() != null) {
                    makeBillingInformation(electricServicePoint.getBillingStatusInformation(), info, userContext);
                }

                // Electric Location Fields
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
                        false, info, userContext);
                    add("Feeder Ref - Noun", electricServicePoint.getElectricLocationFields().getFeederRef().getNoun(),
                        false, info, userContext);
                }

                add("Bus", electricServicePoint.getElectricLocationFields().getBus(), false, info, userContext);

                if (electricServicePoint.getElectricLocationFields() != null) {
                    add("Phase Code", electricServicePoint.getElectricLocationFields().getPhaseCode(), false, info,
                        userContext);
                    add("Linemen Service Area",
                        electricServicePoint.getElectricLocationFields().getLinemenServiceArea(), false, info,
                        userContext);
                    add("Pole Number", electricServicePoint.getElectricLocationFields().getPoleNumber(), false, info,
                        userContext);

                    add("Service Location ID", electricServicePoint.getElectricLocationFields().getServiceLocationID(),
                        false, info, userContext);
                    add("Service Point ID",
                        electricServicePoint.getElectricLocationFields().getElectricServicePointID(), false, info,
                        userContext);

                    if (electricServicePoint.getElectricLocationFields().getNetworkModelRef() != null) {
                        add("Network Model Ref - Code",
                            electricServicePoint.getElectricLocationFields().getNetworkModelRef().getPrimaryIdentifierValue(),
                            false, info, userContext);
                        add("Network Model Ref - Noun",
                            electricServicePoint.getElectricLocationFields().getNetworkModelRef().getNoun(), false,
                            info, userContext);

                    }
                }

                // Electric Meter
                if (electricServicePoint.getElectricMeter() != null) {
                    add("Electric Meter Base ID", electricServicePoint.getElectricMeter().getMeterBaseID(), false,
                        info, userContext);
                    if (electricServicePoint.getElectricMeter().getMeterConnectionStatus() != null) {
                        add("Connection Status",
                            electricServicePoint.getElectricMeter().getMeterConnectionStatus().getValue(), false, info,
                            userContext);
                    }
                    add("Metrology Firmware Version",
                        electricServicePoint.getElectricMeter().getMetrologyFirmwareVersion(), false, info, userContext);
                    add("Metrology Firmware Revision",
                        electricServicePoint.getElectricMeter().getMetrologyFirmwareRevision(), false, info,
                        userContext);
                    add("AMI Device Type", electricServicePoint.getElectricMeter().getAMIDeviceType(), false, info,
                        userContext);
                    add("AMI Vendor", electricServicePoint.getElectricMeter().getAMIVendor(), false, info, userContext);
                    add("Billing Cycle", electricServicePoint.getElectricMeter().getBillingCycle(), false, info,
                        userContext);

                    if (electricServicePoint.getElectricMeter().getUtilityInfo() != null) {
                        add("Owner", electricServicePoint.getElectricMeter().getUtilityInfo().getOwner(), false, info,
                            userContext);
                        if (electricServicePoint.getElectricMeter().getUtilityInfo().getServicePointID() != null) {
                            add("Service Point Value",
                                electricServicePoint.getElectricMeter().getUtilityInfo().getServicePointID().getValue(),
                                false, info, userContext);
                            add("Service Point Type",
                                electricServicePoint.getElectricMeter().getUtilityInfo().getServicePointID().getServiceType(),
                                false, info, userContext);
                        }

                    }
                    if (electricServicePoint.getElectricMeter().getParentMeters() != null) {
                        electricServicePoint.getElectricMeter().getParentMeters().getMeterID().forEach(meterID -> {
                            add("Parent Meter", meterID, false, info, userContext);
                        });
                    }
                    if (electricServicePoint.getElectricMeter().getSubMeters() != null) {
                        electricServicePoint.getElectricMeter().getSubMeters().getMeterID().forEach(meterID -> {
                            add("Sub Meter", meterID, false, info, userContext);
                        });
                    }
                    if (electricServicePoint.getElectricMeter().getModules() != null) {
                        electricServicePoint.getElectricMeter().getModules().getModule().forEach(module -> {
                            add("Module", module.getDescription(), false, info, userContext);
                        });
                    }

                    if (electricServicePoint.getElectricMeter().getConfiguredReadingTypes() != null) {
                        electricServicePoint.getElectricMeter().getConfiguredReadingTypes().getConfiguredReadingType().forEach(
                            configuredReadingType -> {
                                add("Configured Reading Type", configuredReadingType, false, info, userContext);
                            });
                    }

                    if (electricServicePoint.getElectricMeter().getCommunicationsAddress() != null) {
                        add("Communication Address",
                            electricServicePoint.getElectricMeter().getCommunicationsAddress().getValue(), false, info,
                            userContext);
                        add("Communication Port",
                            electricServicePoint.getElectricMeter().getCommunicationsAddress().getCommunicationsPort(),
                            false, info, userContext);
                    }

                    if (electricServicePoint.getElectricMeter().getElectricNameplate() != null) {
                        add("Electric Nameplate - kh",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getKh(), false, info,
                            userContext);
                        add("Electric Nameplate - kr",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getKr(), false, info,
                            userContext);
                        if (electricServicePoint.getElectricMeter().getElectricNameplate().getFrequency() != null
                            && electricServicePoint.getElectricMeter().getElectricNameplate().getFrequency().getUnits() != null) {
                            add("Electric Nameplate - Frequency",
                                electricServicePoint.getElectricMeter().getElectricNameplate().getFrequency().getValue()
                                    + StringUtils.SPACE
                                    + electricServicePoint.getElectricMeter().getElectricNameplate().getFrequency().getUnits().value(),
                                false, info, userContext);
                        }
                        add("Electric Nameplate - Number of elements",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getNumberOfElements(),
                            false, info, userContext);
                        add("Electric Nameplate - Base Type",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getBaseType(), false, info,
                            userContext);
                        add("Electric Nameplate - Accuracy Class ",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getAccuracyClass(), false,
                            info, userContext);
                        if (electricServicePoint.getElectricMeter().getElectricNameplate().getElementsVoltage() != null
                            && electricServicePoint.getElectricMeter().getElectricNameplate().getElementsVoltage().getUnits() != null) {
                            add("Electric Nameplate - Element Voltage",
                                electricServicePoint.getElectricMeter().getElectricNameplate().getElementsVoltage().getValue()
                                    + StringUtils.SPACE
                                    + electricServicePoint.getElectricMeter().getElectricNameplate().getElementsVoltage().getUnits().value(),
                                false, info, userContext);
                        }
                        if (electricServicePoint.getElectricMeter().getElectricNameplate().getSupplyVoltage() != null
                            && electricServicePoint.getElectricMeter().getElectricNameplate().getSupplyVoltage().getUnits() != null) {
                            add("Electric Nameplate - Supply Voltage",
                                electricServicePoint.getElectricMeter().getElectricNameplate().getSupplyVoltage().getValue()
                                    + StringUtils.SPACE
                                    + electricServicePoint.getElectricMeter().getElectricNameplate().getSupplyVoltage().getUnits().value(),
                                false, info, userContext);
                        }
                        if (electricServicePoint.getElectricMeter().getElectricNameplate().getMaxAmperage() != null
                            && electricServicePoint.getElectricMeter().getElectricNameplate().getMaxAmperage().getUnits() != null) {
                            add("Electric Nameplate - Max Amp",
                                electricServicePoint.getElectricMeter().getElectricNameplate().getMaxAmperage().getValue()
                                    + StringUtils.SPACE
                                    + electricServicePoint.getElectricMeter().getElectricNameplate().getMaxAmperage().getUnits().value(),
                                false, info, userContext);
                        }
                        if (electricServicePoint.getElectricMeter().getElectricNameplate().getTestAmperage() != null
                            && electricServicePoint.getElectricMeter().getElectricNameplate().getTestAmperage().getUnits() != null) {
                            add("Electric Nameplate - Test Amp",
                                electricServicePoint.getElectricMeter().getElectricNameplate().getTestAmperage().getValue()
                                    + StringUtils.SPACE
                                    + electricServicePoint.getElectricMeter().getElectricNameplate().getTestAmperage().getUnits().value(),
                                false, info, userContext);
                        }
                        add("Electric Nameplate - Register Ratio",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getRegisterRatio(), false,
                            info, userContext);
                        add("Electric Nameplate - Phases",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getPhases(), false, info,
                            userContext);
                        add("Electric Nameplate - Wire",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getWires(), false, info,
                            userContext);
                        add("Electric Nameplate - Dials",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getDials(), false, info,
                            userContext);
                        add("Electric Nameplate - Form",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getForm(), false, info,
                            userContext);
                        add("Electric Nameplate - Multiplier",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getMultiplier(), false,
                            info, userContext);
                        add("Electric Nameplate - Demand Multiplier",
                            electricServicePoint.getElectricMeter().getElectricNameplate().getDemandMultiplier(),
                            false, info, userContext);

                    }
                }

                // Electric Rating
                if (electricServicePoint.getElectricalRatings() != null) {
                    if (electricServicePoint.getElectricalRatings().getRatedCurrent() != null
                        && electricServicePoint.getElectricalRatings().getRatedCurrent().getUnits() != null) {
                        add("Rated Current", electricServicePoint.getElectricalRatings().getRatedCurrent().getValue()
                            + StringUtils.SPACE
                            + electricServicePoint.getElectricalRatings().getRatedCurrent().getUnits().value(), false,
                            info, userContext);
                    }
                    if (electricServicePoint.getElectricalRatings().getRatedVoltage() != null
                        && electricServicePoint.getElectricalRatings().getRatedVoltage().getUnits() != null) {
                        add("Rated Voltage", electricServicePoint.getElectricalRatings().getRatedVoltage().getValue()
                            + StringUtils.SPACE
                            + electricServicePoint.getElectricalRatings().getRatedVoltage().getUnits().value(), false,
                            info, userContext);
                    }
                    if (electricServicePoint.getElectricalRatings().getRatedPower() != null
                        && electricServicePoint.getElectricalRatings().getRatedPower().getUnits() != null) {
                        add("Rated Power", electricServicePoint.getElectricalRatings().getRatedPower().getValue()
                            + StringUtils.SPACE
                            + electricServicePoint.getElectricalRatings().getRatedPower().getUnits().value(), false,
                            info, userContext);
                    }
                }
                /*
                 * if (electricServicePoint.getPrimaryIdentifier() != null) {
                 * electricServicePointMap.put(electricServicePoint.getPrimaryIdentifier().getValue(), info);
                 * }
                 */
            });
        return info;
    }

    // Service Location - Gas Service Point
    private List<Info> getGasServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        // Map<String, List<Info>> gasServicePointMap = new HashMap<>();
        List<Info> info = new ArrayList<>();

        mspServLoc.getGasServicePoints().getGasServicePoint().forEach(
            gasServicePoint -> {
                add("Gas Meter ID", gasServicePoint.getGasMeterID(), false, info, userContext);
                add("Service Order ID", gasServicePoint.getServiceOrderID(), false, info, userContext);
                add("Revenue Class", gasServicePoint.getRevenueClass(), false, info, userContext);

                if (gasServicePoint.getServicePointStatus().getServiceStatus() != null) {
                    add("Service Point Status - Service Status",
                        gasServicePoint.getServicePointStatus().getServiceStatus().getValue(), false, info, userContext);
                    add("Service Point Status - Account Status",
                        gasServicePoint.getServicePointStatus().getAccountStatus().getValue(), false, info, userContext);
                    add("Service Point Status - Connectivity Status",
                        gasServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), false, info,
                        userContext);
                }
                // Billing Information
                if (gasServicePoint.getBillingStatusInformation() != null) {
                    makeBillingInformation(gasServicePoint.getBillingStatusInformation(), info, userContext);
                }
                add("Billing Cycle", gasServicePoint.getBillingCycle(), false, info, userContext);
                add("Route", gasServicePoint.getRoute(), false, info, userContext);
                add("Buget Bill", gasServicePoint.getBudgetBill(), false, info, userContext);
                add("Shutoff Date", gasServicePoint.getShutOffDate(), false, info, userContext);
                add("Connect Date", gasServicePoint.getConnectDate(), false, info, userContext);
                add("Disconnect Date", gasServicePoint.getDisconnectDate(), false, info, userContext);

                // Gas Meter
                if (gasServicePoint.getGasMeter().getGasNameplate() != null) {
                    if (gasServicePoint.getGasMeter().getGasNameplate().getMechanicalForm() != null) {
                        add("Gas Meter - Mechanical Form",
                            gasServicePoint.getGasMeter().getGasNameplate().getMechanicalForm().value(), false, info,
                            userContext);
                    }
                    if (gasServicePoint.getGasMeter().getGasNameplate().getMeasurementSystem() != null) {
                        add("Gas Meter - Measurement System",
                            gasServicePoint.getGasMeter().getGasNameplate().getMeasurementSystem().value(), false,
                            info, userContext);
                    }
                    if (gasServicePoint.getGasMeter().getGasNameplate().getGasPressure() != null
                        && gasServicePoint.getGasMeter().getGasNameplate().getGasPressure().getMaxPressureUOM() != null) {
                        add("Gas Meter - Gas Pressure",
                            gasServicePoint.getGasMeter().getGasNameplate().getGasPressure().getValue()
                                + StringUtils.SPACE
                                + gasServicePoint.getGasMeter().getGasNameplate().getGasPressure().getMaxPressureUOM().value(),
                            false, info, userContext);
                    }
                    if (gasServicePoint.getGasMeter().getGasNameplate().getGasFlow() != null
                        && gasServicePoint.getGasMeter().getGasNameplate().getGasFlow().getMaxFlowRateUOM() != null) {
                        add("Gas Meter - Gas Flow",
                            gasServicePoint.getGasMeter().getGasNameplate().getGasFlow().getValue()
                                + StringUtils.SPACE
                                + gasServicePoint.getGasMeter().getGasNameplate().getGasFlow().getMaxFlowRateUOM().value(),
                            false, info, userContext);
                    }
                    if (gasServicePoint.getGasMeter().getGasNameplate().getGearDriveSize() != null) {
                        add("Gas Meter - Gas Drive Size",
                            gasServicePoint.getGasMeter().getGasNameplate().getGearDriveSize().value(), false, info,
                            userContext);
                    }
                    add("Gas Meter - Gas Internal Pipe Diameter",
                        gasServicePoint.getGasMeter().getGasNameplate().getInternalPipeDiameter(), false, info,
                        userContext);
                    add("Gas Meter - Gas Temprature Compensation Type",
                        gasServicePoint.getGasMeter().getGasNameplate().getTemperatureCompensationType(), false, info,
                        userContext);
                    add("Gas Meter - Gas Pressure Compensation Type",
                        gasServicePoint.getGasMeter().getGasNameplate().getPressureCompensationType(), false, info,
                        userContext);
                }

                add("Rate Code", gasServicePoint.getRateCode(), false, info, userContext);
                add("Service Sub Type", gasServicePoint.getServiceSubType(), false, info, userContext);

                /*
                 * if (gasServicePoint.getPrimaryIdentifier() != null) {
                 * gasServicePointMap.put(gasServicePoint.getPrimaryIdentifier().getValue(), info);
                 * }
                 */

            });
        // return gasServicePointMap;
        return info;
    }

    // Service Location - Propane Service Point
    private List<Info> getPropaneServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        // Map<String, List<Info>> propaneServicePointMap = new HashMap<>();
        List<Info> info = new ArrayList<>();

        mspServLoc.getPropaneServicePoints().getPropaneServicePoint().forEach(
            propaneServicePoint -> {
                add("Propane Meter ID", propaneServicePoint.getPropaneMeterID(), false, info, userContext);
                add("Service Order ID", propaneServicePoint.getServiceOrderID(), false, info, userContext);
                add("Revenue Class", propaneServicePoint.getRevenueClass(), false, info, userContext);

                if (propaneServicePoint.getServicePointStatus().getServiceStatus() != null) {
                    add("Service Point Status - Service Status",
                        propaneServicePoint.getServicePointStatus().getServiceStatus().getValue(), false, info,
                        userContext);
                    add("Service Point Status - Account Status",
                        propaneServicePoint.getServicePointStatus().getAccountStatus().getValue(), false, info,
                        userContext);
                    add("Service Point Status - Connectivity Status",
                        propaneServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), false, info,
                        userContext);
                }
                // Billing Information
                if (propaneServicePoint.getBillingStatusInformation() != null) {
                    makeBillingInformation(propaneServicePoint.getBillingStatusInformation(), info, userContext);
                }
                add("Billing Cycle", propaneServicePoint.getBillingCycle(), false, info, userContext);
                add("Route", propaneServicePoint.getRoute(), false, info, userContext);
                add("Buget Bill", propaneServicePoint.getBudgetBill(), false, info, userContext);
                add("Shutoff Date", propaneServicePoint.getShutOffDate(), false, info, userContext);
                add("Connect Date", propaneServicePoint.getConnectDate(), false, info, userContext);
                add("Disconnect Date", propaneServicePoint.getDisconnectDate(), false, info, userContext);

                // Gas Meter
                if (propaneServicePoint.getPropaneMeter().getPropaneNameplate() != null) {
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMechanicalForm() != null) {
                        add("Propane Meter - Mechanical Form",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMechanicalForm().value(),
                            false, info, userContext);
                    }
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMeasurementSystem() != null) {
                        add("Propane Meter - Measurement System",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getMeasurementSystem().value(),
                            false, info, userContext);
                    }
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure() != null
                        && propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getMaxPressureUOM() != null) {
                        add("Propane Meter - Gas Pressure",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getValue()
                                + StringUtils.SPACE
                                + propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasPressure().getMaxPressureUOM().value(),
                            false, info, userContext);
                    }
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow() != null
                        && propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getMaxFlowRateUOM() != null) {
                        add("Propane Meter - Gas Flow",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getValue()
                                + StringUtils.SPACE
                                + propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGasFlow().getMaxFlowRateUOM().value(),
                            false, info, userContext);
                    }
                    if (propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGearDriveSize() != null) {
                        add("Propane Meter - Gas Drive Size",
                            propaneServicePoint.getPropaneMeter().getPropaneNameplate().getGearDriveSize().value(),
                            false, info, userContext);
                    }
                    add("Propane Meter - Gas Internal Pipe Diameter",
                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getInternalPipeDiameter(), false,
                        info, userContext);
                    add("Propane Meter - Gas Temprature Compensation Type",
                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getTemperatureCompensationType(),
                        false, info, userContext);
                    add("Propane Meter - Gas Pressure Compensation Type",
                        propaneServicePoint.getPropaneMeter().getPropaneNameplate().getPressureCompensationType(),
                        false, info, userContext);
                }

                add("Rate Code", propaneServicePoint.getRateCode(), false, info, userContext);
                add("Service Sub Type", propaneServicePoint.getServiceSubType(), false, info, userContext);

            });
        return info;
    }

    // Service Location - Water Service Point
    private List<Info> getWaterServicePointInfo(ServiceLocation mspServLoc, YukonUserContext userContext) {

        Map<String, List<Info>> waterServicePointMap = new HashMap<>();
        List<Info> info = new ArrayList<>();

        mspServLoc.getWaterServicePoints().getWaterServicePoint().forEach(
            waterServicePoint -> {
                add("Water Meter ID", waterServicePoint.getWaterMeterID(), false, info, userContext);
                add("Service Order ID", waterServicePoint.getServiceOrderID(), false, info, userContext);
                add("Revenue Class", waterServicePoint.getRevenueClass(), false, info, userContext);
                if (waterServicePoint.getServicePointStatus().getServiceStatus() != null) {
                    add("Service Point Status - Service Status",
                        waterServicePoint.getServicePointStatus().getServiceStatus().getValue(), false, info,
                        userContext);
                    add("Service Point Status - Account Status",
                        waterServicePoint.getServicePointStatus().getAccountStatus().getValue(), false, info,
                        userContext);
                    add("Service Point Status - Connectivity Status",
                        waterServicePoint.getServicePointStatus().getConnectivityStatus().getValue(), false, info,
                        userContext);
                }
                // Billing Information
                if (waterServicePoint.getBillingStatusInformation() != null) {
                    makeBillingInformation(waterServicePoint.getBillingStatusInformation(), info, userContext);
                }
                add("Billing Cycle", waterServicePoint.getBillingCycle(), false, info, userContext);
                add("Route", waterServicePoint.getRoute(), false, info, userContext);
                add("Budget Bill", waterServicePoint.getBudgetbill(), false, info, userContext);
                add("Shutoff Date", waterServicePoint.getShutOffDate(), false, info, userContext);
                add("Connect Date", waterServicePoint.getConnectDate(), false, info, userContext);
                add("Disconnect Date", waterServicePoint.getDisconnectDate(), false, info, userContext);

                // Water Meter
                if (waterServicePoint.getWaterMeter().getWaterNameplate() != null) {
                    if (waterServicePoint.getWaterMeter().getWaterNameplate().getInstallType() != null) {
                        add("Water Meter - Install Type",
                            waterServicePoint.getWaterMeter().getWaterNameplate().getInstallType().value(), false,
                            info, userContext);
                    }
                    if (waterServicePoint.getWaterMeter().getWaterNameplate().getFluidType() != null) {
                        add("Water Meter - Fluid Type",
                            waterServicePoint.getWaterMeter().getWaterNameplate().getFluidType().value(), false, info,
                            userContext);
                    }
                    if (waterServicePoint.getWaterMeter().getWaterNameplate().getDriveType() != null) {
                        add("Water Meter - Drive Type",
                            waterServicePoint.getWaterMeter().getWaterNameplate().getDriveType().value(), false, info,
                            userContext);
                    }
                    add("Water Meter - Pipe Size", waterServicePoint.getWaterMeter().getWaterNameplate().getPipeSize(),
                        false, info, userContext);
                }

                add("Rate Code", waterServicePoint.getRateCode(), false, info, userContext);
                add("Service Sub Type", waterServicePoint.getServiceSubType(), false, info, userContext);

                /*
                 * if (waterServicePoint.getPrimaryIdentifier() != null) {
                 * waterServicePointMap.put(waterServicePoint.getPrimaryIdentifier().getValue(), info);
                 * }
                 */

            });

        return info;
    }

    private void makeBillingInformation(BillingStatusInformation billingInfo, List<Info> info,
            YukonUserContext userContext) {

        billingInfo.getBillingStatusItem().forEach(
            billingItem -> {
                add("Account Recivable", billingItem.getAccountsReceivable(), false, info, userContext);
                add("Account Recivable this Period", billingItem.getAccountsReceivableThisPeriod(), false, info,
                    userContext);
                if (billingItem.getBillingItemsType() != null) {
                    add("Billing Type", billingItem.getBillingItemsType().getValue(), false, info, userContext);
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

                if (accountInfo == MspAccountInformationInfo.DATE) {
                    this.value = formatDate((Date) value, userContext);
                } else if (accountInfo == MspAccountInformationInfo.CALENDAR) {
                    formatDate(((Calendar) value).getTime(), userContext);
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
