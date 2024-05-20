package com.div.dao;

import java.sql.Date;
import java.util.List;
import javax.persistence.TypedQuery;
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
		return hibernateTemplate.execute(session -> {
			TypedQuery<FormDetail> query = session.createQuery("FROM FormDetail WHERE user = :user", FormDetail.class);
			query.setParameter("user", user);
			return query.getResultList();
		});
	}

	public List<FormDetail> findFreeAccessWithExpiry(Date today) {
		return hibernateTemplate.execute(session -> {
			TypedQuery<FormDetail> query = session.createQuery(
					"FROM FormDetail WHERE accessCategory = 'free' AND freeViewExpiry = :today", FormDetail.class);
			query.setParameter("today", today);
			return query.getResultList();
		});
	}

	public void update(FormDetail formDetail) {
		hibernateTemplate.update(formDetail);
	}
}
