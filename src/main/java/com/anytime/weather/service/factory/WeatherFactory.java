package com.anytime.weather.service.factory;

import com.anytime.weather.service.provider.Darksky;
import com.anytime.weather.service.provider.Weatherbit;

/**
 * Use getWeatherDataParser method to get object of type Weather 
 * 
 */
public class WeatherFactory {
	public static Weather getWeatherDataParser(WeatherApiTypes model) {
		Weather weather = null;
		switch (model) {
		case DARKSKY:
			weather = new Darksky();
			break;

		case WEATHERBIT:
			weather = new Weatherbit();
			break;

		default:
			break;
		}
		return weather;
	}
}
