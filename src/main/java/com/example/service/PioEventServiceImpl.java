package com.example.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.PioEvent;
import com.example.model.PioCylinderHistory;

@Service
public class PioEventServiceImpl implements PioEventService{

	@PersistenceContext
    EntityManager em;

	@Transactional
	@Override
	public List<PioEvent> sortAndGroupByEvent(Map<String,String> paramMap) {
		// TODO Auto-generated method stub
		 //CriteriaQuery<PIOEvent> c = em.getCriteriaBuilder().createQuery(PIOEvent.class);
	     //c.from(PIOEvent.class);
	     //List<PIOEvent> list_pioevent = em.createQuery(c).getResultList();
	     String sql = "select id,event,entitytype,entityid,targetentitytype,targetentityid,properties,"
	     		+ "eventtime,eventtimezone,tags,prid,creationtime where 1=1";
	     for(Map.Entry<String, String> entry : paramMap.entrySet()){
	    	 sql += " and  '" + entry.getKey() + "' = " + entry.getValue();
	     }
	     Query query = em.createNativeQuery(sql, PioEvent.class);
	     
	     List<PioEvent> list_pioevent = query.getResultList();
	     
		return null;
	}

	@Override
	public int addHistory(PioCylinderHistory history) {
		// TODO Auto-generated method stub
		int result = 1;
		try {
			em.persist(history);
		} catch (Exception e) {
			// TODO: handle exception
			result = 0;
		}
		
		return result;
	}    
}
