package com.gut.quiz.repository;

import com.gut.quiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByCodeAndFirstNameAndLastNameAndMiddleName(String code, String firstName, String lastName, String middleName);

    boolean existsByCodeAndFirstNameAndLastName(String code, String firstName, String lastName);
}
