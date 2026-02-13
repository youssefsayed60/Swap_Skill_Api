package com.example.demo.Controller;

import com.example.demo.Entity.Question;
import com.example.demo.Entity.Skill;
import com.example.demo.Repository.QuestionRepository;
import com.example.demo.Repository.SkillRepository;
import com.example.demo.Services.OllamaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SkillsController {
    private final SkillRepository skillRepo;
    private final QuestionRepository questionRepo;
    private final OllamaService ollamaService;


    public SkillsController(SkillRepository skillRepo, QuestionRepository questionRepo, OllamaService ollamaService) {
        this.skillRepo = skillRepo;
        this.questionRepo = questionRepo;
        this.ollamaService = ollamaService;
    }

    @GetMapping("/skills")
    public List<Skill> getSkills() {
        return skillRepo.findAll();
    }

    @GetMapping("/questions/{skillId}")
    public List<Question> getQuestions(@PathVariable UUID skillId) {

        Skill skill = skillRepo.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        List<String> generatedQuestions;

        try {
            generatedQuestions =
                    ollamaService.generateQuestions(skill.getName(), "medium", 5);

            if (generatedQuestions.isEmpty()) {
                throw new RuntimeException("AI returned empty response");
            }

        } catch (Exception e) {

            List<Question> existing = questionRepo.findBySkillId(skillId);

            if (!existing.isEmpty()) {
                return existing;
            }

            throw new RuntimeException("Failed to generate questions: " + e.getMessage());
        }

        questionRepo.deleteBySkillId(skillId);

        List<Question> savedQuestions = generatedQuestions.stream()
                .map(q -> {
                    Question question = new Question();
                    question.setId(UUID.randomUUID());
                    question.setQuestion(q);
                    question.setSkill(skill);
                    return question;
                })
                .toList();

        questionRepo.saveAll(savedQuestions);

        return savedQuestions;
    }



    @GetMapping("/questions/by-name/{skillName}")
    public List<Question> getQuestionsBySkillName(@PathVariable String skillName) {
        return questionRepo.findBySkill_Name(skillName);
    }

    @PostMapping("/ai/questions")
    public List<String> generateAiQuestions(@RequestBody Map<String, Object> body) {

        String skill = (String) body.get("skill");
        String difficulty = body.getOrDefault("difficulty", "medium").toString();
        int count = Integer.parseInt(body.getOrDefault("count", 10).toString());

        return ollamaService.generateQuestions(skill, difficulty, count);
    }

}
