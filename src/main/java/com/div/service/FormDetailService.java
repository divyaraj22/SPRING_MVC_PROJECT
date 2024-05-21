package com.div.service;

import java.sql.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.div.dao.FormDetailDAO;
import com.div.pojo.FormDetail;
import com.div.pojo.User;

@Service
@Transactional
public class FormDetailService {

	@Autowired
    private FormDetailDAO formDetailDAO;

    public List<FormDetail> getAllFormDetails() {
        return formDetailDAO.findAll();
    }

    public FormDetail getFormDetailById(int id) {
        return formDetailDAO.findone(id);
    }

    public void saveFormDetail(FormDetail formDetail) {
        formDetailDAO.save(formDetail);
    }

    public void updateFormDetail(FormDetail formDetail) {
        formDetailDAO.update(formDetail);
    }

    public void deleteFormDetail(int id) {
        formDetailDAO.deleteById(id);
    }

    public List<FormDetail> getFormDetailByUser(User user) {
        return formDetailDAO.findByUser(user);
    }

    public List<FormDetail> getFreeAccessWithExpiry(Date today) {
        return formDetailDAO.findFreeAccessWithExpiry(today);
    }
    
    public List<FormDetail> searchFormDetailsByTitle(String title) {
        return formDetailDAO.searchByTitle(title);
    }
    
    public List<FormDetail> getSortedFormDetails(String sortField, String sortOrder) {
        return formDetailDAO.getSortedFormDetails(sortField, sortOrder);
    }
}