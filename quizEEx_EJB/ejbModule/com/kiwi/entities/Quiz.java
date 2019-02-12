package com.kiwi.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the quizzes database table.
 * 
 */
@Entity
@Table(name="quizzes")
@NamedQuery(name="Quiz.findAll", query="SELECT q FROM Quiz q")
public class Quiz implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private boolean active;

	@Lob
	private String category;

	@Lob
	private String name;

	public Quiz() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}