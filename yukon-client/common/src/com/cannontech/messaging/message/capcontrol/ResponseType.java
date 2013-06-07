package com.cannontech.messaging.message.capcontrol;

public enum ResponseType {

    SUCCESS(0), COMMAND_REFUSED(1), TIMEOUT(2);

    private int typeId;

    private ResponseType(int id) {
        typeId = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public static ResponseType getResponseTypeById(int id) throws IllegalArgumentException {

        for (ResponseType type : ResponseType.values()) {
            if (type.getTypeId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException(" Unsupported ResponseType: " + id);
    }
}
