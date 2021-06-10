package com.cannontech.support.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.cannontech.support.service.SupportBundleService;
import com.cannontech.support.service.SupportBundleWriter;
import com.cannontech.tools.zip.ZipWriter;

public class SupportBundleServiceImpl implements SupportBundleService {
    private Logger log = YukonLogManager.getLogger(SupportBundleServiceImpl.class);

    private static final int PAST_BUNDLES_TO_KEEP = 5; // -1 to never delete old bundles

    private Executor executor;
    private List<SupportBundleWriter> writerList;

    private Map<String, Boolean> writersDoneMap;
    private AtomicBoolean inProgress;

    public SupportBundleServiceImpl() {
        writersDoneMap = new ConcurrentHashMap<String, Boolean>();
        inProgress = new AtomicBoolean(false);
    }

    @Override
    public void bundle(final ReadableInstant start, final ReadableInstant stop,
                       final String custName, final String comments,
                       final Set<String> optionalWritersToInclude) {
        boolean ready = inProgress.compareAndSet(false, true);
        if (!ready) {
            log.warn("Cannot create bundle until previous bundle has completed");
            return;
        }

        removeOldFiles();
        executor.execute(new Runnable() {
            public void run() {
                try {
                    writersDoneMap.clear();

                    DateTime now = new DateTime(DateTimeZone.getDefault());

                    for (SupportBundleWriter writer : writerList) {
                        if (!writer.isOptional()
                                || optionalWritersToInclude.contains(writer.getName())) {
                            writersDoneMap.put(writer.getName(), false);
                        }
                    }

                    if (!getBundleDir().isDirectory()) {
                        getBundleDir().mkdir();
                    }

                    File tempFile = File.createTempFile("support_bundle", "zip");
                    ZipWriter zipWriter = new ZipWriter(tempFile);

                    String bundleMetaData = buildMetaData(start, stop, custName, comments,
                                                          optionalWritersToInclude);
                    InputStream infoFileStream =
                        new ByteArrayInputStream(bundleMetaData.getBytes("UTF-8"));
                    zipWriter.writeRawInputStream(infoFileStream, "Info.txt");

                    Duration halfHour = Duration.standardMinutes(30);

                    for (SupportBundleWriter writer : writerList) {
                        if (!writer.isOptional()
                                    || optionalWritersToInclude.contains(writer.getName())) {
                            writer.addToZip(zipWriter, start, stop.toInstant().plus(halfHour));
                            writersDoneMap.put(writer.getName(), true);
                            log.info("Support Bundle - Added Writer: '" + writer.getName() + "'");
                        }
                    }
                    zipWriter.close();

                    String zipFilename = custName + "-" + now.toString("yyyy-MM-dd-HHmmss") + ".zip";
                    tempFile.renameTo(new File(getBundleDir(), zipFilename));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    inProgress.set(false);
                }
            }
        });
    }

    @Override
    public List<File> getBundles() {
        return FileUtil.filterAndOrderZipFile(getBundleDir());
    }

    public List<File> getRfBundles() {
        return FileUtil.filterAndOrderZipFile(getRfBundleDir());
    }

    @Override
    public File getMostRecentBundle() {
        List<File> allBundles = getBundles();
        if (allBundles.size() > 0) {
            return allBundles.get(0);
        }
        return null;
    }

    private void removeOldFiles() {
        if (PAST_BUNDLES_TO_KEEP >= 0) {
            List<File> allFiles = getBundles();
            for (int i = PAST_BUNDLES_TO_KEEP; i < allFiles.size(); i++) {
                log.info("Deleted Old Support Bundle: " + allFiles.get(i).getName());
                allFiles.get(i).delete();
            }
        }
    }

    @Override
    public boolean isInProgress() {
        return inProgress.get();
    }

    @Override
    public Map<String, Boolean> getWritersDone() {
        return Collections.unmodifiableMap(writersDoneMap);
    }

    private File getBundleDir() {
        return new File(CtiUtilities.getYukonBase() + "/Server/SupportBundles/");
    }

    private File getRfBundleDir() {
        return new File(CtiUtilities.getYukonBase() + "/Server/SupportBundles/RfNetworkData/");
    }

    private String buildMetaData(ReadableInstant start, ReadableInstant stop, String custName,
                                 String comments, final Set<String> optionalWritersToInclude) {
        String eol = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("Time Range Selected: ");
        sb.append(start);
        sb.append(" to ");
        sb.append(stop);
        sb.append(eol);
        sb.append("Optional Writers Chosen:");
        sb.append(eol);
        if (optionalWritersToInclude.size() > 0) {
            for (String writerToInclude : optionalWritersToInclude) {
                sb.append(writerToInclude);
                sb.append(eol);
            }
        } else {
            sb.append("none");
            sb.append(eol);
        }
        sb.append(eol);

        if (StringUtils.isNotEmpty(comments)) {
            sb.append(eol);
            sb.append("Comments:");
            sb.append(eol);
            sb.append(eol);
            sb.append(comments);
        }

        return sb.toString();
    }

    @Autowired
    public void setExecutor(@Qualifier("main") Executor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setWriterList(List<SupportBundleWriter> writerList) {
        this.writerList = writerList;
    }
}
