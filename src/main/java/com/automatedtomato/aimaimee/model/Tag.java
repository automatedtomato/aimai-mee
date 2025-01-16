package com.automatedtomato.aimaimee.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "tags")
public class Tag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "content", unique = true, nullable = false)
	private String name;
	
	@ManyToMany(mappedBy = "tags")
	private Set<Article> articles = new HashSet<>();
	
	public Tag() {}
	
	public Tag(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setContent(String name) {
		this.name = name;
	}

	public Set<Article> getArticles() {
		return articles;
	}

	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}
	
	
	
}
