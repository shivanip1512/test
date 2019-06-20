package com.cannontech.amr.rfn.message.status.type;

import java.util.Arrays;

/**
 * The RF node records a status mark for demand reset. The status value may be one of the following:
 * <ul>
 * <li>0 -> {@link #SUCCESS} - Demand Reset completed successfully</li>
 * <li>1 -> {@link #SCHEDULED} - Demand Reset was scheduled as part of a Read and Reset command</li>
 * <li>2 -> {@link #FAILED} - Demand Reset failed</li>
 * <li>3 -> {@link #NOT_SUPPORTED} - Demand Reset is not supported</li>
 * </li>
 * </ul>
 * Demand reset code 0 ({@link #SCHEDULED}) is not applicable for demand resets initiated by Yukon.
 */
public enum DemandResetStatusCode {
    SUCCESS(0),
    SCHEDULED(1),
    FAILED(2),
    NOT_SUPPORTED(3),
    ;
    private int demandResetStatusCodeID;

    private DemandResetStatusCode(int demandResetStatusCodeID) {
        this.demandResetStatusCodeID = demandResetStatusCodeID;
    }

    public int getDemandResetStatusCodeID() {
        return demandResetStatusCodeID;
    }

    /**
     * @param demandResetStatusCodeID
     * @return The {@link DemandResetStatusCode} that corresponds with the given
     *         demandResetStatusCodeID.
     * 
     * @throws IllegalArgumentException if no {@link DemandResetStatusCode}
     *             can be found for the given demandResetStatusCodeID.
     */
    public static DemandResetStatusCode valueOf(int demandResetStatusCodeID) {
        return Arrays.stream(DemandResetStatusCode.values())
            .filter(d -> d.getDemandResetStatusCodeID() == demandResetStatusCodeID).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No enum constant defined for demandResetStatusCodeID "
                + demandResetStatusCodeID));
    }
}
