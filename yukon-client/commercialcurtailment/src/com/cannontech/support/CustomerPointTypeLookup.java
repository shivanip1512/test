package com.cannontech.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;

import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.roles.yukon.EnergyCompanyRole;

public class CustomerPointTypeLookup {
    /**
     * This holds the map of keys to list of point types. In this
     * context, the "key" is the string value that is stored under
     * the EnergyCompanyRole.APPLICABLE_POINT_TYPE_KEY property and
     * the list of point types are the list of strings that correspond
     * to the TYPE column of the CICUSTOMERPOINTDATA table.
     * 
     * The typical usage scenario is that these values are injected
     * by the Spring framework, so look for an applicationContext.xml
     * to locate the actual values.
     * 
     * @param applicablePointLookup
     */
    private Map<String, Set<String>> applicablePointLookup;

    public CustomerPointTypeLookup() {
        super();
    }
    
    protected Set<String> getApplicablePoints(String key) {
        Validate.notEmpty(key, "Key must not be null");
        if (!applicablePointLookup.containsKey(key)) {
            throw new IllegalArgumentException("Applicable Point Type key '" +
                    key + "' has not been configured." );
        }
        return applicablePointLookup.get(key);
    }
    
    /**
     * For a given Energy Company, return the applicable point types
     * that SHOULD be set for all commercial customers.
     * @param energyCompany
     * @return List of applicable point types
     */
    public Set<String> getApplicablePoints(LiteEnergyCompany energyCompany) {
        Set<String> keys = getPointTypeGroups(energyCompany);
        if (keys.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> result = new TreeSet<String>();
        for (String key : keys) {
            result.addAll(getApplicablePoints(key));
        }
        return result;
    }

    public Set<String> getPointTypeGroups(LiteEnergyCompany energyCompany) {
        String property = 
            EnergyCompanyFuncs.getEnergyCompanyProperty(energyCompany, 
                                                        EnergyCompanyRole.APPLICABLE_POINT_TYPE_KEY);
        
        if (property == null) {
            return Collections.emptySet();
        }
        property = property.trim();
        List<String> keys = Arrays.asList(property.split("\\s*,\\s*"));
        
        return new TreeSet<String>(keys);
    }
    
    /**
     * Convenience method that determines the customer's Energy Company and then
     * calls getPointTypeGroups(LiteEnergyCompany).
     * @param liteCICustomer
     * @return
     */
    public Set<String> getPointTypeGroups(LiteCICustomer liteCICustomer) {
        int energyCompanyID = liteCICustomer.getEnergyCompanyID();
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(energyCompanyID);
        return getPointTypeGroups(energyCompany);
    }

    /**
     * Conveneince method that determines the customer's Energy Company and then
     * calls getApplicablePoints(LiteEnergyCompany).
     * @param liteCICustomer
     * @return List of applicable point types
     */
    public Set<String> getApplicablePoints(LiteCICustomer liteCICustomer) {
        int energyCompanyID = liteCICustomer.getEnergyCompanyID();
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(energyCompanyID);
        return getApplicablePoints(energyCompany);
    }

    
    public void setApplicablePointLookup(Map<String, Class<Enum<?>>> applicablePointEnums) {
        applicablePointLookup = new TreeMap<String, Set<String>>();
        for (String key : applicablePointEnums.keySet()) {
            Class<Enum<?>> enumClass = applicablePointEnums.get(key);
            Set<String> enumSet = new TreeSet<String>();
            Enum<?>[] enumConstants = enumClass.getEnumConstants();
            for (Enum<?> e : enumConstants) {
                enumSet.add(e.name());
            }
            applicablePointLookup.put(key, enumSet);
        }
    }

    
    
}
