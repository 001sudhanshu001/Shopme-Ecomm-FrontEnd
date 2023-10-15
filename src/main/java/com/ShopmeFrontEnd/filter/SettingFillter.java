package com.ShopmeFrontEnd.filter;

import com.ShopmeFrontEnd.entity.readonly.Setting;
import com.ShopmeFrontEnd.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Component
public class SettingFillter implements Filter {

    // This filter will be executed for every request for each url
    @Autowired
    private SettingService service;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;

        String url = servletRequest.getRequestURL().toString();

        // for static resources it won't execute
        if(url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")){
            filterChain.doFilter(request, response);
            return;
        }

        // Loading General setting from Database
        List<Setting> generalSettings = service.getGeneralSettings();
        generalSettings.forEach(setting -> {
            request.setAttribute(setting.getKey(), setting.getValue());
        });

//       System.out.println(url);

        filterChain.doFilter(request, response);
    }
}
