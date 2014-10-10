package com.cannontech.web.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.weather.GeographicCoordinate;
import com.cannontech.common.weather.NoaaWeatherDataService;
import com.cannontech.common.weather.WeatherDataService;
import com.cannontech.common.weather.WeatherLocation;
import com.cannontech.common.weather.WeatherStation;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.loadcontrol.WeatherLocationBean;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class WeatherController {

    @Autowired private WeatherDataService weatherDataService;
    @Autowired private NoaaWeatherDataService noaaWeatherDataService;
    @Autowired private FormulaDao formulaDao;

    private static final String baseKey = "yukon.web.modules.adminSetup.config.weather.";
    private static final int numWeatherStationsToReturn = 5;

    @RequestMapping("/config/weather")
    public String weather(ModelMap model) {
        model.addAttribute("weatherLocationBean", new WeatherLocationBean());
        return "config/weather.jsp";
    }

    @RequestMapping("/config/weatherLocationsTable")
    public String weatherLocationsTable(ModelMap model) {

        List<WeatherLocation> weatherLocations = weatherDataService.getAllWeatherLocations();
        Map<String, WeatherStation> weatherStations = noaaWeatherDataService.getAllWeatherStations();

        model.addAttribute("weatherStations", weatherStations);
        model.addAttribute("weatherLocations", weatherLocations);
        model.addAttribute("weatherLocationBean", new WeatherLocationBean());

        return "config/_weatherLocationsTable.jsp";
    }

    @RequestMapping("/config/removeWeatherLocation")
    public String removeWeatherLocation(FlashScope flashScope, int paoId) {
        boolean isUsed = formulaDao.hasFormulaInputPoints(paoId);
        if (isUsed) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "errors.cannotRemoveWeatherLoc"));
        } else {
            weatherDataService.deleteWeatherLocation(paoId);
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "success.removedWeatherLocation"));
        }
        return "redirect:weather";
    }

    @RequestMapping("/config/saveWeatherLocation")
    public String saveWeatherLocation(ModelMap model, WeatherLocationBean weatherLocationBean, BindingResult bindingResult) {

        validateLatLon(weatherLocationBean.getLatitude(), weatherLocationBean.getLongitude(), bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("dialogState", "searching");
            return "config/_weatherStations.jsp";
        }

        double lat = Double.parseDouble(weatherLocationBean.getLatitude());
        double lon = Double.parseDouble(weatherLocationBean.getLongitude());
        GeographicCoordinate requestedCoordinate = new GeographicCoordinate(lat, lon);

        if (StringUtils.isBlank(weatherLocationBean.getName())) {
            bindingResult.rejectValue("name", baseKey + "errors.blankName");
        } else if (!weatherDataService.isNameAvailableForWeatherLocation(weatherLocationBean.getName())){
            bindingResult.rejectValue("name", baseKey + "errors.nameAlreadyUsed");
        }
        if (StringUtils.isBlank(weatherLocationBean.getStationId())) {
            bindingResult.rejectValue("stationId", baseKey + "errors.noStationId");
        }

        if (bindingResult.hasErrors()) {
            int numStations = addWeatherStationsToModel(model, requestedCoordinate);
            if (numStations < numWeatherStationsToReturn) {
                model.addAttribute("dialogState", "searching");
            } else {
                model.addAttribute("dialogState", "saving");
            }

            model.addAttribute("numberWeatherStations", numStations);
            return "config/_weatherStations.jsp";
        }


        WeatherLocation weatherLocation =
            weatherDataService.createWeatherLocation(weatherLocationBean.getName(),
                                                     weatherLocationBean.getStationId(),
                                                     requestedCoordinate);
        weatherDataService.updatePointsForNewWeatherLocation(weatherLocation);
        model.addAttribute("dialogState", "done");
        return "config/_weatherStations.jsp";
    }

    @RequestMapping("/config/findCloseStations")
    public String findCloseStations(ModelMap model, WeatherLocationBean wheatherLocationBean, BindingResult bindingResult) {

        validateLatLon(wheatherLocationBean.getLatitude(), wheatherLocationBean.getLongitude(), bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("dialogState", "searching");
            return "config/_weatherStations.jsp";
        }

        double lat = Double.parseDouble(wheatherLocationBean.getLatitude());
        double lon = Double.parseDouble(wheatherLocationBean.getLongitude());

        GeographicCoordinate requestedCoordinate = new GeographicCoordinate(lat, lon);

        int numStations = addWeatherStationsToModel(model, requestedCoordinate);
        if (numStations < numWeatherStationsToReturn) {
            model.addAttribute("dialogState", "searching");
        } else {
            model.addAttribute("dialogState", "saving");
        }

        model.addAttribute("numberWeatherStations", numStations);
        model.addAttribute("requestedLat", lat);
        model.addAttribute("requestedLon", lon);

        return "config/_weatherStations.jsp";
    }
    
    /**
     * Adds the weather stations found to the model, returns the number of weather stations
     */
    private int addWeatherStationsToModel(ModelMap model, GeographicCoordinate requestedCoordinate) {
        List<WeatherStation> weatherStationResults = noaaWeatherDataService.getWeatherStationsByDistance(requestedCoordinate);

        if (weatherStationResults.size() >= numWeatherStationsToReturn){
            weatherStationResults = weatherStationResults.subList(0, numWeatherStationsToReturn);
        }

        Map<String, Integer> distanceToStation = new HashMap<>();
        for (WeatherStation station : weatherStationResults) {
            int distance = (int) station.getGeoCoordinate().distanceTo(requestedCoordinate);
            distanceToStation.put(station.getStationId(), distance);
        }

        model.addAttribute("weatherStationResults", weatherStationResults);
        model.addAttribute("distanceToStation", distanceToStation);

        return weatherStationResults.size();
    }

    private void validateLatLon(String lat, String lon, BindingResult bindingResult) {
        try {
            Double.parseDouble(lat);
        } catch (NullPointerException | NumberFormatException e) {
            bindingResult.rejectValue("latitude", baseKey + "errors.invalidLatitude");
        }

        try {
            Double.parseDouble(lon);
        } catch (NullPointerException | NumberFormatException e) {
            bindingResult.rejectValue("longitude", baseKey + "errors.invalidLongitude");
        }
    }
}