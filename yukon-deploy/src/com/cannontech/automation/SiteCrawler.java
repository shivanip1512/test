package com.cannontech.automation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SiteCrawler {
    private final boolean verboseLogging = true;
    private final int numThreads = 4;
    private final int timoutSeconds = 25;

    private final String baseUrl;
    private final String contextPath;

    private String jSessionId;
    private final AtomicBoolean[] threadsWaiting = new AtomicBoolean[numThreads];
    private FileWriter urlsFoundLogFileWriter;

    private Set<String> urlsFound = Collections.synchronizedSet(new HashSet<String>());
    private AtomicInteger numSuccesses = new AtomicInteger();
    private Map<String, String> urlErrors = new ConcurrentHashMap<>();
    private AtomicInteger numTimeouts = new AtomicInteger();
    private List<String> errorList = Collections.synchronizedList(new ArrayList<String>());
    private List<String> timeoutList = Collections.synchronizedList(new ArrayList<String>());
    private static Set<String> longRunningUrlPrefixes = new HashSet<>();
    private static Set<String> legacyErrorsToIgnore = new HashSet<>();
    private BlockingQueue<ReferencedUrl> urlsToVisit = new LinkedBlockingQueue<>();
    static {
        // Remove these as they are fixed.
        // No new long running pages should be ignored.
        longRunningUrlPrefixes.add("bulk/addPoints/home");
        longRunningUrlPrefixes.add("bulk/collectionActions");
        longRunningUrlPrefixes.add("reports/simple");
        longRunningUrlPrefixes.add("meter/historicalReadings/view");
        longRunningUrlPrefixes.add("group/commander/collectionProcessing");
        longRunningUrlPrefixes.add("common/deviceDefinition.xml");
        longRunningUrlPrefixes.add("stars/operator/inventory/inventoryActions");
        longRunningUrlPrefixes.add("tools/points/");
        longRunningUrlPrefixes = Collections.unmodifiableSet(longRunningUrlPrefixes);

        // YUK-13891 should fix these
        legacyErrorsToIgnore.add("stars/operator/inventory/view?deviceId=33");
        legacyErrorsToIgnore.add("stars/operator/inventory/view?deviceId=34");
        
        // Remove these as they are fixed.
        // No new errors should be ignored.
        legacyErrorsToIgnore.add("admin/energyCompany/operatorLogin/toggleOperatorLoginStatus");
        legacyErrorsToIgnore.add("capcontrol/tier/feeders");
        legacyErrorsToIgnore.add("editor/cbcBase.jsf");
        legacyErrorsToIgnore.add("operator/WorkOrder/Report.jsp");
        legacyErrorsToIgnore.add("operator/WorkOrder/WOFilter.jsp");
        legacyErrorsToIgnore.add("stars/operator/optOut/admin");
        legacyErrorsToIgnore.add("WebConfig/custom/sample_capcontrol_files/Sample CBC Import.csv");
        legacyErrorsToIgnore.add("WebConfig/custom/sample_capcontrol_files/Sample Hierarchy Import.csv");
        legacyErrorsToIgnore.add("WebConfig/custom/sample_capcontrol_files/Sample Point Mapping Import.csv");
        legacyErrorsToIgnore.add("WebConfig/custom/sample_capcontrol_files/Sample Regulator Import.csv");
        
        // This sometimes happen when capcontrol cache do not get updated.
        legacyErrorsToIgnore.add("capcontrol/substations/0");
        legacyErrorsToIgnore = Collections.unmodifiableSet(legacyErrorsToIgnore);
    }

    public static void main(String... params) throws Exception {
        String username = params.length > 0 ? params[0] : "yukon";
        String password = params.length > 1 ? params[1] : "yukon";
        String baseUrl = params.length > 2 ? params[2] : "http://localhost:8080/";
        // If specified, every URL found will be logged to the specified file.
        String contextPath = params.length > 3 ? params[3] : "";
        String urlsFoundLogFile = params.length > 4 ? params[4] : null;

        new SiteCrawler(baseUrl, contextPath).start(username, password, urlsFoundLogFile);
    }

    public SiteCrawler(String baseUrl, String contextPath) {
        this.baseUrl = baseUrl;
        this.contextPath = contextPath;

        for (int threadNum = 0; threadNum < numThreads; threadNum++) {
            threadsWaiting[threadNum] = new AtomicBoolean(true);
        }
    }

    public void start(String username, String password, String urlsFoundLogFile) throws Exception {
        logVerbose("Logging in... ");
        jSessionId = login(username, password);
        if (jSessionId == null) {
            log("Unable to login to " + baseUrl);
            System.exit(0);
        }
        log("Successfully logged into " + baseUrl + " as " + username);

        if (urlsFoundLogFile != null) {
            File file = new File(urlsFoundLogFile);
            if (file.exists()) {
                file.delete();
            }
            urlsFoundLogFileWriter = new FileWriter(file);
            urlsFoundLogFileWriter.write("URL,Found on URL\n");
        }

        log("");
        log("Ignoring the following URLs. These are known to be very resource intensive:");
        for (String urlToIgnore : longRunningUrlPrefixes) {
            log(urlToIgnore + "/*");
        }
        log("");

        final Thread[] workers = new Thread[numThreads];
        for (int threadNum = 0; threadNum < numThreads; threadNum++) {
            final int finalThreadNum = threadNum;
            workers[finalThreadNum] = new Thread(new Runnable() {
                @Override
                public void run() {
                    ReferencedUrl pair;
                    try {
                        while (true) {
                            threadsWaiting[finalThreadNum].set(true);
                            pair = urlsToVisit.take();
                            threadsWaiting[finalThreadNum].set(false);
                            findLinks(pair.getUrl(), pair.getFromUrl());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("done for now", e);
                    } catch (InterruptedException e) {
                        // Expected way to shut down this thread
                    }
                }
            });
            workers[finalThreadNum].start();
        }
        logVerbose("Started " + numThreads + " threads");

        // Need to populate the queue before calling the monitor (which is responsible for shutting this down)
        findLinks(baseUrl + contextPath + "sitemap", "--");

        // The monitor and output thread
        new Timer().scheduleAtFixedRate(new TimerTask() {
            long start = System.currentTimeMillis();
            long lastCheck = System.currentTimeMillis();
            long lastNumVisited = 0;
            int numPrinted = 0;

            @Override
            public void run() {
                int numLeft = urlsToVisit.size();
                int numFound = urlsFound.size();
                int numSuccess = numSuccesses.get();
                int numError = urlErrors.size();
                int numTimeout = numTimeouts.get();
                int numVisited = numSuccess + numError + numTimeout;

                int secondsSinceLastCheck = (int) ((System.currentTimeMillis() - lastCheck) / 1000);
                int secondsSinceStart = (int) ((System.currentTimeMillis() - start) / 1000);

                double instRate = (double) (numVisited - lastNumVisited) / secondsSinceLastCheck;
                double totalRate = (double) (numVisited) / secondsSinceStart;

                if (numPrinted != 0) {
                    if (!errorList.isEmpty() || !timeoutList.isEmpty()) {
                        logVerbose("\n");
                        logVerbose("******** Problems Found ********");
                        synchronized (errorList) {
                            for (String errMsg : errorList) {
                                log(errMsg);
                            }
                            errorList.clear();
                        }
                        synchronized (timeoutList) {
                            for (String errMsg : timeoutList) {
                                log(errMsg);
                            }
                            timeoutList.clear();
                        }
                    }
                    logVerbose("*********** Progress ***********");
                    logVerbose(String.format("%-8s%-8s%-8s|%-8s%-8s%-10s|%-10s%-10s|%-10s", "Success", "Error",
                        "Timeout", "Found", "Visited", "Remaining", "Curr Rate", "Total Rate", "Busy Threads"));
                    logVerbose(String.format("%-8d%-8d%-8d|%-8d%-8d%-10d|%8.2f/s%8.2f/s|%-10s", numSuccess, numError,
                        numTimeout, numFound, numVisited, numLeft, instRate, totalRate, getThreadStatuses()));
                }
                numPrinted++;

                lastCheck = System.currentTimeMillis();
                lastNumVisited = numVisited;

                if (crawlFinished()) {
                    logVerbose("*********** Finished ***********");
                    for (int threadNum = 0; threadNum < numThreads; threadNum++) {
                        workers[threadNum].interrupt();
                    }
                    cancel();

                    Set<String> newErrors = new HashSet<>();
                    // Remove ignored error URLs.
                    synchronized (urlErrors) {
                        for (String urlError : urlErrors.keySet()) {
                            if (!isExcludedErrorUrl(urlError)) {
                                newErrors.add(urlError);
                            }
                        }
                    }

                    if (urlsFoundLogFileWriter != null) {
                        try {
                            urlsFoundLogFileWriter.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (newErrors.isEmpty()) {
                        log("No new problems found. Build success.");
                        System.exit(0);
                    } else {
                        log("Found " + newErrors.size() + " new problems. Failing build.");
                        log("New problems: ");
                        for (String urlErrorMsg : newErrors) {
                            log(urlErrors.get(urlErrorMsg));
                        }
                        log("");

                        System.exit(-1);
                    }
                }
            }
        }, 0, TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES));
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private void logVerbose(String message) {
        if (verboseLogging) {
            log(message);
        }
    }

    private boolean isExcludedErrorUrl(String url) {
        if (!contextPath.isEmpty()) {
            if (url.startsWith(contextPath)) {
                url = url.substring(contextPath.length());
            } else {
                // The URL is missing the context path so it shouldn't be excluded.
                return false;
            }
        }

        for (String excludedUrl : legacyErrorsToIgnore) {
            if (url.contains(excludedUrl)) {
                return true;
            }
        }
        return false;
    }

    private boolean crawlFinished() {
        for (int threadNum = 0; threadNum < numThreads; threadNum++) {
            if (!threadsWaiting[threadNum].get()) {
                // Found a busy thread
                return false;
            }
        }
        return urlsToVisit.size() == 0;
    }

    private void findLinks(String pageUrl, String fromUrl) throws InterruptedException, IOException {
        if (urlsFoundLogFileWriter != null) {
            synchronized (urlsFoundLogFileWriter) {
                urlsFoundLogFileWriter.write(pageUrl);
                urlsFoundLogFileWriter.write(",");
                urlsFoundLogFileWriter.write(fromUrl);
                urlsFoundLogFileWriter.write("\n");
            }
        }
        try {
            for (Element link : getLinks(pageUrl)) {
                String linkUrl = link.attr("href");
                String absoluteUrl = link.attr("abs:href");
                if (shouldCheckLink(linkUrl, absoluteUrl)) {
                    urlsToVisit.put(new ReferencedUrl(absoluteUrl, pageUrl));
                    urlsFound.add(absoluteUrl);
                }
            }
            numSuccesses.incrementAndGet();
        } catch (HttpStatusException ex) {
            String shortPageUrl = pageUrl.replace(baseUrl, "");
            String shortFromUrl = fromUrl.replace(baseUrl, "");
            int statusCode = ex.getStatusCode();

            String message = "Http Status: " + statusCode + " : ";
            if (statusCode >= 500 && statusCode <= 599) {
                message += "Error Message: \"" + ex.getMessage() + "\", ";
            }
            message += "/" + shortPageUrl + " (found on /" + shortFromUrl + ")";
            errorList.add(message);
            urlErrors.put(shortPageUrl, message);
        } catch (UnsupportedMimeTypeException ex) {
            // Calling this a success
            numSuccesses.incrementAndGet();
        } catch (SocketTimeoutException ex) {
            String shortPageUrl = pageUrl.replace(baseUrl, "");
            String shortFromUrl = fromUrl.replace(baseUrl, "");
            String message = "Timed out after " + timoutSeconds + "s : /" + shortPageUrl + " (found on /"
                + shortFromUrl + ")";
            timeoutList.add(message);
            numTimeouts.incrementAndGet();
        }
    }

    private String getThreadStatuses() {
        String status = "[";
        for (int threadNum = 0; threadNum < numThreads; threadNum++) {
            if (threadsWaiting[threadNum].get()) {
                status += " ";
            } else {
                status += "*";
            }
        }
        status += "]";
        return status;
    }

    private boolean shouldCheckLink(String url, String absoluteUrl) {
        boolean shouldCheck = !urlsFound.contains(absoluteUrl)
                        && !url.isEmpty()
                        && !url.startsWith("#")
                        && !url.toLowerCase().contains("javascript:")
                        && !url.toLowerCase().contains("mailto:")
                        && !url.contains("ACTION=LOGOUT")
                        && !isIgnoredUrl(url)
                        && absoluteUrl.startsWith(baseUrl);
        return shouldCheck;
    }

    private boolean isIgnoredUrl(String url) {
        for (String urlToIgnore : longRunningUrlPrefixes) {
            if (url.contains(urlToIgnore)) {
                return true;
            }
        }
        return false;
    }

    private Elements getLinks(String url) throws IOException {
        Connection connection  = Jsoup.connect(url.replace(" ", "+"))
                .cookie("JSESSIONID", jSessionId)
                .timeout(timoutSeconds * 1000)
                .ignoreHttpErrors(true);
        Document doc = connection.get();

        Connection.Response response = connection.response();
        if (response.statusCode() != 200) {
            Element rootErrorMsg = doc.select("#rootErrorMessage").first();
            String errorMessage = rootErrorMsg != null ? rootErrorMsg.text() : "Unknown Error";
            throw new HttpStatusException(errorMessage, response.statusCode(), url);
        }
        return doc.select("a[href]");
    }

    private String login(String username, String password) {
        Connection.Response loginResponse;
        try {
            loginResponse = Jsoup.connect(baseUrl + contextPath)
                    .data("USERNAME", username, "PASSWORD", password)
                    .method(Method.GET).timeout(20*1000).execute();
        } catch (IOException e) {
            return null;
        }
        return loginResponse.cookie("JSESSIONID");
    }
}
