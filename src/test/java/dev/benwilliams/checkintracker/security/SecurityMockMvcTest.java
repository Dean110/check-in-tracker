package dev.benwilliams.checkintracker.security;

import dev.benwilliams.checkintracker.controller.WebController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(WebController.class)
@ActiveProfiles("test")
class SecurityMockMvcTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void shouldRedirectUnauthenticatedUser() {
        assertThat(mvc.get().uri("/dashboard"))
            .hasStatus3xxRedirection();
    }

    @Test
    @WithMockUser(roles = "USER")
    @Disabled("Re-enable once view templates are added in Task 11")
    void shouldAllowUserAccess() {
        assertThat(mvc.get().uri("/dashboard"))
            .hasStatusOk();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Disabled("Re-enable once view templates are added in Task 11")
    void shouldAllowAdminAccess() {
        assertThat(mvc.get().uri("/admin/users"))
            .hasStatusOk();
    }

    @Test
    @WithMockUser(roles = "USER")
    @Disabled("Re-enable once view templates are added in Task 11")
    void shouldDenyUserAccessToAdmin() {
        assertThat(mvc.get().uri("/admin/users"))
            .hasStatus4xxClientError();
    }
}
