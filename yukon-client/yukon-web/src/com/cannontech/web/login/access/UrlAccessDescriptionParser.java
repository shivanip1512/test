package com.cannontech.web.login.access;

import java.util.List;

interface UrlAccessDescriptionParser {

    public UrlAccess parse(final String description);
    
    public List<UrlAccess> parse(final String[] descriptions);
    
}
