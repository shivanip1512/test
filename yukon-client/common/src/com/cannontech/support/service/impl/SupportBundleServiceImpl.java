package com.cannontech.support.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.support.service.SupportBundleService;
import com.cannontech.support.service.SupportBundleSource;
import com.cannontech.tools.zip.ZipWriter;

public class SupportBundleServiceImpl implements SupportBundleService {
    private Logger log = YukonLogManager.getLogger(SupportBundleServiceImpl.class);
    private static final int PAST_BUNDLES_TO_KEEP = 5; // -1 to never delete old bundles

    private Executor executor;

    private List<SupportBundleSource> sourceList;
    private Map<String, Boolean> sourcesDoneMap;

    private AtomicBoolean inProgress;

    public SupportBundleServiceImpl() {
        sourcesDoneMap = new ConcurrentHashMap<String, Boolean>();
        inProgress = new AtomicBoolean(false);
    }

    @Override
    public void bundle(final ReadableInstant start, final ReadableInstant stop,
                       final String comments,
                       final String custName,
                       final Set<String> optionalSourcesToInclude)
            throws IOException {
        boolean ready = inProgress.compareAndSet(false, true);
        if (ready) {
            removeOldFiles();
            executor.execute(
                new Runnable() {
                    public void run() {
                        try {
                            sourcesDoneMap.clear();
                            
                            DateTime dt = new DateTime(DateTimeZone.getDefault());
                            
                            // Support bundle filename format 
                            // '<customer name>-yyyy-MM-dd-HHmmss.zip'
                            String f = "%02d";
                            String zipFilename = custName + "-"
                                                 + dt.getYear() + "-"
                                                 + String.format(f, dt.getMonthOfYear()) + "-"
                                                 + String.format(f, dt.getDayOfMonth()) + "-"
                                                 + String.format(f, dt.getHourOfDay())
                                                 + String.format(f, dt.getMinuteOfHour())
                                                 + String.format(f, dt.getSecondOfMinute())
                                                 + ".zip";

                            for (SupportBundleSource source : sourceList) {
                                if (!source.isOptional()
                                    || optionalSourcesToInclude.contains(source.getSourceName())) {
                                    sourcesDoneMap.put(source.getSourceName(), false);
                                }
                            }

                            if (!getBundleDir().isDirectory()) {
                                getBundleDir().mkdir();
                            }

                            final File tempDir = new File(getBundleDir(), "temp/");

                            if (!tempDir.isDirectory()) {
                                tempDir.mkdir();
                            }
                            ZipWriter zipWriter =
                                new ZipWriter(new File(tempDir, zipFilename));

                            InputStream infoFileStream;

                            infoFileStream =
                                new ByteArrayInputStream(comments.getBytes("UTF-8"));
                            zipWriter.writeRawInputStream(infoFileStream, "Info.txt");

                            final int halfHourMillis = 1000 * 60 * 30;

                            for (SupportBundleSource source : sourceList) {
                                if (!source.isOptional()
                                    || optionalSourcesToInclude.contains(source.getSourceName())) {
                                    source.addToZip(zipWriter, start,
                                                    stop.toInstant().plus(halfHourMillis));
                                    sourcesDoneMap.put(source.getSourceName(), true);
                                    log.info("Support Bundle - Added Source: '"
                                             + source.getSourceName() + "'");
                                }
                            }
                            zipWriter.close();

                            zipWriter.getFile().renameTo(new File(getBundleDir(), zipFilename));
                            
                            for(File temp: tempDir.listFiles()) {
                                if(temp.isFile()) {
                                    temp.delete();
                                } else {
                                    log.warn("Unable to delete temp directory. This may happen if it contains a sub directory.");
                                }
                            }
                            
                            tempDir.delete();

                        } catch (Exception e) {
                            log.warn("Problem while trying to create support bundle", e);
                        } finally {
                            inProgress.set(false);
                        }
                    }
                });
        } else {
            log.warn("Cannot create bundle until previous bundle has completed");
        }

    }

    @Override
    public List<File> getBundles() {
        File[] allFiles = getBundleDir().listFiles(new FilenameFilter() {
            public boolean accept(File directory, String filename) {
                if (filename.endsWith(".zip")) {
                    return true;
                }
                return false;
            }
        });

        if (allFiles == null) {
            return new ArrayList<File>();
        }
        // sorting by date modified
        Arrays.sort(allFiles, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return (int) (f2.lastModified() - f1.lastModified());
            }
        });

        return Arrays.asList(allFiles);
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
    public File getMostRecentBundle() {
        List<File> allBundles = getBundles();
        if (allBundles.size() > 0) {
            return allBundles.get(0);
        }
        return null;
    }

    @Override
    public File getBundleDir() {
        return new File(CtiUtilities.getYukonBase() + "/Server/SupportBundles/");
    }

    public boolean isInProgress() {
        return inProgress.get();
    }

    @Autowired
    public void setSourceList(List<SupportBundleSource> sourceList) {
        this.sourceList = sourceList;
    }

    @Autowired
    public void setExecutor(@Qualifier("main") Executor executor) {
        this.executor = executor;
    }

    public Map<String, Boolean> getSourcesDone() {
        return Collections.unmodifiableMap(sourcesDoneMap);
    }

}