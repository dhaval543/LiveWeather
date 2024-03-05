package com.anytime.weather.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;



@Service
public class MailContentBuilder {
	
	private static final String NEW_USER_EMAIL_TEMPLATE = "NewUserEmailTemplate";
  /** The template engine. */
  @Autowired
  private TemplateEngine templateEngine;

  /**
   * Builds the html Mail Content.
   *
   */
  @Autowired
  public MailContentBuilder(TemplateEngine templateEngine) {
      this.templateEngine = templateEngine;
  }
 
  
  public String build(String activationLink) {
    Context context = new Context();
    context.setVariable("activationLink", activationLink);
    return templateEngine.process(NEW_USER_EMAIL_TEMPLATE, context);
  }

}
