package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserServiceIntegrationTest extends BaseIntegrationTest {

    private final Keycloak keycloak;

    private String userId;

    @Autowired
    UserServiceIntegrationTest(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    // ТЕСТ СОЗДАНИЯ ЮЗЕРА
    @Test
    @WithMockUser(roles = "MODERATOR")
    public void CreateUserTest() throws Exception {
        UserRequest userRequest = new UserRequest(
                "username", "email@example.com", "password", "FirstName", "LastName");
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isOk());
        userId = keycloak.realm("ITM").users().search(userRequest.getUsername()).get(0).getId(); // СОХРАНЯЕМ ID УСПЕШНО СОЗДАННОГО ЮЗЕРА
    }

    // ТЕСТ ПОЛУЧЕНИЯ ЮЗЕРА ПО ID
    @Test
    @WithMockUser(roles = "MODERATOR")
    public void GetUserByIdTest() throws Exception {
        String id = "e388f274-55bd-44c2-b8e6-c9d999cec02c"; // ID ЮЗЕРА СОЗДАННОГО В KeyCloak
        mvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk());
    }

    // ПОСЛЕ КАЖДОГО ТЕСТА УДАЛЯЕМ СОЗДАННОГО ЮЗЕРА
    @AfterEach
    public void tearDown() {
        if (userId != null) {
            keycloak.realm("ITM").users().get(userId).remove();
        }
    }
}
