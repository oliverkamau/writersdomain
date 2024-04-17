package com.writers.modelapp.config;
import com.writers.modelapp.setups.entity.Users;
import com.writers.modelapp.setups.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final SystemUserDetails userDetailsService;
    private final UserRepo userRepo;


    private final JwtUtil jwtUtil;

    public JwtRequestFilter(SystemUserDetails userDetailsService, UserRepo userRepo, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Users user = null;

        final String authorizationHeader=request.getHeader("Authorization");
        String username=null;
        String jwt=null;

        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            jwt=authorizationHeader.substring(7);

            username=jwtUtil.extractUsername(jwt);
            System.out.println("Username from token is "+username);

        }
        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails= userDetailsService.loadUserByUsername(username);
            user = userRepo.findByUsername(username);
            System.out.println("Logged in "+user.getIsLoggedIn());
            String isLoggedIn = user.getIsLoggedIn()==null?"N":user.getIsLoggedIn();
             if(isLoggedIn.equalsIgnoreCase("N"))
                throw new Exception("Invalid token login to get a fresh token!");


            if(jwtUtil.validateToken(jwt,userDetails)){
                System.out.println("Valid Token for request "+user.getIsLoggedIn());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
