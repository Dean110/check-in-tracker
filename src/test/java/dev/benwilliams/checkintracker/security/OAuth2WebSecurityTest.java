package dev.benwilliams.checkintracker.security;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class OAuth2WebSecurityTest {

    @Autowired
    private RestTestClient restTestClient;

    @Test
    @Disabled("Re-enable once view templates are added in Task 11")
    void shouldRedirectUnauthenticatedUserToOAuth2() {
        restTestClient
            .get().uri("/dashboard")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().location("/oauth2/authorization/google");
    }

    @Test
    @Disabled("Re-enable once view templates are added in Task 11")
    void shouldAllowAccessToPublicEndpoints() {
        restTestClient
            .get().uri("/")
            .exchange()
            .expectStatus().isOk();
        
        restTestClient
            .get().uri("/login")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void shouldInitiateGoogleOAuth2Flow() {
        restTestClient
            .get().uri("/oauth2/authorization/google")
            .exchange()
            .expectStatus().is3xxRedirection();
    }

    @Test
    void shouldInitiateAppleOAuth2Flow() {
        restTestClient
            .get().uri("/oauth2/authorization/apple")
            .exchange()
            .expectStatus().is3xxRedirection();
    }
}
