package com.div.dao;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.TypedQuery;
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

	@Autowired
	public FormDetailDAO(HibernateTemplate hibernateTemplate) {
		super(FormDetail.class);
		this.hibernateTemplate = hibernateTemplate;
	}

	private HibernateTemplate hibernateTemplate;

	public List<FormDetailDTO> findByUser(UserDTO userDto) {
		User user = userDto.toModel();
		List<FormDetail> formDetails = hibernateTemplate.execute(session -> {
			TypedQuery<FormDetail> query = session.createQuery("FROM FormDetail WHERE user = :user", FormDetail.class);
			query.setParameter("user", user);
			return query.getResultList();
		});
		return formDetails.stream().map(FormDetailDTO::fromModel).collect(Collectors.toList());
	}

	public List<FormDetailDTO> findFreeAccessWithExpiry(Date today) {
		List<FormDetail> formDetails = hibernateTemplate.execute(session -> {
			TypedQuery<FormDetail> query = session.createQuery(
					"FROM FormDetail WHERE accessCategory = 'free' AND freeViewExpiry = :today", FormDetail.class);
			query.setParameter("today", today);
			return query.getResultList();
		});
		return formDetails.stream().map(FormDetailDTO::fromModel).collect(Collectors.toList());
	}

	/*
	 * public List<FormDetailDTO> searchByTitle(String title) { List<FormDetail>
	 * formDetails = hibernateTemplate.execute(session -> { TypedQuery<FormDetail>
	 * query = session.createQuery("FROM FormDetail WHERE title LIKE :title",
	 * FormDetail.class); query.setParameter("title", "%" + title + "%"); return
	 * query.getResultList(); }); return
	 * formDetails.stream().map(FormDetailDTO::fromModel).collect(Collectors.toList(
	 * )); }
	 */

	public List<FormDetailDTO> getSortedFormDetails(String sortField, String sortOrder) {
		List<FormDetail> formDetails = hibernateTemplate.execute(session -> {
			String queryStr = "FROM FormDetail ORDER BY " + sortField + " " + sortOrder;
			TypedQuery<FormDetail> query = session.createQuery(queryStr, FormDetail.class);
			return query.getResultList();
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
			StringBuilder queryBuilder = new StringBuilder("FROM FormDetail WHERE user = :user");

			if (searchCriteria.getSearchTitle() != null && !searchCriteria.getSearchTitle().isEmpty()) {
				queryBuilder.append(" AND title LIKE :searchTitle");
			}

			if (searchCriteria.getSortField() != null && searchCriteria.getSortOrder() != null) {
				queryBuilder.append(" ORDER BY ").append(searchCriteria.getSortField()).append(" ")
						.append(searchCriteria.getSortOrder());
			}

			TypedQuery<FormDetail> query = session.createQuery(queryBuilder.toString(), FormDetail.class);
			query.setParameter("user", user);

			if (searchCriteria.getSearchTitle() != null && !searchCriteria.getSearchTitle().isEmpty()) {
				query.setParameter("searchTitle", "%" + searchCriteria.getSearchTitle() + "%");
			}

			List<FormDetail> formDetails = query.getResultList();
			return formDetails.stream().map(FormDetailDTO::fromModel).collect(Collectors.toList());
		});
	}
}
