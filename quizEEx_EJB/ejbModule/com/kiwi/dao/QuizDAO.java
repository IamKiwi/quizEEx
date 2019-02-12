package com.kiwi.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.kiwi.entities.Question;
import com.kiwi.entities.Quiz;
import com.kiwi.entities.User;

//DAO - Data Access Object for Quiz entity
//Designed to serve as an interface between higher layers of application and data.
//Implemented as stateless Enterprise Java bean - server side code that can be invoked even remotely.

@Stateless
public class QuizDAO {
	private final static String UNIT_NAME = "quizeexPU";

	// Dependency injection (no setter method is needed)
	@PersistenceContext(unitName = UNIT_NAME)
	protected EntityManager em;

	public void create(Quiz quiz) {
		em.persist(quiz);
	}

	public Quiz merge(Quiz quiz) {
		return em.merge(quiz);
	}

	public void remove(Quiz quiz) {
		em.remove(em.merge(quiz));
	}

	public Quiz find(Object id) {
		return em.find(Quiz.class, id);
	}

	public List<Quiz> getFullList() {
		List<Quiz> list = null;

		Query query = em.createQuery("select p from Quiz p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public Quiz getQuiz(int id) {
		Quiz single = null;

		Query query = em.createQuery("select p from Quiz p where id = :id");

		query.setParameter("id", id);

		try {
			single = (Quiz) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return single;
	}

	public void deactivateQuiz(Quiz quiz) {
		String select = "select p ";
		String from = "from Quiz p ";
		String where = "where id = :id";

		Query query = em.createQuery(select + from + where);

		query.setParameter("id", quiz.getId());

		Quiz edited = null;
		try {
			edited = (Quiz) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		edited.setActive(false);

		merge(edited);
	}

	public void activateQuiz(Quiz quiz) {
		String select = "select p ";
		String from = "from Quiz p ";
		String where = "where id = :id";

		Query query = em.createQuery(select + from + where);

		query.setParameter("id", quiz.getId());

		Quiz edited = null;
		try {
			edited = (Quiz) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		edited.setActive(true);

		merge(edited);
	}

	public List<String> getCategories() {
		Query query = em.createQuery("select distinct p.category from Quiz p");

		List<String> list = null;

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<Quiz> getQuizByCategory(String category) {
		String select = "select p ";
		String from = "from Quiz p ";
		String where = "where category = :category and active = 1";

		Query query = em.createQuery(select + from + where);

		query.setParameter("category", category);

		List<Quiz> quiz = null;
		try {
			quiz = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return quiz;
	}

	public int getQuizIdByName(String name) {
		String select = "select p ";
		String from = "from Quiz p ";
		String where = "where name = :name and active = 1";

		Query query = em.createQuery(select + from + where);

		query.setParameter("name", name);

		Quiz quiz = null;
		try {
			quiz = (Quiz) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return quiz.getId();
	}
	
	public boolean isQuizUnique(String name) {
		Quiz q = null;

		String select = "select p ";
		String from = "from Quiz p ";
		String where = "where p.name = :name";

		Query query = em.createQuery(select + from + where);

		query.setParameter("name", name);

		try {
			q = (Quiz) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (q == null)
			return true;
		else
			return false;
	}

	// Lazy Loading support
	public int getRowsCount() {
		Query query = em.createQuery("SELECT COUNT(*) FROM Quiz p");
		return ((Long) query.getSingleResult()).intValue();
	}

	public List<Quiz> retrieveQuizes(int first, int pageSize) {
		TypedQuery<Quiz> query = em.createQuery("select p from Quiz p", Quiz.class)
				.setFirstResult(first).setMaxResults(pageSize);
		return query.getResultList();
	}
}
