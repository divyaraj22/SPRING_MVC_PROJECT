package com.div.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.div.pojo.FormDetail;
import com.div.pojo.User;
import com.div.constantsURL.Constants;
import com.div.service.FormDetailService;
import com.div.util.MultipartFileEditor;
import com.div.util.SqlDateEditor;

@Controller
public class FormDetailController {

	public final static Logger logger = Logger.getLogger(FormDetailController.class);

    @Autowired
    private FormDetailService formDetailService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(java.sql.Date.class, new SqlDateEditor("yyyy-MM-dd"));
        binder.registerCustomEditor(byte[].class, new MultipartFileEditor());
    }

    @GetMapping(Constants.ADD_DETAILS)
    public String showAddFormDetail() {
        logger.info("GET request to show add form detail");
        return Constants.VIEW_FORM;
    }

    @PostMapping(Constants.ADD_DETAILS)
    public String saveFormDetail(@Valid @ModelAttribute("formDetail") FormDetail formDetail,
                                 @RequestParam("banner") MultipartFile bannerFile, HttpSession session) throws IOException {
        logger.info("POST request to save form detail: {}");

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            logger.warn(Constants.MSG_USER_NOT_LOGGED_IN);
            return "redirect:" + Constants.LOGIN;
        }

        formDetail.setUser(user);

        if (!bannerFile.isEmpty()) {
            String contentType = bannerFile.getContentType();
            if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
                byte[] imageData = bannerFile.getBytes();
                formDetail.setBanner(imageData);
                formDetail.setContentType(contentType);
            } else {
                logger.warn(Constants.MSG_INVALID_FILE_TYPE);
            }
        }

        formDetailService.saveFormDetail(formDetail);
        return "redirect:" + Constants.ADD_DETAILS;
    }

    @GetMapping(Constants.VIEW_ALL)
    public String viewAllDetails(Model model, HttpSession session) {
        logger.info("GET request to view all details");
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            logger.warn(Constants.MSG_USER_NOT_LOGGED_IN);
            return "redirect:" + Constants.LOGIN;
        }

        List<FormDetail> details = formDetailService.getFormDetailByUser(user);
        model.addAttribute("details", details);
        return Constants.VIEW_ALL_DETAILS;
    }

    @GetMapping(Constants.EDIT_DETAIL)
    public String showEditForm(@RequestParam("id") int id, Model model, HttpSession session) {
        logger.info("GET request to edit form detail with ID: {}");
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            logger.warn(Constants.MSG_USER_NOT_LOGGED_IN);
            return "redirect:" + Constants.LOGIN;
        }

        FormDetail formDetail = formDetailService.getFormDetailById(id);
        if (formDetail == null) {
            logger.warn(String.format(Constants.MSG_FORM_DETAIL_NOT_FOUND, id));
            return "redirect:" + Constants.VIEW_ALL;
        }

        model.addAttribute("formDetail", formDetail);
        return Constants.EDIT_FORM;
    }

    @PostMapping(Constants.UPDATE_DETAILS)
    public String updateDetails(@ModelAttribute FormDetail formDetail,
                                @RequestParam("banner") MultipartFile banner, HttpSession session) throws IOException {
        logger.info("POST request to update form detail with ID: {}");

        FormDetail existingFormDetail = formDetailService.getFormDetailById(formDetail.getId());

        if (existingFormDetail == null) {
            logger.warn(String.format(Constants.MSG_FORM_DETAIL_NOT_FOUND, formDetail.getId()));
            return "redirect:" + Constants.VIEW_ALL;
        }

        formDetail.setUser(existingFormDetail.getUser());

        if (banner != null && !banner.isEmpty()) {
            formDetail.setBanner(banner.getBytes());
            formDetail.setContentType(banner.getContentType());
        } else {
            formDetail.setBanner(existingFormDetail.getBanner());
            formDetail.setContentType(existingFormDetail.getContentType());
        }

        formDetailService.updateFormDetail(formDetail);

        return "redirect:" + Constants.VIEW_ALL;
    }

    @GetMapping(Constants.DELETE_DETAIL)
    public String deleteDetails(@RequestParam("id") int id) {
        logger.info("GET request to delete form detail with ID: {}");
        formDetailService.deleteFormDetail(id);
        return "redirect:" + Constants.VIEW_ALL;
    }
}