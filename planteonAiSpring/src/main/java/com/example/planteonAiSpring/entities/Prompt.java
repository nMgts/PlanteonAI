package com.example.planteonAiSpring.entities;

import com.example.planteonAiSpring.types.PromptType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prompts")
@Getter
@Setter
public class Prompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String prompt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromptType type;

    private String description;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
