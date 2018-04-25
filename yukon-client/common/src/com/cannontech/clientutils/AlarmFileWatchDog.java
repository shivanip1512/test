package com.cannontech.clientutils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import com.cannontech.common.util.CtiUtilities;

/**
 * AlarmFileWatchDog is a custom FileWatcher to Watch the TDCOut.DAT file, and notifies listeners when it
 * changes. AlarmFileWatchDog is using Java NIO WatchService API to monitor the file changes .
 */
public class AlarmFileWatchDog extends Thread {
    /**
     * AlarmFileWatchDog Singleton Instance
     */
    private static AlarmFileWatchDog singleton = null;

    /**
     * ArrayList of ActionListener instance .
     */
    private CopyOnWriteArrayList<ActionListener> actListeners = new CopyOnWriteArrayList<ActionListener>();

    /**
     * File object , to get the file path
     */
    private final File file;
    /**
     * 
     */
    private AtomicBoolean stop = new AtomicBoolean(false);

    /**
     * No Args AlarmFileWatchDog constructor to instantiate AlarmFileWatchDog object
     */
    private AlarmFileWatchDog() {
        this.file = new File(CtiUtilities.getConfigDirPath());
    }

    public static AlarmFileWatchDog getInstance() {
        if (singleton == null) {
            singleton = AlarmFileWatchDogSingletonHelper.INSTANCE;
            singleton.start();
        }
        return singleton;
    }

    private static class AlarmFileWatchDogSingletonHelper {
        private static final AlarmFileWatchDog INSTANCE = new AlarmFileWatchDog();
    }

    /**
     * Check if thread is stopped
     */
    public boolean isStopped() {
        return stop.get();
    }

    /**
     * StopThread method to stop the thread
     */
    public void stopThread() {
        stop.set(true);
    }

    /**
     * Method to listen any change on the observed TDCOut.DAT file and trigger
     * ActionEvent based on that
     */
    public void doOnChange() {
        actListeners.forEach(
            actionListener -> actionListener.actionPerformed(new ActionEvent(this, 0, "FileWatchChange")));
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = file.toPath();
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException e) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY
                        && fileName.toString().equalsIgnoreCase(CtiUtilities.TDC_OUT_FILE)) {
                        doOnChange();
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
        } catch (Throwable e) {

        }
    }

    /**
     * Add an ActionListener to be notified of changes to the watched file
     */
    public synchronized void addActionListener(ActionListener listener) {
        actListeners.add(listener);
    }

    /**
     * Remove an ActionListener so it is no longer notified of changes to the watched file.
     */
    public synchronized void removeActionListener(ActionListener listener) {
        actListeners.remove(listener);
    }
}
