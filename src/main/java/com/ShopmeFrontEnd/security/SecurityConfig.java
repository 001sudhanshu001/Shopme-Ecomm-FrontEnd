package com.ShopmeFrontEnd.security;

import com.ShopmeFrontEnd.security.oauth2.CustomerOauth2UserService;
//import com.ShopmeFrontEnd.security.oauth2.OAuth2LoginSuccessHandler;
import com.ShopmeFrontEnd.security.oauth2.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @PostConstruct
//    void init() {
//        System.out.println("SecurityConfig Object Created....!");
//    }

    private final CustomerOauth2UserService oauth2UserService;

    private final OAuth2LoginSuccessHandler auth2LoginSuccessHandler;

    private final DatabaseLoginSuccessHandler databaseLoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
               .and()
                .authorizeRequests()
                .mvcMatchers("/account_details", "/update_account_details", "/cart", "/orders/**",
                        "/address_book/**", "/checkout", "/place_order",
                        "/reviews/** ", "/write_review/**", "/post_review/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .successHandler(databaseLoginSuccessHandler)
                    .permitAll()
                .and()
                .oauth2Login()
                    .loginPage("/login")
                    .userInfoEndpoint()
                    .userService(oauth2UserService)
                    .and()
                    .successHandler(auth2LoginSuccessHandler) // on successful authentication by oauth this handler will
                                    // be called in which we will save user info in DB if user is not present
                .and()
                .logout().permitAll()
                .and()
                .rememberMe()
                    .key("123456789afjknnwje@nturn_AVNjdvvj")
                    .tokenValiditySeconds(14 * 24 * 60 * 60);
//                .and()
//                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
    }

    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService());

        return daoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(this.daoAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }

}

