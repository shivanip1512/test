package com.cannontech.web.updater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UpdateIdentifier {
    private final static Pattern idSplitter = Pattern.compile("^([^/]+)/(.+)$");

    private DataType type;
    private String remainder;
    private String fullIdentifier;

    public UpdateIdentifier(String fullIdentifier) {
        Matcher m = idSplitter.matcher(fullIdentifier);
        if (!m.matches() || m.groupCount() != 2) {
            throw new RuntimeException("Identifier string isn't well formed: " + fullIdentifier);
        }
        String typeStr = m.group(1);
        this.remainder = m.group(2);
        this.type = DataType.valueOf(typeStr);
        this.fullIdentifier = fullIdentifier;
    }

    public DataType getType() {
        return type;
    }

    public String getRemainder() {
        return remainder;
    }
    
    public String getFullIdentifier() {
        return fullIdentifier;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullIdentifier == null) ? 0 : fullIdentifier.hashCode());
        result = prime * result + ((remainder == null) ? 0 : remainder.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UpdateIdentifier other = (UpdateIdentifier) obj;
        if (fullIdentifier == null) {
            if (other.fullIdentifier != null)
                return false;
        } else if (!fullIdentifier.equals(other.fullIdentifier))
            return false;
        if (remainder == null) {
            if (other.remainder != null)
                return false;
        } else if (!remainder.equals(other.remainder))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return fullIdentifier;
    }
}
