package com.cannontech.util;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

/**
 * The junk drawer for servlets.
 * Added static Attribute values from stars for reference in all web applications. (04/15/2004 SN)
 */

public class ServletUtil {
    
    private static final Logger log = YukonLogManager.getLogger(ServletUtil.class);

    private static final PathMatcher pathMatcher = new AntPathMatcher();
    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

    // Session attributes.
    public static final String ATT_ERROR_MESSAGE = "ERROR_MESSAGE";
    public static final String ATT_CONFIRM_MESSAGE = "CONFIRM_MESSAGE";
    public static final String ATT_YUKON_USER = "YUKON_USER";

    public static final String ATT_REDIRECT = "REDIRECT";
    public static final String ATT_REFERRER = "REFERRER";

    /**
     * if used in session, this attribute should be passed a CtiNavObject
     */
    public static final String NAVIGATE = "CtiNavObject";

    /**
     * if used in session, this attribute should be passed an ArrayList of FilterWrappers
     */
    public static final String FILTER_INVEN_LIST = "InventoryFilters";
    public static final String FILTER_WORKORDER_LIST = "WorkOrderFilters";

    public static final String ATT_GRAPH_BEAN = "GRAPH_BEAN";
    public static final String ATT_REPORT_BEAN = "REPORT_BEAN";
    public static final String ATT_BILLING_BEAN = "BILLING_BEAN";
    public static final String ATT_YC_BEAN = "YC_BEAN";
    // Valid periods
    public static final String ONEDAY = "1 Day";
    public static final String THREEDAYS = "3 Days";
    public static final String FIVEDAYS = "5 Days";
    public static final String ONEWEEK = "1 Week";
    public static final String ONEMONTH = "1 Month";
    public static final String FOURWEEKS = "4 Weeks";
    public static final String FIVEWEEKS = "5 Weeks";

    public static final String TODAY = "Today";
    public static final String YESTERDAY = "Yesterday";
    public static final String PREVTWODAYS = "Prev 2 Days";
    public static final String PREVTHREEDAYS = "Prev 3 Days";
    public static final String PREVFIVEDAYS = "Prev 5 Days";
    public static final String PREVSEVENDAYS = "Prev 7 Days";
    public static final String PREVONEWEEK = "Prev 1 Week";
    public static final String PREVTHIRTYDAYS = "Prev 30 Days";
    public static final String EVENT = "Event";
    public static final String SESSION_INFO = "Session Info";

    // if periods is modified, final ints representing the periods index need to be updated also, in Graph
    // class
    public static String[] historicalPeriods = { ONEDAY, THREEDAYS, ONEWEEK, FOURWEEKS, FIVEWEEKS, ONEMONTH, EVENT };

    public static String[] currentPeriods = { TODAY, PREVTWODAYS, PREVTHREEDAYS,
        // PREVFIVEDAYS,
        PREVSEVENDAYS, EVENT };

    // Date/Time pattern strings that will be tried when
    // attempting to interprate starting dates
    private static final SimpleDateFormat[] dateFormat = { new SimpleDateFormat("MM:dd:yyyy:HH:mm:ss"),
        new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"), new SimpleDateFormat("MM/dd/yyyy HH:mm"),
        new SimpleDateFormat("MM/dd/yy"), new SimpleDateFormat("MM-dd-yy"), new SimpleDateFormat("MM.dd.yy"),
        new SimpleDateFormat("MM/dd/yyyy"), new SimpleDateFormat("MM-dd-yyyy"), new SimpleDateFormat("MM.dd.yyyy"),
        new SimpleDateFormat("HH:mm:ss"), new SimpleDateFormat("HH:mm") };

    // Ever seen this before? hehe
    // this static initializer sets all the simpledateformat to lenient
    static {
        for (int i = 0; i < dateFormat.length; i++) {
            dateFormat[i].setLenient(true);
        }
    }

