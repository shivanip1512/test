package com.cannontech.common.dynamicBilling.model;

import java.math.RoundingMode;

import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;

/**
 * Model class which represents a billing field for a dynamic format
 */
public class DynamicBillingField {

    private int id;
    private int formatId;
    private String name = null;
    private int order;
    private String format = null;
    private int maxLength = 0;
    private String padChar = " ";
    private String padSide = "none";
    private ReadingType readingType = ReadingType.ELECTRIC;
    private Channel channel = Channel.ONE;
    private RoundingMode roundingMode = null;	//RoundingMode.HALF_EVEN;

    public String getFormat() {
        return format;
    }

    public void setFormat(String fieldFormat) {
        this.format = fieldFormat;
    }

    public String getName() {
        return name;
    }

    public void setName(String fieldName) {
        this.name = fieldName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int fieldOrder) {
        this.order = fieldOrder;
    }

    public int getFormatId() {
        return formatId;
    }

    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getPadChar() {
        return padChar;
    }

    public void setPadChar(String padChar) {
        this.padChar = padChar;
    }

    public String toString() {
        return name + ", " + order + ", " + format;
    }

    public String getPadSide() {
        return padSide;
    }

    public void setPadSide(String padSide) {
        this.padSide = padSide;
    }

    public ReadingType getReadingType() {
        return readingType;
    }

    public void setReadingType(ReadingType readingType) {
        this.readingType = readingType;
    }

    public Channel getChannel() {
		return channel;
	}
    
    public void setChannel(Channel channel) {
		this.channel = channel;
	}

    public RoundingMode getRoundingMode() {
		return roundingMode;
	}
    
    public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}
}
