package com.cannontech.multispeak.client;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Returns a preferred prefix for the given namespace URI (Prefix configured in applicationContext.xml).
 */
public class ConfigurableNamespacePrefixMapperImpl extends NamespacePrefixMapper {

    private Map<String, String> mapping;

    @Override
    public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix) {

        String prefix = null;
        if (StringUtils.isBlank(namespaceUri)) {
            prefix = StringUtils.EMPTY;
        }
        if (mapping != null)
            for (String uri : mapping.keySet()) {
                if (namespaceUri.equalsIgnoreCase(uri)) {
                    prefix = mapping.get(uri);
                    break;
                }
            }
        if (prefix == null) {
            prefix = suggestion;
        }
        return prefix;
    }

    public final void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

}