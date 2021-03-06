package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.SopraServiceException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPutDTO;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    User user;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(1L);
        user.setName("TestUser");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setPassword("TestPassword");
        user.setStatus(UserStatus.ONLINE);
        user.setGamesPlayed(0);
        user.setScore(0);
        user.setCorrectlyGuessed(0);
        user.setDuplicateClues(0);
    }

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("testPassword");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("TestUser");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.score", is(user.getScore())))
                .andExpect(jsonPath("$.gamesPlayed", is(user.getGamesPlayed())));
    }
    
     @Test
    public void testGetUsers() throws Exception {
        // given
        user.setId(42L);
        user.setName("Mikko Muller");
        user.setUsername("mikmu");
        user.setStatus(UserStatus.OFFLINE);

        User user2 = new User();
        user2.setId(24L);
        user2.setName("Nikko Nuller");
        user2.setUsername("niknu");
        user2.setStatus(UserStatus.OFFLINE);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        given(userService.getUsers()).willReturn(users);
        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(users.get(0).getName())))
                .andExpect(jsonPath("$[0].username", is(users.get(0).getUsername())))
                .andExpect(jsonPath("$[0].id", is(42)))

                .andExpect(jsonPath("$[1].name", is(users.get(1).getName())))
                .andExpect(jsonPath("$[1].username", is(users.get(1).getUsername())))
                .andExpect(jsonPath("$[1].id", is(24)));
    }

    @Test
    public void test_getUserById() throws Exception {
        // given
        user.setId(42L);
        user.setName("Mikko Muller");
        user.setUsername("mikmu");
        user.setStatus(UserStatus.OFFLINE);

        given(userService.getUserById(user.getId())).willReturn(user);
        // when
        MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.id", is(42)));
    }

    @Test
    public void test_getUserById_invalidInput() throws Exception {
        given(userService.getUserById(null)).willReturn(null);
        // when
        MockHttpServletRequestBuilder getRequest = get("/users/0").contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    public void login_success() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");
        userPutDTO.setPassword("testpassword");

        given(userService.logIn(Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void login_invalidInput() throws Exception {
        user.setStatus(UserStatus.OFFLINE);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");
        userPutDTO.setPassword("abc");

        Mockito.when(userService.logIn(Mockito.any())).thenCallRealMethod();
        given(userService.findByUsername(Mockito.anyString())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_userAlreadyLoggedIn() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");
        userPutDTO.setPassword("TestPassword");

        Mockito.when(userService.logIn(Mockito.any())).thenCallRealMethod();
        given(userService.findByUsername(Mockito.anyString())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void logoutUser_success() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setId((long)1);

        given(userService.getUserById(Mockito.anyLong())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
    }

    @Test
    public void updateUser_validInput_userUpdated() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setId(1L);
        userPutDTO.setName("TestUserUpd");
        userPutDTO.setUsername("testUsernameUpd");
        userPutDTO.setPassword("testPasswordUpd");

        given(userService.updateUser(Mockito.any(),Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isAccepted());
    }

    @Test
    public void updateUser_invalidInput_userUpdated() throws Exception {
       given(userService.updateUser(Mockito.any(), Mockito.any())).willReturn(user);

       MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(null));

       mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void joinGame_success() throws Exception {
        given(userService.getUserById(Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/join/1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isOk());

    }

    @Test
    public void leaveGame_success() throws Exception {
        given(userService.getUserById(Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/leave/1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isOk());
    }

    @Test
    public void updateUserGameStats_success() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setCorrectlyGuessed(2);
        userPutDTO.setDuplicateClues(1);

        given(userService.updateUserGameStats(Mockito.any(), Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/gamestats/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isOk());
    }

    @Test
    public void updateUserScore_success() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setCorrectlyGuessed(2);
        userPutDTO.setDuplicateClues(1);

        given(userService.updateUserScore(Mockito.any(), Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/score/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isOk());
    }


    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new SopraServiceException(String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
