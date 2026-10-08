package com.terraguard.quakexit.acceptance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terraguard.quakexit.common.enums.DomainEnums.Role;
import com.terraguard.quakexit.common.error.GlobalExceptionHandler;
import com.terraguard.quakexit.common.exception.ApiExceptions.DuplicateResourceException;
import com.terraguard.quakexit.common.exception.ApiExceptions.InvalidCredentialsException;
import com.terraguard.quakexit.device.controller.DeviceController;
import com.terraguard.quakexit.device.dto.DeviceDtos.BindRequest;
import com.terraguard.quakexit.device.dto.DeviceDtos.DeviceResponse;
import com.terraguard.quakexit.device.service.DeviceService;
import com.terraguard.quakexit.iam.controller.AuthController;
import com.terraguard.quakexit.iam.dto.AuthDtos.AuthResponse;
import com.terraguard.quakexit.iam.dto.AuthDtos.LoginRequest;
import com.terraguard.quakexit.iam.dto.AuthDtos.RegisterRequest;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.iam.service.AuthService;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class QuakExitStepDefinitions {
    private static final String EMAIL = "resident@example.com";
    private static final String PASSWORD = "ValidPass1";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AuthService authService;
    private DeviceService deviceService;
    private UserRepository userRepository;
    private SubscriptionService subscriptions;
    private MockMvc mockMvc;
    private User resident;
    private org.springframework.test.web.servlet.MvcResult result;
    private DeviceResponse linkedDevice;

    @Before
    public void setUp() {
        authService = mock(AuthService.class);
        deviceService = mock(DeviceService.class);
        userRepository = mock(UserRepository.class);
        subscriptions = mock(SubscriptionService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new AuthController(authService),
                new DeviceController(deviceService, userRepository, subscriptions))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        resident = User.builder().fullName("Test Resident").email(EMAIL)
            .passwordHash("encoded-password").role(Role.HOMEOWNER).build();
        resident.setId(1L);
        linkedDevice = new DeviceResponse(10L, "HUB-001", "AA:BB:CC:DD:EE:FF", "Living room",
            null, null, null, null, null, null, null);
    }

    @Given("a resident with an email that is not registered")
    public void residentEmailIsNotRegistered() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(authService.register(any(RegisterRequest.class))).thenReturn(
            new AuthResponse("registration-token", 86_400_000L, 1L, resident.getFullName(),
                EMAIL, Role.HOMEOWNER));
    }

    @When("the resident sends a registration request with valid data")
    @When("the resident sends a registration request")
    public void sendRegistrationRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
            resident.getFullName(), EMAIL, PASSWORD, "+51999999999", null);
        try {
            result = mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        } catch (Exception ex) {
            throw unwrap(ex);
        }
    }

    @Given("a resident with an email that is already registered")
    public void residentEmailIsAlreadyRegistered() {
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new DuplicateResourceException("El email ya esta registrado"));
    }

    @Given("a registered resident")
    public void registeredResident() {
        when(authService.login(any(LoginRequest.class))).thenReturn(
            new AuthResponse("access-token", 86_400_000L, 1L, resident.getFullName(),
                EMAIL, Role.HOMEOWNER));
    }

    @When("the resident sends a login request with correct credentials")
    public void sendLoginWithCorrectCredentials() throws Exception {
        sendLoginRequest(PASSWORD);
    }

    @When("the resident sends a login request with a wrong password")
    public void sendLoginWithWrongPassword() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenThrow(new InvalidCredentialsException());
        sendLoginRequest("WrongPass1");
    }

    private void sendLoginRequest(String password) throws Exception {
        LoginRequest request = new LoginRequest(EMAIL, password);
        try {
            result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        } catch (Exception ex) {
            throw unwrap(ex);
        }
    }

    @Given("an authenticated resident and an unlinked device code")
    public void authenticatedResidentAndDeviceCode() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(resident));
        when(deviceService.bind(any(BindRequest.class), eq(resident))).thenReturn(linkedDevice);
    }

    @Given("an authenticated resident and a device code that is already linked")
    public void authenticatedResidentAndAlreadyLinkedDeviceCode() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(resident));
        when(deviceService.bind(any(BindRequest.class), eq(resident)))
            .thenThrow(new DuplicateResourceException("El dispositivo ya pertenece a otro usuario"));
    }

    @When("the resident sends a link request with the device code")
    public void sendDeviceLinkRequest() throws Exception {
        BindRequest request = new BindRequest("HUB-001", "AA:BB:CC:DD:EE:FF", "Living room");
        try {
            result = mockMvc.perform(post("/api/v1/devices/bind")
                    .principal(new UsernamePasswordAuthenticationToken(EMAIL, null))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        } catch (Exception ex) {
            throw unwrap(ex);
        }
    }

    @Then("the system responds with status {int}")
    public void responseHasStatus(int status) throws Exception {
        org.hamcrest.MatcherAssert.assertThat(result.getResponse().getStatus(),
            org.hamcrest.Matchers.equalTo(status));
    }

    @Then("the system responds with status 200 and an access token")
    public void successfulLoginResponse() throws Exception {
        responseHasStatus(200);
        org.hamcrest.MatcherAssert.assertThat(result.getResponse().getContentAsString(),
            org.hamcrest.Matchers.containsString("\"token\":\"access-token\""));
    }

    @Then("the account is created")
    public void accountIsCreated() {
        verify(authService).register(any(RegisterRequest.class));
    }

    @Then("the device is associated with the resident")
    public void deviceIsAssociated() {
        verify(deviceService).bind(any(BindRequest.class), eq(resident));
    }

    private RuntimeException unwrap(Exception ex) {
        if (ex instanceof RuntimeException runtimeException) return runtimeException;
        return new RuntimeException(ex);
    }
}
