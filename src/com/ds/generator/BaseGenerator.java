package com.ds.generator;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 
 * @author daixiwei daixiwei15@126.com
 *
 */
public abstract class BaseGenerator implements IGenerator {
	private static final String ROOT_PATH= System.getProperty("user.dir");
	private DruidDataSource dataSource;
	protected String codeDir;
	protected Properties config;
	protected List<TableStruct> tables;
	
	public BaseGenerator() throws Exception{
		FileReader reader = new FileReader(ROOT_PATH+"/config.properties");
		config = new Properties();
		config.load(reader);
		reader.close();
		
		dataSource = new DruidDataSource();
        dataSource.setDriverClassName(config.getProperty("driverName"));
        dataSource.setUrl(config.getProperty("url"));
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(50);
        dataSource.setUsername(config.getProperty("userName"));
        dataSource.setPassword(config.getProperty("passWord"));
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setFilters("stat");
        
        
        codeDir = config.getProperty("codeDir",ROOT_PATH+"/code/");
        
        tables = new ArrayList<TableStruct>();
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SHOW TABLES");
		ResultSet result = stmt.executeQuery();
		while(result.next()){
			String table_name = result.getString(1);
			TableStruct ts = new TableStruct();
			ts.name = table_name;
			tables.add(ts);
		}
		
		for(TableStruct ts : tables){
			stmt = conn.prepareStatement("select * from "+ ts.name +" limit 0,1");
			result = stmt.executeQuery();
			ResultSetMetaData metaData = result.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); ++i) {
				TableRow row = new TableRow();
				row.name = metaData.getColumnName(i);
				row.type = metaData.getColumnType(i);
				ts.rows.add(row);
			}
		}
		
		for(TableStruct ts : tables){
			stmt = conn.prepareStatement("show full fields from "+ ts.name);
			result = stmt.executeQuery();
			int i=0;
			while(result.next()){
				ts.rows.get(i).comment = result.getString("Comment");
				i++;
			}
		}
		
		conn.close();
		
	}
	
	@Override
	public final void build() {
		for(TableStruct ts : tables){
			StringBuilder builder = new StringBuilder();
			build_code(ts,builder);
		}
	}
	
	
	protected abstract void build_code(TableStruct ts,StringBuilder builder);
	
	/**
	 * 表结构
	 */
	protected static final class TableStruct{
		/**
		 * 数据表名
		 */
		public String name;
		/**
		 * 数据表字段集合
		 */
		public List<TableRow> rows = new ArrayList<TableRow>();
		
	}
	
	/**
	 * 表字段信息
	 */
	protected static final class TableRow{
		/**
		 * 数据表字段名
		 */
		public String name;
		/**
		 * 数据表字段类型
		 */
		public int type;
		/**
		 * 数据表字段注释
		 */
		public String comment;
	}
	
	public static String captureName(String name) {
		char[] cs = name.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);
	}
	
	public static void addTab(StringBuilder builder, int tab){
        builder.append("\n");
        for (int i = 0; i < tab; i++){
            builder.append("	");
        }
    }
}
