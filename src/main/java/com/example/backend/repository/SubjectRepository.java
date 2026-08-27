package com.example.backend.repository;

import com.example.backend.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long>{

    List<Subject> findByActiveTrue();
    
}
