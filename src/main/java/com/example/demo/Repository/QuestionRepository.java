package com.example.demo.Repository;

import com.example.demo.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findBySkillId(UUID skillId);
    List<Question> findBySkill_Name(String name);
}
