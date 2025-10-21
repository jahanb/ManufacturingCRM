package com.manufacturing.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.servlet.annotation.WebFilter;
import org.primefaces.webapp.filter.FileUploadFilter;
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilter() {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter());
        registrationBean.addUrlPatterns("*.xhtml");
        registrationBean.setOrder(1);
        return registrationBean;
    }
    @WebFilter(servletNames = {"Faces Servlet"})
    public class PrimeFacesFileUploadFilterConfig extends FileUploadFilter {
        // No additional code needed - just extend FileUploadFilter
    }
}