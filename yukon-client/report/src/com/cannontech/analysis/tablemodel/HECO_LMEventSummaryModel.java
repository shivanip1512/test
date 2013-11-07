package com.cannontech.analysis.tablemodel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.activity.ActivityLog;
import com.cannontech.analysis.data.lm.LMEvent;
import com.cannontech.analysis.data.lm.LMEventCustomer;
import com.cannontech.analysis.data.lm.SettlementCustomer;
import com.cannontech.database.db.customer.CICustomerPointData;

/**
 * Created on Nov 15, 2005
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * @author snebben
 */
public class HECO_LMEventSummaryModel extends HECO_SettlementModelBase
{
	private static final int MAX_KW_DATA = 0;
	private static final int PENALTY_DOLLARS_DATA = 1;
	private static final int NUMBER_VIOLATIONS_DATA = 2;

	public static final String MAX_KW_STRING = "Max kW";
	public static final String PENALTY_DOLLARS_STRING = "Penalty Dollars";
	public static final String NUMBER_VIOLATIONS_STRING = "Violations";
	
	/** Type of information model will display */
	private int dataType = MAX_KW_DATA;
	
	/** A string for the title of the data */
	private static String title = "Load Control Event Summary";
	
	/** Number of columns */	
	/** UNDEFINED FOR THIS REPORT, COLUMNS ARE DYNAMIC...depending on # of customers.*/
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int EVENT_TYPE_COLUMN = 0;
	public final static int DATE_COLUMN = 1;
	public final static int START_TIME_COLUMN = 2;
	public final static int END_TIME_COLUMN = 3;
	public final static int DURATION_COLUMN = 4;	
	
	/** String values for column representation */
	public final static String EVENT_TYPE_STRING = "Type";
	public final static String DATE_STRING = "Date";
	public final static String START_TIME_STRING = "Start";
	public final static String END_TIME_STRING = "End";
	public final static String DURATION_STRING = "Duration";
	
//	private static final String ATT_ACTION_GROUP_TYPE = "actionGroupType";
	
	public static void main(String[] args)
	{
		HECO_LMEventSummaryModel model = new HECO_LMEventSummaryModel();
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal.setTime(model.getStartDate());
		tempCal.set(Calendar.DAY_OF_MONTH, 15);
		model.setStartDate(tempCal.getTime());
//		model.collectData();
		model.getColumnNames();
		
	}
	public static java.util.Comparator actLogComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;
			thisVal = ((ActivityLog)o1).getECName();
			anotherVal = ((ActivityLog)o2).getECName();
			if( thisVal.equalsIgnoreCase(anotherVal) )
			{				
				//if the energyCompanies are equal, we need to sort by timestamp
				thisVal = ((ActivityLog)o1).getDateTime().toString();
				anotherVal = ((ActivityLog)o2).getDateTime().toString();
				Date dt1 = ((ActivityLog)o1).getDateTime();
				Date dt2 = ((ActivityLog)o2).getDateTime();
				if( dt1.compareTo(dt2) == 0 )
				{				
					//if the Timestamps are equal, we need to sort by accountNumber
					thisVal = ((ActivityLog)o1).getAcctNumber();
					anotherVal = ((ActivityLog)o2).getAcctNumber();
					Object acct1 = null, acct2 = null;
					try{
						acct1 = Integer.valueOf(thisVal);
					}catch (NumberFormatException nfe1)
					{
						try{
							acct2 = Integer.valueOf(anotherVal);		
						}catch (NumberFormatException nfe2)
						{
							//both are strings
							return ( thisVal.compareToIgnoreCase(anotherVal) );
						}
						//first one is string, second one is number
						return -1;
					}
					try{
						acct2 = Integer.valueOf(anotherVal);
					}catch (Exception e)
					{
						//first one is number, second one is string
						return 1;
					}
				
					return ( ((Integer)acct1).compareTo((Integer)acct2));
				}	
				return (dt1.compareTo(dt2)); 			
			}				

