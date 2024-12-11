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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaUserValidatorService kafkaUserValidatorService;

    @Autowired
    private UserFactory factory;

    public List<User> listUsers() {
         return repository.findAllUsers();
    }

    public RegisterUserResult register(@Valid UserRegisterDto dto) {
        boolean userExistsOrPending = checkAndHandleExistingUser(dto.username(), repository::existsByName, repository::findByName) ||
                checkAndHandleExistingUser(dto.email(), repository::existsByEmail, repository::findByEmail);
        if (userExistsOrPending) {
            return RegisterUserResult.USER_ALREADY_EXISTS;
        }

        var user = factory.createUser(dto);
        System.out.println("Registered user: " + user);
        UserValidator validator = new UserValidator(user);

        emailService.sendPasswordEmail(user, validator);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        repository.save(user);
        kafkaUserValidatorService.saveValidator(validator);
        System.out.println(validator.getUuid());

        return RegisterUserResult.SUCCESS;
    }

    private boolean checkAndHandleExistingUser(String value, Predicate<String> existsCheck, Function<String, User> findUser) {
        if (existsCheck.test(value)) {

            var user = findUser.apply(value);
            if (user.getStatus() == UserStatus.PENDING) {
                sendNewVerificationKey(user);
            }
            return true;
        }
        return false;
    }

    private void sendNewVerificationKey(User user) {
        var optionalValidator = kafkaUserValidatorService.findByUserId(user.getId());
        if (optionalValidator == null) {
            UserValidator validator = new UserValidator(user);
            emailService.sendVerificationKey(user, validator);
            kafkaUserValidatorService.saveValidator(validator);
        }
    }

    public UpdateUserResult updateUser (UserUpdateDto dto) {
        if (dto.id() == 1) {
            return UpdateUserResult.ADMIN_EDIT_DENIED;
        }

        if (repository.existsByEmail(dto.email())) {
            var user = repository.findByEmail(dto.email());
            if (user.getStatus() != UserStatus.PENDING) {
                return UpdateUserResult.EMAIL_CONFLICT;
            }
        }

        var user = repository.getReferenceById(dto.id());
        factory.updateUser(user, dto);
        repository.save(user);

        return UpdateUserResult.SUCCESS;
    }

    public DeactivateUserResult deactivateUser(Long id) {
        if (id == 1L) {
            return DeactivateUserResult.ADMIN_DELETE_DENIED;
        }
        User user = repository.getReferenceById(id);

        if (user.getStatus() == UserStatus.NOT_ACTIVE) {
            return DeactivateUserResult.USER_ALREADY_NOT_ACTIVE;
        }

        user.deactivate();
        repository.save(user);

        return DeactivateUserResult.SUCCESS;
    }

    public VerifyUserResult verifyUser (String uuid) {
        try {
            UUID.fromString(uuid);
                var validator = kafkaUserValidatorService.findByUuid(uuid);

            if (validator.getExpirationDate().compareTo(Instant.now()) < 0) {
                kafkaUserValidatorService.deleteValidator(validator);
                return VerifyUserResult.EXPIRED_VALIDATION_DATE;
            }

            var user = validator.getIdUser();
            user.validate();
            repository.save(user);

            return VerifyUserResult.SUCCESS;

        }
        catch (NoUuidFoundException e) {
            return VerifyUserResult.USER_NOT_VERIFIED;
        }
        catch (IllegalArgumentException e) {
            return VerifyUserResult.INVALID_UUID_FORMAT;
        }
    }

    public UserDto checkCurrentUser(String username) {
        User user = repository.findByName(username);
        return new UserDto(user);
    }
}
