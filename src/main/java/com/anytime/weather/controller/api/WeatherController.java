package com.anytime.weather.controller.api;

import com.anytime.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This controller class handle request and response for Weather.
 *
 */

@RestController()
@RequestMapping("/anytimeWeatherService")
public class WeatherController {

	@Autowired
	private WeatherService weatherService;

	@RequestMapping(value = "/getAccurateWeather", method = RequestMethod.GET)
	public ResponseEntity getAccurateWeather(@RequestParam String lat, @RequestParam String longi, HttpServletRequest request,
                            HttpServletResponse response) {
		return ResponseEntity.status(HttpStatus.OK).body(weatherService.getAccurateWeather(lat, longi));
	}

	@RequestMapping(value = "/notifyMe", method = RequestMethod.GET)
	public ResponseEntity notifyMe(@RequestParam String lat, @RequestParam String longi,@RequestParam double expectedTemp, HttpServletRequest request,
											 HttpServletResponse response) {
		weatherService.notifyMe(lat,longi,expectedTemp,request);
		return ResponseEntity.status(HttpStatus.OK).body("You will recived mail/sms from us when your expected temp match, Thnks");
	}


}