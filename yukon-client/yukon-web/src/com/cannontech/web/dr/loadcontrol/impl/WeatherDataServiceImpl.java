package com.cannontech.web.dr.loadcontrol.impl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.xml.YukonXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.web.dr.loadcontrol.WeatherDataService;
import com.cannontech.web.dr.loadcontrol.WeatherObservation;

public class WeatherDataServiceImpl implements WeatherDataService {
	
	@Autowired PaoPersistenceService paoPersistenceService;
	@Autowired AttributeService attributeService;
	
	/* 
	 * Web service provided by the National Oceanic and Atmospheric Administration
	 * This url requests the NOAA Weather Station Index
	 */
	String weatherStationIndexUrl = "http://w1.weather.gov/xml/current_obs/index.xml";

	/*
	 * START
	 * 
	 * Snippet of code to insert a new WeatherLocation YukonPAObject into the local Yukon db.
	 * WILL NOT STAY IN THIS CLASS, only for documentation purposes at this point
	 * 
	 * Comments:
	 * 
	 * This snippet will fail if a YukonPAObject with the same name already exists in the db.
	 * 
	 * We may not necessarily need to retrieve the entire YukonPAObject every time we want
	 * to update the points.  A PAObjectId of the corresponding WeatherLocation pao that is getting
	 * updated will suffice.  However, retrieving a specific PAObjectId could become a problem if 
	 * multiple WeatherLocations exist in the db.
	 * 
	 * To update the points right now, we can just query the YukonPAObject table for an object
	 * of Type = 'WEATHER LOCATION' and grab the corresponding PAObjectId (and use PointDao to 
	 * get the points).  Of course, this only works if there is a single WeatherLocation Pao 
	 * in the YukonPAObject table.
	 * 
	 */
	
	/* Create new Weather Location Pao (specify description and name) */
//	CompleteYukonPao weatherPao = new CompleteYukonPao();
//	weatherPao.setDescription("test weather location");
//	weatherPao.setPaoName("New Weather Location Object");

	/* Writing the new Pao to the database */
//	paoPersistenceService.createPaoWithDefaultPoints(weatherPao, PaoType.WEATHER_LOCATION);

	/* retrieve the PAObject from the database */
//	CompleteYukonPao weatherLocation = paoPersistenceService.retreivePao(new PaoIdentifier(???, PaoType.WEATHER_LOCATION), CompleteYukonPao.class);

	/* Temperature and humidity attributes not yet implemented, but this is how the points would be retrieved */
//	PaoIdentifier paoIdentifier = new PaoIdentifier(???, PaoType.WEATHER_LOCATION);
//	attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.TEMPERATURE); 
	
	/*
	 * END
	 */
	
