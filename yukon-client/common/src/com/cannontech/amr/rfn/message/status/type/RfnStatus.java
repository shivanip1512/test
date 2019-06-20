package com.cannontech.amr.rfn.message.status.type;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;

public interface RfnStatus<StatusDataType extends Serializable> extends Serializable {
    public RfnIdentifier getRfnIdentifier();

    public long getTimeStamp();

    public StatusDataType getData();
}
