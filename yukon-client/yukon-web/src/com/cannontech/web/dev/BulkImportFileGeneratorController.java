package com.cannontech.web.dev;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.tools.zip.ZipWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.opencsv.CSVWriter;

@Controller
@RequestMapping("/bulkImportFileGenerator/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class BulkImportFileGeneratorController {
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private MeterDao meterDao;
    private final static Set<PaoType> allMeterTypes = PaoType.getMeterTypes();

    @RequestMapping("home")
    public String bulkImportFileGenerator(ModelMap model, YukonUserContext userContext) {

        return "bulkImportFileGenerator/fileGenerator.jsp";
    }

    @RequestMapping("fileGenerator")
    public String bulkImportFileGenerator(@RequestParam String deviceGroupName, @RequestParam String appendedText,
            HttpServletResponse response, FlashScope flashScope) throws IOException {

        if (StringUtils.isBlank(deviceGroupName) || StringUtils.isBlank(appendedText)) {
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("Both input fields are required"));
            return "redirect:home";
        }

        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(deviceGroupName);
        Set<SimpleDevice> devices = deviceGroupService.getDevices(Collections.singletonList(deviceGroup));

        List<SimpleDevice> meterPaos = new ArrayList<>();
        for (SimpleDevice device : devices) {
            if (allMeterTypes.contains(device.getDeviceType())) {
                meterPaos.add(device);
            }
        }

        List<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meterPaos);

        DateTime now = new DateTime(DateTimeZone.getDefault());
        String zipFilename = "BulkImport" + "-" + now.toString("yyyy-MM-dd-HHmmss") + ".zip";
        File file = new File(CtiUtilities.getYukonBase() + "\\Server\\web\\temp\\" + zipFilename);
        ZipWriter zipWriter = new ZipWriter(file);

        Writer pw = zipWriter.getBufferedWriter("bulkImport", "BulkImport.csv");

        String[] headerRow = new String[3];
        headerRow[0] = BulkFieldColumnHeader.DEVICE_ID.name();
        headerRow[1] = BulkFieldColumnHeader.NAME.name();
        headerRow[2] = BulkFieldColumnHeader.METER_NUMBER.name();

        CSVWriter csvWriter = new CSVWriter(pw);
        csvWriter.writeNext(headerRow);

        for (YukonMeter device : yukonMeters) {
            String[] dataRow = new String[3];
            dataRow[0] = Integer.toString(device.getDeviceId());
            dataRow[1] = device.getName();
            dataRow[2] = device.getMeterNumber();
            csvWriter.writeNext(dataRow);
        }
        pw.flush();

        Writer newPw = zipWriter.getBufferedWriter("bulkImport", "NewBulkImport.csv");
        CSVWriter newCsvWriter = new CSVWriter(newPw);
        newCsvWriter.writeNext(headerRow);

        for (YukonMeter device : yukonMeters) {
            String[] dataRow = new String[3];
            dataRow[0] = Integer.toString(device.getDeviceId());
            dataRow[1] = device.getName() + "_" + appendedText;
            dataRow[2] = device.getMeterNumber() + "_" + appendedText;
            newCsvWriter.writeNext(dataRow);
        }

        newPw.flush();
        zipWriter.close();

        response.setContentType("application/zip");

        // set response header to the filename
        response.setHeader("Content-Disposition", "attachment; filename=\"" + ServletUtil.urlEncode(file.getName())+"\"");
        response.setHeader("Content-Length", Long.toString(file.length()));

        // Download the file through the response object
        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        file.delete();
        return null;
    }

}
