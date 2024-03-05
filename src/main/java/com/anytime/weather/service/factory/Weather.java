package com.anytime.weather.service.factory;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * abstract class Weather is the parent of all classes.
 * use abstract method parseData to parse response.
 * 
 */
public abstract class Weather {
	public abstract Map<String, Object> parseData(ResponseEntity<Object> response);
}
