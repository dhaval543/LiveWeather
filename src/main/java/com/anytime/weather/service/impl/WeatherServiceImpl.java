package com.anytime.weather.service.impl;

import com.anytime.weather.Utils.CacheManager;
import com.anytime.weather.exception.NotifyType;
import com.anytime.weather.model.User;
import com.anytime.weather.service.WeatherService;
import com.anytime.weather.service.factory.Weather;
import com.anytime.weather.service.factory.WeatherApiTypes;
import com.anytime.weather.service.factory.WeatherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This service will be used to implement fetch weather.
 * 
 */

@Service("WeatherService")
public class WeatherServiceImpl implements WeatherService {

	// Due to sometime time unavailablity I am not using Callable,Thread here to parellel call
	// Darksky and Weatherbit API

	static Logger log = Logger.getLogger(WeatherServiceImpl.class.getName());

	private  List<User> listOfCondition = new ArrayList<>();


	@Autowired
	public CacheManager cacheManager;

	private String darkskyKey = System.getenv("DARKSKY_API_KEY_1");
	private String weatherbitKey = System.getenv("WEATHERBIT_API_KEY_1");
	int owmKeyCount = 1, dsKeyCount = 1, wuKeyCount = 1, wbKeyCount = 1;
	private HashMap<String,Object> wData = new HashMap<>();

	@Override
	public Map<String, Object> getAccurateWeather(String lat, String longi) {
		Map<String, Object> darkSky = darksky(lat, longi);
		Map<String, Object> weatherBit = weatherbit(lat, longi);
		Map<String, Object> accurateResult = new HashMap<>();
		LocalDate today = LocalDate.now();
		double tempDS = 0, tempWB = 0;


		if (darkSky.containsKey(lat+longi)) {
			ArrayList getList1 = (ArrayList) darkSky.get(lat+longi);
			Map<String, Object> getList2 = (Map<String, Object>) getList1.get(1);
			Map<String, Object> getList3 = (Map<String, Object>) getList2.get("weather");
			tempDS = (double) getList3.get("temprature");
		}
		if (weatherBit.containsKey(today.toString())) {
			ArrayList getList1 = (ArrayList) weatherBit.get(lat+longi);
			Map<String, Object> getList2 = (Map<String, Object>) getList1.get(1);
			Map<String, Object> getList3 = (Map<String, Object>) getList2.get("weather");
			tempWB = (double) getList3.get("temprature");


		}

		if (tempDS <= tempWB)
			wData.put("temprature",tempDS);
		else
			wData.put("temprature",tempWB);

		accurateResult.put(lat+longi,wData);
		return accurateResult;
	}

	@Override
	public void notifyMe(String lat, String longi, double expectedTemp, HttpServletRequest request) {
		Map<String, Object> accurateResult  = getAccurateWeather(lat,longi);
		User user = (User) request.getSession().getAttribute("user");
		HashMap<String,Object> list1 = (HashMap<String,Object>) accurateResult.get(lat+longi);
		if(accurateResult.containsKey(lat+longi)){
			double currentTemp = (double) list1.get("Temprature");
			if (expectedTemp == currentTemp){
				log.warning(NotifyType.EMAIL + " send mail to "  + user.getEmail());
			}
			else{
				user.setId(user.getId());
				user.setExpetedCondition(expectedTemp);
				user.setLat(lat);
				user.setLongi(longi);
				listOfCondition.add(user);
			}
		}

	}


	@Override
	public Map<String, Object> darksky(String lat, String longi) {

		Map<String, Object> cachedData = cacheManager.get(lat + longi, "darksky", Map.class);
		if (cachedData != null) {
			return cachedData;
		} else {
			return callDarkSky(lat, longi, cacheManager);
		}
	}

	/**
	 * This method is used to fetch data from api.darksky.net
	 */
	public Map<String, Object> callDarkSky(String lat, String longi, CacheManager cacheManager) {
		dsKeyCount++;
		Map<String, Object> json = new HashMap<String, Object>();
		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("https://api.darksky.net/forecast/%s/%s,%s", darkskyKey, lat, longi);
		try {
			ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
			Weather weather = WeatherFactory.getWeatherDataParser(WeatherApiTypes.DARKSKY);
			json = weather.parseData(response);
			cacheManager.put(lat + longi, "darksky", json);
			cacheManager.expire(lat + longi);
		} catch (Exception e) {
			if (dsKeyCount == 2) {
				darkskyKey = System.getenv("DARKSKY_API_KEY_2");
				callDarkSky(lat, longi, cacheManager);
			} else if (dsKeyCount == 3) {
				darkskyKey = System.getenv("DARKSKY_API_KEY_3");
				callDarkSky(lat, longi, cacheManager);
			} else if (dsKeyCount == 4) {
				darkskyKey = System.getenv("DARKSKY_API_KEY_4");
				callDarkSky(lat, longi, cacheManager);
			} else {
				dsKeyCount = 0;
				json.put("darksky", e.toString());
			}
		}
		return json;
	}

	@Override
	public Map<String, Object> weatherbit(String lat, String longi) {
		Map<String, Object> cachedData = (Map<String, Object>) cacheManager.get(lat + longi, "weatherbit", Map.class);
		if (cachedData != null) {
			return cachedData;
		} else {
			return callWeatherBit(lat, longi, cacheManager);
		}
	}

	public  List<User> getListOfCondition() {
		return this.listOfCondition;
	}

	/**
	 * This method is used to fetch data from api.weatherbit.io
	 */
	public Map<String, Object> callWeatherBit(String lat, String longi, CacheManager cacheManager) {
		wbKeyCount++;
		Map<String, Object> json = new HashMap<String, Object>();
		String url = String.format("https://api.weatherbit.io/v2.0/forecast/daily?lat=%s&lon=%s&key=%s", lat, longi,
				weatherbitKey);
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
			Weather weather = WeatherFactory.getWeatherDataParser(WeatherApiTypes.WEATHERBIT);
			json = weather.parseData(response);
			cacheManager.put(lat + longi, "weatherbit", json);
			cacheManager.expire(lat + longi);
		} catch (Exception e) {
			if (wbKeyCount == 2) {
				weatherbitKey = System.getenv("WEATHERBIT_API_KEY_2");
				callWeatherBit(lat, longi, cacheManager);
			} else if (wbKeyCount == 3) {
				weatherbitKey = System.getenv("WEATHERBIT_API_KEY_3");
				callWeatherBit(lat, longi, cacheManager);
			} else if (wbKeyCount == 4) {
				weatherbitKey = System.getenv("WEATHERBIT_API_KEY_4");
				callWeatherBit(lat, longi, cacheManager);
			} else {
				wbKeyCount = 0;
				json.put("accuweather", e.toString());
			}
		}
		return json;

	}
}
