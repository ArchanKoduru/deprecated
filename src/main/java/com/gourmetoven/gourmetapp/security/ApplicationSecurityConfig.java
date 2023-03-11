package com.gourmetoven.gourmetapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Properties;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${user.username.user1}")
    private String user1;
    @Value("${user.password.user1}")
    private String pass1;
    @Value("${user.username.user2}")
    private String user2;
    @Value("${user.password.user2}")
    private String pass2;
    @Value("${user.username.user3}")
    private String user3;
    @Value("${user.password.user3}")
    private String pass3;

    private String passFormat = "%s,ROLE_USER,enabled";



    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeRequests()
                .antMatchers("/h2-ui/**").permitAll();

        //To enable H2-UI
        httpSecurity.headers().frameOptions().disable();

        httpSecurity.csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().httpBasic();


    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inMemoryUserDetailsManager());
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        final Properties users = new Properties();
        String pass_1 = String.format(passFormat,encodeMyPassword(pass1));
        String pass_2 = String.format(passFormat,encodeMyPassword(pass2));
        String pass_3 = String.format(passFormat,encodeMyPassword(pass3));

        users.put(user1,pass_1);
        users.put(user2,pass_2);
        users.put(user3,pass_3);
        return new InMemoryUserDetailsManager(users);
    }

    private String encodeMyPassword(String password)
    {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }
}