			return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
	};
	
	/**
	 * Constructor class
	 */
	public HECO_LMEventSummaryModel(Date start_, Date stop_)
	{
		super(start_, stop_);//default type
		setEnergyCompanyID(new Integer(0));
		moneyFormat.setNegativePrefix("(");
		moneyFormat.setNegativeSuffix(")");
	}

	/**
	 * Constructor class
	 */
	public HECO_LMEventSummaryModel()
	{
		this(null, null);		
	}

	/**
	 * Constructor class
	 * Only ONE energycompanyID is used 
	 */
	public HECO_LMEventSummaryModel(Integer ecID_)
	{
		this();//default type
		setEnergyCompanyID(ecID_);
	}
	

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof Integer)
		{
			int dataObjectsIndex = ((Integer)o).intValue() % (getDataObjects().size()+1);
			int dataType = ((Integer)o).intValue() / (getDataObjects().size() + 1);
//			System.out.println("Column:  " + columnIndex + "    Int: " + ((Integer)o).intValue() + "   Index: " + dataObjectsIndex + "   dataType: " + dataType );

			if( dataObjectsIndex == 0)
			{
				if( columnIndex == START_TIME_COLUMN )
				{
					switch (dataType)
					{
						case MAX_KW_DATA:
							return MAX_KW_STRING;		
						case PENALTY_DOLLARS_DATA:
							return PENALTY_DOLLARS_STRING;
						case NUMBER_VIOLATIONS_DATA:
							return NUMBER_VIOLATIONS_STRING;						
					}
				}
				return null;				
			}
			
			LMEvent lmEvent = (LMEvent)getDataObjects().get(dataObjectsIndex - 1);			
			switch( columnIndex)
			{
				case EVENT_TYPE_COLUMN:
					return lmEvent.getGroupName();
				case DATE_COLUMN:
					return dateFormat.format(lmEvent.getActualStartDateTime());
				case START_TIME_COLUMN:
					return timeFormat.format(lmEvent.getActualStartDateTime());
				case END_TIME_COLUMN:
					return timeFormat.format(lmEvent.getActualStopDateTime());
				case DURATION_COLUMN:
					return decimalFormat.format(lmEvent.getRoundedDuration());
			}
			
			for(int i = 0; i < getCustomerIDS().length; i++ )
			{
				if( columnIndex == i + NUMBER_COLUMNS)
				{
					if( !lmEvent.isERIPaymentEvent())
						return null;

					LMEventCustomer lmec = ((LMEventCustomer)lmEvent.getLmEventCustomerMap().get(getCustomerIDS()[i]));
					switch (dataType)
					{
						case MAX_KW_DATA:
							return (lmec == null || lmec.getMaxKW().doubleValue() <= 0)? null:decimalFormat.format(lmec.getMaxKW());		
						case PENALTY_DOLLARS_DATA:
							return (lmec == null || lmec.getExcessFirmServiceLevelCharge().doubleValue() >= 0) ? null:moneyFormat.format(lmec.getExcessFirmServiceLevelCharge());
						case NUMBER_VIOLATIONS_DATA:
							return (lmec == null) ? null:intFormat.format(lmec.getNumIntervalViolations());						
						default :
							return null;
					}
					
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[NUMBER_COLUMNS + getCustomerIDS().length];
			columnNames [0] = EVENT_TYPE_STRING;
			columnNames [1] = DATE_STRING;
			columnNames [2] = START_TIME_STRING;
			columnNames [3] = END_TIME_STRING;
			columnNames [4] = DURATION_STRING;
			
			int i = 0;
			Iterator iter = getSettlementCustomerMap().entrySet().iterator();
			while(iter.hasNext())
			{
				SettlementCustomer settleCust = (SettlementCustomer)((Map.Entry)iter.next()).getValue();
				columnNames [NUMBER_COLUMNS + (i++)] = settleCust.getCICustomerBase().getCiCustomerBase().getCompanyName();

				CICustomerPointData pointData = settleCust.getPointData(CICustomerPointData.TYPE_SETTLEMENT);
				if( pointData != null)
					pointIDCSV += pointData.getPointID().intValue() + ",";
			}
		}
		return columnNames;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[NUMBER_COLUMNS + getCustomerIDS().length];
				for (int i = 0; i < NUMBER_COLUMNS + getCustomerIDS().length; i++)
					columnTypes[i] = String.class;
		}
			
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			int offset = 0;
			columnProperties = new ColumnProperties[NUMBER_COLUMNS + getCustomerIDS().length];
				//posX, posY, width, height, numberFormatString

				columnProperties[0] = new ColumnProperties(offset, 1, 150, null);
				columnProperties[1] = new ColumnProperties(offset+=150, 1, 50, null);
				columnProperties[2] = new ColumnProperties(offset+=50, 1, 50, null);
				columnProperties[3] = new ColumnProperties(offset+=50, 1, 50, null);
				columnProperties[4] = new ColumnProperties(offset+=50, 1, 50, null);
				
                offset += 50;   //update to current position.
                for (int i = 0; i < getCustomerIDS().length; i++)
                {
                    int width = 75;
                    offset = getAdjustedStartOffset(offset, width);
                    columnProperties[NUMBER_COLUMNS + i] = new ColumnProperties(offset, 1, width, null);
                    offset += width;
				}
		}
		return columnProperties;
	}
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td align='center'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='title-header'>NONE</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Display Options</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}
	@Override
	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
//			String[] paramArray = req.getParameterValues(ATT_ACTION_GROUP_TYPE);
		}
	}

	protected void loadDataVector()
	{
		int size = getDataObjects().size() + 1;	//add one for header row
		for (int i = 0; i < (3* size); i++)	//loop three times for each "group" of data to display (max kw, penalty, violations)
		{
			getData().add(new Integer( i));
		}
	}
}