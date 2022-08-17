package com.cannontech.web.stars.dr.operator.inventory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.model.AssetReportDevice;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.service.AssetReportService;
import com.google.common.collect.Lists;
import com.opencsv.CSVWriter;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class AssetReportController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private AssetReportService assetReportService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DateFormattingService dateFormatting;
    
    private Logger log = YukonLogManager.getLogger(AssetReportController.class);
    private static final String[] header = new String[] {
        "SERIAL_NUMBER",
        "METER_NUMBER",
        "TYPE",
        "NAME",
        "LABEL",
        "ACCOUNT_NUMBER"
    };
    
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
        
        return "operator/inventory/report/table.jsp";
    }

    @RequestMapping("/operator/inventory/report/download")
    public void download(HttpServletResponse resp, YukonUserContext userContext, InventoryCollection collection)
            throws IOException {

        LiteYukonUser user = userContext.getYukonUser();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        int ecId = ecDao.getEnergyCompany(user).getId();
        BlockingQueue<AssetReportDevice> queue = new ArrayBlockingQueue<AssetReportDevice>(100000);
        AtomicBoolean isCompleted = new AtomicBoolean(false);
        String date = dateFormatting.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        String fileName = "AssetsDeviceReport_" + date + ".csv";

        queueDevices(collection, ecId, queue, isCompleted);

        resp.setContentType("text/csv");
        resp.setHeader("Content-Type", "application/force-download");
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
             CSVWriter csvWriter = new CSVWriter(writer);) {
            csvWriter.writeNext(header);
            while (true) {
                if (!isCompleted.compareAndSet(true, false)) {
                    if (!queue.isEmpty()) {
                        AssetReportDevice device = queue.take();
                        if (device != null) {
                            List<String> row = Lists.newArrayList();
                            row.add(device.getSerialNumber());
                            row.add(device.getMeterNumber());
                            HardwareType type = device.getInventoryIdentifier().getHardwareType();
                            if (type.isMeter()) {
                                row.add(accessor.getMessage(device.getPaoIdentifier().getPaoType()));
                            } else {
                                row.add(accessor.getMessage(type));
                            }

                            if (device.getDeviceId() > 0) {
                                row.add(device.getName());
                            } else {
                                row.add("");
                            }
                            row.add(device.getLabel());
                            if (device.getAccountId() > 0) {
                                row.add(device.getAccountNo());
                            } else {
                                row.add("");
                            }
                            String[] dataRows = new String[row.size()];
                            dataRows = row.toArray(dataRows);
                            csvWriter.writeNext(dataRows);
                        }
                    }
                } else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.error("Error while downloading " + e);
        }
    }

    private List<AssetReportDevice> getDevices(InventoryCollection collection, int ecId) {
        
        List<Integer> ids = Lists.transform(collection.getList(), YukonInventory.TO_INVENTORY_ID);
        
        return assetReportService.getAssetReportDevices(ecId, ids);
    }

    private void queueDevices(InventoryCollection collection, int ecId, BlockingQueue<AssetReportDevice> queue,
            AtomicBoolean isCompleted) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<Integer> ids = Lists.transform(collection.getList(), YukonInventory.TO_INVENTORY_ID);
        executorService.submit(new DataQueuer(ecId, ids, queue, isCompleted));
    }

    /**
     * Thread to execute query for queuing devices.
     */
    private class DataQueuer extends Thread {
        BlockingQueue<AssetReportDevice> queue;
        int ecId;
        List<Integer> assetIds;
        AtomicBoolean isCompleted;

        DataQueuer(int ecId, List<Integer> ids, BlockingQueue<AssetReportDevice> queue, AtomicBoolean isCompleted) {
            this.queue = queue;
            this.isCompleted = isCompleted;
            this.assetIds = ids;
            this.ecId = ecId;
        }

        @Override
        public void run() {
            assetReportService.queueAssetReportDevices(ecId, assetIds, queue, isCompleted);
        }
    }

}