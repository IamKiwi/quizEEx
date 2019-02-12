package com.kiwi.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.kiwi.dao.UserDAO;
import com.kiwi.entities.Question;
import com.kiwi.entities.Quiz;
import com.kiwi.entities.User;

@ManagedBean
@ViewScoped
public class UserBB {
	private static final String PAGE_RANKING = "ranking?faces-redirect=true";

	@EJB
	UserDAO userDAO;

	private LazyDataModel<User> users;

	@PostConstruct
	public void initialization() {
		this.setUsers(new LazyDataModel<User>() {
			private static final long serialVersionUID = 1L;
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			
			@Override
			public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(userDAO.getRowsCount("ranking"));
				return userDAO.retrieveUsers(first, pageSize, "ranking");
			}
		});
	}

	public String userHome() {
		return "dashboard";
	}

	public String ranking() {
		return "ranking";
	}

	public String catSelect() {
		return "catselect";
	}

	public String hallOfFame() {
		return PAGE_RANKING;
	}

	public LazyDataModel<User> getUsers() {
		return users;
	}

	public void setUsers(LazyDataModel<User> users) {
		this.users = users;
	}
}
