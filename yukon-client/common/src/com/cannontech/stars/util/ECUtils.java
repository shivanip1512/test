package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;

public class ECUtils {

    public static final int RIGHT_SHOW_ADDTL_PROTOCOLS = 0x20000000;

    /**
     * Get all the energy companies that belongs to (directly or indirectly)
     * this energy company, including itself
     */
    public static List<LiteStarsEnergyCompany> getAllDescendants(final LiteStarsEnergyCompany parent) {
        List<LiteStarsEnergyCompany> descendants = new ArrayList<>();
        descendants.add(parent);

        for (final LiteStarsEnergyCompany child : parent.getChildren()) {
            descendants.addAll(getAllDescendants(child));
        }

        return descendants;
    }

    /**
     * Get all the energy companies that the energy company
     * belongs to (directly or indirectly), including itself
     *
     * This method returns a list of energy companies starting with the child energy company and ending
     * with the energy company right below the default energy company.
     *
     * @param childEC
     * @return
     */
    public static List<LiteStarsEnergyCompany> getAllAscendants(LiteStarsEnergyCompany childEC) {
        List<LiteStarsEnergyCompany> ascendants = new ArrayList<>();
        ascendants.add(childEC);

        while (childEC.getParent() != null) {
            ascendants.add(childEC.getParent());
            childEC = childEC.getParent();
        }

        return ascendants;
    }

    public static boolean isDefaultEnergyCompany(LiteStarsEnergyCompany company) {
        return (company == null) ? false : company.getLiteID() == StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID;
    }
}
