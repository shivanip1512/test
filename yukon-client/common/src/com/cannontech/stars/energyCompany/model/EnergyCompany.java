package com.cannontech.stars.energyCompany.model;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * An immutable class representing an energy company.  This class can only be built using the builder and all
 * energy companies must be created at the same time.
 */
public final class EnergyCompany implements YukonEnergyCompany {
    private final int ecId;
    private final String name;
    private final LiteYukonUser user;
    private final int contactId;

    private EnergyCompany parent;
    private List<EnergyCompany> children;

    private EnergyCompany(int ecId, String name, LiteYukonUser user, int contactId) {
        this.ecId = ecId;
        this.name = name;
        this.user = user;
        this.contactId = contactId;

        // Create a temporary mutable list for children.  The builder will make this immutable before making it
        // available outside this class.
        children = new ArrayList<>();
    }

    /**
     * A builder class to create a hierarchy of EnergyCompany instances.  This class is the ONLY way to create
     * energy companies and they should all be created at the same time.  (This should of course be only done in
     * a single DAO that caches them.)
     */
    public static class Builder {
        private final Map<Integer, EnergyCompany> temporaryMutableMap = new HashMap<>();
        private final Map<Integer, Integer> parentIdsById = new HashMap<>();

        private boolean built = false;

        public void addEnergyCompany(int ecId, String name, LiteYukonUser user, int contactId, Integer parentEcId) {
            checkState(!built, "Cannot add more energy companies.");
            temporaryMutableMap.put(ecId, new EnergyCompany(ecId, name, user, contactId));
            if (parentEcId != null) {
                parentIdsById.put(ecId, parentEcId);
            }
        }

        public Map<Integer, EnergyCompany> build() {
            checkState(!built, "Cannot add build more than once.");
            built = true;

            // Build parent/child relationships.
            for (Map.Entry<Integer, Integer> entry : parentIdsById.entrySet()) {
                EnergyCompany ec = temporaryMutableMap.get(entry.getKey());
                EnergyCompany parent = temporaryMutableMap.get(entry.getValue());
                ec.parent = parent;
                parent.children.add(ec);
            }

            // Make the children lists immutable.
            for (EnergyCompany ec : temporaryMutableMap.values()) {
                ec.children = ImmutableList.copyOf(ec.children);
            }

            return ImmutableMap.copyOf(temporaryMutableMap);
        }
    }

    @Override
    @Deprecated
    public int getEnergyCompanyId() {
        return ecId;
    }
    
    public int getId() {
        return ecId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @Deprecated
    public LiteYukonUser getEnergyCompanyUser() {
        return user;
    }

    public LiteYukonUser getUser() {
        return user;
    }

    public int getContactId() {
        return contactId;
    }

    public EnergyCompany getParent() {
        return parent;
    }

    public List<EnergyCompany> getChildren() {
        return children;
    }

    /**
     * Get all descendants of this energy company (children, children of children, etc.).
     */
    public List<EnergyCompany> getDescendants(boolean addSelf) {
        ImmutableList.Builder<EnergyCompany> builder = ImmutableList.builder();
        for (EnergyCompany child : children) {
            builder.add(child);
            builder.addAll(child.getChildren());
        }
        if (addSelf) {
            builder.add(this);
        }
        return builder.build();
    }
}
