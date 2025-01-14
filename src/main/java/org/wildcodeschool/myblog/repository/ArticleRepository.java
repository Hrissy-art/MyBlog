package org.wildcodeschool.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wildcodeschool.myblog.model.Article;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a WHERE a.content LIKE %:content%")
    List<Article> findByCaracters(String content);
    @Query("SELECT a FROM Article a WHERE a.createdAt > :date")
    List<Article> findByCreatedAt(LocalDateTime date);
    List<Article> findTop5ByOrderByCreatedAtDesc();

}