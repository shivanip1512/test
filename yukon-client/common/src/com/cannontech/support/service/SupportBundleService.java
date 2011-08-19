package com.cannontech.support.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.ReadableInstant;

public interface SupportBundleService {

    public void bundle(ReadableInstant start, ReadableInstant end, String comments, String custName,
                       Set<String> optionalSourcesToInclude) throws IOException;

    public File getBundleDir();

    public List<File> getBundles();

    public boolean isInProgress();

    public Map<String, Boolean> getSourcesDone();

    public File getMostRecentBundle();
}
