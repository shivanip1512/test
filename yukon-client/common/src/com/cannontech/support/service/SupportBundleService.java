package com.cannontech.support.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.ReadableInstant;

import com.cannontech.tools.sftp.SftpWriter.Status;

public interface SupportBundleService {
    public void bundle(ReadableInstant start, ReadableInstant end, String custName,
                       String comments, Set<String> optionalWritersToInclude);

    public List<File> getBundles();

    public File getMostRecentBundle();

    public boolean isInProgress();

    public Map<String, Boolean> getWritersDone();

    public Status uploadViaSftp(File file);
}
