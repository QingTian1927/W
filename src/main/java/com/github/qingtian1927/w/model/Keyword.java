package com.github.qingtian1927.w.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "keywords")
@Data
@NoArgsConstructor
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false, length = 128, columnDefinition = "NVARCHAR(128)")
    private String word;

    @Column(name = "category", nullable = false)
    private String category;

    public Keyword(String word, String category) {
        this.word = word;
        this.category = category;
    }
}
