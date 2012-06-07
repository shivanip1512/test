package com.cannontech.stars.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.cannontech.stars.xml.serialize.StarsOperation;

public class StarsMessage extends SOAPMessage {
    private StarsOperation starsOperation;
    
    public StarsMessage() {
    }
    
    public void setStarsOperation(StarsOperation starsOperation) {
        this.starsOperation = starsOperation;
    }
    
    public StarsOperation getStarsOperation() {
        return starsOperation;
    }

    @Override
    public void addAttachmentPart(AttachmentPart AttachmentPart) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int countAttachments() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public AttachmentPart createAttachmentPart() {
        // TODO Auto-generated method stub
        return null;
    }

    public AttachmentPart getAttachment(SOAPElement element)
            throws SOAPException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator getAttachments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator getAttachments(MimeHeaders headers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getContentDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MimeHeaders getMimeHeaders() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SOAPPart getSOAPPart() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeAllAttachments() {
        // TODO Auto-generated method stub
        
    }

    public void removeAttachments(MimeHeaders headers) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveChanges() throws SOAPException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean saveRequired() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setContentDescription(String description) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeTo(OutputStream out) throws SOAPException, IOException {
        // TODO Auto-generated method stub
    }

}
