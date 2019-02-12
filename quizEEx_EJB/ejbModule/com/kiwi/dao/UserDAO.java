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
import com.kiwi.entities.User;

//DAO - Data Access Object for User entity
//Designed to serve as an interface between higher layers of application and data.
//Implemented as stateless Enterprise Java bean - server side code that can be invoked even remotely.

@Stateless
public class UserDAO {
	private final static String UNIT_NAME = "quizeexPU";

	// Dependency injection (no setter method is needed)
	@PersistenceContext(unitName = UNIT_NAME)
	protected EntityManager em;

	public void create(User user) {
		em.persist(user);
	}

	public User merge(User user) {
		return em.merge(user);
	}

	public void remove(User user) {
		em.remove(em.merge(user));
	}

	public User find(Object id) {
		return em.find(User.class, id);
	}

	public List<User> getAllUsersPoints() {
		List<User> list = null;

		Query query = em.createQuery("select p from User p where p.id <> 1");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public void addPoints(int points, int id) {
		String select = "select p ";
		String from = "from User p ";
		String where = "where id = :id";

		Query query = em.createQuery(select + from + where);

		query.setParameter("id", id);

		User edited = null;
		try {
			edited = (User) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int totalPoints = 0;
		int pointsInDB = edited.getPoints();

		if (pointsInDB == 0)
			totalPoints = points;
		else
			totalPoints = edited.getPoints() + points;

		edited.setPoints(totalPoints);

		merge(edited);
	}

	public List<User> getFullList() {
		List<User> list = null;

		Query query = em.createQuery("select p from User p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public User getUserFromDatabase(String login, String pass) {
		User u = null;

		String select = "select p ";
		String from = "from User p ";
		String where = "where p.email = :login and p.password = :pass";

		Query query = em.createQuery(select + from + where);

		query.setParameter("login", login);
		query.setParameter("pass", pass);

		try {
			u = (User) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return u;
	}

	public void deactivateUser(User user) {
		String select = "select p ";
		String from = "from User p ";
		String where = "where id = :id";

		Query query = em.createQuery(select + from + where);

		query.setParameter("id", user.getId());

		User edited = null;
		try {
			edited = (User) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		edited.setActive(false);

		merge(edited);
	}

	public void activateUser(User user) {
		String select = "select p ";
		String from = "from User p ";
		String where = "where id = :id";

		Query query = em.createQuery(select + from + where);

		query.setParameter("id", user.getId());

		User edited = null;
		try {
			edited = (User) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		edited.setActive(true);

		merge(edited);
	}

	/**
	 * 
	 * @param email
	 * @return true if email address is unique otherwise return false.
	 */
	public boolean isEmailUnique(String email) {
		User u = null;

		String select = "select p ";
		String from = "from User p ";
		String where = "where p.email = :email";

		Query query = em.createQuery(select + from + where);

		query.setParameter("email", email);

		try {
			u = (User) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (u == null)
			return true;
		else
			return false;
	}

	public List<String> getUserRolesFromDatabase(User user) {

		ArrayList<String> roles = new ArrayList<String>();

		if (user.getEmail().equals("admin@quizeex.com")) {
			roles.add("admin");
		} else {
			roles.add("user");
		}

		return roles;
	}

	public List<User> getList(Map<String, Object> searchParams) {
		List<User> list = null;

		// 1. Build query string with parameters
		String select = "select p ";
		String from = "from User p ";
		String where = "";
		String orderby = "order by p.surname asc, p.name";

		// search for surname
		String surname = (String) searchParams.get("surname");
		if (surname != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			where += "p.surname like :surname ";
		}

		// ... other parameters ...

		// 2. Create query object
		Query query = em.createQuery(select + from + where + orderby);

		// 3. Set configured parameters
		if (surname != null) {
			query.setParameter("surname", surname + "%");
		}

		// ... other parameters ...

		// 4. Execute query and retrieve list of User objects
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// Lazy Loading support
	public int getRowsCount(String mode) {
		Query query = null;
		
		if(mode.equals("ranking"))
			query = em.createQuery("SELECT COUNT(*) FROM User p where p.id <> 1");
		else if(mode.equals("admin"))
			query = em.createQuery("SELECT COUNT(*) FROM User p");
		
		
		return ((Long) query.getSingleResult()).intValue();
	}

	public List<User> retrieveUsers(int first, int pageSize, String mode) {
		TypedQuery<User> query = null;
		
		if(mode.equals("ranking")) {
			query = em.createQuery("select p from User p where p.id <> 1 order by p.points desc", User.class)
					.setFirstResult(first).setMaxResults(pageSize);
		} else if(mode.equals("admin")) {
			query = em.createQuery("select p from User p", User.class)
				.setFirstResult(first).setMaxResults(pageSize);
		}
		
		return query.getResultList();
	}

}
