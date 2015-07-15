package com.cannontech.database.data.fdr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.fdr.FDRInterfaceOption;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.google.common.collect.Lists;

@Deprecated
public class FDRInterface extends DBPersistent {
    
    private com.cannontech.database.db.point.fdr.FDRInterface fdrInterface = null;

    // holds instances of com.cannontech.database.db.point.fdr.FDRInterfaceOption
    private Vector<FDRInterfaceOption> interfaceOptionVector = null;

    public FDRInterface() {
        super();
    }

    public void add() throws SQLException {}

    public void delete() throws SQLException {}

    public com.cannontech.database.db.point.fdr.FDRInterface getFdrInterface() {
        if (fdrInterface == null)
            fdrInterface = new com.cannontech.database.db.point.fdr.FDRInterface();

        return fdrInterface;
    }

    public Vector<FDRInterfaceOption> getInterfaceOptionVector() {
        if (interfaceOptionVector == null)
            interfaceOptionVector = new Vector<>();

        return interfaceOptionVector;
    }

    public List<FDRInterfaceOption> getInterfaceOptionList() {
        List<FDRInterfaceOption> options = Lists.newArrayList(getInterfaceOptionVector());

        return options;
    }

    public void retrieve() throws SQLException {
    }

    public void setFdrInterface(com.cannontech.database.db.point.fdr.FDRInterface newFdrInterface) {
        fdrInterface = newFdrInterface;
    }

    public void setInterfaceOptionVector(Vector<FDRInterfaceOption> newInterfaceOptionVector) {
        interfaceOptionVector = newInterfaceOptionVector;
    }

    public String toString() {
        if (getFdrInterface() != null)
            return getFdrInterface().getInterfaceName();

        return null;
    }

    public void update() {}

    /**
     * Returns a valid translation string with the given inputs, else returns null
     */
    public static String createFDRTranslation(String[] labels, String[] values, String pointType) {

        if (labels == null || values == null || (labels.length != values.length)) {
            return null;
        }

        StringBuffer translation = new StringBuffer();
        for (int i = 0; i < labels.length; i++) {

            // append the labels text
            translation.append(labels[i] + ":");

            // append the values text
            translation.append(values[i] + ";");
        }

        // For all interfaces, add the PointType to the end of the translation string
        translation.append("POINTTYPE:" + pointType + ";");

        return translation.toString();
    }

    /**
     * Creates a default translation string for the given interface
     */
    public static FDRTranslation createDefaultTranslation(FDRInterface inter, Integer pointID, String pointType) {

        if (inter == null || inter.getInterfaceOptionVector().size() <= 0
            || inter.getFdrInterface().getAllDirections().length <= 0)
            return new FDRTranslation(pointID); // some dummy with no interface attributes

        String[] labels = new String[inter.getInterfaceOptionVector().size()];
        String[] values = new String[inter.getInterfaceOptionVector().size()];
        for (int i = 0; i < inter.getInterfaceOptionVector().size(); i++) {

            FDRInterfaceOption fdrOption = inter.getInterfaceOptionVector().get(i);

            labels[i] = fdrOption.getOptionLabel();
            values[i] = fdrOption.getAllOptionValues()[0]; // default to the first value in the list
        }

        String dest = inter.getFdrInterface().getInterfaceName();
        for (int i = 0; i < labels.length; i++) {
            String lowercase = labels[i].toLowerCase();
            if (lowercase.contains("destination")) {
                dest = values[i];
                break;
            }
        }

        return new FDRTranslation(pointID, inter.getFdrInterface().getAllDirections()[0],
            inter.getFdrInterface().getInterfaceName(), dest, createFDRTranslation(labels, values, pointType));
    }
    
    
    @SuppressWarnings("resource")
    public static FDRInterface[] getALLFDRInterfaces() {
        
        String databaseAlias = CtiUtilities.getDatabaseAlias();
        
        List<FDRInterface> interfaces = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        // get all the gears that have the passed in DeviceID
        String sql = "SELECT InterfaceID, InterfaceName, PossibleDirections, hasDestination " +
                     " FROM FDRInterface" +
                     " ORDER BY InterfaceName";
        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);

            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            } else {
                pstmt = conn.prepareStatement(sql.toString());
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    com.cannontech.database.db.point.fdr.FDRInterface interf = 
                            new com.cannontech.database.db.point.fdr.FDRInterface();
                    
                    interf.setInterfaceID(rset.getInt("InterfaceID"));
                    interf.setInterfaceName(rset.getString("InterfaceName"));
                    interf.setPossibleDirections(rset.getString("PossibleDirections"));
                    interf.setHasDestination(rset.getString("HasDestination").equalsIgnoreCase("t"));

                    FDRInterface value = new FDRInterface();
                    value.setFdrInterface(interf);
                    interfaces.add(value);
                }

                // Match up all the InterfaceOptions that have the same interfaceID's
                sql = "SELECT InterfaceID, OptionLabel, Ordering, OptionType, OptionValues" +
                        " FROM " + FDRInterfaceOption.TABLE_NAME +
                        " ORDER BY InterfaceID, Ordering";

                pstmt = conn.prepareStatement(sql.toString());
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    
                    FDRInterfaceOption option = new FDRInterfaceOption();
                    
                    option.setInterfaceID(rset.getInt("InterfaceID"));
                    option.setOptionLabel(rset.getString("OptionLabel"));
                    option.setOrdering(rset.getInt("Ordering"));
                    option.setOptionType(rset.getString("OptionType"));
                    option.setOptionValues(rset.getString("OptionValues"));

                    for (FDRInterface iface : interfaces) {
                        
                        if (iface.getFdrInterface().getInterfaceID().equals(option.getInterfaceID())) {
                            
                            iface.getInterfaceOptionVector().add(option);
                            break;
                        }
                    }
                }

            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        FDRInterface retVal[] = new FDRInterface[interfaces.size()];
        interfaces.toArray(retVal);

        return retVal;
    }
}