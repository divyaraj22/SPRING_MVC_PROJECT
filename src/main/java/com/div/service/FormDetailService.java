package com.div.service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.div.dao.FormDetailDAO;
import com.div.dto.FormDetailDTO;
import com.div.dto.UserDTO;
import com.div.pojo.SearchCriteria;

@Service
@Transactional
public class FormDetailService {

	@Autowired
	private FormDetailDAO formDetailDAO;

	public List<FormDetailDTO> getAllFormDetails() {
		return formDetailDAO.findAll().stream().map(FormDetailDTO::fromModel).collect(Collectors.toList());
	}

	public FormDetailDTO getFormDetailById(int id) {
		return formDetailDAO.findOneDTO(id);
	}

	public void saveFormDetail(FormDetailDTO formDetailDto) {
		formDetailDAO.save(formDetailDto);
	}

	public void updateFormDetail(FormDetailDTO formDetailDto) {
		formDetailDAO.update(formDetailDto);
	}

	public void deleteFormDetail(int id) {
		formDetailDAO.deleteByIdDTO(id);
	}

	public List<FormDetailDTO> getFormDetailByUser(UserDTO userDto) {
		return formDetailDAO.findByUser(userDto);
	}

	public List<FormDetailDTO> getFreeAccessWithExpiry(Date today) {
		return formDetailDAO.findFreeAccessWithExpiry(today);
	}

	/*
	 * public List<FormDetailDTO> searchFormDetailsByTitle(String title) { return
	 * formDetailDAO.searchByTitle(title); }
	 */

	public List<FormDetailDTO> getUserFormDetails(UserDTO userDto, SearchCriteria searchCriteria) {
		List<FormDetailDTO> details = formDetailDAO.findByUser(userDto);

		// Apply search filter
		if (searchCriteria.getSearchTitle() != null && !searchCriteria.getSearchTitle().isEmpty()) {
			details = details.stream().filter(
					detail -> detail.getTitle().toLowerCase().contains(searchCriteria.getSearchTitle().toLowerCase()))
					.collect(Collectors.toList());
		}

		// Apply sorting to the filtered results
		if (searchCriteria.getSortField() != null && searchCriteria.getSortOrder() != null) {
			details.sort((detail1, detail2) -> {
				int comparisonResult = 0;
				switch (searchCriteria.getSortField()) {
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
				return "desc".equals(searchCriteria.getSortOrder()) ? -comparisonResult : comparisonResult;
			});
		}

		return details;
	}

}
