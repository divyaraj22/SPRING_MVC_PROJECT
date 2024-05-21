package com.div.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.div.dao.UserDAO;
import com.div.dto.UserDTO;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public List<UserDTO> getAllUsers() {
        return userDAO.findAllDTO();
    }

    public UserDTO getUserById(int id) {
        return userDAO.findOneDTO(id);
    }

    public void saveUser(UserDTO userDto) {
        userDAO.save(userDto);
    }

    public void updateUser(UserDTO userDto) {
        userDAO.update(userDto);
    }

    public void deleteUser(int id) {
        userDAO.deleteByIdDTO(id);
    }

    public UserDTO findByEmail(String email) {
        return userDAO.findByEmail(email);
    }
}
