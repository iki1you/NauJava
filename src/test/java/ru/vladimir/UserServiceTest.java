package ru.vladimir;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vladimir.dto.UserRequest;
import ru.vladimir.model.User;
import ru.vladimir.repository.UserRepository;
import ru.vladimir.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest {
    @Test
    void addUserEncodesPasswordAndSavesUser() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService service = new UserService(repository, passwordEncoder);
        UserRequest request = new UserRequest("student", "Иван", "Иванов", "test-password");
        when(passwordEncoder.encode("test-password")).thenReturn("encoded-password");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.addUser(request);

        assertEquals("student", user.getLogin());
        assertEquals("encoded-password", user.getPasswordHash());
        verify(passwordEncoder).encode("test-password");
        verify(repository).save(user);
    }

    @Test
    void addUserRejectsBlankLogin() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService service = new UserService(repository, passwordEncoder);
        UserRequest request = new UserRequest("", "Иван", "Иванов", "test-password");

        assertThrows(IllegalArgumentException.class, () -> service.addUser(request));

        verifyNoInteractions(repository, passwordEncoder);
    }

    @Test
    void updateUserRejectsMissingUser() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService service = new UserService(repository, passwordEncoder);
        UserRequest request = new UserRequest("student", "Иван", "Иванов", "test-password");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.updateUser(99L, request, true));

        verify(repository).findById(99L);
        verify(repository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }
}