    // Values of the "format" property of the <cti:getProperty> tag
    public static final String FORMAT_UPPER = "upper";
    public static final String FORMAT_LOWER = "lower";
    public static final String FORMAT_CAPITAL = "capital";
    public static final String FORMAT_ALL_CAPITAL = "all_capital";
    public static final String FORMAT_ADD_ARTICLE = "add_article";

    @SuppressWarnings("ucd")
    public static Object[][] executeSQL(String dbAlias, String query, Class<? extends Object>[] types) {
        java.sql.Connection connection = null;
        java.sql.Statement statement = null;
        java.sql.ResultSet resultSet = null;
        Object[][] data = null;

        try {
            connection = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            Vector<Vector<Object>> rows = new Vector<>();
            int columnCount = 0;
            ResultSetMetaData metaData = resultSet.getMetaData();
            columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Vector<Object> rowData = new Vector<>();
                boolean nonNullRow = false;

                for (int i = 1; i <= columnCount; i++) {
                    Class<? extends Object> thisColumn = types[i - 1];
                    Object o;

                    if (thisColumn == Integer.class) {
                        o = new Integer(resultSet.getInt(i));
                    } else if (thisColumn == Double.class) {
                        o = new Double(resultSet.getDouble(i));
                    } else if (thisColumn == Float.class) {
                        o = new Float(resultSet.getFloat(i));
                    } else if (thisColumn == java.util.Date.class) {
                        java.sql.Timestamp t = resultSet.getTimestamp(i);

                        if (t != null) {
                            o = new java.util.Date(t.getTime());
                        } else {
                            o = null;
                        }

                    } else if (thisColumn == String.class) {
                        o = resultSet.getString(i);
                    } else {
                        o = SqlUtils.getResultObject(resultSet, i);
                    }

                    if (o != null) {
                        nonNullRow = true; // at least 1 value in the row is not null
                    }

                    rowData.addElement(o);
                }

                if (rowData.size() > 0 && nonNullRow) {
                    rows.addElement(rowData);
                }
            }

            data = new Object[rows.size()][columnCount];
            for (int i = 0; i < rows.size(); i++) {
                Vector<Object> temp = rows.elementAt(i);
                data[i] = temp.toArray();// temp.copyInto( data[i] );
            }
        } catch (java.sql.SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (java.sql.SQLException e) {}

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (java.sql.SQLException e) {}

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (java.sql.SQLException e) {
                // didnt work
            }
        }

