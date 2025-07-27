package gr.aueb.cf.teacherapp.authentication;

//import gr.aueb.cf.teacherapp.core.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/index.html").permitAll()
                        .requestMatchers("/school/users/register").permitAll()
                        .requestMatchers("/school/teachers/insert").hasAuthority("EDIT_TEACHERS")
                        .requestMatchers(HttpMethod.GET, "/teachers/edit/{uuid}").hasAuthority("EDIT_TEACHERS")
                        .requestMatchers(HttpMethod.POST, "/teachers/edit").hasAuthority("EDIT_TEACHERS")
                        .requestMatchers(HttpMethod.GET, "/teachers/delete/{uuid}").hasAuthority("EDIT_TEACHERS")
                        .requestMatchers("/school/teachers/**").hasAnyRole("ADMIN", "TEACHERS_ADMIN")  // ROLE_ prefix in authorities
                        .requestMatchers("/school/admin/**").hasRole("ADMIN")
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/img/**").permitAll()
                        .anyRequest().authenticated()
                )
                //                        .requestMatchers("/school/teachers/view").hasAuthority("VIEW_TEACHERS")
//                        .requestMatchers("/school/teachers/**").hasAnyAuthority(Role.TEACHER.name())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/school/teachers", true)
//                        .successHandler(authenticationSuccessHandler)
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults())       // username, password and session cookies
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );
                return http.build();
    }
}
