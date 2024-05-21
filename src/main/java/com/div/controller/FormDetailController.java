package com.div.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
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
import com.div.dto.FormDetailDTO;
import com.div.dto.UserDTO;
import com.div.constantsURL.Constants;
import com.div.service.FormDetailService;
import com.div.service.SchedulerService;
import com.div.util.MultipartFileEditor;
import com.div.util.SqlDateEditor;

@Controller
public class FormDetailController {

	public final static Logger logger = Logger.getLogger(FormDetailController.class);

	@Autowired
	private FormDetailService formDetailService;

	@Autowired
	private SchedulerService schedulerService;

	@GetMapping(Constants.TRIGGER_SCHEDULER)
	@Transactional
	public String triggerScheduler() {
		schedulerService.checkAndUpdateAccessCategories();
		return "Scheduler";
	}

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
	public String saveFormDetail(@Valid @ModelAttribute("formDetail") FormDetailDTO formDetailDto,
			@RequestParam("banner") MultipartFile bannerFile,
			@RequestParam(value = "premiumCheckbox", required = false) boolean isPremium, HttpSession session)
			throws IOException {
		logger.info("POST request to save form detail: {}");

		UserDTO userDto = (UserDTO) session.getAttribute("loggedInUser");
		if (userDto == null) {
			logger.warn(Constants.MSG_USER_NOT_LOGGED_IN);
			return "redirect:" + Constants.LOGIN;
		}

		formDetailDto.setUser(userDto);

		if (!bannerFile.isEmpty()) {
			String contentType = bannerFile.getContentType();
			if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
				byte[] imageData = bannerFile.getBytes();
				formDetailDto.setBanner(imageData);
				formDetailDto.setContentType(contentType);
			} else {
				logger.warn(Constants.MSG_INVALID_FILE_TYPE);
			}
		}

		formDetailDto.setPremium(isPremium);
		formDetailService.saveFormDetail(formDetailDto);
		return "redirect:" + Constants.ADD_DETAILS;
	}

	@GetMapping(Constants.VIEW_ALL)
	public String viewAllDetails(@RequestParam(value = "searchTitle", required = false) String searchTitle,
			@RequestParam(value = "sortField", required = false) String sortField,
			@RequestParam(value = "sortOrder", required = false) String sortOrder, Model model, HttpSession session) {

		logger.info("GET request to view all details");
		UserDTO userDto = (UserDTO) session.getAttribute("loggedInUser");
		if (userDto == null) {
			logger.warn(Constants.MSG_USER_NOT_LOGGED_IN);
			return "redirect:" + Constants.LOGIN;
		}

		List<FormDetailDTO> details = formDetailService.getFormDetailByUser(userDto);

		if (searchTitle != null && !searchTitle.isEmpty()) {
			details = details.stream()
					.filter(detail -> detail.getTitle().toLowerCase().contains(searchTitle.toLowerCase()))
					.collect(Collectors.toList());
		}

		if (sortField != null && sortOrder != null) {
			details.sort((detail1, detail2) -> {
				int comparisonResult = 0;
				switch (sortField) {
				case "title":
					comparisonResult = detail1.getTitle().compareToIgnoreCase(detail2.getTitle());
					break;
				case "publicURL":
					comparisonResult = detail1.getPublicURL().compareToIgnoreCase(detail2.getPublicURL());
					break;
				case "accessCategory":
					comparisonResult = detail1.getAccessCategory().compareToIgnoreCase(detail2.getAccessCategory());
					break;
				default:
					break;
				}
				return "desc".equals(sortOrder) ? -comparisonResult : comparisonResult;
			});
		}

		model.addAttribute("details", details);
		return Constants.VIEW_ALL_DETAILS;
	}

	@GetMapping(Constants.EDIT_DETAIL)
	public String showEditForm(@RequestParam("id") int id, Model model, HttpSession session) {
		logger.info("GET request to edit form detail with ID: {}");
		UserDTO userDto = (UserDTO) session.getAttribute("loggedInUser");
		if (userDto == null) {
			logger.warn(Constants.MSG_USER_NOT_LOGGED_IN);
			return "redirect:" + Constants.LOGIN;
		}

		FormDetailDTO formDetailDto = formDetailService.getFormDetailById(id);
		if (formDetailDto == null) {
			logger.warn(String.format(Constants.MSG_FORM_DETAIL_NOT_FOUND, id));
			return "redirect:" + Constants.VIEW_ALL;
		}

		model.addAttribute("formDetail", formDetailDto);
		return Constants.EDIT_FORM;
	}

	@PostMapping(Constants.UPDATE_DETAILS)
	public String updateDetails(@ModelAttribute FormDetailDTO formDetailDto,
			@RequestParam("banner") MultipartFile banner,
			@RequestParam(value = "isPremium", required = false) boolean isPremium, HttpSession session)
			throws IOException {
		logger.info("POST request to update form detail with ID: {}");

		FormDetailDTO existingFormDetailDto = formDetailService.getFormDetailById(formDetailDto.getId());

		if (existingFormDetailDto == null) {
			logger.warn(String.format(Constants.MSG_FORM_DETAIL_NOT_FOUND, formDetailDto.getId()));
			return "redirect:" + Constants.VIEW_ALL;
		}

		formDetailDto.setUser(existingFormDetailDto.getUser());

		if (banner != null && !banner.isEmpty()) {
			formDetailDto.setBanner(banner.getBytes());
			formDetailDto.setContentType(banner.getContentType());
		} else {
			formDetailDto.setBanner(existingFormDetailDto.getBanner());
			formDetailDto.setContentType(existingFormDetailDto.getContentType());
		}

		formDetailDto.setPremium(isPremium);
		formDetailService.updateFormDetail(formDetailDto);

		return "redirect:" + Constants.VIEW_ALL;
	}

	@GetMapping(Constants.DELETE_DETAIL)
	public String deleteDetails(@RequestParam("id") int id) {
		logger.info("GET request to delete form detail with ID: {}");
		formDetailService.deleteFormDetail(id);
		return "redirect:" + Constants.VIEW_ALL;
	}
}