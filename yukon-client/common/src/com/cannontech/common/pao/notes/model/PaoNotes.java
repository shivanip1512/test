package com.cannontech.common.pao.notes.model;

import java.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoNotes {
    
    private int noteId;
    private int paObjectId;
    private String noteText;
    private char status;
    private LiteYukonUser CreatorUserName; 
    private Instant CreationDate;
    private LiteYukonUser EditorUserName;
    private Instant EditDate;
    
    public int getNoteId() {
        return noteId;
    }
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
    public int getPaObjectId() {
        return paObjectId;
    }
    public void setPaObjectId(int paObjectId) {
        this.paObjectId = paObjectId;
    }
    public String getNoteText() {
        return noteText;
    }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
    public char getStatus() {
        return status;
    }
    public void setStatus(char status) {
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
