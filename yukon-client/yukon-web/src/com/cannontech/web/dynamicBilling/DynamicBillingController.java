package com.cannontech.web.dynamicBilling;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.device.base.BillingData;
import com.cannontech.billing.device.base.BillingDeviceBase;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.billing.format.dynamic.DynamicBillingFormatter;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.dao.DynamicBillingFileDao;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.common.dynamicBilling.model.DynamicBillingField;
import com.cannontech.common.dynamicBilling.model.DynamicFormat;
import com.cannontech.web.util.TextView;

public class DynamicBillingController extends MultiActionController {

	private DynamicBillingFormatter dynamicFormatter = null;

	private DynamicBillingFileDao dynamicBillingFileDao = null;

	public ModelAndView overview(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("overview.jsp");

		List<DynamicFormat> allRows = dynamicBillingFileDao.retrieveAll();

		mav.addObject("allRows", allRows);

		return mav;
	}

	public ModelAndView create(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("formatDetail.jsp");

		List<String> availableFields = generateFieldList(BillableField.values());
		availableFields.add(0, "Plain Text");
		mav.addObject("availableFields", availableFields);

		mav.addObject("initiallySelected", -1);
		mav.addObject("title", "Create New Format");

		return mav;
	}

	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("redirect:overview.jsp");

		// delete the selected format
		int formatId = ServletRequestUtils.getIntParameter(request, "formatId");
		dynamicBillingFileDao.delete(formatId);

		// retrieve the new list of format names after deletion
		List<DynamicFormat> allRows = dynamicBillingFileDao.retrieveAll();
		mav.addObject("allRows", allRows);

