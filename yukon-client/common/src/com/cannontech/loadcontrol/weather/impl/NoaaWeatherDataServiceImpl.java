package com.cannontech.loadcontrol.weather.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
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
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.xml.YukonXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.db.pao.dao.StaticPaoInfoDao;
import com.cannontech.loadcontrol.weather.GeographicCoordinate;
import com.cannontech.loadcontrol.weather.NoaaWeatherDataService;
import com.cannontech.loadcontrol.weather.NoaaWeatherDataServiceException;
import com.cannontech.loadcontrol.weather.WeatherObservation;
import com.cannontech.loadcontrol.weather.WeatherStation;

public class NoaaWeatherDataServiceImpl implements NoaaWeatherDataService {

    private Logger log = YukonLogManager.getLogger(NoaaWeatherDataServiceImpl.class);
    /** set to last time weatherStationMap was reloaded from NOAA webservice **/
    private Instant lastRefresh;
    private String noaaUrl = "http://w1.weather.gov/xml/current_obs/index.xml";
    private Map<String, WeatherStation> weatherStationMap = new HashMap<>();

    @Autowired private PaoDao paoDao;
    @Autowired private StaticPaoInfoDao staticPaoInfoDao;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private AttributeService attributeService;
    @Autowired private ConfigurationSource configurationSource;

    /**
     * Tries to query the NOAA web service. If any error occurs this method will throw.
     * @throws NoaaWeatherDataServiceException 
     */
    private Document queryForWeatherStations() throws NoaaWeatherDataServiceException {

        HttpURLConnection httpConnection = null;
        Document response = null;
        try {
            HttpClient client = new HttpClient();
            String stationIndexUrl
                = configurationSource.getString("NOAA_WEATHER_STATION_INDEX_URL", noaaUrl);

            HttpMethod method = new GetMethod(stationIndexUrl);

            String proxyHost = configurationSource.getString("HTTP_PROXY_HOST");
            int proxyPort = configurationSource.getInteger("HTTP_PROXY_PORT", 80);

            if (proxyHost != null) {
                client.getHostConfiguration().setProxy(proxyHost, proxyPort);
            }

            int status = client.executeMethod(method);
            if (status != 200) {
                throw new NoaaWeatherDataServiceException("Failed to retrieve current weather"
                            + " observation from NOAA webservice. HTTP Status: " + status
                            + " for url: " + stationIndexUrl);
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            response = documentBuilder.parse(method.getResponseBodyAsStream());
            method.releaseConnection();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new NoaaWeatherDataServiceException("Unable to populate weather map.", e);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return response;
    }

    @Override
    public Map<String, WeatherStation> getAllWeatherStations() {
        // Its rare for NOAA to change station data so reloading the map every 30 days should suffice
        if (!weatherStationMap.isEmpty()
                && Instant.now().isAfter(lastRefresh.plus(Duration.standardDays(30)))) {
            return weatherStationMap;
        }

        Document response;
        try {
            response = queryForWeatherStations();
        } catch (NoaaWeatherDataServiceException e) {
            log.warn("Could not retrieve weather stations.", e);
            // returned map is either empty, or greater than 1 month old (which might still be up to date)
            return weatherStationMap;
        }

        // further parse xml response to a list of stations
        Element rootNode = response.getDocumentElement();
        YukonXPathTemplate template = YukonXml.getXPathTemplateForNode(rootNode);
        List<Node> stationList = template.evaluateAsNodeList("station");
        Map<String, WeatherStation> newWeatherStationMap = new HashMap<String, WeatherStation>();

        // iterate through stations and build map (key:stationId -> value:WeatherStation)
        for (Node singleStation : stationList) {
            /*
             * The actual station index xml can be found viewed at: (as of 08/29/13)
             * http://w1.weather.gov/xml/current_obs/index.xml
             */
            String stationId = null;
            String stationDesc = null;
            Double latitude = null;
            Double longitude = null;
            String currentObsUrl = null;

            NodeList stationNodeList = singleStation.getChildNodes();
            for (int j = 0; j < stationNodeList.getLength(); j++) {
                Node stationNode = stationNodeList.item(j);

                if (stationNode.getNodeName() == "station_id") {
                    stationId = stationNode.getTextContent();
                } else if (stationNode.getNodeName() == "station_name") {
                    stationDesc = stationNode.getTextContent();
                } else if (stationNode.getNodeName() == "latitude") {
                    latitude = Double.parseDouble(stationNode.getTextContent());
                } else if (stationNode.getNodeName() == "longitude") {
                    longitude = Double.parseDouble(stationNode.getTextContent());
                } else if (stationNode.getNodeName() == "xml_url") {
                    currentObsUrl = stationNode.getTextContent();
                }
            }

            WeatherStation currentStation
                = new WeatherStation(stationId, stationDesc,
                                     new GeographicCoordinate(latitude, longitude),
                                     currentObsUrl);

            newWeatherStationMap.put(stationId, currentStation);
        }

        lastRefresh = new Instant();
        weatherStationMap = newWeatherStationMap;
        return weatherStationMap;
    }

    @Override
    public WeatherStation getClosestWeatherStation(GeographicCoordinate coordinate) {
        return getWeatherStationsByDistance(coordinate).get(0);
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
    public WeatherObservation getCurrentWeatherObservation(WeatherStation weatherStation) throws NoaaWeatherDataServiceException {
        HttpURLConnection httpConnection = null;
        WeatherObservation fullObservation;

        try {
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(weatherStation.getNoaaUrl());

            String proxyHost = configurationSource.getString("HTTP_PROXY_HOST");
            String proxyPort = configurationSource.getString("HTTP_PROXY_PORT", "80");

            if (proxyHost != null) {
                client.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
            }

            int status = client.executeMethod(method);
            if (status != 200) {
                throw new NoaaWeatherDataServiceException("Failed to retrieve current weather"
                            + " observation from NOAA webservice. HTTP Status: " + status
                            + " for url: " + weatherStation.getNoaaUrl());
            }

            //parse xml response
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document response = documentBuilder.parse(method.getResponseBodyAsStream());

            //retrieve specific values from response
            Element rootNode = response.getDocumentElement();
            YukonXPathTemplate template = YukonXml.getXPathTemplateForNode(rootNode);
            // These are not guaranteed to exist. 
            // Need to check for null when accessing these from weather observation
            Double tempInF = template.evaluateAsDouble("temp_f");
            Double relHum = template.evaluateAsDouble("relative_humidity");
//          Instant timestamp = template.evaluateAsInstant("observation_time_rfc822");
            Instant timestamp = Instant.now();

            fullObservation = new WeatherObservation(weatherStation.getStationId(),
                                  tempInF, timestamp,
                                  relHum, timestamp);
            method.releaseConnection();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new NoaaWeatherDataServiceException("Unable to get current weather observation", e);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

        return fullObservation;
    }
}
