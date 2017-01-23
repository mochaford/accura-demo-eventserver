package com.example.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.PioEvent;
import com.example.model.CylinderWrapper;

@Service
public class PioEventServiceImpl implements PioEventService {

	@Autowired
	private EntityManagerFactory emf;
	
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	EntityManager em;
	

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Transactional
	public List<PioEvent> sortAndGroupByEvent(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		// CriteriaQuery<PIOEvent> c =
		// em.getCriteriaBuilder().createQuery(PIOEvent.class);
		// c.from(PIOEvent.class);
		// List<PIOEvent> list_pioevent = em.createQuery(c).getResultList();
		String sql = "select id,cylinderid,materialid,accountid,fillstatus,timestamp,countryCode,"
				+ "duration,flag where 1=1";
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			sql += " and  '" + entry.getKey() + "' = " + entry.getValue();
		}
		try {
			Query query = em.createNativeQuery(sql, CylinderWrapper.class);

			List<CylinderWrapper> list_pioevent = query.getResultList();
			System.out.println("---list_pioevent" + list_pioevent);
			Map<String, List<CylinderWrapper>> groups = new HashMap<String, List<CylinderWrapper>>();
			List<CylinderWrapper> wrappers = null;
			for (CylinderWrapper c : list_pioevent) {
				String key = c.getCylinderId() + c.getAccountId() + c.getMaterialId() + c.getCountryCode();
				if (groups.containsKey(key)) {
					wrappers = groups.get(key);
				} else {
					wrappers = new ArrayList<CylinderWrapper>();
				}
				wrappers.add(c);
				groups.put(key, wrappers);
			}
			System.out.println("---groups" + groups);
			List<CylinderWrapper> eventList = new ArrayList<CylinderWrapper>();
			Set<String> keys = groups.keySet();
			for (String key : keys) {
				List<CylinderWrapper> clist = groups.get(key);
				System.out.println("---www clist: " + clist);
				Collections.sort(clist);
				System.out.println("---www clist.sort: " + clist);
				if (clist.size() >= 2) {
					// Integer count = clist.size()/2;
					// for(Integer i=0;i<count;i++){
					for (Integer i = 0; i < clist.size() - 1; i = i + 2) {
						CylinderWrapper cw1 = clist.get(i);// 100

						CylinderWrapper cw2 = clist.get(i + 1);// 0
						if (cw1.getFillStatus().equals("100") && cw2.getFillStatus().equals("0")) {
							Long milliseconds = CylinderWrapper.formatDateByString(cw2.getTimeStamp()).getTime()
									- CylinderWrapper.formatDateByString(cw1.getTimeStamp()).getTime();
							Integer duration = (int) (milliseconds / 1000 / 60 / 60 / 24);
							cw1.setDuration(duration);
							;
							eventList.add(cw1);
						} else {
							i = i - 1;
							continue;
						}

					}
				}
			}
			List<PioEvent> list_pio_event = new ArrayList<PioEvent>();
			// save events
			for (CylinderWrapper c : eventList) {
				String text = c.getCylinderId() + ' ' + c.getAccountId() + ' ' + c.getMaterialId() + ' '
						+ c.getCountryCode();
				String label = "";
				if (c.getDuration() <= 4)
					label = "low";
				else if (c.getDuration() >= 21)
					label = "high";
				else
					label = "middle";

				getListPioEvent(list_pio_event, c, text, label);

			}
			System.out.println("---www list_pio_event: " + list_pio_event);
			return list_pio_event;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}

	public void getListPioEvent(List<PioEvent> list_pio_event, CylinderWrapper c, String text, String label) {
		Date date = new Date();
		PioEvent event = new PioEvent();
		Long entityid = Long.parseLong((Math.random() * 10 + "")) + date.getTime();
		event.setEntityId(entityid + "");
		event.setProperties("{\"text\": \"" + text + "\", \"label\": \"" + label + "\"}");
		event.setEvent("cylinder");
		event.setEntityType("content");
		list_pio_event.add(event);
	}

	@Transactional
	public int addHistory(CylinderWrapper history) {
		// TODO Auto-generated method stub
		int result = 1;
		try {
			em.persist(history);
		} catch (Exception e) {
			// TODO: handle exception
			result = 0;
			e.printStackTrace();
		}

		return result;
	}

	//@Transactional(propagation = Propagation.REQUIRED)
	public boolean addHistoryList(List<CylinderWrapper> list_history) {
		System.out.println("---addHistoryList" + list_history);
		// TODO Auto-generated method stub
		boolean flag = false;
		int result = 0;
		int batchSize = 100;
		int list_size = list_history.size();
		//EntityManager em = getEm();
		EntityManager em = emf.createEntityManager();
	    EntityTransaction tx = em.getTransaction();
	    tx.begin();
		
		//em.getTransaction().begin();
		try {
			for (CylinderWrapper pioHistroty : list_history) {
				em.persist(pioHistroty);
				result++;
				if (list_size < batchSize && result == list_size) {
					em.flush();
					em.clear();
				} else if ((list_size < batchSize && result % batchSize == 0) || result == list_size) {
					em.flush();
					em.clear();
				}
				flag = true;
				System.out.println("addHistoryList批量保存实体成功，" + em.getClass().getName());
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("--exception : " + e.getMessage());
			flag = false;
			em.getTransaction().rollback();
			e.printStackTrace();
		}

		return flag;
	}

	@Override
	public boolean addEventList(List<PioEvent> list_pio) {
		boolean flag = false;
		int result = 0;
		int batchSize = 100;
		int list_size = list_pio.size();
		em.getTransaction().begin();
		try {
			for (PioEvent event : list_pio) {
				em.persist(event);
				result++;
				if (list_size < batchSize && result == list_size) {
					em.flush();
					em.clear();
					System.out.println("--add Event");
				} else if ((list_size < batchSize && result % batchSize == 0) || result == list_size) {
					em.flush();
					em.clear();
					System.out.println("--add2 Event");
				}
				flag = true;
				System.out.println("addEventList批量保存实体成功，" + em.getClass().getName());
			}

		} catch (Exception e) {
			// TODO: handle exception
			flag = false;
			System.out.println("--exception : " + e.getMessage());
			em.getTransaction().rollback();
			e.printStackTrace();
		}

		return flag;
	}
}
