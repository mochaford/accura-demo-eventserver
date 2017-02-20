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
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

	//@Transactional
	public List<PioEvent> sortAndGroupByEvent(Map<String, String> paramMap) {
		
		String sql = "select id,cylinderid,materialid,locationid,filling_level,timestamp,"
				+ "flag from pio_cylinder_filling_level_history where flag=0 ";
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			sql += " and  '" + entry.getKey() + "' = " + entry.getValue();
		}
		try {
			System.out.println("---sql " + sql);
			Connection conn = DBHelper.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql); 
			ResultSet rs = ps.executeQuery();
			// update flag
			updateHistoryFlag(paramMap,conn);
			List<CylinderWrapper> list_pioevent = new ArrayList<CylinderWrapper>();
			while(rs.next()){
				CylinderWrapper wapper = new CylinderWrapper();
				wapper.setCylinderId(rs.getString("cylinderid"));
				wapper.setMaterialId(rs.getString("materialid"));
				wapper.setFillingLevel(rs.getString("filling_level"));
				wapper.setLocationId(rs.getString("locationid"));
				wapper.setTimeStamp(StringFormatUtils.getStringFromTimestamp(rs.getTimestamp("timestamp")));
				//wapper.setDuration(rs.getInt("duration"));
				wapper.setFlag(rs.getInt("flag"));
				System.out.println("---" + wapper);
				list_pioevent.add(wapper);
			}
			DBHelper.release(conn, null, null);
			//Query query = em.createNativeQuery(sql, CylinderWrapper.class);

			//List<CylinderWrapper> list_pioevent = query.getResultList();
			System.out.println("---list_pioevent" + list_pioevent);
			Map<String, List<CylinderWrapper>> groups = new HashMap<String, List<CylinderWrapper>>();
			List<CylinderWrapper> wrappers = null;
			for (CylinderWrapper c : list_pioevent) {
				String key = c.getCylinderId() + c.getMaterialId() + c.getLocationId();
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
			List<PioEvent> list_pio_event = new ArrayList<PioEvent>();
			Set<String> keys = groups.keySet();
			for (String key : keys) {
				List<CylinderWrapper> clist = groups.get(key);
				System.out.println("---www clist: " + clist);
				Collections.sort(clist);
				System.out.println("---www clist.sort: " + clist);
				if (clist.size() >= 2) {
					// Integer count = clist.size()/2;
					// for(Integer i=0;i<count;i++){
					List<CylinderWrapper> list_cw = new ArrayList();
					for (Integer i = 0; i < clist.size(); i ++) {
						
						CylinderWrapper cw1 = clist.get(i);// 100
						
						if("0".equals(cw1.getFillingLevel())){
							doEventByHistoryPerGroup(list_cw,cw1,list_pio_event);
						}else{
							list_cw.add(cw1);
						}
						System.out.println("--sortAndGroupByEvent--list_cw : " + list_cw);
						/**
						 CylinderWrapper cw2 = clist.get(i + 1);// 0
							if (cw1.getFillingLevel().equals("100") && cw2.getFillingLevel().equals("0")) {
								Long milliseconds = CylinderWrapper.formatDateByString(cw2.getTimeStamp()).getTime()
										- CylinderWrapper.formatDateByString(cw1.getTimeStamp()).getTime();
								Integer duration = (int) (milliseconds / 1000 / 60 / 60 / 24);
								cw1.setDuration(duration);
								eventList.add(cw1);
							} else {
								i = i - 1;
								continue;
							}
						 * */

					}
				}
			}
			//List<PioEvent> list_pio_event = new ArrayList<PioEvent>();
			// save events
			/**
			 * for (CylinderWrapper c : eventList) {
				String text = c.getCylinderId() + " " + c.getMaterialId();
				String label = "";
				if (c.getDuration() <= 5)
					label = "high";
				else if (c.getDuration() > 10)
					label = "low";
				else
					label = "middle";

				getListPioEvent(list_pio_event, c, text, label);

			}
			 * */
			System.out.println(list_pio_event.size() + "---www list_pio_event: " + list_pio_event);
			return list_pio_event;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("--exception3  " + e.getMessage());
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();  
		}

		return null;
	}
	public String getDuringTime(CylinderWrapper bigFillCW,CylinderWrapper zeroFillCW) throws Exception{
		Long milliseconds = CylinderWrapper.formatDateByString(zeroFillCW.getTimeStamp()).getTime()
				- CylinderWrapper.formatDateByString(bigFillCW.getTimeStamp()).getTime();
		Integer duration = (int)Math.ceil((milliseconds / 1000 / 60 / 60 / 24));
		String result = "";
		switch(duration){
			case 0:
				result = "1";
				break;
			case 1:
				result = "1";
				break;
			case 2:
				result = "2";
				break;
			case 3:
				result = "3";
				break;
			case 4:
				result = "4";
				break;
			case 5:
				result = "5";
				break;
			case 6:
				result = "6";
				break;
			case 7:
				result = "7";
				break;
			default:
				result = "7+";
		}
		return result;
	}
	public void doEventByHistoryPerGroup(List<CylinderWrapper> list_cw,CylinderWrapper zeroCylinder,List<PioEvent> list_pio_event) throws Exception{
		System.out.println("--doEventByHistoryPerGroup--list_cw: " + list_cw);
		for(CylinderWrapper cw :list_cw){
			if("0".equals(cw.getFillingLevel())){
				continue;
			}
			String categories = getDuringTime(cw,zeroCylinder);
			System.out.println("--doEventByHistoryPerGroup--categories: " + categories);
			Double fill_level = Double.parseDouble(cw.getFillingLevel());
			String Filling_Level_Range = "";
			System.out.println("--doEventByHistoryPerGroup--fill_level: " + fill_level);
			if(80 < fill_level && fill_level <= 100){
				Filling_Level_Range = "81-100";
			}else if(60 < fill_level && fill_level <= 80){
				Filling_Level_Range = "61-80";
			}else if(40 < fill_level && fill_level <= 60){
				Filling_Level_Range = "41-60";
			}else if(20 < fill_level && fill_level <= 40){
				Filling_Level_Range = "21-40";
			}else if(0 < fill_level && fill_level <= 20){
				Filling_Level_Range = "0-20";
			}
			String text =cw.getMaterialId() + " " +  cw.getLocationId() + " " + Filling_Level_Range;
			System.out.println("--doEventByHistoryPerGroup--text: " + text);
			String label = categories;
			getListPioEvent(list_pio_event, cw, text, label);
			System.out.println("--doEventByHistoryPerGroup--list_pio_event: " + list_pio_event);
		}
		list_cw.clear();
	}

	public int updateHistoryFlag(Map<String, String> paramMap,Connection conn) throws Exception{
		int result = 0;
		String sql = "update pio_cylinder_filling_level_history set flag=1 where 1=1 ";
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			sql += " and  '" + entry.getKey() + "' = " + entry.getValue();
		}
		PreparedStatement ps = null; 
		ps = conn.prepareStatement(sql); 
		int i = ps.executeUpdate(); 
		if(i != 0){
			result  = 1;
		}
		return result; 
	}
	
	public void getListPioEvent(List<PioEvent> list_pio_event, CylinderWrapper c, String text, String label) {
		System.out.println("--text--" + text +"--getListPioEvent--label: " + list_pio_event);
		Date date = new Date();
		PioEvent event = new PioEvent();
		//Long entityid = Long.parseLong((Math.random() * 10 + "")) + date.getTime();
		event.setEntityId("" + new DecimalFormat("0").format(Math.random() * 10) + + date.getTime());
		event.setProperties("{\"text\": \"" + text + "\", \"label\": \"" + label + "\"}");
		event.setEvent("cylinder");
		event.setEntityType("content");
		list_pio_event.add(event);
		System.out.println("--getListPioEvent--list_pio_event: " + list_pio_event);
	}

	//@Transactional
	public int addHistory(CylinderWrapper history) {
		// TODO Auto-generated method stub
		int result = 1;
		try {
			//em.persist(history);
			Connection conn = DBHelper.getConnection();
			String sql = "insert into pio_cylinder_filling_level_history ( locationid, cylinderid, filling_level, flag, materialid, timestamp)"
					+ " VALUES('"+history.getLocationId()+"', '"+history.getCylinderId()+"' , '"+history.getFillingLevel()
					+"' ,'"+history.getFlag()+"' , '"+history.getMaterialId()+"' , '"+history.getTimeStamp()+"')";
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

	public String addHistoryListByJDBC(List<CylinderWrapper> list_history){
		try {
			Connection conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			int size = list_history.size();
			String sql = "INSERT into pio_cylinder_filling_level_history (locationid, cylinderid, filling_level, flag, materialid, timestamp) VALUES(?,?,?,?,?,?)";     
			PreparedStatement prest = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);  
			for(CylinderWrapper wapper : list_history){
				 prest.setString(1, wapper.getLocationId());     
		         prest.setString(2, wapper.getCylinderId());     
		         prest.setString(3, wapper.getFillingLevel());
		         prest.setInt(4, wapper.getFlag()==null?0:wapper.getFlag());     
		         prest.setString(5, wapper.getMaterialId());
		         prest.setTimestamp(6, StringFormatUtils.getTimesstampByString(wapper.getTimeStamp()));
		         prest.addBatch(); 
			}
			 int[] count = prest.executeBatch();  
			 conn.commit();
			 DBHelper.release(conn, null, null);
			 System.out.println("--count " + count);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("--exception " + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();  
			e.printStackTrace();
			return e.getMessage();
		}
		return "success";
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
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();  
		}

		return flag;
	}

	/**
	 * 
	 * reset table pio_cylinder_filling_level_history,pio_event_1
	 * */
	@Override
	public String resetData() {
		try {
			Connection conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			String sql = "TRUNCATE TABLE pio_cylinder_filling_level_history";     
			String sql_event = "TRUNCATE TABLE pio_event_1";  
			PreparedStatement prest = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);  
			PreparedStatement prest_event = conn.prepareStatement(sql_event,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);  
			 System.out.println("-resetDataByParam-count---- " );
			 int[] count = prest.executeBatch();  
			 int[] count_event = prest_event.executeBatch();  
			 System.out.println("-resetDataByParam-count---- " + count);
			 System.out.println("-resetDataByParam-count_event---- " + count_event);
			 conn.commit();
			 DBHelper.release(conn, null, null);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("-resetDataByParam-exception " + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); 
			e.printStackTrace();
			return e.getMessage();
		}
		return "success";
	}
}
