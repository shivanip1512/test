package com.cannontech.web.stars.dr.operator.inventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.MeteringType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.model.AssetReportDevice;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.service.AssetReportService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class AssetReportController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private AssetReportService assetReportService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private DateFormattingService dateFormatting;
    
    private static final EnergyCompanySettingType meteringSetting = EnergyCompanySettingType.METER_MCT_BASE_DESIGNATION;
    
    @RequestMapping("/operator/inventory/report")
    public String report(ModelMap model, InventoryCollection collection) {
        
        model.addAttribute("inventoryCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        return "operator/inventory/report/report.jsp";
    }
    
    @RequestMapping("/operator/inventory/report/data")
    public String report(ModelMap model, LiteYukonUser user, InventoryCollection collection, PagingParameters paging) {
        
        EnergyCompany ec = ecDao.getEnergyCompany(user);
        int ecId = ec.getId();
        
        List<AssetReportDevice> devices = getDevices(collection, ecId);
        SearchResults<AssetReportDevice> paged = SearchResults.pageBasedForWholeList(paging, devices);
        model.addAttribute("devices", paged);
        
        MeteringType meteringType = ecSettingDao.getEnum(meteringSetting, MeteringType.class, ecId);
        model.addAttribute("starsMetering", meteringType == MeteringType.stars);
        
        return "operator/inventory/report/table.jsp";
    }
    
    @RequestMapping("/operator/inventory/report/download")
    public void download(HttpServletResponse resp, YukonUserContext userContext, InventoryCollection collection) 
    throws IOException {
        
        LiteYukonUser user = userContext.getYukonUser();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        int ecId = ecDao.getEnergyCompany(user).getId();
        MeteringType meteringType = ecSettingDao.getEnum(meteringSetting, MeteringType.class, ecId);
        
        List<AssetReportDevice> devices = getDevices(collection, ecId);
        
        String[] header = new String[6];
        
        header[0] = "SERIAL_NUMBER";
        header[1] = "METER_NUMBER";
        header[2] = "TYPE";
        header[3] = "NAME";
        header[4] = "LABEL";
        header[5] = "ACCOUNT_NUMBER";
        
        List<String[]> data = new ArrayList<>();
        for(AssetReportDevice device: devices) {
            
            String[] dataRow = new String[6];
            dataRow[0] = device.getSerialNumber();
            dataRow[1] = device.getMeterNumber();
            
            HardwareType type = device.getInventoryIdentifier().getHardwareType();
            if (type.isMeter() && meteringType != MeteringType.stars) {
                dataRow[2] = accessor.getMessage(device.getPaoIdentifier().getPaoType());
            } else {
                dataRow[2] = accessor.getMessage(type);
            }
            
            if (device.getDeviceId() > 0 ) dataRow[3] = device.getName();
            dataRow[4] = device.getLabel();
            if (device.getAccountId() > 0 ) dataRow[5] = device.getAccountNo();
            
            data.add(dataRow);
        }
        
        String date = dateFormatting.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        
        //write out the file
        WebFileUtils.writeToCSV(resp, header, data, "AssetsDeviceReport_" + date + ".csv");
    }
    
    private List<AssetReportDevice> getDevices(InventoryCollection collection, int ecId) {
        
        List<Integer> ids = Lists.transform(collection.getList(), new Function<InventoryIdentifier, Integer>() {
            @Override
            public Integer apply(InventoryIdentifier input) {
                return input.getInventoryId();
            }
        });
        
        return assetReportService.getAssetReportDevices(ecId, ids);
    }
    
}