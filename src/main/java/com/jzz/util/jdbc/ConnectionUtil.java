package com.jzz.util.jdbc;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此为java.sql.Connection 的工具类。
 * 用意：为了更方便的使用jdbc操作数据库。
 * 
 * @author JZZ 2016-06-29
 * 	modify 2016-11-29
 *
 */
public class ConnectionUtil{
	private static final Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);
	private static final Boolean isDebug = logger.isDebugEnabled();
    /**
     * 功能：使用prepareStatement(...)执行查询
     * 注意事项：querySql内的占位符 必须与paramArray的参数一致。
     * @throws Exception 
     */
    public static List<Map<String,Object>> executeQuery(Connection conn,String querySql,Object[] paramArray) throws Exception{
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Map<String,Object>> resultList = null;
        try{
            stmt = conn.prepareStatement(querySql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            setStmtParam(stmt,paramArray);
            rs = stmt.executeQuery();
            resultList = fromResultSetToList(rs);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            close(rs);
            close(stmt);
        }
        return resultList;
    }
    public static Integer executeUpdate(Connection conn, String insertSql, Object[] paramArray) throws Exception {
        PreparedStatement stmt = null;
        Integer affectCount = 0;
        try{
            stmt = conn.prepareStatement(insertSql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            setStmtParam(stmt,paramArray);
            affectCount = stmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            close(stmt);
        }
        return affectCount;
    }
    public static void close(ResultSet rs) {
        try {
			if(rs!=null && !rs.isClosed()){
			    rs.close();
			}
		} catch (SQLException e) {
			logger.error("Close ResultSet Error", e);
		}
        
    }
    public static void close(PreparedStatement stmt){
        try {
			if(stmt!=null && !stmt.isClosed()){
			    stmt.close();
			}
		} catch (SQLException e) {
			logger.error("Close PreparedStatement Error", e);
		}
    }
    public static void close(Connection conn) {
        try {
			if(conn!=null && !conn.isClosed()){
			    conn.close();
			}
		} catch (SQLException e) {
			logger.error("Close Connection Error", e);
		}
    }

    /**
     * 功能：读取 ResultSet 到 List<Map<String, Object>>  Object 为数据库类型所对应的java类型
     * 需设置 ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY
     * @param rs
     * @return
     * @throws SQLException 
     */
    public static List<Map<String, Object>> fromResultSetToList(ResultSet rs) throws SQLException {
        int rows = 0;
        int colmns = 0;
        ResultSetMetaData metaData = null;
        List<Map<String, Object>> resultList ;
        Map<String,Object> rowsMap ;
        try {
            rs.last();
            rows = rs.getRow();
            rs.beforeFirst();
            resultList = new ArrayList<Map<String, Object>>(rows);
            metaData = rs.getMetaData();
            colmns = metaData.getColumnCount();

            while(rs.next()){// 遍历每一行
                rowsMap = new HashMap<String,Object>();
                for(int i=1; i<=colmns; i++){
                    rowsMap.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                resultList.add(rowsMap);
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
            throw(e);
        }
    }
    /**
     * 功能：为stmt填充参数
     * @param stmt
     * @param array 参数数组，
     * @throws Exception
     */
    public static void setStmtParam(PreparedStatement stmt, Object[] array) throws Exception {
    	if(array == null){
    		return ;
    	}
        for(int i=0; i<array.length; i++){
            setStmtParam(stmt, i+1, array[i]);
        }
    }
    /**
     * 功能：为stmt填充参数
     * 若新增类型 需扩充if else
     * @param stmt
     * @param i 从1计数
     * @param object
     * @throws Exception
     */
    public static void setStmtParam(PreparedStatement stmt, int i, Object object) throws Exception {
    	if(object == null) {
            stmt.setObject(i, object);
    	}else if(object.getClass() == String.class){
            stmt.setString(i, (String) object);
        }else if(object.getClass() == Double.class){
            stmt.setDouble(i, (Double) object);
        }else if(object.getClass() == Long.class){
            stmt.setLong(i, (Long) object);
        }else if(object.getClass() == BigDecimal.class){
            stmt.setBigDecimal(i, (BigDecimal) object);
        }else{
            stmt.setObject(i, object);
        }
    }
    
	/** 保存 
	 * @param conn
	 * @param tableName 表
	 * @param properties <字段名, 值>
	 * @return
	 * @throws Exception
	 */
	public static Integer insert(Connection conn, String tableName, Map<String, Object> properties) throws Exception {
		List<String> columns = new ArrayList<String>(properties.size()+1);
		List<Object> values = new ArrayList<Object>(properties.size()+1);
				
		for (Map.Entry<String, Object> property : properties.entrySet()) {
			columns.add( property.getKey() );
			values.add( property.getValue() );
		}
		
		StringBuffer columnsBuf = new StringBuffer();
		StringBuffer questionBuf = new StringBuffer();
		for(String col : columns) {
			columnsBuf.append(col).append(",");
			questionBuf.append("?").append(",");
		}
		String columnsStr = columnsBuf.substring(0, columnsBuf.length()-1);
		String questionStr = questionBuf.substring(0, questionBuf.length()-1);
		
		String insertSql = " insert into t_ability ("+columnsStr+") values ("+questionStr+") ";
		return ConnectionUtil.executeUpdate(conn, insertSql, values.toArray());
	}
}