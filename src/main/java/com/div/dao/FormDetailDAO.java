package com.div.dao;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import com.div.pojo.FormDetail;
import com.div.pojo.User;

@Repository
public class FormDetailDAO extends GenericDAO<FormDetail, Integer> {
	private final HibernateTemplate hibernateTemplate;

    @Autowired
    public FormDetailDAO(HibernateTemplate hibernateTemplate) {
        super(FormDetail.class);
        this.hibernateTemplate = hibernateTemplate;
    }

    public List<FormDetail> findByUser(User user) {
        try (Session session = hibernateTemplate.getSessionFactory().openSession()) {
            TypedQuery<FormDetail> query = session.createQuery("FROM FormDetail WHERE user = :user", FormDetail.class);
            query.setParameter("user", user);
            return query.getResultList();
        }
    }
}