		return mav;
	}

	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("formatDetail.jsp");
		int fID;

		//retrieve the format id from the page
		fID = ServletRequestUtils.getIntParameter(request, "availableFormat");

		// retrieve the format information
		DynamicFormat formatSelected = dynamicBillingFileDao.retrieve(fID);
		
		// send the appropriate system or dynamic format to the page
		if (formatSelected.getIsSystem()) {
			mav.addObject("initiallySelected", -1);
		} else {
			mav.addObject("initiallySelected", fID);
		}

		// send the retrieved format to the page
		mav.addObject("format", formatSelected);

		// put all available fields in availableFields list and send it to the page
		List<String> availableFields = new ArrayList<String>();
		availableFields = generateFieldList(BillableField.values());
		availableFields.add(0, "Plain Text");
		mav.addObject("availableFields", availableFields);

		// send the previously saved fields to the page
		// does not check for illegal fields
		List<DynamicBillingField> selectedFields = new ArrayList<DynamicBillingField>();
		for (DynamicBillingField fields : formatSelected.getFieldList()) {
			selectedFields.add(fields);
		}
		mav.addObject("selectedFields", selectedFields);

		mav.addObject("title", "Edit Bill Format");

		return mav;
	}

	/**
	 * Clone the selected format. the selected format will still remain and the
	 * saved cloned format will have a different fid
	 * 
	 */
	public ModelAndView clone(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("formatDetail.jsp");
		int fID;

		fID = ServletRequestUtils.getIntParameter(request, "availableFormat");

		// retrieve the format information, 
		// assert that this is a clone into the name
		DynamicFormat formatSelected = dynamicBillingFileDao.retrieve(fID);
		formatSelected.setName(formatSelected.getName()
				+ " (copy)");

		// to give it new format id, give it -1
		mav.addObject("initiallySelected", -1);

		// send the retrieved format to the page
		mav.addObject("format", formatSelected);

		// put all available fields in availableFields and send it to the page
		List<String> availableFields = new ArrayList<String>();
		availableFields = generateFieldList(BillableField.values());
		availableFields.add(0, "Plain Text");
		mav.addObject("availableFields", availableFields);

		// send the previously saved fields to the page
		// does not check for account number and meter position number fields
		// assumes that all the fields in the available fields are consistent
		List<DynamicBillingField> selectedFields = new ArrayList<DynamicBillingField>();
		for (DynamicBillingField fields : formatSelected.getFieldList()) {
			selectedFields.add(fields);
		}
		mav.addObject("selectedFields", selectedFields);

		mav.addObject("title", "Edit Bill Format");

		return mav;
	}
	
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ModelAndView mav = new ModelAndView("redirect:overview.jsp");

		// retrieve all information from the page and save it to db
		DynamicFormat savedFormat = parseIntoDynamicFormat(request);
		dynamicBillingFileDao.save(savedFormat);

		// retrieve new list of formats after saving
		List<DynamicFormat> allRows = dynamicBillingFileDao.retrieveAll();

		mav.addObject("allRows", allRows);

		return mav;
	}

	/**
	 * This method handles the request from the page to display the preview
	 * of all the fields along with format, delimiter, header and footer. this
	 * method calls the actual method to generate the file and the result is
	 * then parsed to be html friendly and then sent to the page
	 */
	public ModelAndView updatePreview(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		StringBuffer returnString = new StringBuffer();
		DynamicFormat format = parseIntoDynamicFormat(request);

		dynamicFormatter.setDynamicFormat(format);

		BillableDevice device = this.getDefaultDevice();

		// use the class method getBillingFileString to give us
		// header+generated string+footer
		returnString.append(StringEscapeUtils.escapeHtml(dynamicFormatter
				.getBillingFileString(Collections.singletonList(device))
				.toString()));

		// for our html page purposes, we try to insert <br /> into the string
		// for a newline
		returnString.insert(StringEscapeUtils.escapeHtml(format.getHeader())
				.length(), "<br>");
		returnString.insert(
				returnString.length()
						- StringEscapeUtils.escapeHtml(format.getFooter())
								.length() - 1, "<br>");

		// replace spaces with &nbsp; for html display
		return new ModelAndView(new TextView(returnString.toString()
				.replaceAll(" ", "&nbsp;")));
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
				if (tempString.hasValue()) {

					// Append reading to end of field name and add to list
					tempList.add(tempString + " - reading");
				}
				if (tempString.hasTimestamp()) {

					// Append timestamp to end of field name and add to list
					tempList.add(tempString + " - timestamp");
				}
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
	private DynamicFormat parseIntoDynamicFormat(HttpServletRequest request)
			throws ServletRequestBindingException {

		DynamicFormat format = new DynamicFormat();
		List<DynamicBillingField> fieldList = new ArrayList<DynamicBillingField>();

		format.setFormatId(ServletRequestUtils.getIntParameter(request, "formatId"));
		format.setName(ServletRequestUtils.getStringParameter( request, "formatName"));
		format.setFooter(ServletRequestUtils.getStringParameter(request, "footer"));
		format.setHeader(ServletRequestUtils.getStringParameter(request, "header"));
		format.setDelim(ServletRequestUtils.getStringParameter(request, "delimiter"));

		// selectedFields is an JSON String representation containing the fields
		// that customer chooses
		String selectedFields = ServletRequestUtils.getStringParameter(request, "fieldArray");
		JSONArray myJSONArray = new JSONArray(selectedFields);

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
			public void populate(DevicePointIdentifier devicePointIdentifier,
					Timestamp timestamp, double value, int unitOfMeasure,
					String pointName, DeviceData meterData) {
				throw new NotImplementedException();
			}
			@Override
            public boolean isEnergy(DevicePointIdentifier devicePointIdentifier) {
                throw new NotImplementedException();
            }
			@Override
            public boolean isDemand(DevicePointIdentifier devicePointIdentifier) {
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
		data.setValue(123456789.0123);
		data.setUnitOfMeasure(1);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.totalConsumption, data);

		// Add rate A consumption
		data = new BillingData();
		data.setData("rate A kWh");
		data.setValue(123456789.12345);
		data.setUnitOfMeasure(1);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.rateAConsumption, data);

		// Add rate B consumption
		data = new BillingData();
		data.setData("rate B kWh");
		data.setValue(123456789.54321);
		data.setUnitOfMeasure(1);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.rateBConsumption, data);

		// Add rate C consumption
		data = new BillingData();
		data.setData("rate C kWh");
		data.setValue(123456789.01234);
		data.setUnitOfMeasure(1);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.rateCConsumption, data);

		// Add rate D consumption
		data = new BillingData();
		data.setData("rate D kWh");
		data.setValue(123456789.0);
		data.setUnitOfMeasure(1);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.rateDConsumption, data);

		// Add total peak demand
		data = new BillingData();
		data.setData("total peak kW");
		data.setValue(9413.95);
		data.setUnitOfMeasure(0);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.totalPeakDemand, data);

		// Add rate A demand
		data = new BillingData();
		data.setData("rate A kW");
		data.setValue(9713.012345);
		data.setUnitOfMeasure(0);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.rateADemand, data);

		// Add rate B demand
		data = new BillingData();
		data.setData("rate B kW");
		data.setValue(9413.0);
		data.setUnitOfMeasure(0);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.rateBDemand, data);

		// Add rate C demand
		data = new BillingData();
		data.setData("rate C kW");
		data.setValue(9413.0);
		data.setUnitOfMeasure(0);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.rateCDemand, data);

		// Add rate D demand
		data = new BillingData();
		data.setData("rate D kW");
		data.setValue(9413.0);
		data.setUnitOfMeasure(0);
		data.setTimestamp(new Timestamp(new Date().getTime()));

		device.addData(BillableField.rateDDemand, data);

		return device;
	}

	public DynamicBillingFileDao getDynamicBillingFileDao() {
		return dynamicBillingFileDao;
	}

	public void setDynamicBillingFileDao(
			DynamicBillingFileDao dynamicBillingFileDao) {
		this.dynamicBillingFileDao = dynamicBillingFileDao;
	}

	public DynamicBillingFormatter getDynamicFormatter() {
		return dynamicFormatter;
	}

	public void setDynamicFormatter(DynamicBillingFormatter dynamicFormatter) {
		this.dynamicFormatter = dynamicFormatter;
	}

}