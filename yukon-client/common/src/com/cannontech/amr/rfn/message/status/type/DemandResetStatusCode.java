package com.cannontech.amr.rfn.message.status.type;

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
    SUCCESS,
    SCHEDULED,
    FAILED,
    NOT_SUPPORTED,
}
