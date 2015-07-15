package com.cannontech.database.db.point.fdr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.fdr.FDRInterface;
import com.cannontech.database.db.DBPersistent;

@Deprecated
public class FDRTranslation extends DBPersistent {
    
    private Integer pointID = null;
    private String directionType = null;
    private String interfaceType = null;
    private String destination = "(not used)"; // not used as of 5-31-2001
    private String translation = null;
    
    //private FDRTranslationEntry fdrTranslationEntry;

    public static final String TABLE_NAME = "FDRTranslation";

    public FDRTranslation() {
        super();
    }

    public FDRTranslation(Integer pointID) {
        super();
        setPointID(pointID);
    }

    /**
     * Create a dummy FDR translation object from some reasonable default values.
     * Attempts to create a DSM2IMPORT interface.
     */
    public static final FDRTranslation createTranslation(Integer pointID) {

        FDRInterface[] interfaces = FDRInterface.getALLFDRInterfaces();
        if (interfaces.length > 0) {
            return new FDRTranslation(pointID, "Receive", "DSM2IMPORT", "DSM2IMPORT", "Point:1;POINTTYPE:Analog;");
        } else
            return new FDRTranslation(pointID);
    }

    public FDRTranslation(Integer pointID, String directionType, String interfaceType, String destination, String translation) {
        super();
        setPointID(pointID);
        setDirectionType(directionType);
        setInterfaceType(interfaceType);
        setTranslation(translation);
        setDestination(destination);

    }

    public void add() throws SQLException {
        Object addValues[] = { getPointID(), getDirectionType(), getInterfaceType(), getDestination(), getTranslation() };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, "POINTID", getPointID());
    }

    public String getDestination() {
        return destination;
    }

    public String getDirectionType() {
        return directionType;
    }

    /**
     * Finds the value of an entry with the translation in the following form (examples):
     * Category:PSEUDO;Remote:14;Point:66;POINTTYPE:Analog;
     * Point:234;Interval (sec):1;POINTTYPE:Analog;
     * 
     * First, the label is searched for, then the value on the right hand side of the : is
     * returned. The label search is case sensitive. Returns null if the value is not
     * found.
     */
    public static String getTranslationValue(String translation, String label) {

        if (translation == null || label == null)
            return null;

        String[] entries = translation.split(";");

        for (String entry : entries) {
            // found the label, return the value
            if (entry.startsWith(label)) {
                int valueBegin = entry.indexOf(":") + 1;
                return entry.substring(valueBegin);
            }
        }

        return null;
    }

    /**
     * Returns the new translation string with the passed in label value set.
     */
    public static String setTranslationValue(String translation, String newVal, String label) {

        if (translation == null || label == null || newVal == null)
            return translation;

        StringBuffer buffer = new StringBuffer(translation);
        int startIndx = translation.indexOf(label + ":");
        int endIndx = startIndx + translation.substring(startIndx, translation.length()).indexOf(";");

        buffer.replace(startIndx, endIndx, label + ":" + newVal);

        return buffer.toString();
    }

    public static Vector<FDRTranslation> getFDRTranslations(Integer pointID) {

        return getFDRTranslations(pointID, CtiUtilities.getDatabaseAlias());
    }

    public static Vector<FDRTranslation> getFDRTranslations(Integer pointID, String databaseAlias) {
        Vector<FDRTranslation> returnVector = new Vector<FDRTranslation>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql =
            "SELECT DirectionType, InterfaceType, Destination, " + "Translation FROM " + TABLE_NAME + " WHERE PointID= ?";

        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);

            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            } else {
                pstmt = conn.prepareStatement(sql.toString());
                pstmt.setInt(1, pointID);

                rset = pstmt.executeQuery();

                while (rset.next()) {
                    returnVector.addElement(new FDRTranslation(pointID, 
                                                               rset.getString("DirectionType"),
                                                               rset.getString("InterfaceType"),
                                                               rset.getString("Destination"),
                                                               rset.getString("Translation")));
                }

            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        return returnVector;
    }

    public String getInterfaceType() {
        return interfaceType;
    }
    
    public FdrInterfaceType getInterfaceEnum() {
        return FdrInterfaceType.valueOf(interfaceType);
    }

    public Integer getPointID() {
        return pointID;
    }

    public String getTranslation() {
        return translation;
    }

    public void retrieve() throws SQLException {
        String selectColumns[] = { "DIRECTIONTYPE", "INTERFACETYPE", "DESTINATION", "TRANSLATION" };

        String constraintColumns[] = { "POINTID" };
        Object constraintValues[] = { getPointID() };

        Object results[] = retrieve(selectColumns, TABLE_NAME, constraintColumns, constraintValues);

        if (results.length == selectColumns.length) {
            setDirectionType((String) results[0]);
            setInterfaceType((String) results[1]);
            setDestination((String) results[2]);
            setTranslation((String) results[3]);
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");

    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDirectionType(String newValue) {
        this.directionType = newValue;
    }

    public void setInterfaceType(String newInterfaceType) {
        interfaceType = newInterfaceType;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    public void setTranslation(String newValue) {
        this.translation = newValue;
    }

    public void update() throws SQLException {
        String setColumns[] = { "DIRECTIONTYPE", "INTERFACETYPE", "DESTINATION", "TRANSLATION" };
        Object setValues[] = { getDirectionType(), getInterfaceType(), getDestination(), getTranslation() };

        String constraintColumns[] = { "POINTID" };
        Object constraintValues[] = { getPointID() };

        update(TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues);
    }

    static public String getDestinationField(String trans) {
        StringTokenizer tokenizer = new StringTokenizer(trans, ";");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextElement().toString();
            if (token.toLowerCase().indexOf("estination") >= 0)
                return token.substring(token.indexOf(":") + 1, token.length());
        }

        return "(not found)";
    }
}
