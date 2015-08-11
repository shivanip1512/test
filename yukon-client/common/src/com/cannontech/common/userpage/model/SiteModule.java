package com.cannontech.common.userpage.model;

import java.util.Map;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * An enum of modules (from module_config.xml) that are used in various places in code.  Not every module needs to be
 * here...just ones with searchable pages and ones that are in the site map.
 */
public enum SiteModule implements DatabaseRepresentationSource {
    
    AMI("amr", SiteMapCategory.AMI),
    DEV("dev", SiteMapCategory.DEVELOPMENT),
    DR("dr", SiteMapCategory.DR),
    CAPCONTROL("capcontrol", SiteMapCategory.VV),
    COMMON("common", SiteMapCategory.COMMON),
    OPERATOR("operator", SiteMapCategory.ASSETS),
    TOOLS("tools", SiteMapCategory.TOOLS),
    ADMIN("adminSetup", SiteMapCategory.ADMIN),
    SUPPORT("support", SiteMapCategory.SUPPORT),
    USER("user", SiteMapCategory.ADMIN),
    CCURT("commercialcurtailment", SiteMapCategory.CCURT),
    CCURT_USER("commercialcurtailment_user", SiteMapCategory.CCURT),
    ;

    private final String name;
    private final SiteMapCategory siteMapCategory;

    SiteModule(String name, SiteMapCategory siteMapCategory) {
        this.name = name;
        this.siteMapCategory = siteMapCategory;
    }

    private final static Map<String, SiteModule> byName;

    static {
        Builder<String, SiteModule> builder = ImmutableMap.builder();
        for (SiteModule siteModule : values()) {
            builder.put(siteModule.name, siteModule);
        }
        byName = builder.build();
    }

    public static SiteModule getByName(String name) {
        return byName.get(name);
    }

    public String getName() {
        return name;
    }

    public SiteMapCategory getSiteMapCategory() {
        return siteMapCategory;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return name;
    }
}
