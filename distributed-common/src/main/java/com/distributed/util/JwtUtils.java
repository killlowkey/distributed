package com.distributed.util;

import com.distributed.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * @author Ray
 * @date created in 2020/12/12 13:40
 */
public class JwtUtils {

    private JwtUtils() {

    }

    // 默认7天过期
    private static final long EXP_DATE = 7 * 24 * 60 * 60 * 1000;
    // 盐值
    private static final Key KEY = Keys.hmacShaKeyFor("3e4b115b-3d5f-45db-8963-6735e051a753".getBytes());

    public static String generateToken(User user) {
        return Jwts
                .builder()
                .setId("distributed-auth")
                // 主题
                .setSubject(user.getUsername())
                // 权限
                .claim("authorities", user.getAuthorities())
                // 签发时间
                .setIssuedAt(new Date())
                // 过期时间
                .setExpiration(new Date(System.currentTimeMillis() + EXP_DATE))
                // 盐值 + 加密算法
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean verifyToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return true;
    }
}
