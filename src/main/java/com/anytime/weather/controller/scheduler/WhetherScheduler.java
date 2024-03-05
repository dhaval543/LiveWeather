package com.anytime.weather.controller.scheduler;

import com.anytime.weather.Utils.CacheManager;
import com.anytime.weather.exception.NotifyType;
import com.anytime.weather.model.User;
import com.anytime.weather.service.WeatherService;
import com.anytime.weather.service.impl.WeatherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
@EnableScheduling
public class WhetherScheduler {

    @Autowired
    public CacheManager cacheManager;

    @Autowired
    private WeatherService weatherService;

    static Logger log = Logger.getLogger(WhetherScheduler.class.getName());

    /**
     * This job will run every 3 hours and get most populer city weather  and store in Map here
     * we have various way to store this data, like excel,RDBMS etc.
     */
    @Scheduled(cron="0 0 */3 * * *")
    public void runEvey3Hour() {
        // set frequently information of populer city to reduce API CAll
        // get weather from darksky, dummy lat & lang
        Map<String, Object> jsonPuneDarksky = weatherService.getAccurateWeather("18.5204", "73.8567"); // Pune
        cacheManager.put("18.5204" + "73.8567", "tempAPI", jsonPuneDarksky);
        cacheManager.expire("18.5204" + "73.8567");

        Map<String, Object> jsonMumbaiDarksky = weatherService.getAccurateWeather("18.5204", "73.8567"); // Mumabi
        cacheManager.put("18.5204" + "73.8567", "tempAPI", jsonMumbaiDarksky);
        cacheManager.expire("18.5204" + "73.8567");

        Map<String, Object> jsonDelhiDarksky = weatherService.getAccurateWeather("18.5204", "73.8567"); // Mumabi
        cacheManager.put("18.5204" + "73.8567", "tempAPI", jsonDelhiDarksky);
        cacheManager.expire("18.5204" + "73.8567");


    }

    /**
     * this job will run every 3hour and get result from cache memory
     *  check user expectation lati and longi and temprature if match send email
     */
    @Scheduled(cron="0 0 */3 * * *")
    public void notifyUserIfConfitionMatch() {


        List<User> userList = weatherService.getListOfCondition();
        for (User user : userList){

            Map<String, Object> cachedData = cacheManager.get(user.getLat() + user.getLongi(), "tempAPI", Map.class);
            Map<String,Object> hMAp = (HashMap<String,Object>)cachedData.get("tempAPI");
            double temp = (double) hMAp.get("Temprature");
            if (user.getExpetedCondition() == temp){
                log.warning(NotifyType.SLACK  + " send notification to " + user.getEmail());
            }


        }

    }

}
