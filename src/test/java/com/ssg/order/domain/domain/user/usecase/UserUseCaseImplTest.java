package com.ssg.order.domain.domain.user.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.domain.user.User;
import com.ssg.order.domain.domain.user.repository.UserReadRepository;
import com.ssg.order.domain.domain.user.repository.UserWriteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("사용자 유즈케이스 테스트")
@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {
    @InjectMocks
    private UserUseCaseImpl userUseCase;

    @Mock
    private UserReadRepository userReadRepository;

    @Mock
    private UserWriteRepository userWriteRepository;

    @Nested
    @DisplayName("사용자 정보 조회")
    class GetUserByUserName {
        @Test
        @DisplayName("사용자 로그인 ID로 조회 시 해당 사용자 정보가 리턴된다")
        void getUserByUserName_ShouldReturnUser() {
            // given
            String userName = "testUser";
            User expectedUser = createUser(1L, userName, "password123");
            when(userReadRepository.getUserByUserName(userName)).thenReturn(expectedUser);

            // when
            User result = userUseCase.getUserByUserName(userName);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUserName()).isEqualTo(userName);
            assertThat(result.getPassword()).isEqualTo("password123");
        }
    }

    @Nested
    @DisplayName("사용자 정보 저장")
    class SaveUser {
        @Test
        @DisplayName("사용자 정보 저장 시 repository의 saveUser가 호출된다")
        void saveUser_ShouldCallRepository() {
            // given
            User user = createUser(1L, "testUser", "password123");

            // when
            userUseCase.saveUser(user);

            // then
            org.mockito.Mockito.verify(userWriteRepository).saveUser(user);
        }
    }

    @Nested
    @DisplayName("사용자 존재 여부 확인")
    class IsUserExists {
        @Test
        @DisplayName("존재하는 사용자 로그인 ID로 확인 시 true가 리턴된다")
        void isUserExists_WithExistingUser_ShouldReturnTrue() {
            // given
            String userName = "existingUser";
            when(userReadRepository.isExistUser(userName)).thenReturn(true);

            // when
            boolean result = userUseCase.isUserExists(userName);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 사용자 로그인 ID로 확인 시 false가 리턴된다")
        void isUserExists_WithNonExistingUser_ShouldReturnFalse() {
            // given
            String userName = "nonExistingUser";
            when(userReadRepository.isExistUser(userName)).thenReturn(false);

            // when
            boolean result = userUseCase.isUserExists(userName);

            // then
            assertThat(result).isFalse();
        }
    }

    private User createUser(Long id, String userName, String password) {
        return User.builder()
            .id(id)
            .userName(userName)
            .password(password)
            .build();
    }
}