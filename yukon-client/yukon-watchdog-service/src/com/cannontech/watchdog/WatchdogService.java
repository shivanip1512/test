package com.cannontech.watchdog;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.SmtpHelper;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.db.version.CTIDatabase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.tools.email.SystemEmailSettingsType;
import com.cannontech.tools.email.impl.EmailServiceImpl;
import com.cannontech.watchdog.base.Watchdog;

public class WatchdogService {

    private static class LogHolder {
        static final Logger log = YukonLogManager.getLogger(WatchdogService.class);
    }

    private static final String resourcePath = "\\common\\i18n\\en_US\\com\\cannontech\\yukon\\watchdog\\root";

    private List<Watchdog> watchdog;
    private static ScheduledFuture<?> schdfuture;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    
    public static void main(String args[]) {
        CtiUtilities.setClientAppName(ApplicationId.WATCHDOG);
        YukonSpringHook.setDefaultContext(YukonSpringHook.WATCHDOG_BEAN_FACTORY_KEY);
        try {
            getLogger().info("Starting watchdog service from main method");
            WatchdogService service = YukonSpringHook.getBean(WatchdogService.class);
            service.start();
            getLogger().info("Started watchdog service.");
        } catch (Throwable t) {
            CTIDatabase database = VersionTools.getDatabaseVersion();
            if (database == null) {
                sendDatabaseConnectionEmail();
            }
            getLogger().error("Error in watchdog service", t);
            System.exit(1);
        }
    }

    /**
     * Send email notification to the subscribers.
     */
    private static void sendDatabaseConnectionEmail() {
        try {
            SmtpHelper smtpHelper = new SmtpHelper();
            if (StringUtils.isEmpty(smtpHelper.getCachedValue(SystemEmailSettingsType.WATCHDOG_SUBSCRIBER_EMAILS.getKey()))) {
                getLogger().warn("No user subscribed for notification for watchdog.");
                return;
            }
            // Setup message source for reading i18n messages.
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            String baseName = StringUtils.EMPTY;
            // Running as Window Service: root.xml file is packed in i18n-en_US.jar and available in client/bin directory only. So
            // retrieve it from class path.
            URL url = WatchdogService.class.getResource("/com/cannontech/yukon/watchdog/root.xml");
            if (url != null) {
                String resourceUrl = url.toURI().toString();
                baseName = resourceUrl.replace(".xml", "");
            } else {
                // In development environment(from Eclipse): root.xml file is not available in class path. So retrieve it by user
                // directory.
                String userDir = System.getProperty("user.dir");
                String resourceDir = userDir.substring(0, userDir.lastIndexOf("\\") + 1).concat(resourcePath);
                baseName = new File(resourceDir).toURI().toString();
            }
            messageSource.setBasename(baseName);

            String subject = messageSource.getMessage("yukon.watchdog.notification.subject", null, Locale.ENGLISH);
            StringBuilder msgBuilder = new StringBuilder(
                    messageSource.getMessage("yukon.watchdog.notification.text", null, Locale.ENGLISH));
            msgBuilder.append("\n\n");
            msgBuilder.append(messageSource.getMessage("yukon.watchdog.notification.DATABASE", null, Locale.ENGLISH));
            String commaSeparatedIds = smtpHelper.getCachedValue(SystemEmailSettingsType.WATCHDOG_SUBSCRIBER_EMAILS.getKey());
            List<String> sendToEmailIds = Arrays.asList(commaSeparatedIds.split("\\s*,\\s*"));
            EmailMessage emailMessage = EmailMessage.newMessageBccOnly(subject, msgBuilder.toString(),
                    smtpHelper.getCachedValue(SystemEmailSettingsType.MAIL_FROM_ADDRESS.getKey()),
                    sendToEmailIds);
            EmailService emailService = new EmailServiceImpl();
            emailService.sendMessage(emailMessage);
        } catch (Exception e) {
            getLogger().error("Unable to send Email", e);
        }
    }

    /*
     * This method checks and starts watchdogs, if the watchdog should to be started.
     * It attempts to start watchdog daily until all watchdogs have started.
     */
    private synchronized void start() throws Exception {
        schdfuture = executor.scheduleAtFixedRate(() -> {
            List<Watchdog> startedWatchdogs = new ArrayList<Watchdog>();
            // Cancel the daily scheduler if there are no more watchdog service to start 
            if (watchdog.isEmpty()) {
                schdfuture.cancel(true);
            }
            watchdog.forEach(watchdog -> {
                if (watchdog.shouldRun()) {
                    watchdog.start();
                    startedWatchdogs.add(watchdog);
                }
            });
            watchdog.removeAll(startedWatchdogs);
        }, 0, 1, TimeUnit.DAYS);
    }

    @Autowired
    void setMonitors(List<Watchdog> watchdog) {
        this.watchdog = watchdog;
    }

    private static Logger getLogger() {
        return LogHolder.log;
    }
}
