package com.hasjay.reppido.report.repository;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.hasjay.reppido.report.model.Report;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class CustomReportRepositoryImpl implements CustomReportRepository {
	
	private EntityManager em;
	
	public CustomReportRepositoryImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public List<Report> getReports() {
		EntityGraph<?> graph = em.getEntityGraph("Report.withCategotyAndComments");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report> cq = cb.createQuery(Report.class);
		Root<Report> rpt = cq.from(Report.class);

		cq.select(rpt);
		return em.createQuery(cq).setHint("javax.persistence.fetchgraph", graph).getResultList();  
		
	}

}
