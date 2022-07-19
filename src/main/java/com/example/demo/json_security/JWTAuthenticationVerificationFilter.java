package com.example.demo.json_security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.SecurityConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JWTAuthenticationVerificationFilter extends BasicAuthenticationFilter {

    public JWTAuthenticationVerificationFilter(AuthenticationManager authManager) {
        super(authManager);
    }
//        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
//        chain.doFilter(req, res);
//        return;
//    }


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String reqHeader = req.getHeader(SecurityConstants.HEADER_STRING);

        if (reqHeader == null || !reqHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            //
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);


        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {

        //
        String sequritytoken = req.getHeader(SecurityConstants.HEADER_STRING);


        if (sequritytoken == null) {
            return null;
        }
        String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build()
                .verify(sequritytoken.replace(SecurityConstants.TOKEN_PREFIX, ""))
                .getSubject();
        if (!(user == null)) {
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        return null;
    }

}