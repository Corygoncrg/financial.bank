package com.example.users.service;

import com.example.shared.dto.UserDto;
import com.example.shared.model.User;
import com.example.shared.model.UserStatus;
import com.example.shared.model.UserValidator;
import com.example.users.dto.user.UserRegisterDto;
import com.example.users.dto.user.UserUpdateDto;
import com.example.users.factory.UserFactory;
import com.example.users.kafka.KafkaUserValidatorService;
import com.example.users.model.*;
import com.example.users.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    public boolean register(@Valid UserRegisterDto dto) {
        if (repository.existsByName(dto.username()) || repository.existsByEmail(dto.email())) {
            return false;
        }

        var user = factory.createUser(dto);
        System.out.println("Registered user: " + user);
        UserValidator validator = new UserValidator(user);

        emailService.sendPasswordEmail(user, validator);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        repository.save(user);
        kafkaUserValidatorService.saveValidator(validator);
        System.out.println(validator.getUuid());

        return true;
    }

    public UpdateUserResult updateUser (UserUpdateDto dto) {
        if (dto.id() == 1) {
            return UpdateUserResult.ADMIN_EDIT_DENIED;
        }

        if (repository.existsByEmail(dto.email())) {
            return UpdateUserResult.EMAIL_CONFLICT;
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
//            if (validator.isEmpty()){
//                return VerifyUserResult.USER_NOT_VERIFIED;
//            }


            if (validator.getExpirationDate().compareTo(Instant.now()) < 0) {
                //todo kafka
//                validatorRepository.delete(validator);
                kafkaUserValidatorService.deleteValidator(validator);
                return VerifyUserResult.EXPIRED_VALIDATION_DATE;
            }

            var user = validator.getIdUser();
            user.validate();
            repository.save(user);

            return VerifyUserResult.SUCCESS;

        } catch (IllegalArgumentException e) {
            return VerifyUserResult.INVALID_UUID_FORMAT;
        }
    }

    public UserDto checkCurrentUser(UserDetails userDetails) {
        User user = repository.findByName(userDetails.getUsername());
        return new UserDto(user);
    }
}
