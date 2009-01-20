package com.cannontech.billing.format.simple;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.daohibe.CustomerStubDaoImpl;
import com.cannontech.cc.daohibe.EconomicEventParticipantDaoImpl;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.cc.service.enums.CurtailmentEventState;
import com.cannontech.cc.service.enums.EconomicEventState;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.impl.EnergyCompanyDaoImpl;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.spring.YukonSpringHook;

/**
 * ***Header Information
 * Field			Start	Stop	Len	Just	Format / Value  Comments	
 * Header Flag		1		8		8	L		%HEADER%		Header Flag Distinguishes the header record from the detail records	
 * Run Number		9		22		14	R		9(14) -or- use YYYYMMDDhhmmss format for timestamp		A unique number must increase with each new file.  Suggestion: Use timestamp of when the file was created.	
 * Interface Name	23		62		40	L		x(40)			Must be defined in the MV-PBS interface table	
 * Record Count		63		67		5	R		9(5)			Contains the number of detail records found in the file (does not include the header record in the count)
 * Last Modified	68		81		14	R		YYYYMMDDhhmmss	Timestamp of when the file was last modified.
 * 
 * General Formatting Rules
 * Each line or record should end with a CR/LF (carriage return / line feed)
 * Files should end with an EOF (End-of-File) character [ASCII(0) or NULL] or with a single blank line (two consecutive CR/LF characters)
 * “Empty” files should contain a header record with a Detail Record Count of zero
 * 
 * ***Detail Records Information
 * Example:
 * ,,Customer Name,Product Name,Event Type,,Event Start,Event Stop,Buy-Through Quantity,,,,String Value,Product Parameter Value,Process Flag,Active Flag
 * 
 * Customer Name   Should be a combination of premiseEservicepoint.  Will be found in customeraccount.accountnumber (Example:  030067629E01)
 * Product Name   Hard coded ISOC-CO (this is the name of the product In PBS)
 * Event Type   One of these values: Economic, Capacity&Contingency
 * Event Start   Format: YYYYMMDDhhmmss
 * Event Stop   Format: YYYYMMDDhhmmss
 * Buy-Through Quantity  9(11).9(4)
 * Buy-Through Price  9(11).9(6)       *** Not Required
 * String Value    Yukon EventNumber
 * Product Parameter Value  Hard coded value Event – CSV (required by interface)
 * Process Flag                    Hard Coded value I (for insert)
 * Active Flag                     Hard Coded Value Y (for yes)
 * 
 */
public class CurtailmentEventsItronFormat extends SimpleBillingFormatBase {

	private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	private final DecimalFormat BUY_THROUGH_FORMAT = new DecimalFormat("0.0000");
	private final String PRODUCT_NAME = "ISOC-CO"; 
	private final String PRODUCT_PARAMETER = "Event - CSV";
	private final String PROCESS_FLAG = "I";
	private final String ACTIVE_FLAG = "Y";

	private int detailRecordCount = 0;

	private EnergyCompanyDao energyCompanyDao;
	private CustomerStubDao customerStubDao;
	private BaseEventDao baseEventDao;

