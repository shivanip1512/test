package com.cannontech.web.dynamicBilling;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import net.sf.jsonOLD.JSONArray;
import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.device.base.BillingData;
import com.cannontech.billing.device.base.BillingDeviceBase;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.billing.format.dynamic.DynamicBillingFormatter;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.dao.DynamicBillingFileDao;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.common.dynamicBilling.model.DynamicBillingField;
import com.cannontech.common.dynamicBilling.model.DynamicFormat;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping(value = "/*")
@CheckRoleProperty(YukonRoleProperty.DYNAMIC_BILLING_FILE_SETUP)
public class DynamicBillingController {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DynamicBillingFormatter dynamicFormatter;
    @Autowired private DynamicBillingFileDao dynamicBillingFileDao;

    @RequestMapping
    public String overview(ModelMap model) {
        List<DynamicFormat> allRows = dynamicBillingFileDao.retrieveAll();
        model.addAttribute("allRows", allRows);
        
        return "overview.jsp";
    }

    @RequestMapping
    public String create(ModelMap model, final YukonUserContext context) {
        List<String> availableFields = generateFieldList(BillableField.values());
        availableFields.add(0, "Plain Text");
        model.addAttribute("availableFields", availableFields);
        
        List<String> readingTypes = getValidReadingTypes();
        model.addAttribute("readingTypes", readingTypes);
        
        List<String> readingChannels = getValidReadingChannels();
        model.addAttribute("readingChannels", readingChannels);
        
        List<String> roundingModes = getValidRoundingModes();
        model.addAttribute("roundingModes", roundingModes);
        
        DynamicFormat format = new DynamicFormat();
        format.setDelim(",");
        model.addAttribute("format", format);
        
        model.addAttribute("initiallySelected", -1);
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        model.addAttribute("title", messageSourceAccessor.getMessage("yukon.web.modules.amr.billing.DETAIL.pageName.create"));
        
        return "formatDetail.jsp";
    }

    @RequestMapping
    public String delete(int availableFormat, ModelMap model) {
        // delete the selected format
        dynamicBillingFileDao.delete(availableFormat);
        
        // retrieve the new list of format names after deletion
        List<DynamicFormat> allRows = dynamicBillingFileDao.retrieveAll();
        model.addAttribute("allRows", allRows);
        
        return "redirect:overview.jsp";
    }

    @RequestMapping
    public String edit(int availableFormat, ModelMap model, final YukonUserContext context) {
        // retrieve the format information
        DynamicFormat formatSelected = dynamicBillingFileDao.retrieve(availableFormat);
        
        // send the appropriate system or dynamic format to the page
        if (formatSelected.getIsSystem()) {
            model.addAttribute("initiallySelected", -1);
        } else {
            model.addAttribute("initiallySelected", availableFormat);
        }

        // send the retrieved format to the page
        model.addAttribute("format", formatSelected);

        // put all available fields in availableFields list and send it to the page
        List<String> availableFields = new ArrayList<String>();
        availableFields = generateFieldList(BillableField.values());
        availableFields.add(0, "Plain Text");
        model.addAttribute("availableFields", availableFields);

        // send the previously saved fields to the page
        // does not check for illegal fields
        List<DynamicBillingField> selectedFields = new ArrayList<DynamicBillingField>();
        for (DynamicBillingField fields : formatSelected.getFieldList()) {
            selectedFields.add(fields);
        }
        model.addAttribute("selectedFields", selectedFields);

        List<String> readingTypes = getValidReadingTypes();
        model.addAttribute("readingTypes", readingTypes);

        List<String> readingChannels = getValidReadingChannels();
        model.addAttribute("readingChannels", readingChannels);

        List<String> roundingModes = getValidRoundingModes();
        model.addAttribute("roundingModes", roundingModes);
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        model.addAttribute("title", messageSourceAccessor.getMessage("yukon.web.modules.amr.billing.DETAIL.pageName.edit"));

        return "formatDetail.jsp";
    }

