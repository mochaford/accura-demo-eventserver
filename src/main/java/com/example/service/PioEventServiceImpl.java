package com.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
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
import com.example.utils.DBHelper;
import com.example.utils.StringFormatUtils;
import com.example.model.CylinderWrapper;

@Service
public class PioEventServiceImpl implements PioEventService {

	//@Autowired
	//private EntityManagerFactory emf;
	
	@PersistenceContext//(type = PersistenceContextType.EXTENDED)
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
		String sql = "select id,cylinderid,materialid,accountid,fillstatus,timestamp,countrycode,"
				+ "duration,flag from pio_cylinder_history where flag=0 ";
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			sql += " and  '" + entry.getKey() + "' = " + entry.getValue();
		}
		try {
			
			Connection conn = DBHelper.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql); 
			ResultSet rs = ps.executeQuery(sql);
			List<CylinderWrapper> list_pioevent = new ArrayList<CylinderWrapper>();
			while(rs.next()){
				CylinderWrapper wapper = new CylinderWrapper();
				wapper.setCylinderId(rs.getString("cylinderid"));
				wapper.setMaterialId(rs.getString("materialid"));
				wapper.setAccountId(rs.getString("accountid"));
				wapper.setFillStatus(rs.getString("fillstatus"));
				wapper.setTimeStamp(StringFormatUtils.getStringFromTimestamp(rs.getTimestamp("timestamp")));
				wapper.setCountryCode(rs.getString("countrycode"));
				wapper.setDuration(rs.getInt("duration"));
				wapper.setFlag(rs.getInt("flag"));
				System.out.println("---" + wapper);
				list_pioevent.add(wapper);
			}
			 
			//Query query = em.createNativeQuery(sql, CylinderWrapper.class);

			//List<CylinderWrapper> list_pioevent = query.getResultList();
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
			System.out.println(list_pio_event.size() + "---www list_pio_event: " + list_pio_event);
			return list_pio_event;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("--exception3 ; " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public void getListPioEvent(List<PioEvent> list_pio_event, CylinderWrapper c, String text, String label) {
		Date date = new Date();
		PioEvent event = new PioEvent();
		//Long entityid = Long.parseLong((Math.random() * 10 + "")) + date.getTime();
		event.setEntityId("" + new DecimalFormat("0").format(Math.random() * 10) + + date.getTime());
		event.setProperties("{\"text\": \"" + text + "\", \"label\": \"" + label + "\"}");
		event.setEvent("cylinder");
		event.setEntityType("content");
		list_pio_event.add(event);
	}

	//@Transactional
	public int addHistory(CylinderWrapper history) {
		// TODO Auto-generated method stub
		int result = 1;
		try {
			//em.persist(history);
			Connection conn = DBHelper.getConnection();
			String sql = "insert into pio_cylinder_history (accountid, countrycode, cylinderid, duration, fillstatus, flag, materialid, timestamp)"
					+ " VALUES('"+history.getAccountId()+"' , '"+history.getCountryCode()+"' , '"+history.getCylinderId()+"' , '"+history.getDuration()
					+"' , '"+history.getFillStatus()+"' , '"+history.getFlag()+"' , '"+history.getMaterialId()+"' , '"+history.getTimeStamp()+"')";
			System.out.println("---addhistory-sql-" + sql);
			Statement state = conn.createStatement();
            int count = state.executeUpdate(sql);
            System.out.println("Operation is successful!");
            System.out.println(" insert" + count + " record");
            //操作后释放资源
            DBHelper.release(conn, state, null);
		} catch (Exception e) {
			// TODO: handle exception
			result = 0;
			System.out.println("addhistory--:" + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	public int addHistoryListByJDBC(List<CylinderWrapper> list_history){
		int result = 0;
		try {
			Connection conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			int size = list_history.size();
			String sql = "INSERT into pio_cylinder_history (accountid, countrycode, cylinderid, duration, fillstatus, flag, materialid, timestamp) VALUES(?,?,?,?,?,?,?,?)";     
			PreparedStatement prest = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);  
			for(CylinderWrapper wapper : list_history){
				 prest.setString(1, wapper.getAccountId());     
		         prest.setString(2, wapper.getCountryCode());     
		         prest.setString(3, wapper.getCylinderId());     
		         prest.setInt(4, wapper.getDuration());  
		         prest.setString(5, wapper.getFillStatus());
		         prest.setInt(6, wapper.getFlag());     
		         prest.setString(7, wapper.getMaterialId());
		         prest.setTimestamp(8, StringFormatUtils.getTimesstampByString(wapper.getTimeStamp()));
		         prest.addBatch(); 
			}
			 int[] count = prest.executeBatch();  
			 conn.commit();
			 DBHelper.release(conn, null, null);
			 System.out.println("--count " + count);
		} catch (Exception e) {
			// TODO: handle exception
			result = 1;
			System.out.println("--exception " + e.getMessage());
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
		//EntityManager em = emf.createEntityManager();
	    EntityTransaction tx = em.getTransaction();
	    tx.begin();
		
		//em.getTransaction().begin();
		try {
			for (CylinderWrapper pioHistroty : list_history) {
				System.out.println("---getCylinderId " + pioHistroty.getCylinderId());
				System.out.println("---getAccountId " + pioHistroty.getAccountId());
				System.out.println("---getMaterialId " + pioHistroty.getMaterialId());
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

	public int addEventListByJDBC(List<PioEvent> list_pio){
		int result = 0;
		try {
			Connection conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			int size = list_pio.size();
			 System.out.println("-addEventListByJDBC event-size " + size);
			String sql = "INSERT into pio_event_1 (id,entityid, properties, event, entitytype,creationtimezone,eventtimezone) VALUES(?,?,?,?,?,?,?)";     
			PreparedStatement prest = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);  
			for(PioEvent wapper : list_pio){
				 prest.setString(1, StringFormatUtils.getShortUuid());
				 prest.setString(2, wapper.getEntityId());     
		         prest.setString(3, wapper.getProperties());     
		         prest.setString(4, wapper.getEvent());     
		         prest.setString(5, wapper.getEntityType());  
		         prest.setString(6, "UTC");  
		         prest.setString(7, "UTC");  
		         prest.addBatch(); 
			}
			 System.out.println("-event-count---- " );
			 int[] count = prest.executeBatch();  
			 conn.commit();
			 DBHelper.release(conn, null, null);
			 System.out.println("-event-count " + count.toString());
		} catch (Exception e) {
			// TODO: handle exception
			result = 1;
			System.out.println("-event-exception " + e.getMessage());
			e.printStackTrace();
		}
		return result;
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
