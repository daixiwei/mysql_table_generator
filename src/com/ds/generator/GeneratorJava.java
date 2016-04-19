package com.ds.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Types;


/**
 * 生成对应Java文件
 * 
 * @author daixiwei daixiwei15@126.com
 *
 */
public class GeneratorJava extends BaseGenerator{
	
	public GeneratorJava() throws Exception{
		super();
	}

	private static void addComment(StringBuilder builder,String str,int tab){
		builder.append("/**");
    	addTab(builder, tab);
    	builder.append(" * "+str);
    	addTab(builder, tab);
    	builder.append(" */");
    	addTab(builder, tab);
	}

	@Override
	protected void build_code(TableStruct ts, StringBuilder builder) {
		String packageName = config.getProperty("package_java");
		
		builder.append("package " + packageName + ";");
		addTab(builder, 0);
		addTab(builder, 0);
		addComment(builder,"此文件为自动生成请勿手工修改",0);
		builder.append("public class " + captureName(ts.name)+"Bean");
		builder.append("{");
		addTab(builder, 1);
		for(int i=0;i<ts.rows.size();++i){
			TableRow row = ts.rows.get(i);
			builder.append("private ");
			builder.append(getType(row.type));
			builder.append(row.name);
			builder.append(";");
			addTab(builder, 1);
		}
		
		for(int i=0;i<ts.rows.size();++i){
			TableRow row = ts.rows.get(i);
			addTab(builder, 1);
			addComment(builder,"获取" + row.comment,1);
			buildGetMethod(row,builder);
			addTab(builder, 1);
			addTab(builder, 1);
			addComment(builder,"设置" + row.comment,1);
			buildSetMethod(row,builder);
			addTab(builder, 1);
		}
		addTab(builder, 0);
		builder.append("}");
		addTab(builder, 0);
		try {
			File fs = new File(codeDir + packageName.replace('.', '/'));
			if(!fs.isDirectory()){
				fs.mkdirs();
			}

			OutputStreamWriter out1 = new OutputStreamWriter(new FileOutputStream(fs.getPath()+"/"+captureName(ts.name)+"Bean.java"),"UTF-8");
			out1.write(builder.toString());
			out1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void buildGetMethod(TableRow row,StringBuilder builder){
		builder.append("public ");
		builder.append(getType(row.type));
		builder.append("get" + captureName(row.name));
		builder.append("(){");
		addTab(builder, 2);
		builder.append("return ");
		builder.append(row.name +";");
		addTab(builder, 1);
		builder.append("}");
	}
	
	private static void buildSetMethod(TableRow row,StringBuilder builder){
		builder.append("public void ");
		builder.append("set" + captureName(row.name));
		builder.append("("+getType(row.type)+row.name+"){");
		addTab(builder, 2);
		builder.append("this."+row.name);
		builder.append(" = " +row.name +";");
		addTab(builder, 1);
		builder.append("}");
	}
	
	private static String getType(int type){
		switch(type){
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.LONGNVARCHAR:
			return "String ";
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return "Byte[] ";
		case Types.BIT:
			return "Boolean ";
		case Types.TINYINT:
		case Types.SMALLINT:
			return "Short ";
		case Types.INTEGER:
			return "Integer ";
		case Types.BIGINT:
			return "Long ";
		case Types.REAL:
			return "Float ";
		case Types.DOUBLE:
		case Types.FLOAT:
			return "Double ";
		case Types.DECIMAL:
		case Types.NUMERIC:
			return "java.math.BigDecimal ";
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return "java.util.Date ";
		default:
			return "";
		}
	}
	
	

}
