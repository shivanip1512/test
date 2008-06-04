package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.common.bulk.mapper.ObjectMappingException;

public class UnmodifiableListCopyMapper<J> implements ObjectMapper<List<J>, List<J>> {

    @Override
    public List<J> map(List<J> from) throws ObjectMappingException {
        return Collections.unmodifiableList(new ArrayList<J>(from));
    }


}
