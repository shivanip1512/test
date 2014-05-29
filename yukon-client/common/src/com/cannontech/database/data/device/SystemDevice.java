package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

public class SystemDevice extends DeviceBase {

    public SystemDevice() {
        super(PaoType.SYSTEM);
    }

    @Override
    public void add() {
        throw new IllegalArgumentException("Instances of " + this.getClass().getName() + " can not be ADDED to the database");
    }

    @Override
    public void addPartial() {
        throw new IllegalArgumentException("Instances of " + this.getClass().getName() + " can not be PARTIALLY_ADDED to the database");
    }

    @Override
    public void delete() {
        throw new IllegalArgumentException("Instances of " + this.getClass().getName() + " can not be DELETE from the database");
    }

    @Override
    public void deletePartial() {
        throw new IllegalArgumentException("Instances of " + this.getClass().getName() + " can not be PARTIALLY_DELETED from the database");
    }
    @Override
    public void update() {
        throw new IllegalArgumentException("Instances of " + this.getClass().getName() + " can not be UPDATED in the database");
    }
}