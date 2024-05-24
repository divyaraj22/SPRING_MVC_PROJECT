package com.div.dao;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import com.div.pojo.FormDetail;
import com.div.pojo.SearchCriteria;
import com.div.pojo.User;
import com.div.dto.FormDetailDTO;
import com.div.dto.UserDTO;

@Repository
public class FormDetailDAO extends GenericDAO<FormDetail, Integer> {

	private HibernateTemplate hibernateTemplate;
	private SessionFactory sessionFactory;

	@Autowired
	public FormDetailDAO(HibernateTemplate hibernateTemplate, SessionFactory sessionFactory) {
		super(FormDetail.class);
		this.hibernateTemplate = hibernateTemplate;
		this.sessionFactory = sessionFactory;
	}

	public List<FormDetailDTO> findByUser(UserDTO userDto) {
		User user = userDto.toModel();
		List<FormDetail> formDetails = hibernateTemplate.execute(session -> {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<FormDetail> cq = cb.createQuery(FormDetail.class);
			Root<FormDetail> root = cq.from(FormDetail.class);
			cq.select(root).where(cb.equal(root.get("user"), user));
			return session.createQuery(cq).getResultList();
		});
		return formDetails.stream().map(FormDetailDTO::fromModel).collect(Collectors.toList());
	}

	public List<FormDetailDTO> findFreeAccessWithExpiry(Date today) {
		List<FormDetail> formDetails = hibernateTemplate.execute(session -> {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<FormDetail> cq = cb.createQuery(FormDetail.class);
			Root<FormDetail> root = cq.from(FormDetail.class);
			cq.select(root).where(
					cb.and(cb.equal(root.get("accessCategory"), "free"), cb.equal(root.get("freeViewExpiry"), today)));
			return session.createQuery(cq).getResultList();
		});
		return formDetails.stream().map(FormDetailDTO::fromModel).collect(Collectors.toList());
	}

	public List<FormDetailDTO> getSortedFormDetails(String sortField, String sortOrder) {
		List<FormDetail> formDetails = hibernateTemplate.execute(session -> {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<FormDetail> cq = cb.createQuery(FormDetail.class);
			Root<FormDetail> root = cq.from(FormDetail.class);
			Order order = "asc".equalsIgnoreCase(sortOrder) ? cb.asc(root.get(sortField))
					: cb.desc(root.get(sortField));
			cq.select(root).orderBy(order);
			return session.createQuery(cq).getResultList();
		});
		return formDetails.stream().map(FormDetailDTO::fromModel).collect(Collectors.toList());
	}

	public void save(FormDetailDTO formDetailDto) {
		save(formDetailDto.toModel());
	}

	public void update(FormDetailDTO formDetailDto) {
		update(formDetailDto.toModel());
	}

	public FormDetailDTO findOneDTO(Integer id) {
		FormDetail formDetail = findOne(id);
		return FormDetailDTO.fromModel(formDetail);
	}

	public void deleteByIdDTO(Integer id) {
		deleteById(id);
	}

	public List<FormDetailDTO> findByUserWithCriteria(UserDTO userDto, SearchCriteria searchCriteria) {
		User user = userDto.toModel();
		return hibernateTemplate.execute(session -> {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<FormDetail> cq = cb.createQuery(FormDetail.class);
			Root<FormDetail> root = cq.from(FormDetail.class);

			Predicate predicate = cb.equal(root.get("user"), user);

			if (searchCriteria.getSearchTitle() != null && !searchCriteria.getSearchTitle().isEmpty()) {
				predicate = cb.and(predicate, cb.like(root.get("title"), "%" + searchCriteria.getSearchTitle() + "%"));
			}

			cq.where(predicate);

			if (searchCriteria.getSortField() != null && searchCriteria.getSortOrder() != null) {
				Order order = "asc".equalsIgnoreCase(searchCriteria.getSortOrder())
						? cb.asc(root.get(searchCriteria.getSortField()))
						: cb.desc(root.get(searchCriteria.getSortField()));
				cq.orderBy(order);
			}

			return session.createQuery(cq).getResultList();
		}).stream().map(FormDetailDTO::fromModel).collect(Collectors.toList());
	}
}
