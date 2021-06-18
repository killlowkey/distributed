package com.distributed.controller;

import com.distributed.entity.ServerResponse;
import com.distributed.entity.User;
import com.distributed.util.JwtUtils;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Ray
 * @date created in 2021/6/18 10:25
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public ServerResponse<Map<String, String>> login(@RequestBody LoginDto loginDto) {

        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ServerResponse.error("username or password is empty");
        }

        if (username.equals("admin") && password.equals("admin")) {
            Map<String, String> data = Map.of("token", JwtUtils.generateToken(new User("admin", "admin", List.of(
                    "ADMIN"))));
            return ServerResponse.success("login success", data);
        }

        return ServerResponse.error("username or password error");
    }

    @Data
    static class LoginDto {
        private String username;
        private String password;
    }
}
