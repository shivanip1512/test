package com.cannontech.yc;

import java.awt.Color;

public enum MessageType{ INFO(Color.BLACK), 
						 ERROR(Color.RED),
						 SUCCESS(Color.BLUE),
						 FRIEND(Color.GREEN);

	private Color color;
	
	private MessageType(Color color) {
	this.color = color;
	}
	
	public Color getColor() {
	return color;
	}
}