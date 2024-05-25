package com.div.controller;

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
import com.div.dto.UserDTO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(Constants.GET_SIGNUP)
    public String showSignUpForm(Model model, HttpServletRequest request) {
        String errMessage = (String) request.getAttribute("err");
        if (errMessage != null) {
            model.addAttribute("err", errMessage);
        }
        Boolean passwordLengthError = (Boolean) request.getAttribute("passwordLengthError");
        if (passwordLengthError != null && passwordLengthError) {
            model.addAttribute("passwordLengthError", true);
        }
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        return Constants.VIEW_SIGNUP;
    }

    @PostMapping(Constants.POST_SIGNUP)
    public String saveUser(@ModelAttribute("user") UserDTO userDto, Model model) {
        userService.saveUser(userDto);
        return Constants.VIEW_LOGIN;
    }

    @GetMapping(Constants.LOGIN)
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return Constants.VIEW_LOGIN;
    }

    @PostMapping(Constants.LOGIN)
    public String loginUser(@RequestParam String email, @RequestParam String password, ModelMap model, HttpSession session) {
        UserDTO userDto = userService.findByEmail(email);
        if (userDto == null) {
            model.addAttribute("err", Constants.ERR_INVALID_EMAIL);
        } else {
            if (userDto.getPassword().equals(password)) {
                session.setAttribute("loggedInUser", userDto);
                model.addAttribute("user", userDto);
                return Constants.VIEW_FORM;
            } else {
                model.addAttribute("err", Constants.ERR_INVALID_PASSWORD);
            }
        }
        return Constants.VIEW_LOGIN;
    }
}