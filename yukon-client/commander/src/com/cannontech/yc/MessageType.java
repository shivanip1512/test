package com.cannontech.yc;

import java.awt.Color;

public enum MessageType{ INFO(Color.BLACK, "Black"), 
						 ERROR(Color.RED, "Red"),
						 SUCCESS(Color.BLUE, "Blue"),
						 INHIBITED(Color.GRAY, "Gray");

	private Color color;
	private String styleString;
	
	private MessageType(Color color, String styleString) {
	    this.color = color;
	    this.styleString = styleString;
	}
	
	public Color getColor() {
	    return color;
	}

	public String getStyleString() {
        return styleString;
    }
		   
    public static MessageType getMessageType(int status) {
        if (status == 83 || status == 85) {
            return MessageType.INHIBITED;
        } else if (status > 1 ){   //0=success, 1="Not Normal" return, but not necessarily an error
            return MessageType.ERROR;
        } else {
            return MessageType.SUCCESS;
        }
    }
}