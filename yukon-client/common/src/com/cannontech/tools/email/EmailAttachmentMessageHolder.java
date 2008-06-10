package com.cannontech.tools.email;

import java.util.List;

import javax.activation.DataSource;

public interface EmailAttachmentMessageHolder extends EmailMessageHolder {
    public List<DataSource> getAttachments();
}
