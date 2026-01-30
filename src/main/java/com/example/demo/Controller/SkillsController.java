package com.example.demo.Controller;

import com.example.demo.Entity.Question;
import com.example.demo.Entity.Skill;
import com.example.demo.Repository.QuestionRepository;
import com.example.demo.Repository.SkillRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SkillsController {
    private final SkillRepository skillRepo;
    private final QuestionRepository questionRepo;

    public SkillsController(SkillRepository skillRepo, QuestionRepository questionRepo) {
        this.skillRepo = skillRepo;
        this.questionRepo = questionRepo;
    }

    // 🔹 GET /skills
    @GetMapping("/skills")
    public List<Skill> getSkills() {
        return skillRepo.findAll();
    }

    // 🔹 GET /questions/{skillId}
    @GetMapping("/questions/{skillId}")
    public List<Question> getQuestions(@PathVariable UUID skillId) {
        return questionRepo.findBySkillId(skillId);
    }
}
