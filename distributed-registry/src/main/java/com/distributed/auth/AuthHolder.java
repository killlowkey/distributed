package com.distributed.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ray
 * @date created in 2021/6/22 23:40
 */
@Component
public class AuthHolder {

    private final Map<String, String> tokenData = new ConcurrentHashMap<>(64);

    public String generateToken(String serviceUrl) {
        String token = UUID.randomUUID().toString();
        this.tokenData.put(serviceUrl, token);
        return token;
    }

    public boolean authToken(/*String serviceUrl,*/ String token) {

//        String resToken = this.tokenData.getOrDefault(serviceUrl, "");
//        return resToken.equals(token);

        for (String value : tokenData.values()) {
            if (value.equals(token)) {
                return true;
            }
        }

        return false;
    }

    public void removeToken(String serviceUrl) {
        this.tokenData.remove(serviceUrl);
    }
}
