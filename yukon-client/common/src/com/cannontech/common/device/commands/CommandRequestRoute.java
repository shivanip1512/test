package com.cannontech.common.device.commands;

import org.springframework.core.style.ToStringCreator;

/**
 * Command request class for route based commands
 */
public class CommandRequestRoute extends CommandRequestBase {

    private int routeId;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("routeId", getRouteId());
        tsc.append("command", getCommandCallback());
        return tsc.toString();
    }
}
