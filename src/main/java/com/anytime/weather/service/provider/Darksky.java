package com.anytime.weather.service.provider;

import com.anytime.weather.service.factory.Weather;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 *  Concrete class Darksky that extends Weather abstract class have parseData 
 *  function which parse our response and returns it.
 * 
 */
public class Darksky extends Weather {
	@Override
	public Map<String, Object> parseData(ResponseEntity<Object> response) {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("darksky", response.getBody());
		return json;
	}
}
