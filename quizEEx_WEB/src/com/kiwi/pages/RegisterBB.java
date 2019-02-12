package com.kiwi.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.kiwi.dao.AnswerDAO;
import com.kiwi.dao.UserDAO;
import com.kiwi.entities.Answer;
import com.kiwi.entities.Question;
import com.kiwi.entities.User;

@Named
@ViewScoped
public class RegisterBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_USER = "/pages/user/dashboard?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private User user = new User();
	private User loaded = null;

	private String passConfirm;

	@EJB
	UserDAO userDAO;
	
	private ResourceBundle messages;

	public ResourceBundle getMessages() {
		return messages;
	}

	public void setMessages(ResourceBundle messages) {
		this.messages = messages;
	}

	@PostConstruct
	public void PostConstruct() {		
		FacesContext context = FacesContext.getCurrentInstance();
		this.messages = ResourceBundle.getBundle("resources.regbean", context.getViewRoot().getLocale());
	}
	
	public void onLoad() throws IOException {
	    FacesContext context = FacesContext.getCurrentInstance();

		// load person passed through session
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);

		loaded = (User) session.getAttribute("user");

		// cleaning: attribute received => delete it from session
		if (loaded != null) {
			user = loaded;
			session.removeAttribute("user");
		} else {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("systemError"), null));
		}
	 }

	public String saveData() {
		if(!userDAO.isEmailUnique(user.getEmail()) && user.getId() == 0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("uniqueEmail"), null));
			return PAGE_STAY_AT_THE_SAME;
		}
		
		if(!(user.getPassword().equals(passConfirm))) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("passMismatch"), null));
			return PAGE_STAY_AT_THE_SAME;
		}
		else
		{
			user.setRole("user");
			try {
				if (user.getId() == 0) {
					user.setActive(true);
					userDAO.create(user);
				} else {
					// existing record
					userDAO.merge(user);
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("saveError"), null));
				return PAGE_STAY_AT_THE_SAME;
			}
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put("registerSuccessful", messages.getString("registerSuccess"));
		    return PAGE_USER;
		}
	}
	
	public String adminMergeUser() {
		try {
			// existing record
			userDAO.merge(user);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("saveError"), null));
			return PAGE_STAY_AT_THE_SAME;
		}
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("updateSuccess", messages.getString("updateSuccess"));
		return "/pages/admin/users?faces-redirect=true";
	}
	
	/****************************** Getters and setters ******************************/
	public String getPassConfirm() {
		return passConfirm;
	}

	public void setPassConfirm(String passConfirm) {
		this.passConfirm = passConfirm;
	}
	
	public User getUser() {
		return user;
	}
}
