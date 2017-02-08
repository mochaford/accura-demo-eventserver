package com.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {

	public static final String url = "jdbc:mysql://127.0.0.1/student";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "root";
    
    static {
        try {
        	Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("加载Mysql数据库驱动失败！");
        }
    }
    
    public static Connection getConnection(){
        Connection conn = null;
        
        String url = "jdbc:postgresql://ec2-184-73-222-90.compute-1.amazonaws.com:5432/d2lsaamtekotkf?sslmode=require";
        String user = "hsikldswgfdloa";
        String password = "69ce55f038ca237ad57bdb26fd9965a5b44f41a14f39b88a1ad1596943f0a63d";
        try {
            conn = DriverManager.getConnection(url, user, password);
            //conn.setAutoCommit(false);
            System.out.println("connection is successful!");
        } catch (SQLException e) {
            System.out.println("connection error！");
            e.printStackTrace();
        }
        return conn;
    }
    public static void release(Connection conn,Statement state,ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(state != null){
            try {
                state.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
