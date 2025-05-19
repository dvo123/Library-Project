package com.dung.library.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dung.library.user.model.Student; // Fixed import
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByName(String name);
}