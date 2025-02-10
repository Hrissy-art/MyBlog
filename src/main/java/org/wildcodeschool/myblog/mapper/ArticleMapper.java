package org.wildcodeschool.myblog.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wildcodeschool.myblog.dto.ArticleCreateDTO;
import org.wildcodeschool.myblog.dto.ArticleDTO;
import org.wildcodeschool.myblog.dto.AuthorDTO;
import org.wildcodeschool.myblog.model.*;
import org.wildcodeschool.myblog.repository.ArticleRepository;
import org.wildcodeschool.myblog.repository.AuthorRepository;
import org.wildcodeschool.myblog.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArticleMapper {
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final ArticleRepository articleRepository;
    @Autowired
    public ArticleMapper(CategoryRepository categoryRepository, AuthorRepository authorRepository, ArticleRepository articleRepository) {
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.articleRepository = articleRepository;
    }
    public ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setUpdatedAt(article.getUpdatedAt());
        if (article.getCategory() != null) {
            articleDTO.setCategoryName(article.getCategory().getName());
        }
        if (article.getImages() != null) {
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

    public Article convertToEntity(ArticleCreateDTO articleCreateDTO) {
        Article article = new Article();

        // Mapper les propriétés simples
        article.setTitle(articleCreateDTO.getTitle());
        article.setContent(articleCreateDTO.getContent());

        // Assigner la catégorie (vérifie si tu as une entité 'Category' et si tu dois la chercher dans la DB)
        Category category = categoryRepository.findById(articleCreateDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        article.setCategory(category);

        List<Image> images = articleCreateDTO.getImages().stream().map(imageDTO -> {
            Image image = new Image();
            image.setUrl(imageDTO.getUrl());

            // Associer les articles à cette image (si l'ID d'article existe)
            List<Article> associatedArticles = imageDTO.getArticlesIds().stream()
                    .map(id -> articleRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Article non trouvé avec l'ID : " + id)))
                    .collect(Collectors.toList());

            image.setArticles(associatedArticles); // Lier l'image aux articles
            return image;
        }).collect(Collectors.toList());

        article.setImages(images); // Associer les images à l'article

        // Ajouter les auteurs
        List<ArticleAuthor> articleAuthors = articleCreateDTO.getAuthors().stream().map(authorContributionDTO -> {
            ArticleAuthor articleAuthor = new ArticleAuthor();

            // Créer un objet 'Author' basé sur l'ID (assume que tu as une entité Author)
            Author author = authorRepository.findById(authorContributionDTO.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Auteur introuvable"));

            articleAuthor.setAuthor(author);
            articleAuthor.setContribution(authorContributionDTO.getContribution());

            return articleAuthor;
        }).collect(Collectors.toList());

        article.setArticleAuthors(articleAuthors);

        return article;
    }
}
