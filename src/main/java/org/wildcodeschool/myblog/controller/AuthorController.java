package org.wildcodeschool.myblog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcodeschool.myblog.dto.AuthorDTO;
import org.wildcodeschool.myblog.model.Author;
import org.wildcodeschool.myblog.model.ArticleAuthor;
import org.wildcodeschool.myblog.repository.AuthorRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<AuthorDTO> authorDTOs = authors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(authorDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(author));
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody AuthorDTO authorDTO) {
        Author author = new Author();
        author.setFirstname(authorDTO.getFirstname());
        author.setLastname(authorDTO.getLastname());
        Author savedAuthor = authorRepository.save(author);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedAuthor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author == null) {
            return ResponseEntity.notFound().build();
        }

        author.setFirstname(authorDTO.getFirstname());
        author.setLastname(authorDTO.getLastname());
        Author updatedAuthor = authorRepository.save(author);
        return ResponseEntity.ok(convertToDTO(updatedAuthor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author == null) {
            return ResponseEntity.notFound().build();
        }

        authorRepository.delete(author);
        return ResponseEntity.noContent().build();
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(author.getId());
        authorDTO.setFirstname(author.getFirstname());
        authorDTO.setLastname(author.getLastname());
        if (author.getArticleAuthors() != null) {
            authorDTO.setArticleAuthorIds(author.getArticleAuthors()
                    .stream()
                    .map(ArticleAuthor::getId)
                    .collect(Collectors.toList()));
        }
        return authorDTO;
    }
}
