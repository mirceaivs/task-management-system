package javaweb.task_management_system.utility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javaweb.task_management_system.exceptions.InvalidAction;
import javaweb.task_management_system.exceptions.InvalidUserException;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
//    private final JwtAuthenticationConverter jwtConverter;

    @Autowired
    public JwtCookieFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/auth") && !request.getRequestURI().endsWith("logout") ) {
            filterChain.doFilter(request, response);  // Skip filter processing
            return;
        }

        Cookie[] cookies = request.getCookies();

        if(cookies == null) {
            throw new ResourceNotFoundException("Couldn't find the cookie!");
        }

        for( Cookie cookie : cookies) {
            if("jwt".equals(cookie.getName())) {
                String token = cookie.getValue();
                try{
                    Jwt jwt = jwtDecoder.decode(token);

                    String roles = jwt.getClaimAsString("roles");


                    List<GrantedAuthority> authorities = Arrays.stream(roles.split(" "))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    System.out.println("JWT SUBJECT HERE: "+ jwt.getSubject());
                    Authentication authentication = new JwtAuthenticationToken(jwt, authorities, roles);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }catch (Exception e){
                    SecurityContextHolder.clearContext();
                    throw new InvalidAction("Something went wrong processing the token!");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

