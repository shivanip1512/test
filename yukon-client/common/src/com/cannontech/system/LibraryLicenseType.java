package com.cannontech.system;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

public enum LibraryLicenseType {
    
    APACHE_2_0("Apache 2.0"),
    APACHE_LIKE("Apache-like"),
    APACHE_LIKE_NO_ACKNOWLEDGEMENT("Apache-like with no acknowledgement clause"),
    APACHE_STYLE("Apache-style"),
    BSD("BSD"),
    BSD_2_CLAUSE("BSD 2-clause"),
    BSD_3_CLAUSE("BSD 3-clause"),
    BSD_REVISED("Revised BSD"),
    BSD_STYLE("BSD-style"),
    CC0("CC0"),
    CDDL("CDDL"),
    CDDL_1_0("CDDL 1.0"),
    CDDL_1_1("CDDL 1.1"),
    CPL_1_0("CPL 1.0"),
    EATON("Eaton-owned"),
    EPL_1_0("EPL 1.0"),
    GPL_V2_WITH_CPE("GPLv2 with classpath exception"),
    H2("H2 License (MPL 2.0 or EPL 1.0)"),
    LGPL("LGPL"),
    LGPL_2_0("LGPL 2.0"),
    LGPL_2_1("LGPL 2.1"),
    MICROSOFT("Microsoft indemnification"),
    MIT("MIT"),
    MIT_LIKE("MIT-like"),
    MPL("MPL"),
    MPL_1_0("MPL 1.0"),
    MPL_1_1("MPL 1.1"),
    MPL_2_0("MPL 2.0"),
    ORACLE_OTN("Oracle Technology Network License"),
    OSGI_2_0("OSGI Specification License 2.0"),
    PUBLIC_DOMAIN("Public domain"),
    UNKNOWN("???"),
    W3C("W3C");

    private String licenseName;
    
    private static Map<String, LibraryLicenseType> licenseLookup;
    
    static {
        licenseLookup = Maps.uniqueIndex(Arrays.asList(values()), LibraryLicenseType::getLicenseName);
    }
    
    @JsonCreator
    public static LibraryLicenseType getByLicenseName(String licenseName) {
        return Optional.ofNullable(licenseLookup.getOrDefault(licenseName, null))
                .orElseThrow(() -> new IllegalArgumentException("Unknown license type: " + licenseName));
    }

    private LibraryLicenseType(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseName() {
        return licenseName;
    }
}
