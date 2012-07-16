package com.cannontech.stars.dr.hardware.model;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.google.common.collect.ImmutableMap;

/**
 * Class representing the command parameters for a command to send to an LM device.  Uses the builder pattern.
 * {@link LmHardwareCommand.Builder} constructor takes the required arguments. Optional arguments are stored
 * as {@link LmHardwareCommandParam} in the params via {@link LmHardwareCommand.Builder#withParam} 
 * and retrieved via {@link LmHardwareCommand#findParam}.
 * 
 * The {@link LmHardwareCommandParam.PRIORITY} param is automatically added to the parameters as a '7'.
 * Adding this parameter via {@link LmHardwareCommand.Builder#withParam} would then override it.
 * 
 * {@link LmHardwareCommandParam} values can only be added if they are the proper class type for that particular
 * parameter, otherwise {@link IllegalArgumentException} is thrown by {@link LmHardwareCommand.Builder#withParam}.
 */
public class LmHardwareCommand {
    
    private final LiteLmHardwareBase device;
    private final LmHardwareCommandType type;
    private final LiteYukonUser user;
    private final ImmutableMap<LmHardwareCommandParam, Object> params;
    
    public static class Builder {
        // Required
        private final LiteLmHardwareBase device;
        private final LmHardwareCommandType type;
        private final LiteYukonUser user;
        
        // Optional
        private ImmutableMap.Builder<LmHardwareCommandParam, Object> paramBuilder = new ImmutableMap.Builder<LmHardwareCommandParam, Object>();
        
        public Builder(LiteLmHardwareBase device, LmHardwareCommandType type, LiteYukonUser user) {
            this.device = device;
            this.type = type;
            this.user = user;
        }
        
        /**
         * Adds {@link LmHardwareCommandParam} value if it is the proper class type for that particular
         * parameter, otherwise {@link IllegalArgumentException} is thrown.
         * 
         * @see {@link LmHardwareCommandParam#getClazz()}
         */
        public void withParam(LmHardwareCommandParam param, Object value) {
            if (!value.getClass().isAssignableFrom(param.getClazz())) {
                String errorMsg = "Parameter " + param.name() + " is not valid for object with class: " + value.getClass().getName();
                errorMsg += ", expected: " + param.getClazz().getName();
                throw new IllegalArgumentException(errorMsg);
            }
            paramBuilder.put(param, value);
        }
        
        public LmHardwareCommand build() {
            return new LmHardwareCommand(this);
        }
    }
    
    private LmHardwareCommand(Builder b) {
        this.device = b.device;
        this.type = b.type;
        this.user = b.user;
        this.params = b.paramBuilder.build();
    }
    
    public LiteLmHardwareBase getDevice() {
        return device;
    }
    
    public LmHardwareCommandType getType() {
        return type;
    }
    
    public LiteYukonUser getUser() {
        return user;
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
    
}