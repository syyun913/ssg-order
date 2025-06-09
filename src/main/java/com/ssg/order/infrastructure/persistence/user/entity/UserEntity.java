package com.ssg.order.infrastructure.persistence.user.entity;

import com.ssg.order.infrastructure.persistence.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Entity
@Builder
@AllArgsConstructor
@DynamicInsert
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false, columnDefinition = "varchar(100)")
    private String userName;

    @Column(name = "password", nullable = false, columnDefinition = "varchar(100)")
    private String password;
}
