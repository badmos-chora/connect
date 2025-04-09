package org.backend.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class FollowRequestControllerTest {

@Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void getFollowRequests() {
testRestTemplate.getForEntity("/fr", String.class);
    }
}