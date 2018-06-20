package com.cannontech.common.pao.notes.model;

import org.joda.time.Instant;

import com.cannontech.common.pao.notes.PaoNoteStatus;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class PaoNote {
    
    private int noteId;
    private int paoId;
    private String noteText;
    private PaoNoteStatus status;
    private String createUserName; 
    private Instant createDate;
    private String editUserName;
    private Instant editDate;
    private LiteYukonPAObject liteYukonPAObject;
    
    public int getNoteId() {
        return noteId;
    }
    
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
    
    public int getPaoId() {
        return paoId;
    }
    
    public void setPaoId(int paObjectId) {
        this.paoId = paObjectId;
    }
    
    public String getNoteText() {
        return noteText;
    }
    
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
    
    public PaoNoteStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaoNoteStatus status) {
        this.status = status;
    }
    
    public String getCreateUserName() {
        return createUserName;
    }
    
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
    
    public Instant getCreateDate() {
        return createDate;
    }
    
    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }
    
    public String getEditUserName() {
        return editUserName;
    }
    
    public void setEditUserName(String editUserName) {
        this.editUserName = editUserName;
    }
    
    public Instant getEditDate() {
        return editDate;
    }
    
    public void setEditDate(Instant editDate) {
        this.editDate = editDate;
    }

    public LiteYukonPAObject getLiteYukonPAObject() {
        return liteYukonPAObject;
    }

    public void setLiteYukonPAObject(LiteYukonPAObject liteYukonPAObject) {
        this.liteYukonPAObject = liteYukonPAObject;
    }
    
}
