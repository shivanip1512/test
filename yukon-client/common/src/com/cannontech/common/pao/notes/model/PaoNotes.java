package com.cannontech.common.pao.notes.model;

import java.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoNotes {
    
    private Integer noteId;
    private Integer paObjectId;
    private String noteText;
    private Character status;
    private LiteYukonUser CreatorUserName; 
    private Instant CreationDate;
    private LiteYukonUser EditorUserName;
    private Instant EditDate;
    
    public Integer getNoteId() {
        return noteId;
    }
    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }
    public Integer getPaObjectId() {
        return paObjectId;
    }
    public void setPaObjectId(Integer paObjectId) {
        this.paObjectId = paObjectId;
    }
    public String getNoteText() {
        return noteText;
    }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
    public Character getStatus() {
        return status;
    }
    public void setStatus(Character status) {
        this.status = status;
    }
    public LiteYukonUser getCreatorUserName() {
        return CreatorUserName;
    }
    public void setCreatorUserName(LiteYukonUser creatorUserName) {
        CreatorUserName = creatorUserName;
    }
    public Instant getCreationDate() {
        return CreationDate;
    }
    public void setCreationDate(Instant creationDate) {
        CreationDate = creationDate;
    }
    public LiteYukonUser getEditorUserName() {
        return EditorUserName;
    }
    public void setEditorUserName(LiteYukonUser editorUserName) {
        EditorUserName = editorUserName;
    }
    public Instant getEditDate() {
        return EditDate;
    }
    public void setEditDate(Instant editDate) {
        EditDate = editDate;
    }
    
    
}
