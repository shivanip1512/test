package com.cannontech.database.db.point.stategroup;

public class PointStateHelper {
    public static <T extends Enum<T> & PointState> T decodeRawState(Class<T> enumClass, double rawState) {
        T[] enumConstants = enumClass.getEnumConstants();
        for (int i = 0; i < enumConstants.length; i++) {
            T state = enumConstants[i];
            if (state.getRawState() == rawState) {
                return state;
            }
        }
        throw new IllegalArgumentException(String.format("Raw state %d is not a valid state for %s",
                                                         rawState,
                                                         enumClass.getSimpleName()));
    }
}
