package com.ssg.order.infrastructure.persistence.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.user.User;
import com.ssg.order.infrastructure.persistence.user.entity.UserEntity;
import com.ssg.order.infrastructure.persistence.user.mapper.UserPersistenceMapper;
import com.ssg.order.infrastructure.persistence.user.repository.UserJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("사용자 레포지토리 테스트")
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @InjectMocks
    private UserRepository userRepository;

    @Mock
    private UserPersistenceMapper mapper;

    @Mock
    private UserJpaRepository userJpaRepository;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .userName("testUser")
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .userName("testUser")
                .build();
    }

    @DisplayName("username으로 사용자 조회")
    @Nested
    class GetUserByUserNameTests {

        @Test
        @DisplayName("username으로 사용자 조회 시 존재한다면 사용자 정보를 리턴한다")
        void getUserByUserName_Success() {
            // given
            when(userJpaRepository.findByUserName("testUser")).thenReturn(Optional.of(userEntity));
            when(mapper.toDomain(any(UserEntity.class))).thenReturn(user);

            // when
            User foundUser = userRepository.getUserByUserName("testUser");

            // then
            assertThat(foundUser).isNotNull();
            assertThat(foundUser.getUserName()).isEqualTo("testUser");
        }

        @Test
        @DisplayName("username으로 사용자 조회 시 사용자가 존재하지 않으면 예외를 리턴한다")
        void getUserByUserName_NotFound_ThrowsException() {
            // given
            when(userJpaRepository.findByUserName("nonExistentUser")).thenReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userRepository.getUserByUserName("nonExistentUser")
            );

            // then
            assertAll(
                () -> assertEquals(BusinessErrorCode.NOT_FOUND_USER, exception.getErrorCode()),
                () -> assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage())
            );
        }
    }

    @DisplayName("사용자 존재 여부 조회")
    @Nested
    class IsExistUserTests {

        @Test
        @DisplayName("사용자가 존재하는 경우 true를 리턴한다")
        void isExistUser_Exists_ReturnsTrue() {
            // given
            when(userJpaRepository.findByUserName("testUser")).thenReturn(Optional.of(userEntity));

            // when
            boolean exists = userRepository.isExistUser("testUser");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("사용자가 존재하지 않는 경우 false를 리턴한다")
        void isExistUser_NotExists_ReturnsFalse() {
            // given
            when(userJpaRepository.findByUserName("nonExistentUser")).thenReturn(Optional.empty());

            // when
            boolean exists = userRepository.isExistUser("nonExistentUser");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Test
    @DisplayName("사용자 저장 성공 시 예외가 발생하지 않는다.")
    void saveUser_Success() {
        // given
        when(mapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // when & then
        assertDoesNotThrow(() -> userRepository.saveUser(user));
    }
} 