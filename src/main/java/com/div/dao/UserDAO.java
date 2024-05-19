package com.div.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import com.div.pojo.User;

@Repository
public class UserDAO extends GenericDAO<User, Integer> {
    public UserDAO() {
        super(User.class);
    }
    
    private HibernateTemplate hibernateTemplate;
    
    @Autowired
    public UserDAO(HibernateTemplate hibernateTemplate) {
        super(User.class);
        this.hibernateTemplate = hibernateTemplate;
    }
    
    public User findByEmail(String email) {
        Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }
}