	public URL getStationUrlFromLatLong(Double latitude, Double longitude) {
		Pair<Double, Double> specifiedLatLon = new Pair<Double, Double>(latitude, longitude);
		HttpURLConnection httpConnection = null;
		Document response = null;
		
		try {
			//request and read in index.xml from NOAA Weather Station Index
			URL url = new URL(weatherStationIndexUrl);
			httpConnection = (HttpURLConnection) url.openConnection();

			//parse xml response
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			response = documentBuilder.parse(httpConnection.getInputStream());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		
		Element rootNode = response.getDocumentElement();
		YukonXPathTemplate template = YukonXml.getXPathTemplateForNode(rootNode);

		// info to keep for determining closest station
		Node stationNode = null;
		String stationUrl = "";
		YukonXPathTemplate stationTemplate = null;
		Double closestDistance = null;
		Double newDistance = null;
		Pair<Double, Double> stationLatLong = new Pair<Double, Double>(null, null);

		while ((stationNode = template.evaluateAsNode("station")) != null) {
			if (stationUrl == null) {
				// should only happen first iteration
				stationTemplate = YukonXml.getXPathTemplateForNode(stationNode);
				stationUrl = stationTemplate.evaluateAsString("xml_url");
				stationLatLong.setFirst(stationTemplate.evaluateAsDouble("latitude"));
				stationLatLong.setSecond(stationTemplate.evaluateAsDouble("longitude"));
				closestDistance = distanceBetweenLatLongPairs(specifiedLatLon, stationLatLong);
				rootNode.removeChild(stationNode);
				continue;
			}

			// get new template on new station
			stationTemplate = YukonXml.getXPathTemplateForNode(stationNode);

			// get new lat long of station
			stationLatLong.setFirst(stationTemplate.evaluateAsDouble("latitude"));
			stationLatLong.setSecond(stationTemplate.evaluateAsDouble("longitude"));

			// determine distance from given lat/long
			newDistance = distanceBetweenLatLongPairs(specifiedLatLon, stationLatLong);
			
			System.out.println(newDistance + " -- " + closestDistance);
			
			if (newDistance < closestDistance) {
				// update closest station information
				closestDistance = newDistance;
				stationUrl = stationTemplate.evaluateAsString("xml_url");
			}
			
			//remove processed station
			rootNode.removeChild(stationNode);
		}

		//return url to closest weather station XML feed
		URL resultingUrl = null;
		try {
			resultingUrl = new URL(stationUrl);
		} catch (MalformedURLException ex) {}
		return resultingUrl;
	}
	
	public WeatherObservation getCurrentWeatherObservation(URL stationUrl) {
    	HttpURLConnection httpConnection = null;
    	WeatherObservation fullObservation = new WeatherObservation();
    	
		try {
			//request current weather observations
			httpConnection = (HttpURLConnection) stationUrl.openConnection();
			
			//parse xml response
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputStream input = httpConnection.getInputStream();
			Document response = documentBuilder.parse(input);
			
			//retrieve specific values from response
			Element rootNode = response.getDocumentElement();
			YukonXPathTemplate template = YukonXml.getXPathTemplateForNode(rootNode);
			String stationId = template.evaluateAsString("station_id");
			String locationDesc = template.evaluateAsString("location");
			Double latitude = template.evaluateAsDouble("latitude");
			Double longitude = template.evaluateAsDouble("longitude");
			String observationTime = template.evaluateAsString("observation_time_rfc822");
			String conditionDesc = template.evaluateAsString("weather");
			Double tempInF = template.evaluateAsDouble("temp_f");
			Double tempInC = template.evaluateAsDouble("temp_c");
			Double relHum = template.evaluateAsDouble("relative_humidity");
			String windDirection = template.evaluateAsString("wind_dir");
			Double windDegrees = template.evaluateAsDouble("wind_degrees");
			Double windSpeedMPH = template.evaluateAsDouble("wind_mph");
			Double windSpeedKT = template.evaluateAsDouble("wind_kt");
			Double dewPointF = template.evaluateAsDouble("dewpoint_f");
			Double dewPointC = template.evaluateAsDouble("dewpoint_c");
			Double heatIndexF = template.evaluateAsDouble("heat_index_f");
			Double heatIndexC = template.evaluateAsDouble("heat_index_c");
			Double visibility = template.evaluateAsDouble("visibility_mi");
			
			//enter values into new WeatherObservation object
			fullObservation = new WeatherObservation(stationId, locationDesc, latitude, longitude,
					observationTime, conditionDesc, tempInF, tempInC, relHum, windDirection, windDegrees,
					windSpeedMPH, windSpeedKT, dewPointF, dewPointC, heatIndexF, heatIndexC, visibility);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		
		return fullObservation;
	}
	
	public Double distanceBetweenLatLongPairs(Pair<Double, Double> clientPair, Pair<Double, Double> newPair) {
		final Double radiusOfEarth = 6371.0;  // in kilometers
		
		Double clientLatRad = Math.toRadians(clientPair.getFirst());
		Double clientLongRad = Math.toRadians(clientPair.getSecond());
		Double newLatRad = Math.toRadians(newPair.getFirst());
		Double newLongRad = Math.toRadians(newPair.getSecond());

		Double x = (clientLongRad - newLongRad) * Math.cos((newLatRad + clientLatRad) / 2);
		Double y = (clientLatRad - newLatRad);
		Double distance = (Math.pow(x, 2.0) + Math.pow(y, 2.0)) * radiusOfEarth;

		return distance;
	}
	
}
