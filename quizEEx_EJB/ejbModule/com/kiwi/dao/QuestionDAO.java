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

//DAO - Data Access Object for Question entity
//Designed to serve as an interface between higher layers of application and data.
//Implemented as stateless Enterprise Java bean - server side code that can be invoked even remotely.

@Stateless
public class QuestionDAO {
	private final static String UNIT_NAME = "quizeexPU";

	// Dependency injection (no setter method is needed)
	@PersistenceContext(unitName = UNIT_NAME)
	protected EntityManager em;

	public void create(Question quiz) {
		em.persist(quiz);
	}

	public Question merge(Question quiz) {
		return em.merge(quiz);
	}

	public void remove(Question quiz) {
		em.remove(em.merge(quiz));
	}

	public Question find(Object id) {
		return em.find(Question.class, id);
	}

	public List<Question> getFullList() {
		List<Question> list = null;

		Query query = em.createQuery("select p from Question p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public List<Question> getQuestionsForQuiz(int id) {
		List<Question> list = null;

		Query query = em.createQuery("select p from Question p where p.idQuiz = :id");
		query.setParameter("id", id);

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	// Lazy Loading support
	public int getRowsCount(int id) {
        Query query = em.createQuery("SELECT COUNT(*) FROM Question p where p.idQuiz = :id");
        query.setParameter("id", id);
        return ((Long) query.getSingleResult()).intValue();
    }
	
	 public List<Question> retrieveQuestionsForQuiz(int id, int first, int pageSize) {
	        TypedQuery<Question> query = em.createQuery("select p from Question p where p.idQuiz = :id", Question.class)
	            .setFirstResult(first)
	            .setMaxResults(pageSize);
	        query.setParameter("id", id);
	        return query.getResultList();
	 }
}
