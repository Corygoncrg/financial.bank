package com.example.users.service;

import com.example.shared.dto.UserDto;
import com.example.shared.model.User;
import com.example.shared.model.UserStatus;
import com.example.users.dto.user.UserRegisterDto;
import com.example.users.dto.user.UserUpdateDto;
import com.example.users.model.*;
import com.example.users.repository.UserRepository;
import com.example.users.repository.UserValidatorRepository;
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
    private UserValidatorRepository validatorRepository;

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
        //todo kafkaSend user to receive validator
        UserValidator validator = new UserValidator(user);

        emailService.sendPasswordEmail(user, validator);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        repository.save(user);
        //todo kafka
        validatorRepository.save(validator);
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
            var validatorOptional = validatorRepository.findByUuid(uuid);
//todo kafka
            if (validatorOptional.isEmpty()){
                return VerifyUserResult.USER_NOT_VERIFIED;
            }

            var validator = validatorOptional.get();

            if (validator.getExpirationDate().compareTo(Instant.now()) < 0) {
                //todo kafka
                validatorRepository.delete(validator);
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
