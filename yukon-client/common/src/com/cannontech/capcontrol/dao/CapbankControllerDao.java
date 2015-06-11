package com.cannontech.capcontrol.dao;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public interface CapbankControllerDao {
    
    public static final YukonRowMapper<LiteCapControlObject> LITE_ORPHAN_MAPPER =
            new YukonRowMapper<LiteCapControlObject>() {
                @Override
                public LiteCapControlObject mapRow(YukonResultSet rs) throws SQLException {
                    
                    LiteCapControlObject lco = new LiteCapControlObject();
                    lco.setId(rs.getInt("PAObjectID"));
                    lco.setType(rs.getString("TYPE"));
                    lco.setDescription(rs.getString("Description"));
                    lco.setName(rs.getString("PAOName"));
                    // This is used for orphans. We will need to adjust the SQL if we intend to use
                    // this
                    // for anything other than orphaned cbcs.
                    lco.setParentId(0);
                    return lco;
                }
            };
    
    /**
     * Assigns a cbc to a capbank and performs the necessary dbchange message sending.
     * @param controllerId the id of the CBC being assigned
     * @param capbankName the name of the capbank being assigned to.
     * @return true if the assignment occurred with only one row in the db updated, false otherwise.
     */
    public boolean assignController(int controllerId, String capbankName);
    
    /**
     * Assigns a cbc to a capbank and performs the necessary dbchange message sending.
     * @param capbankId the id of the capbank being assigned to.
     * @param controllerId the id of the CBC being assigned
     * @return true if the assignment occurred with only one row in the db updated, false otherwise.
     */
    public boolean assignController(int capbankId, int controllerId);

    /**
     * Removes all assignments for the given controller
     * @param controller the paoId of the CBC
     * @return true if the unassignment occurred with only one row in the db 
     * updated, false otherwise.
     */
    public boolean unassignController(int controller);
    
    /**
     * This method checks to see if a serial number is valid for a CBC (that is, it checks
     * to see that no other CBC in the database has the same serial number.)
     * @param deviceId the deviceId of the CBC who is allowed to have the serialNumber (if any.)
     * @param serialNumber the serial number in question.
     * @return true if the serial number doesn't already exist in the database, false otherwise.
     */
    public boolean isSerialNumberValid(Integer deviceId, int serialNumber);
    
    /**
     * This method checks to see if a serial number is valid for a new CBC.
     * @param serialNumber the serial number in question.
     * @return true if the serial number doesn't already exist in the database, false otherwise.
     */
    public boolean isSerialNumberValid(int serialNumber);
	
	public List<LiteCapControlObject> getOrphans();
	
	/**
	 * Looks up the subbus that the cbc is attached to.
	 * 
	 * @param cbcPaoId the paoId of the CBC
	 * @return the paoidentifier of the parent bus
	 * @throws NotFoundException if orphaned
	 */
	public PaoIdentifier getParentBus(int cbcPaoId);
}