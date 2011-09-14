package com.cannontech.support.service;

import org.joda.time.ReadableInstant;

import com.cannontech.tools.zip.ZipWriter;

public interface SupportBundleWriter {
    public void addToZip(ZipWriter zipWriter, ReadableInstant start, ReadableInstant stop);

    public boolean isOptional();

    /**
     * This name uniquely identifies this writer and is used as part of the i18n key for displaying
     * to the user.
     */
    public String getName();
}
