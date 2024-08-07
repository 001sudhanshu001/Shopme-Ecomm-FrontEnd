package com.ShopmeFrontEnd.filter;

import com.ShopmeFrontEnd.Util.AmazonS3Util;
import com.ShopmeFrontEnd.entity.readonly.Setting;
import com.ShopmeFrontEnd.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(-120)
public class SettingFillter implements Filter {
    // This filter will be executed for every request for each url

    private final AmazonS3Util amazonS3Util;

    private final SettingService service;
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
            if(setting.getKey().equals("SITE_LOGO")) {
                String preSignedUrl = amazonS3Util.generatePreSignedUrl(setting.getValue().substring(1));
                request.setAttribute("SITE_LOGO", preSignedUrl);
            } else {
                request.setAttribute(setting.getKey(), setting.getValue());
            }

        });

//        request.setAttribute("S3_BASE_URI", Constants.S3_BASE_URI); // OLD, Now the SITE_LOGO is already set
        filterChain.doFilter(request, response);
    }
}