    /**
     * Copy the selected format. the selected format will still remain and the
     * saved copied format will have a different fid
     * 
     */
    @RequestMapping
    public String copy(int availableFormat, ModelMap model, final YukonUserContext context) {
        // retrieve the format information, 
        // assert that this is a copy into the name
        DynamicFormat formatSelected = dynamicBillingFileDao.retrieve(availableFormat);

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        formatSelected.setName(messageSourceAccessor.getMessage("yukon.web.billing.formatNameCopy", formatSelected.getName()));

        // to give it new format id, give it -1
        model.addAttribute("initiallySelected", -1);

        // send the retrieved format to the page
        model.addAttribute("format", formatSelected);

        // put all available fields in availableFields and send it to the page
        List<String> availableFields = new ArrayList<String>();
        availableFields = generateFieldList(BillableField.values());
        availableFields.add(0, "Plain Text");
        model.addAttribute("availableFields", availableFields);

        // send the previously saved fields to the page
        // does not check for account number and meter position number fields
        // assumes that all the fields in the available fields are consistent
        List<DynamicBillingField> selectedFields = new ArrayList<DynamicBillingField>();
        for (DynamicBillingField fields : formatSelected.getFieldList()) {
            selectedFields.add(fields);
        }
        model.addAttribute("selectedFields", selectedFields);

        List<String> readingTypes = getValidReadingTypes();
        model.addAttribute("readingTypes", readingTypes);
        
        List<String> readingChannels = getValidReadingChannels();
        model.addAttribute("readingChannels", readingChannels);

        List<String> roundingModes = getValidRoundingModes();
        model.addAttribute("roundingModes", roundingModes);

        model.addAttribute("title", "Edit Format");

        return "formatDetail.jsp";
    }
    
    @RequestMapping
    public String save(int formatId, String formatName, String footer, String header, String delimiter, String fieldArray, ModelMap model) {
        // retrieve all information from the page and save it to db
        DynamicFormat savedFormat = parseIntoDynamicFormat(formatId, formatName, footer, header, delimiter, fieldArray);
        dynamicBillingFileDao.save(savedFormat);
        
        // retrieve new list of formats after saving
        List<DynamicFormat> allRows = dynamicBillingFileDao.retrieveAll();

        model.addAttribute("allRows", allRows);

        return "redirect:overview.jsp";
    }

    /**
     * This method handles the request from the page to display the preview
     * of all the fields along with format, delimiter, header and footer. this
     * method calls the actual method to generate the file and the result is
     * then parsed to be html friendly and then sent to the page
     */
    @RequestMapping
    public @ResponseBody String updatePreview(int formatId, String formatName, String footer, String header, String delimiter, String fieldArray, ModelMap model) {

        StringBuffer returnString = new StringBuffer();
        DynamicFormat format = parseIntoDynamicFormat(formatId, formatName, footer, header, delimiter, fieldArray);

        dynamicFormatter.setDynamicFormat(format);

        BillableDevice device = this.getDefaultDevice();

        // use the class method getBillingFileString to give us
        // header+generated string+footer
        returnString.append(StringEscapeUtils.escapeHtml(dynamicFormatter
                .getBillingFileString(Collections.singletonList(device))
                .toString()));

        // for our html page purposes, replace carriage returns with <BR>
        returnString = new StringBuffer(returnString.toString().replaceAll("\r\n", "<BR>"));

        // replace spaces with &nbsp; for html display
        return returnString.toString().replaceAll(" ", "&nbsp;");
    }

    @RequestMapping
    public @ResponseBody String updateFormatName(int formatId, String formatName, ModelMap model, final YukonUserContext context) throws ServletException {

        DynamicFormat format = new DynamicFormat();
        format.setFormatId(formatId);
        format.setName(formatName);
            
        if (!dynamicBillingFileDao.isFormatNameUnique(format)){
            
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
            return messageSourceAccessor.getMessage("yukon.web.billing.nonUniqueFormatName");
        }

        // returns a blank text view since to error occured.   
        return null;
    }

    /**
     * Helper method to create a list of billable field names for the UI. Will
     * try to remove account number and meter position number from the list.
     * 
     * @param fields - Array of fields
     * @return A list of the names of fields
     */
    private List<String> generateFieldList(BillableField[] fields) {

        List<String> tempList = new ArrayList<String>();
        for (BillableField tempString : fields) {

            // if it has account number, skip iteration
            if (tempString.equals(BillableField.accountNumber)) {
                continue;
            }

            // if it has meter position number, skip iteration
            if (tempString.equals(BillableField.meterPositionNumber)) {
                continue;
            }

            // put the word reading and timestamp into the right fields
            if (tempString.hasValue() || tempString.hasTimestamp()) {
//                    || tempString.hasRate() || tempString.hasUnitOfMeasure() ) {   //not being used yet, will uncomment at a later time
                if (tempString.hasValue()) {

                    // Append reading to end of field name and add to list
                    tempList.add(tempString + " - reading");
                }
                if (tempString.hasTimestamp()) {
                    
                    // Append timestamp to end of field name and add to list
                    tempList.add(tempString + " - timestamp");
                } 
                
                /** This section has been commented out as it is not being used yet, but has been implemented */
                /*
                if (tempString.hasRate()) {

                    // Append rate to end of field name and add to list
                    tempList.add(tempString + " - rate");
                }
                if (tempString.hasUnitOfMeasure()) {

                    // Append unitMeasure to end of field name and add to list
                    tempList.add(tempString + " - unitMeasure");
                }
                */
            } else {
                tempList.add(tempString + "");
            }
        }
        return tempList;
    }