        return data;
    }

    public static Date getEndingDateOfInterval(Date startingDate, String period) {

        int numDays;
        period = period.trim();

        if (period.equalsIgnoreCase(ONEDAY) || period.equalsIgnoreCase(TODAY) || period.equalsIgnoreCase(PREVTWODAYS)
            || period.equalsIgnoreCase(PREVTHREEDAYS) || period.equalsIgnoreCase(PREVFIVEDAYS)
            || period.equalsIgnoreCase(PREVSEVENDAYS) || period.equalsIgnoreCase(PREVONEWEEK)
            || period.equalsIgnoreCase(PREVTHIRTYDAYS)) {
            numDays = 1;
        } else if (period.equalsIgnoreCase(YESTERDAY)) {
            numDays = 0;
        } else if (period.equalsIgnoreCase(THREEDAYS)) {
            numDays = 3;
        } else if (period.equalsIgnoreCase(FIVEDAYS)) {
            numDays = 5;
        } else if (period.equalsIgnoreCase(ONEWEEK)) {
            numDays = 7;
        } else if (period.equalsIgnoreCase(FOURWEEKS)) {
            numDays = 28;
        } else if (period.equalsIgnoreCase(FIVEWEEKS)) {
            numDays = 35;
        } else if (period.equalsIgnoreCase(ONEMONTH)) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(startingDate);
            c.add(Calendar.MONTH, 1);

            Date endOfInterval = c.getTime();

            numDays = TimeUtil.absDifferenceInDays(startingDate, endOfInterval);
        } else if (period.equalsIgnoreCase(EVENT)) {
            return startingDate;
        } else {
            return null;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(startingDate);
        cal.add(Calendar.DAY_OF_YEAR, numDays);

        return cal.getTime();
    }

    /**
     * Returns the HTML string definition for a Java Color object.
     * 
     * @return String
     * @param Color
     */
    public static synchronized String getHTMLColor(Color c) {
        String r = Integer.toHexString(c.getRed());
        String g = Integer.toHexString(c.getGreen());
        String b = Integer.toHexString(c.getBlue());

        return (r.length() <= 1 ? "0" + r : r) + (g.length() <= 1 ? "0" + g : g) + (b.length() <= 1 ? "0" + b : b);
    }

    public static java.util.Date getStartingDateOfInterval(Date startingDate, String period) {

        int numDays;
        period = period.trim();

        if (period.equalsIgnoreCase(TODAY)) {
            return startingDate;
        } else if (period.equalsIgnoreCase(PREVTWODAYS) || period.equalsIgnoreCase(YESTERDAY)) {
            numDays = -1;
        } else if (period.equalsIgnoreCase(PREVTHREEDAYS)) {
            numDays = -2; // we want the previous three days including today so we really only need 2 previous
                          // ones.
        } else if (period.equalsIgnoreCase(PREVFIVEDAYS)) {
            numDays = -4;
        } else if (period.equalsIgnoreCase(PREVSEVENDAYS) || period.equalsIgnoreCase(PREVONEWEEK)) {
            numDays = -6;
        } else if (period.equalsIgnoreCase(PREVTHIRTYDAYS)) {
            numDays = -29;
        } else if (period.equalsIgnoreCase(EVENT)) {
            return new Date(0);
        } else {
            return startingDate;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(startingDate);
        cal.add(Calendar.DAY_OF_YEAR, numDays);

        return cal.getTime();
    }

    @Deprecated
    public static java.util.Date getToday() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * Returns a Date that represents the beginning of tomorrow.
     * Creation date: (3/28/00 4:15:54 PM)
     * 
     * @return java.util.Date
     * @deprecated use TimeUtil.getMidnight
     */
    @Deprecated
    public static java.util.Date getTomorrow() {
        return getTomorrow(TimeZone.getDefault());

    }

    /**
     * Returns a Date that represents the beginning of tomorrow in the given TimeZone.
     * 
     * @param tz
     * @return
     * @deprecated use TimeUtil.getMidnight
     */
    @Deprecated
    public static Date getTomorrow(TimeZone tz) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(tz);
        cal.setTime(new Date());

        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * Returns a Date that represents the beginning of yesterday.
     * Creation date: (3/28/00 4:22:52 PM)
     * 
     * @return java.util.Date
     * @deprecated use TimeUtil.getMidnight
     */
    @Deprecated
    public static java.util.Date getYesterday() {
        return getYesterday(TimeZone.getDefault());
    }

    /**
     * Returns a Date that represents the beginning of yesterday in the given TimeZone.
     * 
     * @param tz
     * @return
     * @deprecated use TimeUtil.getMidnight
     */
    @Deprecated
    public static Date getYesterday(TimeZone tz) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(tz);
        cal.setTime(new Date());

        cal.add(Calendar.DAY_OF_YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    /**
     * Create a Date object using the given String and TimeZone
     * Creation date: (3/28/00 11:28:32 AM)
     * 
     * @return java.util.Date
     * @param str java.lang.String
     */
    public static synchronized java.util.Date parseDateStringLiberally(String dateStr) {
        return parseDateStringLiberally(dateStr, TimeZone.getDefault());
    }

    /**
     * Method parseDateStringLiberally.
     * Create a Date object using the given String and TimeZone
     * 
     * @param dateStr
     * @param tz
     * @return Date
     */
    public static synchronized Date parseDateStringLiberally(String dateStr, TimeZone tz) {
        java.util.Date retVal = null;

        for (int i = 0; i < dateFormat.length; i++) {
            try {
                DateFormat df = dateFormat[i];
                df.setTimeZone(tz);
                retVal = df.parse(dateStr);
                break;
            } catch (java.text.ParseException pe) {}
        }

        return retVal;
    }

    /**
     * Used to return NULL for a param if it is not found OR if it is not set
     * 
     * @param req_
     * @param name_
     * @return
     */
    public synchronized static String getParameter(HttpServletRequest req_, String name_) {
        return getParameter(req_, name_, null);
    }

    /**
     * Convenience method to get a servlet parameter.
     * 
     * @param req
     * @param parameterName
     * @param defaultValue
     * @return
     */
    public synchronized static String getParameter(HttpServletRequest req, String parameterName, String defaultValue) {
        String s = req.getParameter(parameterName);
        return (s == null || s.length() == 0 ? defaultValue : s);
    }

    /**
     * Convert a string into the capitalized format.
     * 
     * @return String
     * @param word String
     */
    public static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase().concat(word.substring(1).toLowerCase());
    }

    /**
     * Capitalize every word in a phrase.
     * 
     * @return String
     * @param phrase String
     */
    public static String capitalizeAll(String phrase) {
        StringTokenizer st = new StringTokenizer(phrase, " ", true);
        StringBuffer sb = new StringBuffer();
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            if (word.equals(" ")) {
                sb.append(word);
            } else {
                sb.append(capitalize(word));
            }
        }

        return sb.toString();
    }

    /**
     * Add an indefinite article in front of a word
     * 
     * @param word
     * @return
     */
    public static String addArticle(String word) {
        if (word.charAt(0) == 'a' || word.charAt(0) == 'A' || word.charAt(0) == 'e' || word.charAt(0) == 'E'
            || word.charAt(0) == 'i' || word.charAt(0) == 'I' || word.charAt(0) == 'o' || word.charAt(0) == 'O'
            || word.charAt(0) == 'u' || word.charAt(0) == 'U') {
            return "an " + word;
        }

        return "a " + word;
    }

    /**
     * Returns the current Yukon user object found in the session.
     *
     */
    public static LiteYukonUser getYukonUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (LiteYukonUser) session.getAttribute(ATT_YUKON_USER);
    }

    /**
     * Returns the current Yukon user object found in the request.
     * 
     * @throws NotLoggedInException if no session exists
     */
    public static LiteYukonUser getYukonUser(ServletRequest request) throws NotLoggedInException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            //TODO: Remove this logging later. Added for YUK-18491.
            log.trace("No session found for the user.");
            throw new NotLoggedInException();
        }
        LiteYukonUser yukonUser = getYukonUser(session);
        if (yukonUser == null) {
          //TODO: Remove this logging later. Added for YUK-18491.
            log.trace("Could not find yukonUser in the session. Session id:" + session.getId());
            throw new NotLoggedInException();
        }
        return yukonUser;
    }

    /**
     * Returns the fully qualified URL that was requested
     *
     */
    public static String getFullURL(HttpServletRequest req) {
        if (req == null) {
            return "";
        }

        String q = "";
        if (req.getQueryString() != null) {
            q = "?" + req.getQueryString();
        }

        return req.getRequestURI() + q;
    }

    /**
     * Returns the URL for the "host". Could be useful
     * for building links that go into emails.
     */
    public static URL getHostURL(HttpServletRequest req) {
        try {
            StringBuffer hostString = req.getRequestURL();
            URL fullHostUrl = new URL(hostString.toString());
            URL hostUrl = new URL(fullHostUrl.getProtocol(), fullHostUrl.getHost(), fullHostUrl.getPort(), "");
            return hostUrl;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to build host URL", e);
        }
    }

    /**
     * If the url starts with a slash like '/dashboard', the applications context
     * path will be added to the url: '/yukon/dashboard'. In production this doesn't
     * usually matter since Yukon is the root application but in development it is
     * usually set to '/yukon'.
     */
    public static String createSafeUrl(ServletRequest request, String url) {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (url.startsWith("/")) {
                url = httpRequest.getContextPath() + url;
            }
        }
        /*
         * Replace any multiple consecutive '/' with a single '/'
         * Except immediately after a colon (to allow 'http://')
         */
        url = url.replaceAll("(?<!\\:)/+", "/");
        return url;
    }

    /**
     * Strips what could be harmful redirect information out of a URL.
     * At the very least returning "/".
     * 
     * @param request
     * @param url
     * @return a stripped version of the URL
     */
    public static String createSafeRedirectUrl(final ServletRequest request, final String url) {
        if (url == null) {
            return ServletUtil.createSafeUrl(request, "/");
        }
        
        /*
         * Browsers are permissive in letting the slash going either way (ex: http:// or http:\\)
         * Scheme (http, ftp, etc) is a letter, optionally followed by letters, numbers, '+', '-', or '.' per RFC-3986. 
         * Allowing all character sequences (without a ':') as a scheme here for simplicity.
         */
        String colonSlashSlash = ".+?:[/\\\\]{2}";
        
        Matcher matcher = Pattern.compile(colonSlashSlash + ".+?([/\\\\].*)$").matcher(url);
        boolean matches = matcher.matches();

        Matcher matcher2 = Pattern.compile(colonSlashSlash + ".+$").matcher(url);
        boolean matches2 = !matches && matcher2.matches();

        String safeUrl;
        if (matches) {
            String matchedUrl = matcher.group(1);
            safeUrl = (matchedUrl != null) ? matchedUrl : "/";
        } else if (matches2) {
            safeUrl = "/";
        } else {
            safeUrl = url;
        }

        return ServletUtil.createSafeUrl(request, safeUrl);
    }

    /**
     * Using a <String, String> Map, build a name1=value1&name2=value2 style URL query string.
     * Does NOT encode parameters, assumes parameters will be appropriately encoded already by caller.
     * 
     * @param propertiesMap
     * @param escapeHtml
     * @return queryString
     */
    public static String buildQueryStringFromMap(Map<String, String> encodedParameters, boolean escapeHtml) {

        Set<Entry<String, String>> entrySet = encodedParameters.entrySet();
        String queryString = buildQueryStringFromEntries(entrySet, escapeHtml);

        return queryString;
    }

    /**
     * Using a <String, String> MultiMap, build a name1=value1&name2=value2 style URL query string.
     * Does NOT encode parameters, assumes parameters will be appropriately encoded already by caller.
     * The difference between this method and buildQueryStringFromMap(Map<String,String> encodedParameters)
     * is that the Multimap allows for multiple values per parameter name.
     * 
     * @param propertiesMap
     * @param escapeHtml
     * @return queryString
     */
    public static String buildQueryStringFromMap(Multimap<String, String> encodedParameters, boolean escapeHtml) {

        Collection<Entry<String, String>> entries = encodedParameters.entries();
        String queryString = buildQueryStringFromEntries(entries, escapeHtml);

        return queryString;
    }

    private static String buildQueryStringFromEntries(Iterable<Entry<String, String>> entrySet, boolean escapeHtml) {
        List<String> parameterPairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : entrySet) {
            String thisPair = entry.getKey() + "=" + entry.getValue();
            parameterPairs.add(thisPair);
        }

        String queryString = StringUtils.join(parameterPairs, "&");
        if (escapeHtml) {
            queryString = StringEscapeUtils.escapeHtml4(queryString);
        }
        return queryString;
    }

    /**
     * Using a <String, String> Map, build a name1=value1&name2=value2 style URL query string.
     * Does NOT encode parameters, assumes parameters will be appropriately encoded already by caller.
     * The result queryString is escaped for html.
     * 
     * @param propertiesMap
     * @return queryString
     */
    public static String buildQueryStringFromMap(Map<String, String> encodedParameters) {
        return buildQueryStringFromMap(encodedParameters, true);
    }

    /**
     * Using a <String, String> Map, build a name1=value1&name2=value2 style URL query string
     * using safe URL encoding.
     * 
     * @param propertiesMap
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String buildSafeQueryStringFromMap(Map<String, String> propertiesMap, boolean htmlOutput) {
        try {
            final String urlEncoding = "UTF-8";
            List<String> parameterPairs = new ArrayList<>(propertiesMap.size());
            for (String parameter : propertiesMap.keySet()) {
                String thisPair =
                    URLEncoder.encode(parameter, urlEncoding) + "="
                        + URLEncoder.encode(propertiesMap.get(parameter), urlEncoding);
                parameterPairs.add(thisPair);
            }

            String queryString = StringUtils.join(parameterPairs, htmlOutput ? "&" : "&amp;");

            return queryString;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to build query string", e);
        }
    }

    public static String makeWindowsSafeFileName(String fileName) {

        char[] badChars = { ' ', '\\', '/', ':', '*', '?', '<', '>', '|' };

        for (char c : badChars) {
            fileName = fileName.replace(c, '_');
        }

        return fileName;
    }

    /**
     * Returns the url passed in with the parameter and value appended.
     * Note: this method does not protect against adding a parameter that is
     * already on the query string.
     * 
     * @param url
     * @param parameterMap
     * @return a full path and query string
     */
    public static String addParameters(String url, String parameter, String value) {
        HashMap<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put(parameter, new String[] { value });
        StringBuffer fullURL = new StringBuffer();
        fullURL.append(url);
        if (!url.contains("?")) {
            fullURL.append("?");
        } else {
            fullURL.append("&");
        }

        String queryString = buildQueryStringParameters(parameterMap);
        fullURL.append(queryString);
        return fullURL.toString();
    }

    /**
     * Returns a URL that points to the same page as request, but has newParameter and newValue
     * either appended to the end or replaced. For example, if you requested the page
     * /dir/script.jsp?color=red&flavor=tart
     * and then called
     * tweakRequestURI(request, "flavor", "salty")
     * the String returned would be
     * /dir/script.jsp?color=red&flavor=salty
     * 
     * Alternatively can be used to remove a parameter from a URI.
     * If newValue is null then newParameter will be removed from the
     * generated request string.
     * 
     * @param request the HttpServletRequest object for the current page
     * @param newParameter the name of the parameter to add or replace
     * @param newValue the value of the new parameter
     * @return a full path and query string
     */
    private static String tweakRequestURI(HttpServletRequest request, String newParameter, String newValue) {
        StringBuffer result = new StringBuffer();
        result.append(request.getRequestURI());
        result.append("?");
        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        if (newValue == null) {
            parameterMap.remove(newParameter);
        } else {
            parameterMap.put(newParameter, new String[] { newValue });
        }
        String queryString = buildQueryStringParameters(parameterMap);
        result.append(queryString);
        return result.toString();
    }

    private static String buildQueryStringParameters(Map<String, String[]> parameterMap) {
        String queryString;
        try {
            final String urlEncoding = "UTF-8";
            List<String> parameterPairs = new ArrayList<>(parameterMap.size());
            for (Iterator<String> iter = parameterMap.keySet().iterator(); iter.hasNext();) {
                String thisParameter = iter.next();
                String thisSafeParameter = URLEncoder.encode(thisParameter, urlEncoding);
                String[] theseValues = parameterMap.get(thisParameter);
                for (int i = 0; i < theseValues.length; i++) {
                    String thisPair = thisSafeParameter + "=" + URLEncoder.encode(theseValues[i], urlEncoding);
                    parameterPairs.add(thisPair);
                }
            }
            queryString = StringUtils.join(parameterPairs.iterator(), "&");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return queryString;
    }

    /**
     * Calls tweakRequestURI() with the same parameters, but escapes the result so it
     * can be displayed on a web page (most importantly converting '&' to '&amp;').
     * 
     * @param request the HttpServletRequest object for the current page
     * @param newParameter the name of the parameter to add or replace
     * @param newValue the value of the new parameter
     * @return an HTML escaped full path and query string
     */
    public static String tweakHTMLRequestURI(HttpServletRequest request, String newParameter, String newValue) {
        return StringEscapeUtils.escapeHtml4(tweakRequestURI(request, newParameter, newValue));
    }

    /**
     * Returns a URL that points to the same request URL passed in, but has newParameter and newValue
     * either appended to the end or replaced. For example, if you requested the page
     * /dir/script.jsp?color=red&flavor=tart
     * and then called
     * tweakRequestURI(request, "flavor", "salty")
     * the String returned would be
     * /dir/script.jsp?color=red&flavor=salty
     * 
     * Alternatively can be used to remove a parameter from a URI.
     * If newValue is null then newParameter will be removed from the
     * generated request string.
     * 
     * @param requestUrl url of the request along with query string params
     * @param newParameter the name of the parameter to add or replace
     * @param newValue the value of the new parameter
     * @return a full path with query string
     */
    public static String tweakRequestURL(String requestUrl, String newParameter, String newValue) {
        StringBuffer result = new StringBuffer();
        result.append(getBaseUrl(requestUrl));
        result.append("?");
        Map<String, String[]> parameterMap = getQueryStringParams(requestUrl);
        if (newValue == null) {
            parameterMap.remove(newParameter);
        } else {
            parameterMap.put(newParameter, new String[] { newValue });
        }
        String queryString = buildQueryStringParameters(parameterMap);
        result.append(queryString);
        return result.toString();
    }

    private static String getBaseUrl(String requestUrl) {
        String baseUrl = requestUrl;
        int questionMark = requestUrl.indexOf('?');
        if (questionMark > 0) {
            baseUrl = requestUrl.substring(0, questionMark);
        }
        return baseUrl;
    }

    private static Map<String, String[]> getQueryStringParams(String requestUrl) {
        Map<String, String[]> paramMap = new HashMap<>();
        final String urlDecoding = "UTF-8";

        try {
            int questionMark = requestUrl.indexOf('?');
            if (questionMark > 0) {
                String queryString = requestUrl.substring(questionMark + 1);
                String[] params = StringUtils.split(queryString, "&");
                for (String param : params) {
                    String[] nameValue = StringUtils.split(param, "=");
                    if (nameValue.length == 2) {
                        paramMap.put(nameValue[0], new String[] { URLDecoder.decode(nameValue[1], urlDecoding) });
                    } else {
                        paramMap.put(nameValue[0], new String[] { "" });
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return paramMap;
    }

    /**
     * Prints out the stack trace of the Throwable. HTML characters are escaped.
     * Certain lines will be printed as bold and red. Which lines are determined
     * within this function, but is currently configured to be methods in any
     * com.cannontech package
     * 
     * @param t the Throwable who's stack trace will be printed
     * @param p the PrintWriter on which the stack trace will be printed
     */
    @SuppressWarnings("ucd")
    public static String printNiceHtmlStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter p = new PrintWriter(sw);
        String[] keyWords = { "com.cannontech" };
        synchronized (p) {
            p.write("<pre>");
            while (t != null) {
                Throwable cause = t.getCause();
                if (cause == null) {
                    String escapedCause = StringEscapeUtils.escapeHtml4(t.toString());
                    p.println(escapedCause);
                    StackTraceElement[] trace = t.getStackTrace();
                    for (StackTraceElement element : trace) {
                        String className = element.getClassName();
                        boolean specialLine = false;
                        for (String keyWord : keyWords) {
                            if (className.contains(keyWord)) {
                                specialLine = true;
                                break;
                            }
                        }
                        String thisLine = element.toString();
                        String safeLine = StringEscapeUtils.escapeHtml4(thisLine);
                        if (specialLine) {
                            p.println("<span style=\"font-weight: bold;\">\tat " + safeLine + "</span>");
                        } else {
                            p.println("<span style=\"color: #666;\">\tat " + safeLine + "</span>");
                        }
                    }

                } else {
                    String escapedCause = StringEscapeUtils.escapeHtml4(t.toString());
                    p.println(escapedCause);
                    p.write("\n");
                }
                t = cause;
            }
            p.write("</pre>");
        }
        return sw.toString();
    }

    public static boolean isPathMatch(HttpServletRequest request, List<String> patterns) {
        String pathWithinApplication = urlPathHelper.getPathWithinApplication(request);

        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, pathWithinApplication)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAjaxRequest(ServletRequest req) {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest httpReq = (HttpServletRequest) req;
            String header = httpReq.getHeader("X-Requested-With");
            if (header != null) {
                return header.startsWith("XMLHttpRequest");
            }
        }
        return false;
    }

    public static void deleteAllCookies(final HttpServletRequest request, final HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (final Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            cookie.setValue("");
            String contextPath = request.getContextPath();
            if ("".equals(contextPath)) {
                contextPath = "/";
            }
            cookie.setPath(contextPath);
            response.addCookie(cookie);
        }
    }

    /**
     * Helper method to convert the parameter map return by request.getParameterMap()
     * which is a Map<String, String[]> into a Map<string, String>.
     * Note: Multiple values for a parameter are reduced the value of the last of them.
     * 
     * @param request
     * @return
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {

        Map<String, String[]> parameterMapWithArrays = request.getParameterMap();

        Map<String, String> parameterMap = new HashMap<>();

        for (String pKey : parameterMapWithArrays.keySet()) {
            String[] vals = parameterMapWithArrays.get(pKey);
            for (int i = 0; i < vals.length; i++) {
                parameterMap.put(pKey, vals[i]);
            }
        }

        return parameterMap;
    }

    public static <T> Map<T, Boolean> convertSetToMap(Set<T> allExistingAttributes) {
        Map<T, Boolean> existingMap = new HashMap<>();

        // convert to a map of true's because JSP EL can use this to check "contains"
        for (T attribute : allExistingAttributes) {
            existingMap.put(attribute, Boolean.TRUE);
        }

        return existingMap;
    }

    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("could not encode string", e);
        }
    }

    public static Map<String, String> urlEncode(Map<String, String> parameters) {

        Map<String, String> encoded = new HashMap<>();

        for (String name : parameters.keySet()) {
            String encodedName = urlEncode(name);
            String encodedValue = urlEncode(parameters.get(name));
            encoded.put(encodedName, encodedValue);
        }

        return encoded;
    }

    /**
     * Helper method to put all String parameters with a given prefix into a map
     */
    public static Map<String, String> getStringParameters(HttpServletRequest request, String prefix) {

        Map<String, String> returnMap = new HashMap<>();

        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {

            if (key.startsWith(prefix)) {
                String paramKey = key.substring(prefix.length());
                String[] value = parameterMap.get(key);

                returnMap.put(paramKey, value[0]);
            }

        }

        return returnMap;
    }

    /**
     * Helper method to convert a string of Integers into a list
     * 
     * @param integerStringList - String of comma separated paoIds
     * @return Integer list of paoIds
     */
    public static List<Integer> getIntegerListFromString(String integerStringList) {

        List<Integer> idList = new ArrayList<>();

        if (integerStringList != null && integerStringList.length() > 0) {
            String[] ids = integerStringList.split(",");
            for (String id : ids) {
                idList.add(Integer.valueOf(id.trim()));
            }
        }
        return idList;

    }

    public static <T extends Enum<T>> EnumSet<T> convertStringArrayToEnums(String[] enumStrings, Class<T> enumClass) {
        EnumSet<T> result = EnumSet.noneOf(enumClass);
        if (enumStrings != null) {
            for (String enumStr : enumStrings) {
                T value = Enum.valueOf(enumClass, enumStr);
                result.add(value);
            }
        }
        return result;
    }

    public static String getDefaultYukonExternalUrl(HttpServletRequest request) {
        String defaultYukonExternalUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            defaultYukonExternalUrl += ":" + request.getServerPort();
        }
        return defaultYukonExternalUrl;
    }
    
    public static String getUriHostWithoutPrefix(String uriHost) {
        return uriHost.startsWith("www.") ? uriHost.substring(4) : uriHost;
    }
}