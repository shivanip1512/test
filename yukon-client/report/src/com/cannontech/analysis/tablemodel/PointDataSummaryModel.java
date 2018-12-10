package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.device.LPMeterData;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.NaturalOrderComparator;

public class PointDataSummaryModel extends ReportModelBase<LPMeterData>
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 40;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int METER_NUMBER_COLUMN = 1;
	public final static int PHYSICAL_ADDRESS_COLUMN = 2;
	public final static int PAO_TYPE_COLUMN = 3;	
	//The above enums make up the group by */
	
	public final static int POINT_NAME_COLUMN = 4;
	public final static int CHANNEL_NUMBER_COLUMN = 5;
	public final static int CHANNEL_INTERVAL_COLUMN = 6;
	public final static int MISSING_DATA_FLAG_COLUMN = 7;
	public final static int TIME_COLUMN = 8;
	public final static int VALUE_COLUMN = 9; 
	
	public final static int START_DATE_COLUMN = 10;
	public final static int DAILY_HIGH_TIME_COLUMN = 11;
	public final static int DAILY_HIGH_COLUMN = 12;
	public final static int DAILY_AVERAGE_COLUMN = 13;
	public final static int DAILY_LOW_TIME_COLUMN = 14;
	public final static int DAILY_LOW_COLUMN = 15;
	public final static int TOTAL_KWH_COLUMN = 16;
	public final static int DAILY_MISSING_DATA_FLAG_COLUMN = 17;
	
	public final static int PEAKS_TITLE_COLUMN = 18;
	public final static int LOWS_TITLE_COLUMN = 19;

//	These are columns that could be eliminated with the use of subreporting!!!
	public final static int PEAK_TIMESTAMP_0_COLUMN = 20;
	public final static int PEAK_VALUE_0_COLUMN = 21;
	public final static int LOW_TIMESTAMP_0_COLUMN = 22;
	public final static int LOW_VALUE_0_COLUMN = 23;

	public final static int PEAK_TIMESTAMP_1_COLUMN = 24;
	public final static int PEAK_VALUE_1_COLUMN = 25;
	public final static int LOW_TIMESTAMP_1_COLUMN = 26;
	public final static int LOW_VALUE_1_COLUMN = 27;

	public final static int PEAK_TIMESTAMP_2_COLUMN = 28;
	public final static int PEAK_VALUE_2_COLUMN = 29;
	public final static int LOW_TIMESTAMP_2_COLUMN = 30;
	public final static int LOW_VALUE_2_COLUMN = 31;

	public final static int PEAK_TIMESTAMP_3_COLUMN = 32;
	public final static int PEAK_VALUE_3_COLUMN = 33;
	public final static int LOW_TIMESTAMP_3_COLUMN = 34;
	public final static int LOW_VALUE_3_COLUMN = 35;

	public final static int PEAK_TIMESTAMP_4_COLUMN = 36;
	public final static int PEAK_VALUE_4_COLUMN = 37;
	public final static int LOW_TIMESTAMP_4_COLUMN = 38;
	public final static int LOW_VALUE_4_COLUMN = 39;

	public final static int POINT_TOTAL_KWH_COLUMN= 40;
	
	public final static int INTERVALS_PER_HOUR = 41;
	public final static int INTERVALS_PER_DAY = 42;	
	public final static int DAILY_COUNT = 43;
	
