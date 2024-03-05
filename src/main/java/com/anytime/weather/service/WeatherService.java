package com.anytime.weather.service;

import com.anytime.weather.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * WeatherService implements an application to define method which have business
 * logic and communicate with controller and weather data.
 * 
 */
public interface WeatherService {

	Map<String, Object> getAccurateWeather(String lat, String longi);

	List<User> getListOfCondition();

	void notifyMe(String lat, String longi, double expectedTemp, HttpServletRequest request);

	Map<String, Object> darksky(String lat, String longi);

	Map<String, Object> weatherbit(String lat, String longi);

}