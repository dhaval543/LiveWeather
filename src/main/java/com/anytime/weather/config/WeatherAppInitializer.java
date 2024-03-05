package com.anytime.weather.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WeatherAppInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.register(WeatherAppConfig.class);

        DispatcherServlet dispatcherServlet
                = new DispatcherServlet(webApplicationContext);
        ServletRegistration
                .Dynamic myCustomDispatcherServlet
                = servletContext.addServlet(
                "myDispatcherServlet", dispatcherServlet);

        myCustomDispatcherServlet.setLoadOnStartup(1);

        myCustomDispatcherServlet.addMapping("/views/*");
    }
}
