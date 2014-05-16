package com.cannontech.database.db.point.stategroup;

import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum Disconnect410State implements PointState {
    CONFIRMED_DISCONNECTED(0),
    CONNECTED(1),
    UNCONFIRMED_DISCONNECTED(2),
    CONNECT_ARMED(3), 
    ;

    private final static ImmutableMap<Integer, Disconnect410State> byRawState;
    static {
        Function<Disconnect410State, Integer> keyFunction =
            new Function<Disconnect410State, Integer>() {
                @Override
                public Integer apply(Disconnect410State state) {
                    return state.rawState;
                }
            };
        byRawState = Maps.uniqueIndex(Arrays.asList(values()), keyFunction);
    }

    public static Disconnect410State getByRawState(int rawState) {
        return byRawState.get(rawState);
    }

    private final int rawState;

    private Disconnect410State(int rawState) {
        this.rawState = rawState;
    }

    @Override
    public int getRawState() {
        return rawState;
    }
}
