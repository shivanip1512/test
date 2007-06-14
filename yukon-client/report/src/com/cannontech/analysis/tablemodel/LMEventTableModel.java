package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

public class LMEventTableModel extends BareReportModelBase<LMEventTableModel.ModelRow> implements LoadableModel//, CommonModelAttributes 
{

private int energyCompanyId;
private Date fromDate;
private Date toDate;

private List<ModelRow> data = Collections.emptyList();
private List<ModelRow> tempData = Collections.emptyList();

private static final List<ColumnData> columnData = new ArrayList<ColumnData>();

public LMEventTableModel() {
}

public List<ColumnData> getColumnData() {
    return columnData;
}

static public class ModelRow {
    public String programName;
    public Integer accountNumber;
    public String customerFirstName;
    public String customerLastName;
    public String startDate;
    public String stopDate;
    public Integer optOutEventCount;
    public Double optOutControlHours;
    public Double customerControlTime;
    public String calcStartDate;
    public String calcStopDate;
}

//public void loadData() {
//
//    StringBuffer sql = buildPreliminarySQLStatement();
//    CTILogger.info(sql.toString()); 
//    
//    java.sql.Connection conn = null;
//    java.sql.PreparedStatement pstmt = null;
//    java.sql.ResultSet rset = null;
//    
//    try {
//        conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
//
//        if( conn == null ) {
//            CTILogger.error(getClass() + ":  Error getting database connection.");
//            return;
//        }
//        else {
//            pstmt = conn.prepareStatement(sql.toString());
//            rset = pstmt.executeQuery();
//            
//            while( rset.next()) {
//                addPreliminaryDataRow(rset);
//            }
//        }
//    }
//    catch( java.sql.SQLException e ) {
//        e.printStackTrace();
//    }
//    
//    finally{
//        try{
//            if( pstmt != null )
//                pstmt.close();
//            if( conn != null )
//                conn.close();
//        }
//        catch( java.sql.SQLException e ) {
//            e.printStackTrace();
//        }
//    }
//    CTILogger.info("Report Records Collected from Database: " + data.size());
//    
//}

/**
 * Add <innerClass> objects to data, retrieved from rset.
 * @param ResultSet rset
 */
public void addPreliminaryDataRow(ResultSet rset)
{
    try
    {
        String programName_ = rset.getString(1);
        Integer accountNumber_ = rset.getInt(2);
        String customerFirstName_= rset.getString(3);
        String customerLastName_ = rset.getString(4);
        
        ModelRow row = new ModelRow();
        row.programName =programName_; 
        row.accountNumber = accountNumber_;
        row.customerFirstName = customerFirstName_;
        row.customerLastName = customerLastName_;
        
        // get all the enrollment start times for this program and account
        StringBuffer sql = buildStartStopTimesSQLStatement( row.programName, row.accountNumber);
        CTILogger.info(sql.toString()); 
        
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet timesResultSet = null;
        
//        try {
//            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
//
//            if( conn == null ) {
//                CTILogger.error(getClass() + ":  Error getting database connection.");
//                return;
//            }
//            else {
////                pstmt = conn.prepareStatement(sql.toString());
////                timesResultSet = pstmt.executeQuery();
////                tempData = Collections.emptyList();
////                while( timesResultSet.next()) {
////                    if (timesResultSet.isFirst()) {
////                        Date startDate = timesResultSet.getDate(1);
////                        if (startDate)
////                    }else {
////                        // insert another row for the extra start times found
////                        Date startDate = timesResultSet.getDate(1);
////                        ModelRow tempRow = new ModelRow();
////                        tempRow.programName =programName_; 
////                        tempRow.accountNumber = accountNumber_;
////                        tempRow.customerFirstName = customerFirstName_;
////                        tempRow.customerLastName = customerLastName_;
////                        
////                        tempData.add(tempRow);
////                    }
////                }
//            }
//        }
//        catch( java.sql.SQLException e ) {
//            e.printStackTrace();
//        }
        
        data.add(row);

    }
    catch(java.sql.SQLException e)
    {
        e.printStackTrace();
    }
}

public StringBuffer buildPreliminarySQLStatement()
{
    StringBuffer sql = new StringBuffer ("SELECT DISTINCT ywc.AlternateDisplayName AS programName, ca.AccountNumber AS accountNumber, cont.ContFirstName AS customerFirstName, cont.ContLastName AS customerLastName ");
    sql.append("FROM YukonWebConfiguration ywc ");
    sql.append("JOIN LMProgramWebPublishing lmpwp ON lmpwp.WebSettingsID = ywc.ConfigurationID ");
    sql.append("JOIN LMProgramEvent lmpe ON lmpe.ProgramID = lmpwp.ProgramID ");
    sql.append("JOIN CustomerAccount ca ON ca.AccountID = lmpe.AccountID ");
    sql.append("JOIN Customer c ON c.CustomerID = ca.CustomerID ");
    sql.append("JOIN Contact cont ON cont.ContactID = c.PrimaryContactID ");
    sql.append("JOIN LMCustomerEventBase lmceb ON lmpe.eventid = lmceb.eventid ");
    sql.append("ORDER BY programName, AccountNumber");
    sql.append(";");
    return sql;
}


/**
 * Add <innerClass> objects to data, retrieved from rset.
 * @param ResultSet rset
 */
public void addStartStopTimesDataRow(ResultSet rset)
{
    try
    {
        String programName_ = rset.getString(1);
        Integer accountNumber_ = rset.getInt(2);
        String customerFirstName_= rset.getString(3);
        String customerLastName_ = rset.getString(4);
        
        ModelRow row = new ModelRow();
        row.programName =programName_; 
        row.accountNumber = accountNumber_;
        row.customerFirstName = customerFirstName_;
        row.customerLastName = customerLastName_;
        data.add(row);

    }
    catch(java.sql.SQLException e)
    {
        e.printStackTrace();
    }
}

public StringBuffer buildStartStopTimesSQLStatement(String programName, Integer accountNumber)
{
    StringBuffer sql = new StringBuffer ("SELECT DISTINCT EventDateTime ");
    sql.append("FROM YukonWebConfiguration ywc ");
    sql.append("JOIN LMProgramWebPublishing lmpwp ON lmpwp.WebSettingsID = ywc.ConfigurationID ");
    sql.append("JOIN LMProgramEvent lmpe ON lmpe.ProgramID = lmpwp.ProgramID ");
    sql.append("JOIN CustomerAccount ca ON ca.AccountID = lmpe.AccountID ");
    sql.append("JOIN LMCustomerEventBase lmceb ON lmpe.eventid = lmceb.eventid ");
    sql.append("WHERE lmceb.ActionID = 1011 AND ywc.AlternateDisplayName = '" + programName + "' AND ca.AccountNumber = '" + accountNumber + "'");
    sql.append(";");
    return sql;
}

@Override
protected ModelRow getRow(int rowIndex) {
    return data.get(rowIndex);
}

@Override
protected Class<ModelRow> getRowClass() {
    return ModelRow.class;
}

public String getTitle() {
    return "LM Event Report";
}

public int getRowCount() {
    return data.size();
}

public void setEnergyCompanyId(int energyCompanyId) {
    this.energyCompanyId = energyCompanyId;
}

public void setStartDate(Date startDate) {
    this.fromDate = startDate;
}

public void setStopDate(Date stopDate) {
    this.toDate = stopDate;
}
}