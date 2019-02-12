package com.kiwi.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.kiwi.entities.Answer;
import com.kiwi.entities.Question;
import com.kiwi.entities.Quiz;

//DAO - Data Access Object for Answer entity
//Designed to serve as an interface between higher layers of application and data.
//Implemented as stateless Enterprise Java bean - server side code that can be invoked even remotely.

@Stateless
public class AnswerDAO {
	private final static String UNIT_NAME = "quizeexPU";

	// Dependency injection (no setter method is needed)
	@PersistenceContext(unitName = UNIT_NAME)
	protected EntityManager em;

	public void create(Answer answer) {
		em.persist(answer);
	}

	public Answer merge(Answer answer) {
		return em.merge(answer);
	}

	public void remove(Answer answer) {
		em.remove(em.merge(answer));
	}

	public Answer find(Object id) {
		return em.find(Answer.class, id);
	}

	public List<Answer> getFullList() {
		List<Answer> list = null;

		Query query = em.createQuery("select p from Answer p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public List<Answer> getAnswersForQuestion(int id) {
		List<Answer> list = null;

		Query query = em.createQuery("select p from Answer p where p.idQuestion = :id");
		query.setParameter("id", id);

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public Answer getCorrectAnswersForQuestion(int id) {
		Answer list = null;

		Query query = em.createQuery("select p from Answer p where p.idQuestion = :id and p.isCorrect = 1");
		query.setParameter("id", id);

		try {
			list = (Answer) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public void changeToCorrect(Answer answer) {
		String select = "select p ";
		String from = "from Answer p ";
		String where = "where id = :id";

		Query query = em.createQuery(select + from + where);

		query.setParameter("id", answer.getId());
		
		Answer edited = null;
		try {
			edited = (Answer) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		edited.setIsCorrect(true);
		
		merge(edited);
	}
	
	public void changeToIncorrect(Answer answer) {
		String select = "select p ";
		String from = "from Answer p ";
		String where = "where id = :id";

		Query query = em.createQuery(select + from + where);

		query.setParameter("id", answer.getId());
		
		Answer edited = null;
		try {
			edited = (Answer) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		edited.setIsCorrect(false);
		
		merge(edited);
	}
	
	// Lazy Loading support
	public int getRowsCount(int id) {
        Query query = em.createQuery("SELECT COUNT(*) FROM Answer p where p.idQuestion = :id");
        query.setParameter("id", id);
        return ((Long) query.getSingleResult()).intValue();
    }
	
	 public List<Answer> retrieveAnswersForQuestion(int id, int first, int pageSize) {
	        TypedQuery<Answer> query = em.createQuery("select p from Answer p where p.idQuestion = :id", Answer.class)
	            .setFirstResult(first)
	            .setMaxResults(pageSize);
	        query.setParameter("id", id);
	        return query.getResultList();
	 }
}
