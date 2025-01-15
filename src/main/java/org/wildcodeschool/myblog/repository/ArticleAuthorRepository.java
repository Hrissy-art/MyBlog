package org.wildcodeschool.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wildcodeschool.myblog.model.ArticleAuthor;

public interface ArticleAuthorRepository extends JpaRepository<ArticleAuthor, Long> {
}