// end of subreport columns

	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "Device";
	public final static String PAO_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	//The above enums make up the group by */
	
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String CHANNEL_NUMBER_STRING = "Channel";
	public final static String CHANNEL_INTERVAL_STRING = "Interval";
	public final static String MISSING_DATA_FLAG_STRING = "Missing Data";
	public final static String TIME_STRING = "Time";
	public final static String VALUE_STRING = "Value"; 


	public final static String START_DATE_STRING = "Date";
	public final static String DAILY_HIGH_TIME_STRING = "High Time";
	public final static String DAILY_HIGH_STRING = "High";
	public final static String DAILY_AVERAGE_STRING = "Average";
	public final static String DAILY_LOW_TIME_STRING = "Low Time";
	public final static String DAILY_LOW_STRING = "Low";
	public final static String TOTAL_KWH_STRING = "Total Usage";	
	public final static String DAILY_MISSING_DATA_FLAG_STRING = "Quality";
	
	public final static String PEAKS_TITLE_STRING = "PEAKS";
	public final static String LOWS_TITLE_STRING = "LOWS";

	public final static String PEAK_TIMESTAMP_0_STRING = "Peak Timestamp";
	public final static String PEAK_VALUE_0_STRING = "Peak Value";
	public final static String LOW_TIMESTAMP_0_STRING = "Low Timestamp";
	public final static String LOW_VALUE_0_STRING = "Low Value";

	public final static String PEAK_TIMESTAMP_1_STRING = "Peak Timestamp1";
	public final static String PEAK_VALUE_1_STRING = "Peak Value1";
	public final static String LOW_TIMESTAMP_1_STRING = "Low Timestamp1";
	public final static String LOW_VALUE_1_STRING = "Low Value1";

	public final static String PEAK_TIMESTAMP_2_STRING = "Peak Timestamp2";
	public final static String PEAK_VALUE_2_STRING = "Peak Value2";
	public final static String LOW_TIMESTAMP_2_STRING = "Low Timestamp2";
	public final static String LOW_VALUE_2_STRING = "Low Value2";

	public final static String PEAK_TIMESTAMP_3_STRING = "Peak Timestamp3";
	public final static String PEAK_VALUE_3_STRING = "Peak Value3";
	public final static String LOW_TIMESTAMP_3_STRING = "Low Timestamp3";
	public final static String LOW_VALUE_3_STRING = "Low Value3";

	public final static String PEAK_TIMESTAMP_4_STRING = "Peak Timestamp4";
	public final static String PEAK_VALUE_4_STRING = "Peak Value4";
	public final static String LOW_TIMESTAMP_4_STRING = "Low Timestamp4";
	public final static String LOW_VALUE_4_STRING = "Low Value4";

	public final static String POINT_TOTAL_KWH_STRING = "Point Total kWh";

	public final static String INTERVALS_PER_HOUR_STRING = "Intervals Per Hour";
	public final static String INTERVALS_PER_DAY_STRING = "Intervals Per Day";
	public final static String DAILY_COUNT_STRING = "Daily Count";

	//contain values of Integer (pointID ) to MeterAndPointData object vlaues
    private HashMap<Integer, List<MeterAndPointData>> allMPDataPeaks = new HashMap<Integer, List<MeterAndPointData>>();
    private HashMap<Integer, List<MeterAndPointData>> allMPDataLows = new HashMap<Integer, List<MeterAndPointData>>();
	
	//contains all PointID (Integer) values to number of missing intervals (Integer).
    private HashMap<Integer, Integer> allMissingData = new HashMap<Integer, Integer>();
	
	/** A string for the title of the data */
	private static String title = "Point Data Summary Report";

	//Extensions of PointTypes
	public final static int LOAD_PROFILE_POINT_TYPE = 101;	//some "unused" PointType int
	public final static int CALC_POINT_TYPE = PointType.CalcAnalog.getPointTypeId();
	public final static int ANALOG_POINT_TYPE = PointType.Analog.getPointTypeId();
	public final static int DEMAND_ACC_POINT_TYPE = PointType.DemandAccumulator.getPointTypeId();
	public final static int PULSE_ACC_POINT_TYPE = PointType.PulseAccumulator.getPointTypeId();
	public final static int STATUS_POINT_TYPE = PointType.Status.getPointTypeId();

	public final static String LOAD_PROFILE_POINT_TYPE_STRING = "All Load Profile";	//some "unused" PointType int
	public final static String CALC_POINT_TYPE_STRING = "All Calculated";
	public final static String ANALOG_POINT_TYPE_STRING = "All Analog";
	public final static String DEMAND_ACC_POINT_TYPE_STRING = "All Demand Accumulator";
	public final static String PULSE_ACC_POINT_TYPE_STRING = "All Pulse Accumulator";
	public final static String STATUS_POINT_TYPE_STRING = "All Status";
	
	private int pointType = DEMAND_ACC_POINT_TYPE;	//default to Load Profile PointTypes
	
	private final static int[] ALL_POINT_TYPES = new int[]
	{
		CALC_POINT_TYPE,
		ANALOG_POINT_TYPE,
		DEMAND_ACC_POINT_TYPE,
		PULSE_ACC_POINT_TYPE,
		STATUS_POINT_TYPE	
	};

	//servlet attributes/parameter strings
	protected static final String ATT_POINT_TYPE = "pointType";
	protected static final String ATT_ORDER_BY = "orderBy";
	protected static final String ATT_SHOW_DETAILS = "showDetails";
	
	private boolean showDetails = false;
	
	public static final int ORDER_BY_DEVICE_NAME = 0;
	public static final int ORDER_BY_METER_NUMBER = 1;
	public static final int ORDER_BY_PHYSICAL_ADDRESS = 2;
	private int orderBy = ORDER_BY_DEVICE_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_DEVICE_NAME, ORDER_BY_METER_NUMBER, ORDER_BY_PHYSICAL_ADDRESS 
	};

    private boolean excludeDisabledDevices = false;
    private static final String ATT_EXCLUDE_DISABLED_DEVICES = "excludeDisabledDevices";

    public Comparator<LPMeterData> lpDataSummaryComparator = new java.util.Comparator<LPMeterData>()
	{
		@Override
        public int compare(LPMeterData o1, LPMeterData o2){
            final MeterAndPointData mpData1 = o1.getMeterAndPointData();
            final MeterAndPointData mpData2 = o2.getMeterAndPointData();
            
		    String thisVal = NULL_STRING;
		    String anotherVal = NULL_STRING;
		    if( getOrderBy() == ORDER_BY_DEVICE_NAME)
		    {
		        thisVal = mpData1.getMeter().getName();
		        anotherVal = mpData2.getMeter().getName();
		    }
		    if( getOrderBy() == ORDER_BY_PHYSICAL_ADDRESS)
		    {
                thisVal = mpData1.getMeter().getSerialOrAddress();
                anotherVal = mpData2.getMeter().getSerialOrAddress();
		    }
		    if( getOrderBy() == ORDER_BY_METER_NUMBER)
		    {
                NaturalOrderComparator noComp = new NaturalOrderComparator(); 
                return noComp.compare(mpData1.getMeter().getMeterNumber(), mpData2.getMeter().getMeterNumber()); 
		    }
	        return ( thisVal.compareToIgnoreCase(anotherVal));
		}
	};
	
    public static Comparator<MeterAndPointData> mpDataValueComparator = new Comparator<MeterAndPointData>()
	{
		@Override
        public int compare(MeterAndPointData o1, MeterAndPointData o2)
		{
			double thisVal = o1.getValue().doubleValue();
			double anotherVal = o2.getValue().doubleValue();
			return ( thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		@Override
        public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	/**
	 * Default Constructor
	 */
	public PointDataSummaryModel()
	{
		this(null, null);
	}
	/**
	 * Constructor class
	 * @param startTime_ rph.timestamp
	 * @param stopTime_ rph.timestamp
	 */
	public PointDataSummaryModel(Date start_, Date stop_, int summaryPointType)
	{
		super(start_, stop_);
		setFilterModelTypes(new ReportFilter[]{
				ReportFilter.METER,
				ReportFilter.DEVICE,
				ReportFilter.GROUPS,
				ReportFilter.RTU}
				);
		setPointType(summaryPointType);
	}
	/**
	 * Constructor class
	 * @param startTime_ rph.timestamp
	 * @param stopTime_ rph.timestamp
	 */
	public PointDataSummaryModel(Date start_, Date stop_)
	{
		this(start_, stop_, DEMAND_ACC_POINT_TYPE);
	}	
	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
	    //NOT IMPLEMENTED, data rows are added in the collectData method 
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
    public SqlFragmentSource buildSQLStatement()
    {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT PAO.PAOBJECTID, PAO.PAONAME, PAO.CATEGORY, PAO.TYPE, DMG.METERNUMBER, DCS.ADDRESS, ");
        sql.append(" P.POINTNAME, RPH.POINTID, TIMESTAMP, VALUE, QUALITY ");
		
		if (getPointType() == LOAD_PROFILE_POINT_TYPE || getPointType() == DEMAND_ACC_POINT_TYPE) {
            sql.append(", DLP.LOADPROFILEDEMANDRATE, DLP.VOLTAGEDMDRATE, DLP.LASTINTERVALDEMANDRATE, DLP.VOLTAGEDMDINTERVAL ");
        }
		
        sql.append(" FROM RAWPOINTHISTORY RPH, POINT P, YUKONPAOBJECT PAO ");
        sql.append(" left outer join DEVICEMETERGROUP DMG on PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append(" left outer join DEVICECARRIERSETTINGS DCS on PAO.PAOBJECTID = DCS.DEVICEID ");
		if (getPointType() == LOAD_PROFILE_POINT_TYPE || getPointType() == DEMAND_ACC_POINT_TYPE) {
            sql.append(", DEVICELOADPROFILE DLP ");
        }
				
		sql.append(" WHERE P.POINTID = RPH.POINTID AND P.PAOBJECTID = PAO.PAOBJECTID ");  //Use PAO for ordering
		
		sql.append(" AND TIMESTAMP > ").appendArgument(getStartDate());
		sql.append(" AND TIMESTAMP <= ").appendArgument(getStopDate());
		
		if (getPointType() == LOAD_PROFILE_POINT_TYPE ) {
		    sql.append(" AND PAO.PAOBJECTID = DLP.DEVICEID ");
			sql.append(" AND P.POINTTYPE").eq_k(PointType.DemandAccumulator);
			sql.append(" AND (P.POINTOFFSET >= ").appendArgument(PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND);
			sql.append(" AND P.POINTOFFSET <= ").appendArgument(PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND);
			sql.append(") ");
		} else if ( getPointType() == STATUS_POINT_TYPE ) {
			sql.append(" AND P.POINTTYPE").eq_k(PointType.Status);
		} else if ( getPointType() == DEMAND_ACC_POINT_TYPE ) {
		    sql.append(" AND PAO.PAOBJECTID = DLP.DEVICEID ");
			sql.append(" AND P.POINTTYPE").eq_k(PointType.DemandAccumulator);
			//Do not allow LP data, those points fall into the LP point type option.
			sql.append(" AND (P.POINTOFFSET < ").appendArgument(PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND);
			sql.append(" OR P.POINTOFFSET > ").appendArgument(PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND);
			sql.append(")");			
		} else if ( getPointType() == PULSE_ACC_POINT_TYPE ) {
        	sql.append(" AND P.POINTTYPE").eq_k(PointType.PulseAccumulator);
        } else if ( getPointType() == ANALOG_POINT_TYPE ) {
			sql.append(" AND P.POINTTYPE").eq_k(PointType.Analog);
		} else if ( getPointType() == CALC_POINT_TYPE) {
			sql.append(" AND (P.POINTTYPE").eq_k(PointType.CalcAnalog);
			sql.append(" OR P.POINTTYPE").eq_k(PointType.CalcStatus);
			sql.append(" )");
		}
		if (excludeDisabledDevices) {
		    sql.append(" AND PAO.DISABLEFLAG").eq("N");
		}
		//Use paoIDs in query if they exist
        final String[] groups = getBillingGroups();
        
		if (groups != null && groups.length > 0) {
			sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");
			
            SqlFragmentSource deviceGroupSqlWhereClause = getGroupSqlWhereClause("PAO.PAOBJECTID");
            sql.append(" AND ", deviceGroupSqlWhereClause);
		}
		if( getPaoIDs() != null && getPaoIDs().length > 0) {
			sql.append(" AND P.PAOBJECTID IN (", getPaoIDs(), ")");
		}
		
         sql.append(" ORDER BY PAO.PAOBJECTID, P.POINTNAME, TIMESTAMP ");
		//Ordering will be done by the comparator
		return sql;
	}
		
    @Override
    public void collectData()
    {
        //Reset all objects, new data being collected!
        setData(null);
        allMPDataLows = new HashMap<Integer, List<MeterAndPointData>>();
        allMPDataPeaks = new HashMap<Integer, List<MeterAndPointData>>();
        allMissingData = new HashMap<Integer, Integer>();

        SqlFragmentSource sql = buildSQLStatement();
        CTILogger.info(sql.toString());	
        CTILogger.info("START DATE > " + getStartDate() + "  -  STOP DATE <= " + getStopDate());

        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        template.query(sql.getSql(), sql.getArguments(), new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rset) throws SQLException, DataAccessException {

                int currentPointid = -1;
                int countData = 0;
                String lpDemandRate = null;
                String voltageDemandRate = null;
                String liDemandRate = null;
                String voltageDemandInterval = null;
                Vector<MeterAndPointData> tempMPDataVector = new Vector<MeterAndPointData>();	//Vector of MeterAndPointData
                while( rset.next()) {
                    Integer paobjectID = new Integer(rset.getInt(1));
                    String paoName = rset.getString(2);
                    @SuppressWarnings("unused")
                    String paoCategory = rset.getString(3);
                    PaoType paoType = PaoType.getForDbString(rset.getString(4));
                    String meterNumber = rset.getString(5);
                    String address = rset.getString(6);
                    String pointName = rset.getString(7);
                    Integer pointID = new Integer(rset.getInt(8));

                    if (pointID.intValue() != currentPointid) { //enter all
                    
                        if (currentPointid != -1) {	//not the first time
                            loadSummaryData(new Integer(currentPointid), tempMPDataVector);
                            tempMPDataVector = new Vector<MeterAndPointData>();  //get ready for the next PointID
                            //verify all data exists, count should be equal to timeInMillis/interval
                            long nowTime = new Date().getTime();							
                            long endTime = (getStopDate().getTime() > nowTime ? nowTime:getStopDate().getTime());
                            long totalTime  = (endTime - getStartDate().getTime())/1000; //convert to seconds
                            int intervals = 0;

                            LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(currentPointid);
                            if (lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND && voltageDemandRate != null) {
                                intervals = (int) (totalTime / Integer.valueOf(voltageDemandRate).intValue());
                            } else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND && lpDemandRate != null) {
                                intervals = (int) (totalTime / Integer.valueOf(lpDemandRate).intValue());
                            } else if (lp.getPointTypeEnum() == PointType.DemandAccumulator && liDemandRate != null) {
                                intervals = (int) (totalTime / Integer.valueOf(liDemandRate).intValue());
                            }
                            // else if( pointOffset == PointOffsets.PT_OFFSET_VOLTAGE_DEMAND && voltageDemandInterval != null)
                            //     intervals = (int) (totalTime / Integer.valueOf(voltageDemandInterval).intValue());


                            if (countData < intervals) {
                                getAllMissingDataPointIDs().put(new Integer(currentPointid), new Integer(intervals - countData));
                            }
                            countData = 0;
                        }
                    }
                    Timestamp ts = rset.getTimestamp(9);
                    Double value = new Double(rset.getDouble(10));
                    Integer quality = new Integer(rset.getInt(11));

                    lpDemandRate = null;
                    voltageDemandRate = null;
                    liDemandRate = null;
                    voltageDemandInterval = null;
                    if (getPointType() == LOAD_PROFILE_POINT_TYPE || getPointType() == DEMAND_ACC_POINT_TYPE)
                    {
                        lpDemandRate = String.valueOf( rset.getInt(12));
                        voltageDemandRate = String.valueOf(rset.getInt(13));
                        liDemandRate = String.valueOf(rset.getInt(14));
                        voltageDemandInterval = String.valueOf(rset.getInt(15));
                    }

                    PaoIdentifier paoIdentifier = new PaoIdentifier(paobjectID, paoType);
                    //meter is partially loaded!!! disabled flag, routeId, routeName = FAKE!!!
                    PlcMeter meter = new PlcMeter(paoIdentifier, meterNumber, paoName, false, "", -1, address);
                    
                    MeterAndPointData mpData = new MeterAndPointData(meter, pointID, pointName, new Date(ts.getTime()), value, quality);

                    LPMeterData lpMeterData = new LPMeterData(mpData, liDemandRate, voltageDemandInterval, lpDemandRate, voltageDemandRate);
                    getData().add(lpMeterData);
                    tempMPDataVector.add(mpData);
                    currentPointid = pointID.intValue();
                    countData++;
                }
                //Add last PointID's summary data
                loadSummaryData(new Integer(currentPointid), tempMPDataVector);
                //Load the last missing data
                long nowTime = new Date().getTime();							
                long endTime = (getStopDate().getTime() > nowTime ? nowTime:getStopDate().getTime());
                long totalTime  = (endTime - getStartDate( ).getTime())/1000; //convert to seconds
                int intervals = 0;

                LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(currentPointid);							
                if( lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND && voltageDemandRate != null)
                    intervals = (int) (totalTime / Integer.valueOf(voltageDemandRate).intValue());
                else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND && lpDemandRate != null)
                    intervals = (int) (totalTime / Integer.valueOf(lpDemandRate).intValue());
                else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_PROFILE_CHANNEL2 && lpDemandRate != null)
                    intervals = (int) (totalTime / Integer.valueOf(lpDemandRate).intValue());
                else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_PROFILE_CHANNEL3 && lpDemandRate != null)
                    intervals = (int) (totalTime / Integer.valueOf(lpDemandRate).intValue());
                else if( lp.getPointTypeEnum() == PointType.DemandAccumulator && liDemandRate != null)
                    intervals = (int) (totalTime / Integer.valueOf(liDemandRate).intValue());
                //				else if( pointOffset == PointOffsets.PT_OFFSET_VOLTAGE_DEMAND && voltageDemandInterval != null)
                //				    intervals = (int) (totalTime / Integer.valueOf(voltageDemandInterval).intValue());

                if( countData < intervals)
                    getAllMissingDataPointIDs().put(new Integer(currentPointid), new Integer(intervals - countData));

                if( getData() != null)
                {
                    Collections.sort(getData(), lpDataSummaryComparator);
                    if( getOrderBy() == DESCENDING)
                        Collections.reverse(getData());
                }
                return null;
            }
        });

        CTILogger.info("Report Records Collected from Database: " + getData().size());
        return;
    }
	
	/**
	 * @param pointid
	 * @param  allMPData - Vector of MeterAndPointData values
	 */
    private void loadSummaryData(Integer pointID, List<MeterAndPointData> allMPData)
	{
		Collections.sort(allMPData, mpDataValueComparator);
	
		if( allMPData.size() < 5)
		{
			allMPDataLows.put(pointID, allMPData);
			allMPDataPeaks.put(pointID, allMPData);
		}	
		else
		{
			allMPDataLows.put(pointID, allMPData.subList(0, 5));
			allMPDataPeaks.put(pointID, allMPData.subList(allMPData.size()-5, allMPData.size()));
		}
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	@Override
    public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof LPMeterData)
		{
		    LPMeterData lpMeterData = ((LPMeterData)o);
			switch( columnIndex)
			{			
				case PAO_NAME_COLUMN:
					return lpMeterData.getMeterAndPointData().getMeter().getName();
				case PAO_TYPE_COLUMN:
				    return lpMeterData.getMeterAndPointData().getMeter().getPaoType().getPaoTypeName();
				case METER_NUMBER_COLUMN:
				    return lpMeterData.getMeterAndPointData().getMeter().getMeterNumber();
				case PHYSICAL_ADDRESS_COLUMN:
                    return lpMeterData.getMeterAndPointData().getMeter().getSerialOrAddress();
				case POINT_NAME_COLUMN:
				    return lpMeterData.getMeterAndPointData().getPointName();
				case CHANNEL_NUMBER_COLUMN:
				{
				    LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpMeterData.getMeterAndPointData().getPointID().intValue());
				    if( lp != null && 
				            (lp.getPointOffset() >= PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND && lp.getPointOffset() <= PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND))
				    {
				        return String.valueOf(lp.getPointOffset() - 100);	//100 is the offset that load profile points start at (101, 102, 103, 104)
				    }
				    return null;
				}
				case CHANNEL_INTERVAL_COLUMN:
				{
				    LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpMeterData.getMeterAndPointData().getPointID().intValue());
				    if( lp != null)
				    {
				    	if( lp.getPointTypeEnum() == PointType.DemandAccumulator) {
					        if (lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND)
					            return lpMeterData.getVoltageDemandRate();
					        else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND)	//All other intervals are demandIntervals
					            return lpMeterData.getDemandRate();
					        else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_PROFILE_CHANNEL2)
					            return lpMeterData.getDemandRate();
					        else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_PROFILE_CHANNEL3)
					            return lpMeterData.getDemandRate();
					        else if( lp.getPointTypeEnum() == PointType.DemandAccumulator)
							    return lpMeterData.getLastIntervalDemand();
	//				        else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_VOLTAGE_DEMAND)
	//				            return lpMeterData.getLastIntervalVoltage();
				    	}
				    }
				    return null;
				}
				case MISSING_DATA_FLAG_COLUMN:
				    Integer missing = getAllMissingDataPointIDs().get(lpMeterData.getMeterAndPointData().getPointID());
				    return (missing == null) ? "Complete" : String.valueOf(missing.intValue()) + " Readings";
				        
				case TIME_COLUMN:
				{
					GregorianCalendar tempCal = new GregorianCalendar();
					tempCal.setTimeInMillis(lpMeterData.getMeterAndPointData().getTimeStamp().getTime());
					return tempCal.getTime();
				}
				case VALUE_COLUMN:
				{
					return lpMeterData.getMeterAndPointData().getValue();
				}
				case START_DATE_COLUMN:
				{
					GregorianCalendar tempCal = new GregorianCalendar();
					tempCal.setTimeInMillis(lpMeterData.getMeterAndPointData().getTimeStamp().getTime());
					return getBeginingOfDay(tempCal).getTime();
				}
				case PEAKS_TITLE_COLUMN:
					return PEAKS_TITLE_STRING;
				case LOWS_TITLE_COLUMN:
					return LOWS_TITLE_STRING;

				case PEAK_TIMESTAMP_0_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 4);
				case PEAK_VALUE_0_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 4);
				case LOW_TIMESTAMP_0_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 0);
				case LOW_VALUE_0_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 0);
				case PEAK_TIMESTAMP_1_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 3);
				case PEAK_VALUE_1_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 3);
				case LOW_TIMESTAMP_1_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 1);
				case LOW_VALUE_1_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 1);
				case PEAK_TIMESTAMP_2_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 2);
				case PEAK_VALUE_2_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 2);
				case LOW_TIMESTAMP_2_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 2);
				case LOW_VALUE_2_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 2);
				case PEAK_TIMESTAMP_3_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 1);
				case PEAK_VALUE_3_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 1);
				case LOW_TIMESTAMP_3_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 3);
				case LOW_VALUE_3_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 3);
				case PEAK_TIMESTAMP_4_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 0);
				case PEAK_VALUE_4_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 0);
				case LOW_TIMESTAMP_4_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 4);
				case LOW_VALUE_4_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 4);
					
				case INTERVALS_PER_HOUR:	//NOT A REAL COLUMN!!!
				{
				    int divisor = -1;
				    LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpMeterData.getMeterAndPointData().getPointID().intValue());
				    if( lp != null)
				    {
				    	if( lp.getPointTypeEnum() == PointType.DemandAccumulator) {
					        if (lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND ||
					                lp.getPointOffset() == PointOffsets.PT_OFFSET_VOLTAGE_DEMAND)
					        {
					            //Return null for voltage since this CASE is used to calculate total kWh
	//				            divisor = Integer.valueOf(lpMeterData.getVoltageDemandRate()).intValue();
					            return null;	//a null value "should" give us an "N/A" value.
					        }
					        else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND)	//All other intervals are demandIntervals
					            divisor = Integer.valueOf( lpMeterData.getDemandRate()).intValue();
                            else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_PROFILE_CHANNEL2)
                                divisor = Integer.valueOf( lpMeterData.getDemandRate()).intValue();
                            else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_PROFILE_CHANNEL3)
                                divisor = Integer.valueOf( lpMeterData.getDemandRate()).intValue();
					        else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_KW_DEMAND)	//All other intervals are demandIntervals
					            divisor = Integer.valueOf( lpMeterData.getLastIntervalDemand()).intValue();
				    	}
				    }				    
				    if (divisor >  -1)
				        return new Integer(3600 / divisor);
				    return null;
				}
				case INTERVALS_PER_DAY://NOT A REAL COLUMN!!!
				{
				    int divisor = -1;
				    LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpMeterData.getMeterAndPointData().getPointID().intValue());
				    if( lp != null)
				    {
				    	if( lp.getPointTypeEnum() == PointType.DemandAccumulator) {
					        if (lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND)
					            divisor = Integer.valueOf(lpMeterData.getVoltageDemandRate()).intValue();
					        else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND)	//All other intervals are demandIntervals
					            divisor = Integer.valueOf( lpMeterData.getDemandRate()).intValue();
					        else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_PROFILE_CHANNEL2)
                                divisor = Integer.valueOf( lpMeterData.getDemandRate()).intValue();
                            else if (lp.getPointOffset() == PointOffsets.PT_OFFSET_PROFILE_CHANNEL3)
                                divisor = Integer.valueOf( lpMeterData.getDemandRate()).intValue();
					        else if (lp.getPointTypeEnum() == PointType.DemandAccumulator)
					            divisor = Integer.valueOf(lpMeterData.getLastIntervalDemand()).intValue();
				    	}
				    }				    
				    if (divisor >  -1)
				        return new Integer(86400 / divisor);
				    return null;
				}				
				case POINT_TOTAL_KWH_COLUMN:	//handled by JFR expressions in Report definition
				    return null;
			}
		}
		return null;
	}

	/**
	 * @param i
	 * @return
	 */
    private Date getPeakTimestamp(int pointID, int i)
	{
		List<MeterAndPointData> peaks = allMPDataPeaks.get(new Integer(pointID));
		if( peaks != null && peaks.size() > i)
		{
			return peaks.get(i).getTimeStamp();
		}
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
    private Double getPeakValue(int pointID, int i)
	{
		List<MeterAndPointData> peaks = allMPDataPeaks.get(new Integer(pointID));
		if( peaks != null && peaks.size() > i)
			return peaks.get(i).getValue();
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
    private Date getLowTimestamp(int pointID, int i)
	{
		List<MeterAndPointData> lows = allMPDataLows.get(new Integer(pointID));
		if( lows != null && lows.size() > i)
		{
		    return lows.get(i).getTimeStamp();
		}
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
    private Double getLowValue(int pointID, int i)
	{
		List<MeterAndPointData> lows = allMPDataLows.get(new Integer(pointID));
		if( lows != null && lows.size() > i)
			return lows.get(i).getValue();
		return null;
	}	

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
    @Override
    public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				PAO_NAME_STRING,
				METER_NUMBER_STRING,
				PHYSICAL_ADDRESS_STRING,
				PAO_TYPE_STRING,
				
				POINT_NAME_STRING,
				CHANNEL_NUMBER_STRING,
				CHANNEL_INTERVAL_STRING,				
				MISSING_DATA_FLAG_STRING,

				TIME_STRING,
				VALUE_STRING, 

				START_DATE_STRING,
				DAILY_HIGH_TIME_STRING,
				DAILY_HIGH_STRING,
				DAILY_AVERAGE_STRING,
				DAILY_LOW_TIME_STRING,
				DAILY_LOW_STRING,
				TOTAL_KWH_STRING,
				DAILY_MISSING_DATA_FLAG_STRING,		
				
				PEAKS_TITLE_STRING,
				LOWS_TITLE_STRING,
				
				PEAK_TIMESTAMP_0_STRING,
				PEAK_VALUE_0_STRING,
				LOW_TIMESTAMP_0_STRING,
				LOW_VALUE_0_STRING,

				PEAK_TIMESTAMP_1_STRING,
				PEAK_VALUE_1_STRING,
				LOW_TIMESTAMP_1_STRING,
				LOW_VALUE_1_STRING,

				PEAK_TIMESTAMP_2_STRING,
				PEAK_VALUE_2_STRING,
				LOW_TIMESTAMP_2_STRING,
				LOW_VALUE_2_STRING,

				PEAK_TIMESTAMP_3_STRING,
				PEAK_VALUE_3_STRING,
				LOW_TIMESTAMP_3_STRING,
				LOW_VALUE_3_STRING,

				PEAK_TIMESTAMP_4_STRING,
				PEAK_VALUE_4_STRING,
				LOW_TIMESTAMP_4_STRING,
				LOW_VALUE_4_STRING,
				
				POINT_TOTAL_KWH_STRING,
				
				//These are not real columns!
				INTERVALS_PER_HOUR_STRING,
				INTERVALS_PER_DAY_STRING,
				DAILY_COUNT_STRING				
			};
		}
		return columnNames;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
    @Override
    public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[]{
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
								
				String.class,
				String.class,
				String.class,
				Date.class,
				Double.class,
				
				Date.class,
				Date.class,
				Double.class,
				Double.class,
				Date.class,
				Double.class,
				Double.class,
				String.class,

				String.class,
				String.class,

				Date.class,
				Double.class,
				Date.class,
				Double.class,
				
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Double.class,
				
				//These are not real columns
				Integer.class,
				Integer.class,
				Integer.class
				
			};
		}
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
    @Override
    public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				new ColumnProperties(0, 1, 175, null),
				new ColumnProperties(175, 1, 100, null),
				new ColumnProperties(275, 1, 100, null),	//NA?
				new ColumnProperties(375, 1, 100, null),	//NA?
				
				new ColumnProperties(0, 1, 175, null),	//NA?
				new ColumnProperties(175, 1, 100, null),
				new ColumnProperties(275, 1, 100, null),
				new ColumnProperties(375, 1, 100, null),
				new ColumnProperties(0, 1, 75, "HH:mm:ss"),	//NA?
				new ColumnProperties(75, 1, 75, "#,###,##0.00####"),	//NA?

				new ColumnProperties(0, 1, 50, "MM-dd-yyyy"),
				new ColumnProperties(75, 1, 75, "HH:mm:ss"),
				new ColumnProperties(150, 1, 75, "#,###,##0.00####"),
				new ColumnProperties(225, 1, 75, "#,###,##0.00####"),
				new ColumnProperties(300, 1, 75, "HH:mm:ss"),
				new ColumnProperties(375, 1, 75, "#,###,##0.00####"),
				new ColumnProperties(450, 1, 75, "#,###,###,##0.00####"),
				new ColumnProperties(50, 1, 25, null),
				
				new ColumnProperties(50, 1, 225, null),
				new ColumnProperties(325, 1, 225, null),
				
				new ColumnProperties(50, 30, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 30, 100, "#,###,##0.00####"),
				new ColumnProperties(325, 30, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 30, 100, "#,###,##0.00####"),

				new ColumnProperties(50, 42, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 42, 100, "#,###,##0.00####"),
				new ColumnProperties(325, 42, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 42, 100, "#,###,##0.00####"),
				
				new ColumnProperties(50, 54, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 54, 100, "#,###,##0.00####"),
				new ColumnProperties(325, 54, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 54, 100, "#,###,##0.00####"),

				new ColumnProperties(50, 66, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 66, 100, "#,###,##0.00####"),
				new ColumnProperties(325, 66, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 66, 100, "#,###,##0.00####"),

				new ColumnProperties(50, 78, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 78, 100, "0.00####"),
				new ColumnProperties(325, 78, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 78, 100, "#,###,##0.00####"),
				
				new ColumnProperties(450, 1, 100, "#,###,###,##0.0000####")
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	@Override
    public String getTitleString()
	{
		return title;
	}
	/**
	 * @return
	 */
	public int getPointType()
	{
		return pointType;
	}

	/**
	 * valid pointTYpes are listed in this classs, above
	 * @param i
	 */
	public void setPointType(int i)
	{
		pointType = i;
	}
	
    public String getPointTypeString(int pointTypeID) {
        if (pointTypeID == LOAD_PROFILE_POINT_TYPE)
            return LOAD_PROFILE_POINT_TYPE_STRING;
        else if (pointTypeID == CALC_POINT_TYPE)
            return CALC_POINT_TYPE_STRING;
        else if (pointTypeID == ANALOG_POINT_TYPE)
            return ANALOG_POINT_TYPE_STRING;
        else if (pointTypeID == DEMAND_ACC_POINT_TYPE)
            return DEMAND_ACC_POINT_TYPE_STRING;
        else if (pointTypeID == PULSE_ACC_POINT_TYPE)
            return PULSE_ACC_POINT_TYPE_STRING;
        else if (pointTypeID == STATUS_POINT_TYPE)
            return STATUS_POINT_TYPE_STRING;
        else
            return "UNKNOWN";
    }
	/**
	 * @return
	 */
	public static int[] getAllPointTypes()
	{
		return ALL_POINT_TYPES;
	}	
	/**
	 * @return
	 */
	public int getOrderBy()
	{
		return orderBy;
	}

	/**
	 * @param i
	 */
	public void setOrderBy(int i)
	{
		orderBy = i;
	}
	public String getOrderByString(int orderBy)
	{
		switch (orderBy)
		{
			case ORDER_BY_DEVICE_NAME:
				return "Device Name";
			case ORDER_BY_METER_NUMBER:
			    return "Meter Number";
			case ORDER_BY_PHYSICAL_ADDRESS:
			    return "Physical Address";
		}
		return "UNKNOWN";
	}
	public static int[] getAllOrderBys()
	{
		return ALL_ORDER_BYS;
	}	
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='title-header'>Point Type</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllPointTypes().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='" + ATT_POINT_TYPE +"' value='" + getAllPointTypes()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getPointTypeString(getAllPointTypes()[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='title-header'>Data Display</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" + ATT_SHOW_DETAILS+"' value='true'>Show Daily Summary" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Order By</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllOrderBys().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='"+ATT_ORDER_BY+"' value='" + getAllOrderBys()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getOrderByString(getAllOrderBys()[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td class='title-header'>Disabled Devices</td>" +LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;        
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td><input type='checkbox' name='"+ATT_EXCLUDE_DISABLED_DEVICES+"' value='true'> Exclude Disabled Devices" + LINE_SEPARATOR;
        html += "          </td>" + LINE_SEPARATOR;
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
			String param = req.getParameter(ATT_POINT_TYPE);
			if( param != null)
				setPointType(Integer.valueOf(param).intValue());
			else
				setPointType(LOAD_PROFILE_POINT_TYPE);
			
			param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_DEVICE_NAME);
			
			param = req.getParameter(ATT_SHOW_DETAILS);
			if( param != null)
			    setShowDetails(param.equalsIgnoreCase("true")?true:false);
			else 
			    setShowDetails(false);
			
			param = req.getParameter(ATT_EXCLUDE_DISABLED_DEVICES);
			if( param != null) {
			    excludeDisabledDevices = CtiUtilities.isTrue(param);
			}
		}
	}
    /**
     * @return Returns the allMissingDataPointIDs.
     */
    public HashMap<Integer, Integer> getAllMissingDataPointIDs()
    {
        return allMissingData;
    }
    public boolean isShowDetails()
    {
        return showDetails;
    }
    public void setShowDetails(boolean showDetails)
    {
        this.showDetails = showDetails;
    }
}
