package com.thatbackendguy.quizapp.interceptor;

import com.thatbackendguy.quizapp.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor
{

    private final JwtUtils jwtUtil;

    @Autowired
    public JwtInterceptor(JwtUtils jwtUtil)
    {

        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {

        var authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
        {
            var token = authorizationHeader.substring(7);

            var username = jwtUtil.extractClaims(token).getSubject();

            if (username != null && jwtUtil.validateToken(token, username))
            {
                request.setAttribute("username", username);
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        return false;

    }

}
