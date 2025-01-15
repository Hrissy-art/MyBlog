package org.wildcodeschool.myblog.dto;

import java.util.List;

public class ImageDTO {
    private Long id;
    private String url;
    private List<Long> articlesIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter et Setter pour 'url'
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Getter et Setter pour 'articlesIds'
    public List<Long> getArticlesIds() {
        return articlesIds;
    }

    public void setArticlesIds(List<Long> articlesIds) {
        this.articlesIds = articlesIds;
    }
}

