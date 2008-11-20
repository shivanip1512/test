package com.cannontech.stars.ws.handlerImpl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsInventoryBaseService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.StarsClientRequestException;
import com.cannontech.stars.ws.dto.StarsControllableDeviceDTO;
import com.cannontech.stars.ws.handler.StarsControllableDeviceHandler;

import static com.cannontech.stars.util.StarsClientRequestException.INVALID_ARGUMENT;
import static com.cannontech.stars.util.StarsClientRequestException.ACCOUNT_NOT_FOUND;
import static com.cannontech.stars.util.StarsClientRequestException.DEVICE_NOT_FOUND;
import static com.cannontech.stars.util.StarsClientRequestException.DEVICE_ALREADY_EXISTS;
import static com.cannontech.stars.util.StarsClientRequestException.DEVICE_ASSIGNED_TO_ANOTHER_ACCOUNT;
import com.cannontech.user.YukonUserContext;

public class StarsControllableDeviceHandlerImpl implements
        StarsControllableDeviceHandler {

    private static final Logger log = YukonLogManager.getLogger(StarsControllableDeviceHandlerImpl.class);
    private CustomerAccountDao customerAccountDao;
    private StarsSearchDao starsSearchDao;
    private StarsInventoryBaseService starsInventoryBaseService;

    // Spring-IOC
    public void setStarsInventoryBaseService(
            StarsInventoryBaseService starsInventoryBaseService) {
        this.starsInventoryBaseService = starsInventoryBaseService;
    }

    // Spring-IOC
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }

    // Spring-IOC
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    private String getAccountNumber(StarsControllableDeviceDTO deviceInfo) {
        String acctNum = deviceInfo.getAccountNumber();
        if (StringUtils.isBlank(acctNum)) {
            throw new StarsClientRequestException(INVALID_ARGUMENT,
                                                  "Account Number is required");
        }
        return acctNum;
    }

    private String getSerialNumber(StarsControllableDeviceDTO deviceInfo) {
        String serialNum = deviceInfo.getSerialNumber();
        if (StringUtils.isBlank(serialNum)) {
            throw new StarsClientRequestException(INVALID_ARGUMENT,
                                                  "Serial Number is required");
        }
        return serialNum;
    }

    private String getDeviceType(StarsControllableDeviceDTO deviceInfo) {
        String deviceType = deviceInfo.getDeviceType();
        if (StringUtils.isBlank(deviceType)) {
            throw new StarsClientRequestException(INVALID_ARGUMENT,
                                                  "Device type is required");
        }
        return deviceType;
    }

    private CustomerAccount getCustomerAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        CustomerAccount custAcct = customerAccountDao.getByAccountNumber(getAccountNumber(deviceInfo),
                                                                         energyCompany.getLiteID());
        if (custAcct == null) {
            String msg = "Account not found, accountNumber=[" + getAccountNumber(deviceInfo) + "]";
            msg += ", energyCompany=[" + energyCompany.getName() + "]";
            log.error(msg);
            throw new StarsClientRequestException(ACCOUNT_NOT_FOUND, msg);
        }
        return custAcct;
    }

    private int getDeviceTypeId(StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        int deviceTypeId = InventoryUtils.getDeviceTypeID(getDeviceType(deviceInfo),
                                                          energyCompany);
        if (deviceTypeId <= 0) {
            String msg = "Invalid device type=[" + getDeviceType(deviceInfo) + "]";
            msg += ", energyCompany=[" + energyCompany.getName() + "]";
            log.error(msg);
            throw new StarsClientRequestException(INVALID_ARGUMENT, msg);
        }
        return deviceTypeId;
    }

    /**
     * For Import - Populates the Model object and calls Service to add a
     * hardware device to the customer account. Handles only LMHardware devices
     * for now, will need to support other device types later.
     * @param fields
     * @param energyCompany
     * @param userContext
     * @return LiteInventoryBase
     */
    public LiteInventoryBase addDeviceToAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany, YukonUserContext userContext) {

        LiteInventoryBase liteInv = null;
        try {
            // Get Inventory, if exists on account
            liteInv = getInventoryOnAccount(deviceInfo, energyCompany);
            // Inventory already exists on the account
            if (liteInv != null) {
                String msg = "Hardware already exists on the account, accountNumber=[" + getAccountNumber(deviceInfo) + "]";
                msg += ", serialNumber=[" + getSerialNumber(deviceInfo) + "], energyCompany=[" + energyCompany.getName() + "]";
                log.error(msg);
                throw new StarsClientRequestException(DEVICE_ALREADY_EXISTS,
                                                      msg);
            }

            // add device to account
            liteInv = internalAddDeviceToAccount(deviceInfo,
                                                 energyCompany,
                                                 userContext);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return liteInv;
    }

    // Gets Inventory if exists on the account
    private LiteInventoryBase getInventoryOnAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        LiteInventoryBase liteInvAcct = null;

        try {
            CustomerAccount custAcct = getCustomerAccount(deviceInfo,
                                                          energyCompany);
            LiteInventoryBase liteInv = getInventory(deviceInfo, energyCompany);
            if (liteInv != null && liteInv.getAccountID() == custAcct.getAccountId()) {
                liteInvAcct = liteInv;
            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return liteInvAcct;

    }

    // Gets Inventory if exists for the energyCompany
    // Handles only LMHardware devices for now, will need to support other
    // device types later.
    private LiteInventoryBase getInventory(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        LiteInventoryBase liteInv = null;

        try {
            int deviceTypeId = getDeviceTypeId(deviceInfo, energyCompany);
            int categoryID = InventoryUtils.getInventoryCategoryID(deviceTypeId,
                                                                   energyCompany);
            boolean lmHardware = InventoryUtils.isLMHardware(categoryID);
            if (lmHardware) {
                LiteStarsLMHardware lmhw = (LiteStarsLMHardware) starsSearchDao.searchLMHardwareBySerialNumber(getSerialNumber(deviceInfo),
                                                                                                               energyCompany);
                liteInv = lmhw;
            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return liteInv;
    }

    // Creates new Inventory based on the deviceType
    // Handles only LMHardware devices for now, will need to support other
    // device types later.
    private LiteInventoryBase createNewInventory(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany) {
        LiteInventoryBase liteInv = null;

        try {
            int deviceTypeId = getDeviceTypeId(deviceInfo, energyCompany);
            int categoryID = InventoryUtils.getInventoryCategoryID(deviceTypeId,
                                                                   energyCompany);
            boolean lmHardware = InventoryUtils.isLMHardware(categoryID);
            if (lmHardware) {
                LiteStarsLMHardware lmhw = new LiteStarsLMHardware();
                lmhw.setCategoryID(categoryID);
                lmhw.setLmHardwareTypeID(deviceTypeId);
                lmhw.setManufacturerSerialNumber(getSerialNumber(deviceInfo));
                lmhw.setInstallDate(new Date().getTime());
                // force not null
                lmhw.setAlternateTrackingNumber("");
                lmhw.setNotes("");
                lmhw.setDeviceLabel("");

                liteInv = lmhw;
            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return liteInv;
    }

    private LiteInventoryBase internalAddDeviceToAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany, YukonUserContext userContext) {

        LiteInventoryBase liteInv = null;
        try {
            // See if Inventory exists
            liteInv = getInventory(deviceInfo, energyCompany);

            if (liteInv != null) {
                // Inventory associated to an account, error out
                if (liteInv.getAccountID() > 0) {
                    String msg = "Hardware already assigned to another account, serialNumber=[" + getSerialNumber(deviceInfo) + "]";
                    msg += ", energyCompany=[" + energyCompany.getName() + "], requested accountNumber=[" + getAccountNumber(deviceInfo) + "]";
                    log.error(msg);
                    throw new StarsClientRequestException(DEVICE_ASSIGNED_TO_ANOTHER_ACCOUNT,
                                                          msg);
                }
                // existing inventory, reset some fields
                liteInv.setRemoveDate(0);
                liteInv.setInstallDate(new Date().getTime());
            }// Inventory doesn't exist, create a new one
            else {
                liteInv = createNewInventory(deviceInfo, energyCompany);
            }

            // By this point, we should have an existing or new Inventory to add
            // to account
            if (liteInv == null) {
                String msg = "Invalid device type=[" + getDeviceType(deviceInfo) + "]";
                msg += ", energyCompany=[" + energyCompany.getName() + "]";
                throw new StarsClientRequestException(INVALID_ARGUMENT, msg);
            }
            // set common fields on the Inventory
            setInventoryValues(deviceInfo, liteInv, energyCompany);

            // call service to add device on the customer account
            liteInv = starsInventoryBaseService.addDeviceToAccount(liteInv,
                                                                   energyCompany,
                                                                   userContext.getYukonUser());

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return liteInv;
    }

    private void setInventoryValues(StarsControllableDeviceDTO deviceInfo,
            LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) {
        // update install date, if specified
        Date installDate = deviceInfo.getFieldInstallDate();
        if (installDate != null) {
            liteInv.setInstallDate(installDate.getTime());
        }

        // update Device label, if specified
        String deviceLabel = deviceInfo.getDeviceLabel();
        if (deviceLabel != null) {
            liteInv.setDeviceLabel(deviceLabel);
        }

        // Derive Account Id
        CustomerAccount custAcct = getCustomerAccount(deviceInfo, energyCompany);
        liteInv.setAccountID(custAcct.getAccountId());

        // Derive and update Installation Company id, if specified
        String serviceCompany = deviceInfo.getServiceCompanyName();
        if (serviceCompany != null) {
            if (StringUtils.isBlank(serviceCompany)) {
                liteInv.setInstallationCompanyID(0);
            } else {
                List<LiteServiceCompany> companies = energyCompany.getAllServiceCompanies();
                for (LiteServiceCompany entry : companies) {
                    if (entry.getCompanyName().equalsIgnoreCase(serviceCompany)) {
                        liteInv.setInstallationCompanyID(entry.getCompanyID());
                        break;
                    }
                }

            }
        }
    }

    public static String forceNotNull(String str) {
        return (str == null) ? "" : str.trim();
    }

    /**
     * For Import - Populates the Model object and calls Service to Update a
     * hardware device info on the customer account. Ex., Field install date,
     * Service Company etc. Handles only LMHardware devices for now, will need
     * to support other device types later.
     * @param fields
     * @param energyCompany
     * @param userContext
     * @return LiteInventoryBase
     */
    public LiteInventoryBase updateDeviceOnAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany, YukonUserContext userContext) {

        LiteInventoryBase liteInv = null;
        try {
            // Get Inventory if exists on account
            liteInv = getInventoryOnAccount(deviceInfo, energyCompany);
            // Inventory exists on the account
            if (liteInv != null) {
                // existing inventory, reset some fields
                liteInv.setRemoveDate(0);

                // set common fields on the Inventory
                setInventoryValues(deviceInfo, liteInv, energyCompany);

                // call service to update device on the customer account
                liteInv = starsInventoryBaseService.updateDeviceOnAccount(liteInv,
                                                                          energyCompany,
                                                                          userContext.getYukonUser());
            } else {
                // add device to account
                liteInv = internalAddDeviceToAccount(deviceInfo,
                                                     energyCompany,
                                                     userContext);
            }

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return liteInv;
    }

    /**
     * For Import - Populates the Model object and calls Service to Remove a
     * hardware device from the customer account. Handles only LMHardware
     * devices for now, will need to support other device types later.
     * @param fields
     * @param energyCompany
     * @param userContext
     * @return
     */
    public void removeDeviceFromAccount(StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany, YukonUserContext userContext) {

        LiteInventoryBase liteInv = null;
        try {
            // Get Inventory if exists on account
            liteInv = getInventoryOnAccount(deviceInfo, energyCompany);
            // Error, if Inventory not found on the account
            if (liteInv == null) {
                String msg = "Hardware not found on the account, accountNumber=[" + getAccountNumber(deviceInfo) + "]";
                msg += ", serialNumber=[" + getSerialNumber(deviceInfo) + "], energyCompany=[" + energyCompany.getName() + "]";
                log.error(msg);
                throw new StarsClientRequestException(DEVICE_NOT_FOUND, msg);
            }

            // Remove date defaults to current date, if not specified
            Date removeDate = deviceInfo.getFieldRemoveDate();
            if (removeDate == null) {
                removeDate = new Date();
            }
            liteInv.setRemoveDate(removeDate.getTime());

            // Remove device from the account
            starsInventoryBaseService.removeDeviceFromAccount(liteInv,
                                                              false,
                                                              energyCompany,
                                                              userContext.getYukonUser());
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
