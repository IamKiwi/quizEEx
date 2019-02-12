package com.kiwi.admin;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.kiwi.dao.UserDAO;
import com.kiwi.entities.Question;
import com.kiwi.entities.Quiz;
import com.kiwi.entities.User;

@ManagedBean
@ViewScoped
public class AdminBB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_USER_EDIT = "useredit?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	@EJB
	UserDAO userDAO;

	private LazyDataModel<User> users;

	@PostConstruct
	public void initialization() {
		this.setUsers(new LazyDataModel<User>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(userDAO.getRowsCount("admin"));
				return userDAO.retrieveUsers(first, pageSize, "admin");
			}
		});
	}

	public String adminHome() {
		return "dashboard";
	}

	public String adminUsers() {
		return "users";
	}

	public String adminQuizes() {
		return "quizes";
	}

	public List<User> getFullUserList() {
		return userDAO.getFullList();
	}

	public String editUser(User user) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute("user", user);
		return PAGE_USER_EDIT;
	}

	public String deactivateUser(User user) {
		userDAO.deactivateUser(user);
		return PAGE_STAY_AT_THE_SAME;
	}

	public String activateUser(User user) {
		userDAO.activateUser(user);
		return PAGE_STAY_AT_THE_SAME;
	}

	public LazyDataModel<User> getUsers() {
		return users;
	}

	public void setUsers(LazyDataModel<User> users) {
		this.users = users;
	}
}
