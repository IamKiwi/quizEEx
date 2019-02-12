package com.jsfcourse.login;

import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.simplesecurity.RemoteClient;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.kiwi.dao.UserDAO;
import com.kiwi.entities.User;

@Named
@RequestScoped
public class LoginBB {
	private static final String PAGE_MAIN_ADMIN = "/pages/admin/dashboard.xhtml?faces-redirect=true";
	private static final String PAGE_MAIN_USER = "/pages/user/dashboard.xhtml?faces-redirect=true";
	private static final String PAGE_LOGIN = "/public/index";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private String login;
	private String pass;
	
	private ResourceBundle messages;
	
	
	@Inject
	@EJB
	UserDAO userDAO;
	
	@PostConstruct
    public void initialization() {
		FacesContext context = FacesContext.getCurrentInstance();
		this.messages = ResourceBundle.getBundle("resources.login", context.getViewRoot().getLocale());
	}
	
	public String doLogin() {
		FacesContext ctx = FacesContext.getCurrentInstance();

		// 1. verify login and password - get User from "database"
		User user = userDAO.getUserFromDatabase(login, pass);

		// 2. if bad login or password - stay with error info
		if (user == null) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messages.getString("incorrectLoginOrPass"), null));
			return PAGE_STAY_AT_THE_SAME;
		} else if (!user.isActive()) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messages.getString("inactiveAccount"), null));
			return PAGE_STAY_AT_THE_SAME;
		}

		// 3. if logged in: get User roles, save in RemoteClient and store it in session
		
		RemoteClient<User> client = new RemoteClient<User>(); //create new RemoteClient
		client.setDetails(user);
		
		List<String> roles = userDAO.getUserRolesFromDatabase(user); //get User roles 
		
		if (roles != null) { //save roles in RemoteClient
			for (String role: roles) {
				client.getRoles().add(role);
			}
		}
	
		//store RemoteClient with request info in session (needed for SecurityFilter)
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		client.store(request);

		// and enter the system (now SecurityFilter will pass the request)
		if(client.isInRole("admin"))
			return PAGE_MAIN_ADMIN;
		else
			return PAGE_MAIN_USER;
	}
	
	public String doLogout(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		//Invalidate session
		// - all objects within session will be destroyed
		// - new session will be created (with new ID)
		session.invalidate();
		return PAGE_LOGIN;
	}

	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public ResourceBundle getMessages() {
		return messages;
	}

	public void setMessages(ResourceBundle messages) {
		this.messages = messages;
	}
	

	
}
