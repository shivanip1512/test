package com.eaton.builders.drsetup.loadgroup;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

public class LoadGroupCreateService {

    public static Pair<JSONObject, JSONObject> buildAndCreateVirtualItronLoadGroup() {
        return new LoadGroupItronCreateBuilder.Builder(Optional.empty())
                .withRelay(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withDisableControl(Optional.empty())
                .create();
    }
    
}