    /**
     * Helper method to get the parameters out of the request and build a DynamicFormat
     * object.
     */
    private DynamicFormat parseIntoDynamicFormat(int formatId, String formatName, String footer, String header, String delimiter, String fieldArray) {
        
        DynamicFormat format = new DynamicFormat();
        
        List<DynamicBillingField> fieldList = format.getFieldList();
        
        format.setFormatId(formatId);
        format.setName(formatName);
        format.setFooter(footer);
        format.setHeader(header);
        format.setDelim(delimiter);
        
        // selectedFields is an JSON String representation containing the fields
        // that customer chooses
        JSONArray myJSONArray = new JSONArray(fieldArray);
        
        // for loop for temporarily saving the selected fields, as well as the
        // formats associated(date, value)
        for (int i = 0; i < myJSONArray.length(); i++) {
            DynamicBillingField field = new DynamicBillingField();
            field.setFormat("");
            field.setFormatId(format.getFormatId());
            field.setOrder(i);
            
            JSONObject object = myJSONArray.getJSONObject(i);
            
            field.setName(object.getString("field"));
            field.setFormat(object.getString("format"));
            
            String maxLength = object.getString("maxLength");
            if(!StringUtils.isEmpty(maxLength)) {
                field.setMaxLength(Integer.valueOf(maxLength));
            }
            
            field.setPadChar(object.getString("padChar"));
            
            String padSide = object.getString("padSide");
            if(!StringUtils.isEmpty(padSide)){
                field.setPadSide(padSide);
            }
            
            String readingTypeStr = object.getString("readingType");
            if(!StringUtils.isEmpty(readingTypeStr)){
                ReadingType readingType = ReadingType.valueOf(readingTypeStr);
                field.setReadingType(readingType);
            }
            
            String readingChannelStr = object.getString("readingChannel");
            if(!StringUtils.isEmpty(readingChannelStr)){
                Channel channel = Channel.valueOf(readingChannelStr);
                field.setChannel(channel);
            }
            
            String roundingModeStr = object.getString("roundingMode");
            if(!StringUtils.isEmpty(roundingModeStr)){
                RoundingMode roundingMode = RoundingMode.valueOf(roundingModeStr);
                field.setRoundingMode(roundingMode);
            }
            
            fieldList.add(i, field);
        }
        format.setFieldList(fieldList);
        
        return format;
    }

