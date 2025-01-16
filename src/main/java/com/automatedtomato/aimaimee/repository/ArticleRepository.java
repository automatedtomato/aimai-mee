package com.automatedtomato.aimaimee.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automatedtomato.aimaimee.model.Article;
import com.automatedtomato.aimaimee.model.Tag;
import com.automatedtomato.aimaimee.model.User;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	List<Article> findByAuthor(User author);
	boolean existsByAuthor(User author);
	int countByAuthor(User author);
	
	Article findByTitle(String title);
	List<Article> findByTitleContaining(String title);
	boolean existsByTitle(String title);
	
	List<Article> findByTags(Tag tag);
	List<Article> findByTagsContaining(Tag tag);
	boolean existsByTags(String tagName);
	
	List<Article> findByContentContaining(String contentPart);
	boolean existsByContentContaining(String contentPart);
	
	List<Article> findByCreatedAtAfter(LocalDateTime createdAt);
	
	List<Article> findByLastModifiedAtAfter(LocalDateTime lastModifiedAt);
	
	List<Article> findAllByOrderByNsumsOfLikesDesc(int numsOfLikes);
}
