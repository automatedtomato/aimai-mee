package com.automatedtomato.aimaimee.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;


@Entity
@Table(name="articles")
public class Article {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@Column(name = "content", columnDefinition = "TEXT")
	private String content;
	
	@Column(name = "creation_date")
	private LocalDateTime createdAt;
	
	@Column(name = "modification_date")
	private LocalDateTime lastModifiedAt;
	
	@Column(name = "likes")
	private int numsOfLikes;
	
	@ManyToMany
	@JoinTable(
		name = "article_tags",
		joinColumns = @JoinColumn(name = "article_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id")
		)
	private Set<Tag> tags;
	
	public Article() {
		this.createdAt = LocalDateTime.now();
		this.lastModifiedAt = LocalDateTime.now();
		this.numsOfLikes = 0;
		this.tags = new HashSet<>();
	}
	
	public Article(User author, String title, String content, Set<Tag> tags) {
		this();
		this.author = author;
		this.title = title;
		this.content = content;
		this.tags = tags;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public int getNumsOfLikes() {
		return numsOfLikes;
	}

	public void setNumsOfLikes(int numsOfLikes) {
		this.numsOfLikes = numsOfLikes;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	// Helper methods for tag management
	public void addTag(Tag tag) {
		this.tags.add(tag);
		tag.getArticles().add(this);
	}
	
	public void removeTag(Tag tag) {
		this.tags.remove(tag);
		tag.getArticles().remove(this);
	}

}
