package com.cannontech.web.tools.commander.model;

public class CommandParams {

    private CommandType type;
    private Integer paoId;
    private Integer routeId;
    private String serialNumber;
    private String command;
    
    public CommandType getType() {
        return type;
    }
    
    public void setType(CommandType type) {
        this.type = type;
    }
    
    public Integer getPaoId() {
        return paoId;
    }
    
    public void setPaoId(Integer paoId) {
        this.paoId = paoId;
    }
    
    public Integer getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public CommandParams copy() {
        CommandParams params = new CommandParams();
        params.setCommand(command);
        params.setPaoId(paoId);
        params.setRouteId(routeId);
        params.setSerialNumber(serialNumber);
        params.setType(type);
        
        return params;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((command == null) ? 0 : command.hashCode());
        result = prime * result + ((paoId == null) ? 0 : paoId.hashCode());
        result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
        result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CommandParams other = (CommandParams) obj;
        if (command == null) {
            if (other.command != null)
                return false;
        } else if (!command.equals(other.command))
            return false;
        if (paoId == null) {
            if (other.paoId != null)
                return false;
        } else if (!paoId.equals(other.paoId))
            return false;
        if (routeId == null) {
            if (other.routeId != null)
                return false;
        } else if (!routeId.equals(other.routeId))
            return false;
        if (serialNumber == null) {
            if (other.serialNumber != null)
                return false;
        } else if (!serialNumber.equals(other.serialNumber))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("CommandParams [type=%s, paoId=%s, routeId=%s, serialNumber=%s, command=%s]", type, paoId, routeId, serialNumber, command);
    }
    
}