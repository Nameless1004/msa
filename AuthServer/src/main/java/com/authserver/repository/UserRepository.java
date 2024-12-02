package com.authserver.repository;

import com.authserver.entity.User;
import com.authserver.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email=:email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.nickname=:nickname")
    Optional<User> findByNickname(@Param("nickname") String nickname);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :id AND u.isDeleted = true")
    boolean isDeletedUser(@Param("id") String userId);

    default User findByEmailOrElseThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new NotFoundException("존재하지 않는 이메일입니다."));
    }

    default User findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }
}
