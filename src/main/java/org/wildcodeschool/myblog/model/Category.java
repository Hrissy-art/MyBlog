package org.wildcodeschool.myblog.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Article> articles;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public List<Article> getArticles() {   // Corrected method
        return articles;  // Return the list of articles
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    // Constructeurs
    public Category() {}

    public Category(String name) {
        this.name = name;
    }

}