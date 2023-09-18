package com.sciencesakura.sample.infra.web;

import com.sciencesakura.sample.domain.task.Priority;
import com.sciencesakura.sample.domain.task.TaskStatus;
import com.sciencesakura.sample.domain.user.UserStatus;
import com.sciencesakura.sample.infra.OrderConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(String.class, Order.class, new OrderConverter());
    registry.addConverter(String.class, Priority.class, Priority::of);
    registry.addConverter(String.class, TaskStatus.class, TaskStatus::of);
    registry.addConverter(String.class, UserStatus.class, UserStatus::of);
  }
}
