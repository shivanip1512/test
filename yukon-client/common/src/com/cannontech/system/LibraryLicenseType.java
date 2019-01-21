package com.cannontech.system;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

public enum LibraryLicenseType {
    
    APACHE_2_0("Apache 2.0", Attribution.REQUIRED),
    APACHE_LIKE("Apache-like", Attribution.REQUIRED),
    APACHE_LIKE_NO_ACKNOWLEDGEMENT("Apache-like with no acknowledgement clause", Attribution.NONE),
    APACHE_STYLE("Apache-style", Attribution.REQUIRED),
    BOOST("Boost", Attribution.NONE),
    BSD("BSD", Attribution.REQUIRED),
    BSD_2_CLAUSE("BSD 2-clause", Attribution.REQUIRED),
    BSD_3_CLAUSE("BSD 3-clause", Attribution.REQUIRED),
    BSD_REVISED("Revised BSD", Attribution.REQUIRED),
    BSD_STYLE("BSD-style", Attribution.REQUIRED),
    CC0("CC0", Attribution.NONE),
    CCA_2_5("Creative Commons Attribution 2.5", Attribution.REQUIRED),
    CCA_3_0("Creative Commons Attribution 3.0", Attribution.REQUIRED),
    CDDL("CDDL", Attribution.REQUIRED),
    CDDL_1_0("CDDL 1.0", Attribution.REQUIRED),
    CDDL_1_1("CDDL 1.1", Attribution.REQUIRED),
    CPL_1_0("CPL 1.0", Attribution.REQUIRED),
    EATON("Eaton-owned", Attribution.NONE),
    EPL_1_0("EPL 1.0", Attribution.REQUIRED),
    GPL("GPL", Attribution.REQUIRED),
    GPL_V2("GPL 2.0", Attribution.REQUIRED),
    GPL_V2_WITH_CPE("GPLv2 with classpath exception", Attribution.REQUIRED),
    H2("H2 License (MPL 2.0 or EPL 1.0)", Attribution.REQUIRED),
    LGPL("LGPL", Attribution.REQUIRED),
    LGPL_2_0("LGPL 2.0", Attribution.REQUIRED),
    LGPL_2_1("LGPL 2.1", Attribution.REQUIRED),
    MICROSOFT("Microsoft indemnification", Attribution.NONE),
    MIT("MIT", Attribution.REQUIRED),
    MIT_LIKE("MIT-like", Attribution.REQUIRED),
    MPL("MPL", Attribution.REQUIRED),
    MPL_1_0("MPL 1.0", Attribution.REQUIRED),
    MPL_1_1("MPL 1.1", Attribution.REQUIRED),
    MPL_2_0("MPL 2.0", Attribution.REQUIRED),
    ORACLE_OTN("Oracle Technology Network License", Attribution.NONE),
    OSGI_2_0("OSGI Specification License 2.0", Attribution.REQUIRED),
    PUBLIC_DOMAIN("Public domain", Attribution.NONE),
    UNKNOWN("???", Attribution.NONE),
    W3C("W3C", Attribution.REQUIRED);

    enum Attribution {
        REQUIRED,
        NONE
    };
    
    private String licenseName;
    private Attribution attribution;
    
    private static Map<String, LibraryLicenseType> licenseLookup;
    
    static {
        licenseLookup = Maps.uniqueIndex(Arrays.asList(values()), LibraryLicenseType::getLicenseName);
    }
    
    @JsonCreator
    public static LibraryLicenseType getByLicenseName(String licenseName) {
        return Optional.ofNullable(licenseLookup.getOrDefault(licenseName, null))
                .orElseThrow(() -> new IllegalArgumentException("Unknown license type: " + licenseName));
    }

    private LibraryLicenseType(String licenseName, Attribution attribution) {
        this.licenseName = licenseName;
        this.attribution = attribution;
    }

    public String getLicenseName() {
        return licenseName;
    }
    
    public boolean isAttributionRequired() {
        return attribution == Attribution.REQUIRED;
    }
}
