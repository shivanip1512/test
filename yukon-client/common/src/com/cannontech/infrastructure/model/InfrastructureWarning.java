package com.cannontech.infrastructure.model;

import java.util.Arrays;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Represents a single infrastructure warning for a particular device.
 */
public class InfrastructureWarning implements Displayable {
    private final PaoIdentifier paoIdentifier;
    private final InfrastructureWarningType warningType;
    private final Object[] arguments;
    private final InfrastructureWarningSeverity severity;
    
    /**
     * Create a new InfrastructureWarning with the default severity.
     */
    public InfrastructureWarning(PaoIdentifier paoIdentifier, 
                                 InfrastructureWarningType warningType, 
                                 Object... arguments) {
        this(paoIdentifier, warningType, InfrastructureWarningSeverity.LOW, arguments);
    }
    
    public InfrastructureWarning(PaoIdentifier paoIdentifier, 
                                 InfrastructureWarningType warningType, 
                                 InfrastructureWarningSeverity severity,
                                 Object... arguments) {
        this.paoIdentifier = paoIdentifier;
        this.warningType = warningType;
        this.arguments = arguments;
        this.severity = severity;
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

    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable(warningType.getFormatKey() + "." + severity, arguments);
    }

    @Override
    public String toString() {
        return "InfrastructureWarning [paoIdentifier=" + paoIdentifier + ", warningType=" + warningType + ", arguments="
               + Arrays.toString(arguments) + ", severity=" + severity + "]";
    }
}
