package com.cannontech.common.weather.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.common.util.xml.YukonXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.common.weather.GeographicCoordinate;
import com.cannontech.common.weather.NoaaWeatherDataService;
import com.cannontech.common.weather.NoaaWeatherDataServiceException;
import com.cannontech.common.weather.WeatherObservation;
import com.cannontech.common.weather.WeatherStation;
import com.cannontech.system.dao.GlobalSettingDao;

public class NoaaWeatherDataServiceImpl implements NoaaWeatherDataService {

    private Logger log = YukonLogManager.getLogger(NoaaWeatherDataServiceImpl.class);
    /** set to last time weatherStationMap was reloaded from NOAA webservice **/
    private Instant lastRefresh;
    private String noaaUrl = "http://w1.weather.gov/xml/current_obs/index.xml";
    private Map<String, WeatherStation> weatherStationMap = new HashMap<>();

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;

    /**
     * Tries to query the NOAA web service. If any error occurs this method will throw.
     * 
     * @throws NoaaWeatherDataServiceException
     */
    private Document queryForWeatherStations() throws NoaaWeatherDataServiceException {

        Document response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHTTPClient();
            String stationIndexUrl = configurationSource.getString("NOAA_WEATHER_STATION_INDEX_URL", noaaUrl);
            HttpGet method = new HttpGet(stationIndexUrl);
            HttpResponse httpResponse = httpClient.execute(method);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                throw new NoaaWeatherDataServiceException("Failed to retrieve weather station index"
                    + " from NOAA webservice. HTTP Status: " + statusCode + " for url: " + stationIndexUrl
                    + ". This may be caused by invalid Proxy settings (Http Proxy Yukon configuration setting)");
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            response = documentBuilder.parse(httpResponse.getEntity().getContent());
            method.releaseConnection();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new NoaaWeatherDataServiceException(
                "Unable to populate weather station map from NOAA HTTP web service.", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException io) {
                throw new NoaaWeatherDataServiceException("Unable to close the http connection.", io);
            }
        }
        return response;
    }

    @Override
    public Map<String, WeatherStation> getAllWeatherStations() {
        // Its rare for NOAA to change station data so reloading the map every 30 days should suffice
        if (!weatherStationMap.isEmpty() && Instant.now().isBefore(lastRefresh.plus(Duration.standardDays(30)))) {
            log.debug("Returning cached weatherStationMap.");
            return weatherStationMap;
        }

        Document response;
        try {
            response = queryForWeatherStations();
        } catch (NoaaWeatherDataServiceException e) {
            log.warn("Could not retrieve updated weather stations.", e);
            // returned map is either empty, or greater than 1 month old (which might still be up to date)
            return weatherStationMap;
        }

        // further parse xml response to a list of stations
        Element rootNode = response.getDocumentElement();
        YukonXPathTemplate template = YukonXml.getXPathTemplateForNode(rootNode);
        List<Node> stationList = template.evaluateAsNodeList("station");
        Map<String, WeatherStation> newWeatherStationMap = new HashMap<>();

        // iterate through stations and build map (key:stationId ->value:WeatherStation)
        for (Node singleStation : stationList) {
            /*
             * The actual station index xml can be found viewed at: (as of
             * 08/29/13) http://w1.weather.gov/xml/current_obs/index.xml
             */
            String stationId = null;
            String stationDesc = null;
            Double latitude = null;
            Double longitude = null;
            String currentObsUrl = null;

            NodeList stationNodeList = singleStation.getChildNodes();
            for (int j = 0; j < stationNodeList.getLength(); j++) {
                Node stationNode = stationNodeList.item(j);

                if (stationNode.getNodeName().equals("station_id")) {
                    stationId = stationNode.getTextContent();
                } else if (stationNode.getNodeName().equals("station_name")) {
                    stationDesc = stationNode.getTextContent();
                } else if (stationNode.getNodeName().equals("latitude")) {
                    latitude = Double.parseDouble(stationNode.getTextContent());
                } else if (stationNode.getNodeName().equals("longitude")) {
                    longitude = Double.parseDouble(stationNode.getTextContent());
                } else if (stationNode.getNodeName().equals("xml_url")) {
                    currentObsUrl = stationNode.getTextContent();
                }
            }

            WeatherStation currentStation =
                new WeatherStation(stationId, stationDesc, 
                    new GeographicCoordinate(latitude, longitude), currentObsUrl);

            newWeatherStationMap.put(stationId, currentStation);
        }

        lastRefresh = new Instant();

        weatherStationMap = newWeatherStationMap;
        return weatherStationMap;
    }

    @Override
    public List<WeatherStation> getWeatherStationsByDistance(final GeographicCoordinate requestedCoordinate) {
        Map<String, WeatherStation> allStations = getAllWeatherStations();
        List<WeatherStation> weatherStations = new ArrayList<>(allStations.values());

        Collections.sort(weatherStations, new Comparator<WeatherStation>() {
            @Override
            public int compare(WeatherStation ws1, WeatherStation ws2) {
                double dist1 = ws1.getGeoCoordinate().distanceTo(requestedCoordinate);
                double dist2 = ws2.getGeoCoordinate().distanceTo(requestedCoordinate);
                return Double.compare(dist1, dist2);
            }
        });
        return weatherStations;
    }

    @Override
    public WeatherObservation getCurrentWeatherObservation(WeatherStation weatherStation)
            throws NoaaWeatherDataServiceException {
        WeatherObservation fullObservation;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = buildHTTPClient();
            HttpGet method = new HttpGet(weatherStation.getNoaaUrl());
            HttpResponse httpResponse = httpClient.execute(method);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new NoaaWeatherDataServiceException("Failed to retrieve current weather"
                    + " observation from NOAA webservice. HTTP Status: " + statusCode + " for url: "
                    + weatherStation.getNoaaUrl() + ". This may be caused by invalid Proxy settings (Http Proxy " +
                    		"Yukon configuration setting)");
            }

            // parse xml response
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document response = documentBuilder.parse(httpResponse.getEntity().getContent());

            // retrieve specific values from response
            Element rootNode = response.getDocumentElement();
            YukonXPathTemplate template = YukonXml.getXPathTemplateForNode(rootNode);
            // These are not guaranteed to exist.
            // Need to check for null when accessing these from weather observation
            Double tempInF = template.evaluateAsDouble("temp_f", false);
            if (tempInF == null) {
                log.info("NOAA Weather Station " + weatherStation.getStationId()
                    + " is missing temperature data and will not be included in requested weather observation.");
            }
            Double relHum = template.evaluateAsDouble("relative_humidity" , false);
            if (relHum == null) {
                log.info("NOAA Weather Station " + weatherStation.getStationId()
                    + " is missing humidity data and will not be included in requested weather observation.");
            }

            Instant timestamp = Instant.now();
            String ts = template.evaluateAsString("observation_time_rfc822");
            SimpleDateFormat inputDF = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            try {
                timestamp = new Instant(inputDF.parse(ts));
            } catch (ParseException e) {
                log.error("Unable to use timestamp provided by NOAA weather observation. Using now time.", e);
            }

            fullObservation = new WeatherObservation(weatherStation.getStationId(), tempInF, relHum, timestamp);
            method.releaseConnection();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new NoaaWeatherDataServiceException("Unable to get current weather observation", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException io) {
                throw new NoaaWeatherDataServiceException("Unable to close the HTTP connection.", io);
            }
        }

        return fullObservation;
    }

    /**
     * If HTTP_PROXY global setting is set to ip:port, this method will set this value on the HttpClient and
     * return the same,
     * otherwise this method will just return an http client object.
     */
    private CloseableHttpClient buildHTTPClient() {
        CloseableHttpClient client = null;
        Optional<YukonHttpProxy> httpProxy = YukonHttpProxy.fromGlobalSetting(globalSettingDao);
        if (httpProxy.isPresent()) {
            HttpHost proxy = new HttpHost(httpProxy.get().getHost(), httpProxy.get().getPort());
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            client = HttpClients.custom().setRoutePlanner(routePlanner).build();
        } else {
            client = HttpClients.custom().build();
        }
        return client;
    }

}
