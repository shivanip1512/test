package com.cannontech.stars.dr.hardware.model;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableMap;
/** 
 * Class representing the command and parameters for a load management command to
 * be sent to the entire network via broadcast messaging.
 */
public class LmCommand {
    
    protected LmHardwareCommandType type;
    protected LiteYukonUser user;
    protected Map<LmHardwareCommandParam, Object> params = new HashMap<>();
    
    public void setType(LmHardwareCommandType type) {
        this.type = type;
    }

    public void setUser(LiteYukonUser user) {
        this.user = user;
    }

    public void setParams(Map<LmHardwareCommandParam, Object> params) {
        this.params = params;
    }

    public LmHardwareCommandType getType() {
        return type;
    }
    
    public LiteYukonUser getUser() {
        return user;
    }
    
    public Map<LmHardwareCommandParam, Object> getParams() {
        return params;
    }
    
    /**
     * Returns the optional parameter casted as the provided class or null if it does not exist.
     * This method should be provided the proper class for the provided {@link LmHardwareCommandParam}.
     * 
     * If provide the proper class this will always work since parameters are not allowed to be added
     * as an improper object type via {@link LmHardwareCommand.Builder#withParam}.
     * 
     * @see {@link LmHardwareCommandParam#getClazz()}
     */
    public <T extends Object> T findParam(LmHardwareCommandParam param, Class<T> returnAs) {
        
        Object o = params.get(param);
        if (o != null) {
            return returnAs.cast(o);
        }
        
        return null;
    }
    
    /**
     * Returns a copy of the options immutable map.
     */
    public ImmutableMap<LmHardwareCommandParam, Object> optionsCopy() {
        return ImmutableMap.copyOf(params);
    }
    
    @Override
    public String toString() {
        return String.format("LmHardwareCommand [type=%s, user=%s, params=%s]", type, user, params);
    }
    
}