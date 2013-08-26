package com.cannontech.web.dr.loadcontrol;

import java.net.URL;

import com.cannontech.common.util.Pair;

public interface WeatherDataService {

	/**
	 * Requests the NOAA weather station index.xml (http://w1.weather.gov/xml/current_obs/index.xml)
	 * Scans through the entire list to find the closest weather station to the given lat/lon
	 * @param latitude
	 * @param longitude
	 * @return - Url corresponding to the closest weather station's hourly weather observation XML feed
	 */
	public URL getStationUrlFromLatLong(Double latitude, Double longitude);
	
	/**
	 * Requests the hourly weather observations from the given NOAA weather station XML feed. 
	 * @param stationUrl
	 * @return - NOTE: Completeness of observations data may vary from reading to reading.
	 */
	public WeatherObservation getCurrentWeatherObservation(URL stationUrl);
	
	/**
	 * Uses equirectangular approximation to estimate distance between lat/long pairs
	 * @param clientPair
	 * @param newPair
	 * @return - Estimated distance in km
	 */
	public Double distanceBetweenLatLongPairs(Pair<Double, Double> clientPair, Pair<Double, Double> newPair);
	
}
