package com.pape.timetodo.global.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Principal {
    


    public static String getUser(){
        String username = null;
		Object principal = null;
		try {
			principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			
		}
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = "anonymousUser";
		}
		return username;
    }


    public static boolean isLogin() {
		if (getUser() == null || getUser().equals("anonymousUser"))
			return false;
		else
			return true;
	}
}
