package org.wildcodeschool.myblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcodeschool.myblog.dto.ArticleDTO;
import org.wildcodeschool.myblog.dto.AuthorDTO;
import org.wildcodeschool.myblog.model.*;
import org.wildcodeschool.myblog.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final AuthorRepository authorRepository;
    private final ArticleAuthorRepository articleAuthorRepository;



    public ArticleController(ArticleRepository articleRepository, CategoryRepository categoryRepository, ImageRepository imageRepository, AuthorRepository authorRepository, ArticleAuthorRepository articleAuthorRepository) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.authorRepository = authorRepository;
        this.articleAuthorRepository = articleAuthorRepository;

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
     if (article.getImages() != null && !article.getImages().isEmpty()) {
        List<Image> validImages = new ArrayList<>();
        for (Image image : article.getImages()) {
            if (image.getId() != null) {
                // Vérification des images existantes
                Image existingImage = imageRepository.findById(image.getId()).orElse(null);
                if (existingImage != null) {
                    validImages.add(existingImage);
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            } else {
                // Création de nouvelles images
                Image savedImage = imageRepository.save(image);
                validImages.add(savedImage);
            }
        }
        article.setImages(validImages);
    }
        Article savedArticle = articleRepository.save(article);
        if (article.getArticleAuthors() != null) {
            for (ArticleAuthor articleAuthor : article.getArticleAuthors()) {
                Author author = articleAuthor.getAuthor();
                author = authorRepository.findById(author.getId()).orElse(null);
                if (author == null) {
                    return ResponseEntity.badRequest().body(null);
                }

                articleAuthor.setAuthor(author);
                articleAuthor.setArticle(savedArticle);
                articleAuthor.setContribution(articleAuthor.getContribution());

                articleAuthorRepository.save(articleAuthor);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedArticle));
      }

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
        if (articleDetails.getImages() != null) {
            List<Image> validImages = new ArrayList<>();
            for (Image image : articleDetails.getImages()) {
                if (image.getId() != null) {
                    // Vérification des images existantes
                    Image existingImage = imageRepository.findById(image.getId()).orElse(null);
                    if (existingImage != null) {
                        validImages.add(existingImage);
                    } else {
                        return ResponseEntity.badRequest().build(); // Image non trouvée, retour d'une erreur
                    }
                } else {
                    // Création de nouvelles images
                    Image savedImage = imageRepository.save(image);
                    validImages.add(savedImage);
                }
            }
            // Mettre à jour la liste des images associées
            article.setImages(validImages);
        } else {
            // Si aucune image n'est fournie, on nettoie la liste des images associées
            article.getImages().clear();
        }
        if (articleDetails.getArticleAuthors() != null) {
            // Supprimer manuellement les anciens ArticleAuthor
            for (ArticleAuthor oldArticleAuthor : article.getArticleAuthors()) {
                articleAuthorRepository.delete(oldArticleAuthor);
            }

            List<ArticleAuthor> updatedArticleAuthors = new ArrayList<>();

            for (ArticleAuthor articleAuthorDetails : articleDetails.getArticleAuthors()) {
                Author author = articleAuthorDetails.getAuthor();
                author = authorRepository.findById(author.getId()).orElse(null);
                if (author == null) {
                    return ResponseEntity.badRequest().build();
                }

                // Créer et associer la nouvelle relation ArticleAuthor
                ArticleAuthor newArticleAuthor = new ArticleAuthor();
                newArticleAuthor.setAuthor(author);
                newArticleAuthor.setArticle(article);
                newArticleAuthor.setContribution(articleAuthorDetails.getContribution());

                updatedArticleAuthors.add(newArticleAuthor);
            }

            for (ArticleAuthor articleAuthor : updatedArticleAuthors) {
                articleAuthorRepository.save(articleAuthor);
            }

            article.setArticleAuthors(updatedArticleAuthors);
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
// Supprimer les associations ArticleAuthor manuellement
        if (article.getArticleAuthors() != null) {
            for (ArticleAuthor articleAuthor : article.getArticleAuthors()) {
                articleAuthorRepository.delete(articleAuthor);

    }

    }
        // Supprimer l'article lui-même
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
        }if (article.getImages() != null) {
            articleDTO.setImageUrls(article.getImages().stream().map(Image::getUrl).collect(Collectors.toList()));
        }

        if (article.getArticleAuthors() != null) {
            articleDTO.setAuthors(article.getArticleAuthors().stream()
                    .filter(articleAuthor -> articleAuthor.getAuthor() != null)
                    .map(articleAuthor -> {
                        AuthorDTO authorDTO = new AuthorDTO();
                        authorDTO.setId(articleAuthor.getAuthor().getId());
                        authorDTO.setFirstname(articleAuthor.getAuthor().getFirstname());
                        authorDTO.setLastname(articleAuthor.getAuthor().getLastname());
                        return authorDTO;
                    })
                    .collect(Collectors.toList()));
        }
        return articleDTO;
    }
}
