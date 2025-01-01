package com.example.users.service;

import com.example.shared.dto.UserDto;
import com.example.shared.exception.NoUuidFoundException;
import com.example.shared.model.User;
import com.example.shared.model.UserStatus;
import com.example.shared.model.UserValidator;
import com.example.users.dto.user.UserRegisterDto;
import com.example.users.dto.user.UserUpdateDto;
import com.example.users.factory.UserFactory;
import com.example.users.kafka.KafkaUserValidatorService;
import com.example.users.model.DeactivateUserResult;
import com.example.users.model.RegisterUserResult;
import com.example.users.model.UpdateUserResult;
import com.example.users.model.VerifyUserResult;
import com.example.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository repository;

    @Mock
    EmailService emailService;

    @Mock
    KafkaUserValidatorService kafkaUserValidatorService;

    @Mock
    UserFactory factory;

    @InjectMocks
    UserService service;

    @Test
    @DisplayName("Test if username already exists")
    void registerUsernameAlreadyExists() {
        var dto = new UserRegisterDto("test", "test@email.com");
        var user = new User();
        when(repository.existsByName(dto.username())).thenReturn(true);
        when(repository.findByName(dto.username())).thenReturn(user);

        var result = service.register(dto);

        assertEquals(RegisterUserResult.USER_ALREADY_EXISTS, result);
        verify(repository, never()).save(any());
        verify(emailService, never()).sendPasswordEmail(any(), any());
        verify(kafkaUserValidatorService, never()).saveValidator(any());
    }

    @Test
    @DisplayName("Test if email already exists")
    void registerEmailAlreadyExists() {
        var dto = new UserRegisterDto("test", "test@email.com");
        var user = new User();
        when(repository.existsByEmail(dto.email())).thenReturn(true);
        when(repository.findByEmail(dto.email())).thenReturn(user);

        var result = service.register(dto);

        assertEquals(RegisterUserResult.USER_ALREADY_EXISTS, result);
        verify(repository, never()).save(any());
        verify(emailService, never()).sendPasswordEmail(any(), any());
        verify(kafkaUserValidatorService, never()).saveValidator(any());
    }

    @Test
    @DisplayName("Test emailService is called")
    void registerPasswordSent() {
        var dto = mock(UserRegisterDto.class);
        var user = new User();
        user.setPassword("password");
        when(repository.existsByName(dto.username())).thenReturn(false);
        when(repository.existsByEmail(dto.email())).thenReturn(false);
        when(factory.createUser(dto)).thenReturn(user);

        service.register(dto);

        verify(emailService).sendPasswordEmail(any(User.class), any(UserValidator.class));
    }

    @Test
    @DisplayName("Test repository is called")
    void registerUserInRepository() {
        var dto = mock(UserRegisterDto.class);
        var user = new User();
        user.setPassword("password");
        when(repository.existsByName(dto.username())).thenReturn(false);
        when(repository.existsByEmail(dto.email())).thenReturn(false);
        when(factory.createUser(dto)).thenReturn(user);

        var result = service.register(dto);

        assertEquals(RegisterUserResult.SUCCESS, result);

        verify(repository).save(any(User.class));
        verify(kafkaUserValidatorService).saveValidator((any(UserValidator.class)));
    }

    @Test
    @DisplayName("Test denied attempt at editing admin")
    void updateAdminEditDenied() {
        var dto = new UserUpdateDto(1L, "Ben", "email@example.com", UserStatus.ACTIVE);

        var result = service.updateUser(dto);

        assertEquals(UpdateUserResult.ADMIN_EDIT_DENIED, result);
    }
    @Test
    @DisplayName("Test email conflict on update")
    void updateEmailConflict() {

        var dto = new UserUpdateDto(2L, "Ben", "email@example.com", UserStatus.ACTIVE);
        var user = new User();
        when(repository.existsByEmail(dto.email())).thenReturn(true);
        when(repository.findByEmail(dto.email())).thenReturn(user);

        var result = service.updateUser(dto);

        assertEquals(UpdateUserResult.EMAIL_CONFLICT, result);
    }

    @Test
    @DisplayName("Test successful update")
    void updateUserReturnSuccess() {
        var user = mock(User.class);
        var dto = new UserUpdateDto(2L, "Ben", "email@example.com", UserStatus.ACTIVE);
        when(repository.existsByEmail(dto.email())).thenReturn(false);
        when(repository.getReferenceById(dto.id())).thenReturn(user);

        var result = service.updateUser(dto);

        verify(repository, atLeastOnce()).save(user);
        assertEquals(UpdateUserResult.SUCCESS, result);
    }

    @Test
    @DisplayName("Test admin delete attempt denied")
    void deactivateAdmin() {
        var id = 1L;

        var result = service.deactivateUser(id);

        assertEquals(DeactivateUserResult.ADMIN_DELETE_DENIED, result);
    }

    @Test
    @DisplayName("Test deactivate user not active/not exists")
    void deactivateUserNotActive() {
        var id = 2L;
        var user = mock(User.class);
        when(repository.getReferenceById(id)).thenReturn(user);
        when(user.getStatus()).thenReturn(UserStatus.NOT_ACTIVE);

        var result = service.deactivateUser(id);

        assertEquals(DeactivateUserResult.USER_ALREADY_NOT_ACTIVE, result);
    }

    @Test
    @DisplayName("Test successful deactivation")
    void deactivateSuccessful() {
        var id = 2L;
        var user = mock(User.class);

        when(repository.getReferenceById(id)).thenReturn(user);

        var result = service.deactivateUser(id);

        assertEquals(DeactivateUserResult.SUCCESS, result);
    }

    @Test
    @DisplayName("Test user not verified")
    void verifyUserNotVerified() {
        var uuid = UUID.randomUUID().toString();
        when(kafkaUserValidatorService.findByUuid(uuid)).thenThrow(NoUuidFoundException.class);

        var result = service.verifyUser(uuid);

        assertEquals(VerifyUserResult.USER_NOT_VERIFIED, result);
    }

    @Test
    @DisplayName("Test validation date expired")
    void verifyExpiredValidationDate() {
        var uuid = UUID.randomUUID().toString();
        var validator = new UserValidator();
        validator.setExpirationDate(Instant.now());

        when(kafkaUserValidatorService.findByUuid(uuid)).thenReturn(validator);

        var result = service.verifyUser(uuid);

        verify(kafkaUserValidatorService).deleteValidator(validator);

        assertEquals(VerifyUserResult.EXPIRED_VALIDATION_DATE, result);
    }

    @Test
    @DisplayName("Test successful validation")
    void verifySuccessfulValidation() {
        var uuid = UUID.randomUUID().toString();
        var user = mock(User.class);
        var mockValidator = mock(UserValidator.class);

        when(mockValidator.getExpirationDate()).thenReturn(Instant.now().plusSeconds(300));
        when(mockValidator.getIdUser()).thenReturn(user);

        when(kafkaUserValidatorService.findByUuid(uuid)).thenReturn(mockValidator);

        var result = service.verifyUser(uuid);

        verify(user).validate();
        verify(repository).save(user);

        assertEquals(VerifyUserResult.SUCCESS, result);
    }

    @Test
    @DisplayName("Test invalid uuid format")
    void verifyCatchInvalidUuidFormat() {
        var uuid = "super-secrete-Uuid-c36ad4-ad";

        var result = service.verifyUser(uuid);

        assertEquals(VerifyUserResult.INVALID_UUID_FORMAT, result);
    }

    @Test
    @DisplayName("Test user is returned")
    void checkCurrentUser() {
        var username = "Jin";
        var user = mock(User.class);
        var dto = new UserDto(user);

        when(repository.findByName(username)).thenReturn(user);

        var result = service.checkCurrentUser(username);

        assertEquals(dto, result);

    }
}