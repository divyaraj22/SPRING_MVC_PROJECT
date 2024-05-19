package com.div.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.div.service.UserService;
import com.div.constantsURL.Constants;
import com.div.pojo.User;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

	public final static Logger logger = Logger.getLogger(UserController.class);
	
    @Autowired
    private UserService userService;

    @GetMapping(Constants.GET_SIGNUP)
    public String showSignUpForm(Model model) {
        logger.info("GET request to show sign up form");
        model.addAttribute("user", new User());
        return Constants.VIEW_SIGNUP;
    }

    @PostMapping(Constants.POST_SIGNUP)
    public String saveUser(@ModelAttribute("user") User user) {
        logger.info("POST request to save user: {}");
        userService.saveUser(user);
        return Constants.VIEW_LOGIN;
    }

    @GetMapping(Constants.LOGIN)
    public String showLoginForm(Model model) {
        logger.info("GET request to show login form");
        model.addAttribute("user", new User());
        return Constants.VIEW_LOGIN;
    }

    @PostMapping(Constants.LOGIN)
    public String loginUser(@RequestParam String email, @RequestParam String password, ModelMap model,
                            HttpSession session) {
        logger.info("POST request to login user with email: {}");
        User user = userService.findByEmail(email);
        if (user == null) {
            logger.warn("Login attempt with invalid email: {}");
            model.addAttribute("err", Constants.ERR_INVALID_EMAIL);
        } else {
            if (user.getPassword().equals(password)) {
                logger.info("User logged in successfully: {}");
                session.setAttribute("loggedInUser", user);
                model.addAttribute("user", user);
                return Constants.VIEW_FORM;
            } else {
                logger.warn("Login attempt with invalid password for email: {}");
                model.addAttribute("err", Constants.ERR_INVALID_PASSWORD);
            }
        }
        return Constants.VIEW_LOGIN;
    }
}
