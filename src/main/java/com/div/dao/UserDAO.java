package com.div.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import com.div.pojo.User;
import com.div.dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserDAO extends GenericDAO<User, Integer> {

    @Autowired
    public UserDAO(HibernateTemplate hibernateTemplate) {
        super(User.class);
        this.hibernateTemplate = hibernateTemplate;
    }

    private HibernateTemplate hibernateTemplate;

    public UserDTO findByEmail(String email) {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        User user = query.uniqueResult();
        return UserDTO.fromModel(user);
    }

    public List<UserDTO> findAllDTO() {
        List<User> users = findAll();
        return users.stream().map(UserDTO::fromModel).collect(Collectors.toList());
    }

    public void save(UserDTO userDto) {
        save(userDto.toModel());
    }

    public void update(UserDTO userDto) {
        update(userDto.toModel());
    }

    public UserDTO findOneDTO(Integer id) {
        User user = findOne(id);
        return UserDTO.fromModel(user);
    }

    public void deleteByIdDTO(Integer id) {
        deleteById(id);
    }
}
