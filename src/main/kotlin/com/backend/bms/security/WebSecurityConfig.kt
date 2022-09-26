package com.backend.bms.security

import com.backend.bms.security.jwt.AuthEntryPointJwt
import com.backend.bms.security.jwt.AuthTokenFilter
import com.backend.bms.security.services.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import kotlin.jvm.Throws

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true,
)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var userDetailsService: UserDetailsServiceImpl;

    @Autowired
    private lateinit var unauthorizedHandler: AuthEntryPointJwt;

    @Bean
    fun authenticationJwtTokenFilter(): AuthTokenFilter {
        return AuthTokenFilter();
    }

    @Throws( Exception::class )
    override fun configure( auth: AuthenticationManagerBuilder? ) {
        auth?.let { auth.userDetailsService( userDetailsService ).passwordEncoder( passwordEncoder() ) } ;
    }

    @Bean
    @Throws( Exception::class )
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean();
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder();
    }

    override fun configure( web: WebSecurity? ) {
        web?.let { web.ignoring().antMatchers( HttpMethod.POST, "/auth/**" ) }
    }

    @Throws( Exception::class )
    override fun configure( http: HttpSecurity? ) {
        http?.let {
            http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint( unauthorizedHandler ).and()
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
                .authorizeRequests().antMatchers( HttpMethod.POST, "/auth/**" ).permitAll()
                .anyRequest().authenticated();

            http.addFilterBefore( authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java );
        }
    }
}