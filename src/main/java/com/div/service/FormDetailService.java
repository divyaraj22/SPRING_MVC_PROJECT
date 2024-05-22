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

	public List<FormDetailDTO> getUserFormDetails(UserDTO userDto, SearchCriteria searchCriteria) {
		return formDetailDAO.findByUserWithCriteria(userDto, searchCriteria);
	}

}
