package com.cannontech.analysis.tablemodel;

import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.util.NaturalOrderComparator;

public class RouteDBModel extends CarrierDBModel 
{
    /** Enum values for column representation */
    public final static int ROUTEDB_ROUTE_NAME_COLUMN = 0;
	public final static int ROUTEDB_METER_NAME_COLUMN = 1;
    public final static int ROUTEDB_ENABLED_COLUMN = 2;
    public final static int ROUTEDB_METER_TYPE_COLUMN = 3;
	public final static int ROUTEDB_METER_NUMBER_COLUMN = 4;
	public final static int ROUTEDB_ADDRESS_COLUMN = 5;
	
	/** String values for column representation */
	public final static String ROUTEDB_ROUTE_NAME_STRING = "Route Name";
	public final static String ROUTEDB_METER_NAME_STRING = "Meter Name";
    public final static String ROUTEDB_ENABLED_STRING  = "Enabled";
	public final static String ROUTEDB_METER_TYPE_STRING = "Type";
	public final static String ROUTEDB_METER_NUMBER_STRING = "Meter #";
	public final static String ROUTEDB_ADDRESS_STRING  = "Address";

	
	public static final int ORDER_BY_METER_NAME = 0;
	public static final int ORDER_BY_METER_NUMBER = 1;
	private int orderBy = ORDER_BY_METER_NAME;	//default
    
	private static final int[] ALL_ORDER_BYS = new int[] {
		ORDER_BY_METER_NAME, 
        ORDER_BY_METER_NUMBER
	};

	private static final String ATT_ORDER_BY = "orderBy";
	
	public Comparator<Meter> routeDBComparator = new java.util.Comparator<Meter>() {
		public int compare(Meter o1, Meter o2){

		    String thisVal = o1.getRoute();
		    String anotherVal = o2.getRoute();
            
			if( thisVal.equalsIgnoreCase(anotherVal)) {
				    
			    if( getOrderBy() == ORDER_BY_METER_NUMBER) {
                    NaturalOrderComparator noComp = new NaturalOrderComparator(); 
                    return noComp.compare(o1.getMeterNumber(), o2.getMeterNumber()); 
			    }
	
			    if (getOrderBy() == ORDER_BY_METER_NAME || thisVal.equalsIgnoreCase(anotherVal)) {
			        thisVal = o1.getName();
			        anotherVal = o2.getName();
				}
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}
	};
	
	@Override
    public Object getAttribute(int columnIndex, Object o) {
        if ( o instanceof Meter)
        {
            Meter meter = (Meter)o;
            switch( columnIndex)
            {
                case ROUTEDB_ROUTE_NAME_COLUMN:
                    return meter.getRoute();

                case ROUTEDB_METER_NAME_COLUMN:
                    return meter.getName();
                
                case ROUTEDB_ENABLED_COLUMN:
                    return (meter.isDisabled() ? "No" : "Yes");
        
                case ROUTEDB_METER_TYPE_COLUMN:
                    return meter.getPaoType().getPaoTypeName();

                case ROUTEDB_METER_NUMBER_COLUMN:
                    return meter.getMeterNumber();
                    
                case ROUTEDB_ADDRESS_COLUMN:
                    return meter.getAddress();
            }
        }
        return null;
    }


	@Override
	public void collectData() {
		super.collectData();
        
		if(getData() != null) {
		    //Order the records
			Collections.sort(getData(), routeDBComparator);
			if( getSortOrder() == DESCENDING)
			    Collections.reverse(getData());				
		}
	}

    @Override
	public String[] getColumnNames() {
		if( columnNames == null)
		{
			columnNames = new String[]{
			    ROUTEDB_ROUTE_NAME_STRING,
			    ROUTEDB_METER_NAME_STRING,
			    ROUTEDB_ENABLED_STRING,
			    ROUTEDB_METER_TYPE_STRING,
			    ROUTEDB_METER_NUMBER_STRING,
			    ROUTEDB_ADDRESS_STRING
			};
		}
		return columnNames;
	}
	
    @Override
	public ColumnProperties[] getColumnProperties() {
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				new ColumnProperties(0, 1, 280, null),
				new ColumnProperties(0, 1, 280, null),
                new ColumnProperties(280, 1, 100, null),
                new ColumnProperties(380, 1, 110, null),
                new ColumnProperties(490, 1, 110, null),
                new ColumnProperties(600, 1, 110, null)
			};
		}
		return columnProperties;
	}

	@Override
    public Class[] getColumnTypes() {
        if( columnTypes == null)
        {
            columnTypes = new Class[]{
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class
            };
        }
        return columnTypes;
    }
    
	@Override
	public String getTitleString() {
		return title + " - Routes";
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int i) {
		orderBy = i;
	}
	
	public String getOrderByString(int orderBy) {
		switch (orderBy)
		{
			case ORDER_BY_METER_NAME:
				return "Meter Name";
			case ORDER_BY_METER_NUMBER:
				return "Meter Number";
		}
		return "UNKNOWN";
	}
	
	public static int[] getAllOrderBys() {
		return ALL_ORDER_BYS;
	}
	
	@Override
	public String getHTMLOptionsTable() {
	    String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
	    
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Order By</td>" +LINE_SEPARATOR;
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
		
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}
	
	@Override
	public void setParameters( HttpServletRequest req ) {
	    super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_METER_NAME);			
		}
	}
}