	@Required
	public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
		this.energyCompanyDao = energyCompanyDao;
	}
	@Required
	public void setCustomerStubDao(CustomerStubDao customerStubDao) {
		this.customerStubDao = customerStubDao;
	}
	@Required
	public void setBaseEventDao(BaseEventDao baseEventDao) {
		this.baseEventDao = baseEventDao;
	}
	
	private String buildHeaderString() {
		String header = new String();
		header += "%HEADER%,";	//header flag
		Date now = new Date();
		header += DATE_TIME_FORMAT.format(now) + ",";	//run number; use now as it is unique.
		header += "EVENTS,";	//interface name defined by MV_PBS interface table (Itron defined)
		header += getReadingCount() +",";		//TODO - detail record count (not including header)
		header += DATE_TIME_FORMAT.format(now) + "\r\n";	//timestamp last modified
		return header;
	}
	
	private String getEventDataString(CICustomerStub ciCustomerStub, BaseEvent event) {

		if (event instanceof CurtailmentEvent) {
			return getCurtailmentDataString(ciCustomerStub, (CurtailmentEvent)event);
		} else if (event instanceof EconomicEvent) {
			return getEconomicDataString(ciCustomerStub, (EconomicEvent)event);
		}
		return "";
	}
	
	private String getCurtailmentDataString(CICustomerStub ciCustomerStub, CurtailmentEvent event) {
		if (event.getState() != CurtailmentEventState.CANCELLED) {
			String eventDataStr = "";
			LiteCustomer liteCustomer = ciCustomerStub.getLite();
			eventDataStr += buildDataString(getCustomerName(liteCustomer),
					getEventTypeString(event), 
					event.getStartTime(), 
					event.getStopTime(), 
					null, 
					event.getDisplayName());
			return eventDataStr;
		}
		return "";
	}
	
	private String getEconomicDataString(CICustomerStub ciCustomerStub, EconomicEvent event) {
		if (event.getState() != EconomicEventState.CANCELLED) {
			EconomicService economicService = YukonSpringHook.getBean("economicService", EconomicService.class);
			EconomicEventParticipantDao economicEventParticipantDao = YukonSpringHook.getBean("economicEventParticipantDao", EconomicEventParticipantDaoImpl.class);
			EconomicEventPricing eventPricing = event.getLatestRevision();

			String eventDataStr = "";
			LiteCustomer liteCustomer = ciCustomerStub.getLite();

			int numberOfWindows = event.getInitialWindows();
            for (int i = 0; i < numberOfWindows; i++) {
            	EconomicEventParticipant economicEventParticipant = 
            		economicEventParticipantDao.getForCustomerAndEvent(ciCustomerStub, event);
                EconomicEventParticipantSelectionWindow participantSelectionWindow = 
                    economicService.getCustomerSelectionWindow(eventPricing, economicEventParticipant, i);
                
                eventDataStr += buildDataString(getCustomerName(liteCustomer), 
                		getEventTypeString(event), 
                		participantSelectionWindow.getWindow().getStartTime(),
                		participantSelectionWindow.getWindow().getStopTime(),
                		participantSelectionWindow.getEnergyToBuy(),
                		event.getDisplayName());
			}
			return eventDataStr;
		}
		return "";
	}
	
	private String buildDataString(String customerName, String eventType, Date startTime, Date stopTime, BigDecimal buyThroughEnergy, String displayName) {
		String eventDataStr = ""; 
		eventDataStr += ",,";
		eventDataStr += customerName +",";
		eventDataStr += PRODUCT_NAME +",";
		eventDataStr += eventType + ",";
		eventDataStr += ",";
		eventDataStr += DATE_TIME_FORMAT.format(startTime) + ",";
		eventDataStr += DATE_TIME_FORMAT.format(stopTime) + ",";
		eventDataStr += (buyThroughEnergy!=null ? BUY_THROUGH_FORMAT.format(buyThroughEnergy): "") +",";	//buy through quantity - n/a
		eventDataStr += ",";	//buy through price - not required
		eventDataStr += ",,";
		eventDataStr += displayName + ",";	//String value
		eventDataStr += PRODUCT_PARAMETER + ",";
		eventDataStr += PROCESS_FLAG + ",";
		eventDataStr += ACTIVE_FLAG;
		eventDataStr +="\r\n";
    	detailRecordCount++;
    	return eventDataStr;
	}


	/** Returns the string for the CustomerName field.
	 * This is currently implemented as the CustomerAccount.AccountNumber field (Per Xcel).
	 * @return customerName
	 */
	private String getCustomerName(LiteCustomer liteCustomer) {

		String customerName = "";
		Vector<Integer> accountIDs = liteCustomer.getAccountIDs();
		if (!accountIDs.isEmpty() ) {	//setup "should" be such that there is only one account per customer
			SimpleJdbcTemplate simpleJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", SimpleJdbcTemplate.class);
			String sql = "Select AccountNumber from CustomerAccount " +
						" where AccountId = ? ";
			try {
				customerName = simpleJdbcTemplate.queryForObject(sql, String.class, accountIDs.get(0));
			} catch(IncorrectResultSizeDataAccessException e) {
				CTILogger.error(e);
			}
		}
		return customerName;
		
	}

	private String getEventTypeString(BaseEvent event) {
		if (event instanceof CurtailmentEvent) {
			return "Capacity&Contingency";
		} else if (event instanceof EconomicEvent) {
			return "Economic";
		} else {
			return "unknown";
		}
	}

	private String getEvents() {
		detailRecordCount = 0;
		String eventString = new String();
		LiteEnergyCompany liteEnergyCompany = energyCompanyDao.getEnergyCompany(getBillingFileDefaults().getLiteYukonUser());
        List<CICustomerStub> customersForEC = customerStubDao.getCustomersForEC(liteEnergyCompany.getEnergyCompanyID());

        for (CICustomerStub customerStub : customersForEC) {
            List<BaseEvent> allEvents = baseEventDao.getAllForCustomer(customerStub, getBillingFileDefaults().getEarliestStartDate(), getBillingFileDefaults().getEndDate());
            for (BaseEvent event : allEvents) {
            	String eventDataString = getEventDataString(customerStub, event);
            	eventString += eventDataString;
            }
        }
        String returnString = new String();
        returnString = buildHeaderString();
        returnString += eventString;
        return returnString;        
    }
	
	@Override
	public boolean writeToFile(OutputStream out) throws IOException {
		out.write(getEvents().toString().getBytes());
		return true;
	}

	@Override
	public int getReadingCount() {
		return detailRecordCount;
	}
}
