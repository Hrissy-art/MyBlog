package org.wildcodeschool.myblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcodeschool.myblog.dto.ArticleDTO;
import org.wildcodeschool.myblog.model.Article;
import org.wildcodeschool.myblog.model.Category;
import org.wildcodeschool.myblog.repository.ArticleRepository;
import org.wildcodeschool.myblog.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;

    public ArticleController(ArticleRepository articleRepository, CategoryRepository categoryRepository) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;

    }

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ArticleDTO> articleDTOs = articles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(articleDTOs);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(article));
    }
    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody Article article) {
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        if (article.getCategory() != null) {
            Category category = categoryRepository.findById(article.getCategory().getId()).orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().body(null);
            }
            article.setCategory(category);
        }
        Article savedArticle = articleRepository.save(article);
        ArticleDTO articleDTO = convertToDTO(savedArticle);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleDTO);    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {

        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        article.setTitle(articleDetails.getTitle());
        article.setContent(articleDetails.getContent());
        article.setUpdatedAt(LocalDateTime.now());
        if (articleDetails.getCategory() != null) {
            Category category = categoryRepository.findById(articleDetails.getCategory().getId()).orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().body(null);
            }
            article.setCategory(category);
        }
        Article updatedArticle = articleRepository.save(article);
        ArticleDTO articleDTO = convertToDTO(updatedArticle);

        return ResponseEntity.ok(articleDTO);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {

        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        articleRepository.delete(article);
        return ResponseEntity.noContent().build();
    }

    //example postman http://localhost:8080/articles/searchContent?content=edited
    @GetMapping("/searchContent")
    public ResponseEntity<List<ArticleDTO>> getArticlesContent(@RequestParam String content) {
        List<Article> articles = articleRepository.findByCaracters(content);

        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ArticleDTO> articleDTOs = articles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(articleDTOs);
    }
//example : http://localhost:8080/articles/createdAfter?date=2023-01-01T10:00:00

    @GetMapping("/createdAfter")
    public ResponseEntity<List<ArticleDTO>> getArticlesCreatedAt(@RequestParam String date) {
        try {
            LocalDateTime localDate = LocalDateTime.parse(date);
            List<Article> articles = articleRepository.findByCreatedAt(localDate);
            if (articles.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<ArticleDTO> articleDTOs = articles.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(articleDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/lastArticles")
    public ResponseEntity<List<ArticleDTO>> getFiveLastArticles() {
        List<Article> articles = articleRepository.findTop5ByOrderByCreatedAtDesc();
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ArticleDTO> articleDTOs = articles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(articleDTOs);
    }
    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setUpdatedAt(article.getUpdatedAt());
        if (article.getCategory() != null) {
            articleDTO.setCategoryName(article.getCategory().getName());
        }
        return articleDTO;
    }
}
