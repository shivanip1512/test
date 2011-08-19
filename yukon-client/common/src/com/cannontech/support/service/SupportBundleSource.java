package com.cannontech.support.service;

import org.joda.time.ReadableInstant;

import com.cannontech.tools.zip.ZipWriter;

public interface SupportBundleSource {

    public void addToZip(ZipWriter zipWriter, ReadableInstant start, ReadableInstant stop);

    public boolean isOptional();

    /**
     * Return an i18n key representing the name of this bundle for the user.
     */
    public String getSourceName();

}
