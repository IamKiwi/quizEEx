package com.kiwi.pages;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class PagesBB {
	public String register() {
		return "register";
	}
	
	public String login() {
		return "login";
	}
}
