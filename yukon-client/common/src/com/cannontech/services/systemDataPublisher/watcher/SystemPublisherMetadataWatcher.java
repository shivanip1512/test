package com.cannontech.services.systemDataPublisher.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.services.systemDataPublisher.service.impl.SystemDataServiceInitializer;

public class SystemPublisherMetadataWatcher {

    @Autowired private SystemDataServiceInitializer systemDataServiceInitializer;
    private static final Logger log = YukonLogManager.getLogger(SystemPublisherMetadataWatcher.class);

    public Runnable watch() {
        return new Runnable() {
            public void run() {
                try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                    File file = new File(CtiUtilities.getYukonBase(), "Server/Config/encryptedSystemPublisherMetadata.yaml");
                    Path path = file.toPath().getParent();
                    path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                    while (true) {
                        WatchKey key;
                        key = watcher.take();
                        if (key == null) {
                            continue;
                        }
                        // Prevent receiving two separate ENTRY_MODIFY events: file modified and time stamp updated.
                        Thread.sleep(100);
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();

                            @SuppressWarnings("unchecked") WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            Path filename = ev.context();

                            if (kind == StandardWatchEventKinds.OVERFLOW) {
                                continue;
                            } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
                                    && filename.toString().equals(file.getName())) {
                                doOnChange();
                            }
                            boolean valid = key.reset();
                            if (!valid) {
                                break;
                            }
                        }
                    }
                } catch (Throwable e) {
                    log.error("Error occurred while watching encryptedSystemPublisherMetadata.yaml", e);
                }
            }

            private void doOnChange() throws IllegalBlockSizeException, BadPaddingException, IOException {
                log.info("encryptedSystemPublisherMetadata.yaml has been modified. Changes will be processed and published to the cloud.");
                systemDataServiceInitializer.publishCloudDataConfigurations();
            }
        };
    }
}
