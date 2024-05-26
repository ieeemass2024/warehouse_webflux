package com.example.warehouse.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.security.jwt.key}")
    private String key;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    private final ReactiveStringRedisTemplate template;

    public JwtUtils(ReactiveStringRedisTemplate template) {
        this.template = template;
    }

    public String createJwt(UserDetails userDetails, String id, String username) {
        if (key == null || key.isEmpty()) {
            logger.error("JWT key is not set");
            return null;
        }

        logger.info("JWT key: {}", key);
        logger.info("JWT expire: {}", expire);

        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expireTime = this.expireTime();
        String token = JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(username)
                .withClaim("id", id)
                .withClaim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expireTime)
                .withIssuedAt(new Date())
                .sign(algorithm);
        logger.info("Created JWT: {}", token);
        return token;
    }



    public Mono<DecodedJWT> resolveJWT(String jwtToken) {
        String convertToken = this.convertToken(jwtToken);
        if (convertToken == null) {
            logger.warn("Token is null or does not start with 'Bearer '");
            return Mono.empty();
        }

        Algorithm algorithm = Algorithm.HMAC256(this.key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(convertToken);
            logger.info("Resolved JWT: {}", decodedJWT);
            return this.isInvalidJwt(decodedJWT.getId())
                    .flatMap(isInvalid -> {
                        if (isInvalid) {
                            logger.warn("JWT is invalid: {}", decodedJWT.getId());
                            return Mono.empty();
                        }
                        return Mono.just(decodedJWT);
                    });
        } catch (JWTVerificationException e) {
            logger.error("JWT verification failed: {}", e.getMessage());
            return Mono.empty();
        }
    }

    public Mono<Boolean> invalidateJwt(String jwtToken) {
        String convertToken = this.convertToken(jwtToken);
        if (convertToken == null) return Mono.just(false);

        Algorithm algorithm = Algorithm.HMAC256(this.key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(convertToken);
            String jwtId = decodedJWT.getId();
            return setJwtBlack(jwtId, decodedJWT.getExpiresAt());
        } catch (JWTVerificationException e) {
            logger.error("JWT invalidation failed: {}", e.getMessage());
            return Mono.just(false);
        }
    }

    private Mono<Boolean> setJwtBlack(String jwtId, Date time) {
        return this.isInvalidJwt(jwtId).flatMap(isInvalid -> {
            if (isInvalid) return Mono.just(false);

            Date now = new Date();
            long expire = Math.max(time.getTime() - now.getTime(), 0);

            return template.opsForValue()
                    .set(Const.JWT_BLACK_LIST + jwtId, "", expire)
                    .map(result -> true);
        });
    }

    private Mono<Boolean> isInvalidJwt(String jwtId) {
        return template.hasKey(Const.JWT_BLACK_LIST + jwtId)
                .defaultIfEmpty(false);
    }

    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expire);
        return calendar.getTime();
    }

    private String convertToken(String jwtToken) {
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            return null;
        }
        return jwtToken.substring(7);
    }

    public UserDetails decodedJwtToUser(DecodedJWT decodedJWT) {
        String username = decodedJWT.getSubject();
        String[] authorities = decodedJWT.getClaim("authorities").asArray(String.class);
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("****")
                .authorities(authorities)
                .build();
    }
}
