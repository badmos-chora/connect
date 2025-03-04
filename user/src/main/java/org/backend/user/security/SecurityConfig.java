package org.backend.user.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@Configuration
@ImportResource({"classpath:/spring-security.xml"})
public class SecurityConfig {
}
