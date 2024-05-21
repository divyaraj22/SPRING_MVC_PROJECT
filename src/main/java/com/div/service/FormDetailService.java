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

    public List<FormDetailDTO> searchFormDetailsByTitle(String title) {
        return formDetailDAO.searchByTitle(title);
    }

    public List<FormDetailDTO> getSortedFormDetails(String sortField, String sortOrder) {
        return formDetailDAO.getSortedFormDetails(sortField, sortOrder);
    }
    public List<FormDetailDTO> getUserFormDetails(UserDTO userDto, String searchTitle, String sortField, String sortOrder) {
        List<FormDetailDTO> details = formDetailDAO.findByUser(userDto);

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

        return details;
    }
}