    /**
     * Helper method to create a BillableDevice populated with some default hard-coded values
     * for example purposes
     * 
     * @return Default billable device
     */
    private BillableDevice getDefaultDevice() {
        
        BillingDeviceBase device = new BillingDeviceBase() {
            
            @Override
            public void populate(PointIdentifier pointIdentifier,
                    Timestamp timestamp, double value, int unitOfMeasure,
                    String pointName, DeviceData meterData) {
                throw new NotImplementedException();
            }
            @Override
            public boolean isEnergy(PointIdentifier pointIdentifier) {
                throw new NotImplementedException();
            }
            @Override
            public boolean isDemand(PointIdentifier pointIdentifier) {
                throw new NotImplementedException();
            }
        };
        
        // Add meter data to channel one
        device.addData(Channel.ONE, ReadingType.DEVICE_DATA,
                BillableField.meterNumber, "111111111");
        
        device.addData(Channel.ONE, ReadingType.DEVICE_DATA,
                BillableField.accountNumber, "111111111");
        
        device.addData(Channel.ONE, ReadingType.DEVICE_DATA,
                BillableField.meterPositionNumber, "1");
        
        device.addData(Channel.ONE, ReadingType.DEVICE_DATA,
                BillableField.address, "123456789");
        
        device.addData(Channel.ONE, ReadingType.DEVICE_DATA,
                BillableField.paoName, "Default Device");
        
        // Add total consumption
        BillingData data = new BillingData();
        data.setData("total kWh");
        data.setValue(123456789.0123456);
        data.setUnitOfMeasure(UnitOfMeasure.KWH.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.totalConsumption, data);
        
        // Add rate A consumption
        data = new BillingData();
        data.setData("rate A kWh");
        data.setValue(123456789.12345);
        data.setUnitOfMeasure(UnitOfMeasure.KWH.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.rateAConsumption, data);
        
        // Add rate B consumption
        data = new BillingData();
        data.setData("rate B kWh");
        data.setValue(123456789.54321);
        data.setUnitOfMeasure(UnitOfMeasure.KWH.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.rateBConsumption, data);
        
        // Add rate C consumption
        data = new BillingData();
        data.setData("rate C kWh");
        data.setValue(123456789.01234);
        data.setUnitOfMeasure(UnitOfMeasure.KWH.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.rateCConsumption, data);
        
        // Add rate D consumption
        data = new BillingData();
        data.setData("rate D kWh");
        data.setValue(123456789.0);
        data.setUnitOfMeasure(UnitOfMeasure.KWH.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.rateDConsumption, data);
        
        // Add total peak demand
        data = new BillingData();
        data.setData("total peak kW");
        data.setValue(9413.95);
        data.setUnitOfMeasure(UnitOfMeasure.KW.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.totalPeakDemand, data);
        
        // Add rate A demand
        data = new BillingData();
        data.setData("rate A kW");
        data.setValue(9713.012345);
        data.setUnitOfMeasure(UnitOfMeasure.KW.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.rateADemand, data);
        
        // Add rate B demand
        data = new BillingData();
        data.setData("rate B kW");
        data.setValue(9413.0);
        data.setUnitOfMeasure(UnitOfMeasure.KW.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.rateBDemand, data);
        
        // Add rate C demand
        data = new BillingData();
        data.setData("rate C kW");
        data.setValue(9413.0);
        data.setUnitOfMeasure(UnitOfMeasure.KW.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.rateCDemand, data);
        
        // Add rate D demand
        data = new BillingData();
        data.setData("rate D kW");
        data.setValue(9413.0);
        data.setUnitOfMeasure(UnitOfMeasure.KW.getId());
        data.setTimestamp(new Timestamp(new Date().getTime()));
        
        setAllReadingTypes(device, BillableField.rateDDemand, data);
        
        return device;
    }

    /**
     * This function sets all the default data for all readingTypes
     * 
     * @param device
     * @param billableField
     * @param data
     */
    private void setAllReadingTypes(BillingDeviceBase device,
            BillableField billableField, BillingData data) {
        
        ReadingType[] readingTypes = ReadingType.values();
        for (ReadingType readingType : readingTypes) {
            device.addData(Channel.ONE, readingType, billableField, data);
            
            // Add some channel 2/3 data for total fields.
            if (billableField == BillableField.totalConsumption ||
                    billableField == BillableField.totalPeakDemand ) {
                device.addData(Channel.TWO, readingType, billableField, data);
                device.addData(Channel.THREE, readingType, billableField, data);
            }
        }
    }

    private List<String> getValidReadingTypes(){
        List<String> readingTypeStrs = new ArrayList<String>();
        
        Set<ReadingType> readingTypeExcludeList = Collections.singleton(ReadingType.DEVICE_DATA);
        ReadingType[] readingTypes = ReadingType.values();
        for (ReadingType readingType : readingTypes) {
            if(!readingTypeExcludeList.contains(readingType)){
                readingTypeStrs.add(readingType.toString());
            }
        }
        
        return readingTypeStrs;
    }

    private List<String> getValidReadingChannels(){
        List<String> readingChannelStrs = new ArrayList<String>();
        
        Set<Channel> readingChannelExcludeList = Collections.singleton(Channel.FOUR);
        Channel[] channels = Channel.values();
        for (Channel channel : channels) {
            if(!readingChannelExcludeList.contains(channel)){
                readingChannelStrs.add(channel.toString());
            }
        }        
        return readingChannelStrs;
    }

    private List<String> getValidRoundingModes(){
        List<String> roundingModeStrs = new ArrayList<String>();
        
        Set<RoundingMode> roundingModeExcludeList = Collections.singleton(RoundingMode.UNNECESSARY);
        RoundingMode[] roundingModes = RoundingMode.values();
        for (RoundingMode roundingMode : roundingModes) {
            if(!roundingModeExcludeList.contains(roundingMode)){
                roundingModeStrs.add(roundingMode.toString());
            }
        }
        
        return roundingModeStrs;
    }
}