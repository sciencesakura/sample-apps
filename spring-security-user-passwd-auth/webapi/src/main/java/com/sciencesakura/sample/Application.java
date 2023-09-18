package com.sciencesakura.sample;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of the application.
 */
@SpringBootApplication
public class Application {

  static {
    ToStringBuilder.setDefaultStyle(ToStringStyle.JSON_STYLE);
  }

  /**
   * The entry point of the application.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
