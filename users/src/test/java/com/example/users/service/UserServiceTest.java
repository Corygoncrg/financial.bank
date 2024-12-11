package com.example.users.service;

import com.example.shared.dto.UserDto;
import com.example.shared.model.User;
import com.example.shared.model.UserStatus;
import com.example.shared.model.UserValidator;
import com.example.users.dto.user.UserRegisterDto;
import com.example.users.dto.user.UserUpdateDto;
import com.example.users.factory.UserFactory;
import com.example.users.kafka.KafkaUserValidatorService;
import com.example.users.model.DeactivateUserResult;
import com.example.users.model.UpdateUserResult;
import com.example.users.model.VerifyUserResult;
import com.example.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository repository;

    @Mock
    EmailService emailService;

    @Mock
    KafkaUserValidatorService validatorRepository;

    @Mock
    UserFactory factory;

    @InjectMocks
    UserService service;

    @Test
    @DisplayName("Test if username already exists")
    void registerUsernameAlreadyExists() {
        var dto = mock(UserRegisterDto.class);
        when(repository.existsByName(dto.username())).thenReturn(true);

        var result = service.register(dto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test if email already exists")
    void registerEmailAlreadyExists() {
        var dto = mock(UserRegisterDto.class);
        when(repository.existsByEmail(dto.email())).thenReturn(true);

        var result = service.register(dto);

        assertFalse(result);
    }

    @SuppressWarnings("unused")
    @Test
    @DisplayName("Test emailService is called")
    void registerPasswordSent() {
        var dto = mock(UserRegisterDto.class);
        when(repository.existsByEmail(dto.email())).thenReturn(false);

        service.register(dto);

        verify(emailService).sendPasswordEmail(any(User.class), any(UserValidator.class));
    }

    @Test
    @DisplayName("Test repository is called")
    void registerUserInRepository() {
        var dto = mock(UserRegisterDto.class);
        when(repository.existsByEmail(dto.email())).thenReturn(false);

        var result = service.register(dto);

        assertTrue(result);

        verify(repository).save(any(User.class));
        verify(validatorRepository).save((any(UserValidator.class)));
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
        when(repository.existsByEmail(dto.email())).thenReturn(true);

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

        var result = service.verifyUser(uuid);

        assertEquals(VerifyUserResult.USER_NOT_VERIFIED, result);
    }

    @Test
    @DisplayName("Test validation date expired")
    void verifyExpiredValidationDate() {
        var uuid = UUID.randomUUID().toString();
        var validator = new UserValidator();
        validator.setExpirationDate(Instant.now());
        Optional<UserValidator> optionalValidator = Optional.of(validator);

        when(validatorRepository.findByUuid(uuid)).thenReturn(optionalValidator);

        var result = service.verifyUser(uuid);

        verify(validatorRepository).delete(validator);

        assertEquals(VerifyUserResult.EXPIRED_VALIDATION_DATE, result);
    }

    @Test
    @DisplayName("Test successful validation")
    void verifySuccessfulValidation() {
        var uuid = UUID.randomUUID().toString();
        var user = mock(User.class);
        var mockValidator = mock(UserValidator.class);
        Optional<UserValidator> optionalValidator = Optional.of(mockValidator);

        when(mockValidator.getExpirationDate()).thenReturn(Instant.now().plusSeconds(300));
        when(mockValidator.getIdUser()).thenReturn(user);

        when(validatorRepository.findByUuid(uuid)).thenReturn(optionalValidator);

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
        var details = mock(UserDetails.class);
        var username = "Jin";
        var user = mock(User.class);
        var dto = new UserDto(user);

        when(repository.findByName(username)).thenReturn(user);

        var result = service.checkCurrentUser(username);

        assertEquals(dto, result);

    }
}