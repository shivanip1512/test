package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.MutableDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.user.YukonUserContext;

/**
 * This provides support for creating "binning" dynamic device groups. A binning
 * device group is one that sorts a subset of all devices into a single level of sub groups
 * (or bins). For example, the Rolodex dynamic group sorts all of the devices into
 * bins according to the first character of their PAO Name. 
 * 
 * By having the provider extend this class, the implementer only needs to write a few simple
 * methods.
 *
 * @param <T> The type that represents a bin, usually a String
 */
public abstract class BinningDeviceGroupProviderBase<T> extends DeviceGroupProviderSqlBase {
    private SimpleJdbcOperations jdbcTemplate;
    @Autowired private ObjectFormattingService objectFormatingService;
    
    @Override
    public Set<SimpleDevice> getChildDevices(DeviceGroup group) {
        
        if (group instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
            
            BinnedDeviceGroup binnedDeviceGroup = (BinnedDeviceGroup) group;
            
            Set<SimpleDevice> devices = getDevicesInBin(binnedDeviceGroup.bin);
            
            return Collections.unmodifiableSet(devices);
        }
        
        // this must be our parent group
        return Collections.emptySet();
    }

    protected Set<SimpleDevice> getDevicesInBin(T bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid, ypo.type");
        sql.append("FROM YukonPaObject ypo");
        sql.append("WHERE ");
        sql.append(getChildWhereForBin(bin, "ypo.paobjectid"));
        
        YukonDeviceRowMapper mapper = new YukonDeviceRowMapper();
        List<SimpleDevice> devices = jdbcTemplate.query(sql.getSql(), mapper, sql.getArguments());
        
        return new HashSet<SimpleDevice>(devices);
    }

   @Override
    public List<DeviceGroup> getChildGroups(final DeviceGroup group) {
        if (group instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
            return Collections.emptyList();
        }
        
        List<T> allBins = getAllBins();
        
        List<DeviceGroup> result = new MappingList<T, DeviceGroup>(allBins, new ObjectMapper<T, DeviceGroup>() {
            public DeviceGroup map(T from) {
                BinnedDeviceGroup binnedDeviceGroup = createGroupForBin(group,
                                                                        from);
                
                return binnedDeviceGroup;
            }

        });
        
        return Collections.unmodifiableList(result);
    }

   private BinnedDeviceGroup createGroupForBin(final DeviceGroup group, T bin) {
       String groupName = getGroupName(bin);
       BinnedDeviceGroup binnedDeviceGroup = new BinnedDeviceGroup();
       binnedDeviceGroup.bin = bin;
       binnedDeviceGroup.setName(groupName);
       binnedDeviceGroup.setParent(group);
       binnedDeviceGroup.setType(group.getType());
       return binnedDeviceGroup;
   };
    
    /**
     * This determines the String that will be used to display the group.
     * For some groups, using a primary key id for the bin will work best, in
     * which case this method will have to look up an appropriate name for 
     * the id.
     * 
     * @param bin
     * @return
     */
    protected String getGroupName(T bin) {
        if (bin instanceof BuiltInAttribute) {
            return ((BuiltInAttribute) bin).getDescription();
        }
        return bin.toString();
    }

    /**
     * This must return a list of all bins. There should be no duplicates.
     * This information could be static if all of the bins are always known 
     * ahead of time. But more likely this information would be queried 
     * from the database (possibly with a "select distinct" type query) 
     * every time it is called.
     * @return
     */
    protected abstract List<T> getAllBins();

    @Override
    public boolean isChildDevice(DeviceGroup base, YukonDevice device) {
        if (base instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
            BinnedDeviceGroup bdg = (BinnedDeviceGroup) base;
            
            boolean inBin = isDeviceInBin(bdg.bin, device);
            
            return inBin;
        } else {
            // this must be the stored dynamic group, of which no device is a direct child
            return false;
        }
    }
    
    protected boolean isDeviceInBin(T bin, YukonDevice device) {
        Set<T> binsForDevice = getBinsForDevice(device);
        return binsForDevice.contains(bin);
    }

    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, SimpleDevice device) {
        if (base instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
            BinnedDeviceGroup bdg = (BinnedDeviceGroup) base;
            
            boolean inBin = isDeviceInBin(bdg.bin, device);
            if (inBin) {
                return Collections.singleton(base);
            } else {
                return Collections.emptySet();
            }
        } else {
            // this must be the stored dynamic group
            Set<T> bins = getBinsForDevice(device);
            
            if (bins.isEmpty()) {
                return Collections.emptySet();
            } else {
                Set<DeviceGroup> result = new HashSet<DeviceGroup>(bins.size());
                for (T bin : bins) {
                    DeviceGroup deviceGroup = createGroupForBin(base, bin);
                    result.add(deviceGroup);
                }

                return result;
            }
        }
    }
    
    @Override
    public boolean isDeviceInGroup(DeviceGroup base, YukonDevice device) {
        Set<DeviceGroup> groupMembership = getGroupMembership(base, device);
        return !groupMembership.isEmpty();
    }

    /**
     * Return the bin for a given device, or null if the
     * device is not in a bin.
     * @param device
     * @return
     */
    protected abstract Set<T> getBinsForDevice(YukonDevice device);

    protected class BinnedDeviceGroup extends MutableDeviceGroup {
        public T bin;

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isModifiable() {
            return false;
        }
        
        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public String getName(YukonUserContext context, String defaultName) {
            if (bin instanceof BuiltInAttribute) {
                return objectFormatingService.formatObjectAsString(((BuiltInAttribute)bin).getMessage(), context);
            } 
            return defaultName;
        }
    }

	@Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    
	    if (group instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
	        BinnedDeviceGroup bdg = (BinnedDeviceGroup) group;
    	    SqlFragmentSource whereClause = getChildWhereForBin(bdg.bin, identifier);
    	    return whereClause;
	    } else {
    	    // because there are no child devices under this dynamic group
    	    return new SimpleSqlFragment("0 = 1");
	    }
    }
	
	/**
	 * This method should return an SQL select statement as a SqlFragmentSource that selects 
	 * a single column that represents the PAObjectId of every device within the 
	 * given bin.
	 * 
	 * Example:
	 *   SELECT ypo.paobjectId from YukonPaobject ypo where ...
	 * 
	 * @param bin The bin under which the returned paobjectids should exist
	 * @return a valid SQL select statement
	 */
	protected abstract SqlFragmentSource getChildSqlSelectForBin(T bin);

    protected SqlFragmentSource getChildWhereForBin(T bin, String identifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier);
        sql.append(" IN (", getChildSqlSelectForBin(bin), ")"); 
        return sql;
    } 
	
	/**
	 * This method should return an SQL select statement as a String that selects
	 * a single column that represents the PAObjectId of every device within every
	 * bin. This may be every device in the system or it could be a subset.
	 * @return
	 */
	protected abstract SqlFragmentSource getAllBinnedDeviceSqlSelect();

	protected SqlFragmentSource getAllBinnedDeviceSqlWhere(String identifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier);
        sql.append(" IN (", getAllBinnedDeviceSqlSelect(), ")"); 
        return sql;
	} 
	
    @Override
	public SqlFragmentSource getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    
	    if (group instanceof BinningDeviceGroupProviderBase.BinnedDeviceGroup) {
	        return getChildDeviceGroupSqlWhereClause(group, identifier);
	    } else {
	        return getAllBinnedDeviceSqlWhere(identifier);
	    }
    }
    
    @Autowired
    public final void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public SimpleJdbcOperations getJdbcTemplate() {
        return jdbcTemplate;
    }
    
}
