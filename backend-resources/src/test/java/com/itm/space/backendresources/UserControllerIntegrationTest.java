package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.util.List;

public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private UserService userService;

    // ТЕСТ СОЗДАНИЯ ЮЗЕРА
    @Test
    @WithMockUser(roles = "MODERATOR")
    public void CreateUserTest() throws Exception {
        UserRequest userRequest = new UserRequest( // ЗАПРОС НА СОЗДАНИЕ С ТЕСТОВЫМИ ДАННЫМИ
                "username", "email@example.com", "password", "FirstName", "LastName");
        mvc.perform(requestWithContent(post("/api/users"), userRequest)) // ВЫПОЛНЯЕМ POST ЗАПРОС
                .andExpect(status().isOk()); // ДОЛЖЕН ВЫДАТЬ 200 ОК
        verify(userService).createUser(any(UserRequest.class));
    }

    // ТЕСТ ПОЛУЧЕНИЯ ЮЗЕРА ПО ID
    @Test
    @WithMockUser(roles = "MODERATOR")
    public void GetUserByIdTest() throws Exception {
        UUID userId = UUID.randomUUID(); // ГЕНЕРИМ РАНДОМНЫЙ ID
        UserResponse userResponse = new UserResponse( // ИМИТАЦИЯ ОТВЕТА СЕРВЕРА
                "FirstName", "LastName", "testemail@example.com", List.of(), List.of());
        doReturn(userResponse).when(userService).getUserById(userId); // ВОЗВРАЩЩАЕМ ОТВЕТ СЕРВЕРА
        mvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk());
        verify(userService).getUserById(userId);
    }

    // ТЕСТ HELLO
    @Test
    @WithMockUser(roles = "MODERATOR")
    public void HelloTest() throws Exception {
        mvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk());
    }

    // ТЕСТ HELLO ДЛЯ НЕ АВТОРИЗИРОВАННОГО ЮЗЕРА
    @Test
    @WithMockUser
    void HelloByUnauthorizedTest() throws Exception {
        mvc.perform(get("/api/users/hello"))
                .andExpect(status().isForbidden()); // ДОЛЖЕН ВЫДАТЬ 403 Forbidden Т.К. ПОЛЬЗОВАТЕЛЬ НЕ МОДЕРАТОР
    }

    // ТЕСТ СОЗДАНИЯ ЮЗЕРА С НЕКОРЕКТНЫМИ ДАННЫМИ
    @Test
    @WithMockUser(roles = "MODERATOR")
    void CreateUserByInvalidUserRequestTest() throws Exception {
        UserRequest userRequest = new UserRequest("", "email", "", "", "");
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isBadRequest()); // ДОЛЖЕН ВЫДАТЬ 400 Bad Request Т.К. ДАННЫЕ НЕ КОРРЕКТНЫ
    }
}