    package com.div.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GenericDAO<T,ID extends Serializable>{

	private final Class<T> clazz;
	
	@Autowired
    protected SessionFactory sessionFactory;

    protected GenericDAO(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    public T findone(ID id) {
    	return sessionFactory.getCurrentSession().get(clazz,id);
    }
    
    public List<T> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from " + clazz.getName(), clazz).list();
    }

    public void save(T entity) {
        sessionFactory.getCurrentSession().persist(entity);
    }

    public void update(T entity) {
        sessionFactory.getCurrentSession().merge(entity);
    }

    public void delete(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    public void deleteById(ID entityId) {
        T entity = findone(entityId);
        delete(entity);
    }
}
