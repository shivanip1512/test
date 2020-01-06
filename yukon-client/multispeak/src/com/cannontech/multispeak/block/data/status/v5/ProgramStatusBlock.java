package com.cannontech.multispeak.block.data.status.v5;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.multispeak.block.syntax.v5.SyntaxItem;
import com.cannontech.multispeak.block.v5.Block;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;

public class ProgramStatusBlock implements Block {

    public Integer correlationId;
    public String programName;
    public String gearName;
    public String currentStatus;
    public Date startDateTime;
    public Date stopDateTime;
    public Date gearChangeTime;

    public ProgramStatusBlock(Integer correlationId, String programName, String gearName, String currentStatus, Date startDateTime, Date stopDateTime, Date gearChangeTime) {
        super();
        this.correlationId = correlationId;
        this.programName = programName;
        this.gearName = gearName;
        this.currentStatus = currentStatus;
        this.startDateTime = startDateTime;
        this.stopDateTime = stopDateTime;
        this.gearChangeTime = gearChangeTime;

    }

    @Override
    public String getField(SyntaxItem syntaxItem) {
        if (syntaxItem.equals(SyntaxItem.PROGRAM_NAME)) {
            return programName;
        } else if (syntaxItem.equals(SyntaxItem.CURRENT_STATUS)) {
            return currentStatus;
        } else if (syntaxItem.equals(SyntaxItem.START_DATETIME)) {
            if (startDateTime != null) {
                return Iso8601DateUtil.formatIso8601Date(startDateTime);
            };
        } else if (syntaxItem.equals(SyntaxItem.STOP_DATETIME)) {
            if (stopDateTime != null) {
                return Iso8601DateUtil.formatIso8601Date(stopDateTime);
            };
        } else if (syntaxItem.equals(SyntaxItem.GEAR_CHANGETIME)) {
            if (gearChangeTime != null) {
                return Iso8601DateUtil.formatIso8601Date(gearChangeTime);
            };
        } else if (syntaxItem.equals(SyntaxItem.GEAR_NAME)) {
            return gearName;
        } else if (syntaxItem.equals(SyntaxItem.CORRELATION_ID)) {
            return String.valueOf(correlationId);
        } else {
            CTILogger.error("SyntaxItem: " + syntaxItem + " - Not Valid for ProgramStatusBlock");
        }
        return "";
    }

    // For DR Request, Not setting formattedBlockTemplateID so provide only default objectId
    @Override
    public String getObjectId() {
        return MultispeakFuncs.DEFAULT_OBJECT_GUID;
    }

    // Not using this for DR functionality
    @Override
    public boolean hasData() {
        return true;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setStopDateTime(Date stopDateTime) {
        this.stopDateTime = stopDateTime;
    }

    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    public void setGearName(Integer correlationId) {
        this.correlationId = correlationId;
    }
    
    public void setGearChangeTime(Date gearChangeTime) {
        this.gearChangeTime = gearChangeTime;
    }

}
