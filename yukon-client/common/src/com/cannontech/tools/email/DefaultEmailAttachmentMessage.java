package com.cannontech.tools.email;

import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;

public class DefaultEmailAttachmentMessage extends DefaultEmailMessage implements
    EmailAttachmentMessageHolder {
    private List<DataSource> attachments = new ArrayList<DataSource>();

    @Override
    public List<DataSource> getAttachments() {
        return attachments;
    }

    public void addAttachment(DataSource dataSource) {
        attachments.add(dataSource);
    }
}
