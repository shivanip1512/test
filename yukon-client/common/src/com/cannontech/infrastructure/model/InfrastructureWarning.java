package com.cannontech.infrastructure.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Sets;

/**
 * Represents a single infrastructure warning for a particular device.
 */
public class InfrastructureWarning implements Displayable, Serializable {
    private static final long serialVersionUID = 1L;
    private final PaoIdentifier paoIdentifier;
    private final InfrastructureWarningType warningType;
    private final Object[] arguments;
    private final InfrastructureWarningSeverity severity;
    private final Instant timestamp;
    
    /**
     * Create a new InfrastructureWarning with the default severity.
     */
    public InfrastructureWarning(PaoIdentifier paoIdentifier, 
                                 InfrastructureWarningType warningType) {
        this(paoIdentifier, warningType, InfrastructureWarningSeverity.LOW, Instant.now(), new Object[0]);
    }
    
    /**
     * Create a new InfrastructureWarning with the default severity.
     */
    public InfrastructureWarning(PaoIdentifier paoIdentifier, 
                                 InfrastructureWarningType warningType,
                                 Object... arguments) {
        this(paoIdentifier, warningType, InfrastructureWarningSeverity.LOW, Instant.now(), arguments);
    }
    
    /**
     * Create a new InfrastructureWarning with the specified severity.
     */
    public InfrastructureWarning(PaoIdentifier paoIdentifier, 
                                 InfrastructureWarningType warningType, 
                                 InfrastructureWarningSeverity severity,
                                 Instant timestamp,
                                 Object... arguments) {
        this.paoIdentifier = paoIdentifier;
        this.warningType = warningType;
        this.arguments = arguments;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public InfrastructureWarningType getWarningType() {
        return warningType;
    }

    public Object[] getArguments() {
        return arguments;
    }
    
    public InfrastructureWarningSeverity getSeverity() {
        return severity;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable(warningType.getFormatKey() + "." + severity.name(), arguments);
    }

    @Override
    public String toString() {
        return "InfrastructureWarning [paoIdentifier=" + paoIdentifier + ", warningType=" + warningType + ", arguments="
               + Arrays.toString(arguments) + ", severity=" + severity + ", timestamp=" + timestamp + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result + ((severity == null) ? 0 : severity.hashCode());
        result = prime * result + ((warningType == null) ? 0 : warningType.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        InfrastructureWarning other = (InfrastructureWarning) obj;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null) {
                return false;
            }
        } else if (!paoIdentifier.equals(other.paoIdentifier)) {
            return false;
        }
        if (severity != other.severity) {
            return false;
        }
        if (warningType != other.warningType) {
            return false;
        }
        // Not comparing timestamp here because we use this equals method to check if a new warning
        // is a duplicate of an old warning.
         
        return true;
    }

    public boolean equalsWithArgs(Object obj) {
        return equals(obj) && Sets.newHashSet(((InfrastructureWarning) obj).getArguments())
                                                                           .stream()
                                                                           .map(Object::toString)
                                                                           .collect(Collectors.toSet())
                                                                           .equals(Sets.newHashSet((this.getArguments())));
    }

}
