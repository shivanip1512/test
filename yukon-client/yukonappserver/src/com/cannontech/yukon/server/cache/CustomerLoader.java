package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.database.db.customer.Customer;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class CustomerLoader implements Runnable 
{
    //if total contact number is over this, better not load this way
    private final int MAX_CUSTOMER_LOAD = 50000;
    private boolean willNeedDynamicLoad = false;
    
    //	Map<Integer(custID), LiteCustomer>
    private Map allCustsMap = null;
    private ArrayList allCustomers = null;
    private String databaseAlias = null;
    
    /**
     * CustomerLoader constructor comment.
     */
    public CustomerLoader(ArrayList custArray, Map custMap, String alias) 
    {
        super();
        this.allCustomers = custArray;
        this.databaseAlias = alias;
        this.allCustsMap = custMap;
    }
    
    /**
     * run method comment.
     */
    public void run() 
    {
        Date timerStart = null;
        Date timerStop = null;
        
        //temp code
        timerStart = new Date();
        //get all the customer contacts that are assigned to a customer
        String sqlString = 
            "select CustomerID, PrimaryContactID, TimeZone, CustomerTypeID, CustomerNumber, RateScheduleID, " +
            "AltTrackNum, TemperatureUnit " +
            "from " + Customer.TABLE_NAME;
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        
        int maxCustomerID = 0;
        int loadIterator = MAX_CUSTOMER_LOAD;
        
        try
        {
            conn = PoolManager.getInstance().getConnection( this.databaseAlias );
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);
            
            while (rset.next() && loadIterator > 0)
            {
                int cstID = rset.getInt(1);
                int contactID = rset.getInt(2);
                String timeZone = rset.getString(3).trim();
                int custTypeID = rset.getInt(4);
                String custNumber = rset.getString(5).trim();
                int custRateScheduleID = rset.getInt(6);
                String custAltTrackNum = rset.getString(7).trim();
                String temperatureUnit = rset.getString(8).trim();
                
                LiteCustomer lc;
                if( custTypeID == CustomerTypes.CUSTOMER_CI) {
                    lc = new LiteCICustomer(cstID);
                } else {
                    lc = new LiteCustomer( cstID );
                }
                lc.setPrimaryContactID(contactID);
                lc.setTimeZone(timeZone);
                lc.setCustomerTypeID(custTypeID);
                lc.setCustomerNumber(custNumber);
                lc.setRateScheduleID(custRateScheduleID);
                lc.setAltTrackingNumber(custAltTrackNum);
                lc.setTemperatureUnit(temperatureUnit);
                
                allCustomers.add(lc);
                if(maxCustomerID < lc.getCustomerID())
                    maxCustomerID = lc.getCustomerID();
                loadIterator--;
            }
            
            
            sqlString = 
                "SELECT ca.CustomerID, ca.ContactID, ca.Ordering " + 
                "FROM CustomerAdditionalContact ca, " + 
                Customer.TABLE_NAME + " c " + 
                "WHERE ca.CustomerID=c.CustomerID " +
                "and ca.CustomerID <= " + maxCustomerID + " " +
                "ORDER BY ca.Ordering";
            
            Vector vectVals = new Vector(32);
            rset = stmt.executeQuery(sqlString);
            
            while( rset.next() )
            {
                //maps CustomerID,ContactID
                int[] vals = { rset.getInt(1), rset.getInt(2) };
                vectVals.add( vals );
            }
            
            //assign our Contacts to their owner Customer
            for( int i = 0; i < allCustomers.size(); i++ )
            {
                LiteCustomer lc = (LiteCustomer)allCustomers.get(i);
                boolean found = false; //tries to make this fast
                
                for( int j = 0; j < vectVals.size(); j++ )
                {
                    int[] map = (int[])vectVals.get(j);
                    if( map[0] == lc.getCustomerID() )
                    {
                        lc.getAdditionalContacts().add( DaoFactory.getContactDao().getContact(map[1]) );
                        
                        found = true;
                    }
                    else if( found )  //speed it up!
                        break;
                }
                //Put all customers in the map
                allCustsMap.put( new Integer(lc.getCustomerID()), lc);
            }
            
            if (VersionTools.starsExists()) {
                sqlString =	"SELECT acct.AccountID, map.EnergyCompanyID, acct.CustomerID " +
                "FROM CustomerAccount acct, ECToAccountMapping map " +
                "WHERE acct.AccountID = map.AccountID " +
                "and acct.CustomerID <= " + maxCustomerID + " " +
                "order by acct.customerID";
                
                rset = stmt.executeQuery(sqlString);
                while( rset.next())
                {	//TODO we are updating this everytime, and it should really only be updated after the acctIDs are all collected.
                    int acctID = rset.getInt(1);
                    int ecID = rset.getInt(2);
                    int cstID = rset.getInt(3);
                    ((LiteCustomer)allCustsMap.get(new Integer(cstID))).setEnergyCompanyID(ecID);
                    ((LiteCustomer)allCustsMap.get(new Integer(cstID))).getAccountIDs().add(new Integer(acctID));
                }
            }
            
            //TODO incorporate EnergyCompanyCustomerList to get the energycompany value.
            sqlString = 
                "select CustomerID, MainAddressID, CompanyName, " +
                "CustomerDemandLevel, CurtailAmount, CICustType " +
                "from " + CICustomerBase.TABLE_NAME + " " +
                "where CustomerID <= " + maxCustomerID;
            
            rset = stmt.executeQuery(sqlString);
            
            while( rset.next() )
            {
                int cstID = rset.getInt(1);
                int addressID = rset.getInt(2);
                String name = rset.getString(3).trim();
                double dmdLevel = rset.getDouble(4);
                double curtAmount = rset.getDouble(5);
                int cstTypeID = rset.getInt(6);
                ((LiteCICustomer)allCustsMap.get(new Integer(cstID))).setMainAddressID(addressID);
                ((LiteCICustomer)allCustsMap.get(new Integer(cstID))).setCompanyName(name);
                ((LiteCICustomer)allCustsMap.get(new Integer(cstID))).setDemandLevel(dmdLevel);
                ((LiteCICustomer)allCustsMap.get(new Integer(cstID))).setCurtailAmount(curtAmount);
                ((LiteCICustomer)allCustsMap.get(new Integer(cstID))).setCICustType(cstTypeID);
            }
        }
        catch( java.sql.SQLException e )
        {
            CTILogger.error( e.getMessage(), e );
        }
        finally
        {
            try
            {
                if( stmt != null )
                    stmt.close();
                if( conn != null )
                    conn.close();
            }
            catch( SQLException e )
            {
                CTILogger.error( e.getMessage(), e );
            }

            timerStop = new java.util.Date();
            CTILogger.info( 
                           (timerStop.getTime() - timerStart.getTime())*.001 + 
                           " Secs for CICustomerLoader with Contacts (" + allCustomers.size() + " loaded)" );
            if(loadIterator <= 0)
            {
                CTILogger.warn("Customer loader limit exceeded!  System will need to use " +
                "dynamic loading for some customers.");
                willNeedDynamicLoad = true;
            }
        }
    }
    
    public boolean limitExceeded()
    {
        return willNeedDynamicLoad;
    }
}
