package com.ds.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Types;

/**
 * 生成对应C#文件
 * 
 * @author daixiwei daixiwei15@126.com
 *
 */
public class GeneratorCSharp extends BaseGenerator{

	public GeneratorCSharp() throws Exception {
		super();
	}

	private static void addComment(StringBuilder builder,String str,int tab){
		builder.append("/// <summary>");
    	addTab(builder, tab);
    	builder.append("/// "+str);
    	addTab(builder, tab);
    	builder.append("/// </summary>");
    	addTab(builder, tab);
	}
	
	@Override
	protected void build_code(TableStruct ts, StringBuilder builder) {
		String packageName = config.getProperty("package_cs");
		
		builder.append("using System;");
		addTab(builder, 0);
		addTab(builder, 0);
		builder.append("namespace "+packageName);
		addTab(builder, 0);
		builder.append("{");
		addTab(builder, 1);
		addComment(builder,"此文件为自动生成请勿手工修改",1);
		builder.append("public class ").append(captureName(ts.name));
		addTab(builder, 1);
        builder.append("{");
        addTab(builder, 2);
        
        for(int i=0;i<ts.rows.size();++i){
			TableRow row = ts.rows.get(i);
			addTab(builder, 2);
			addComment(builder,row.comment,2);
			builder.append("public ");
			builder.append(getType(row.type));
			builder.append(captureName(row.name));
			builder.append(" { get; set; }");
			addTab(builder, 2);
		}
        
        addTab(builder, 1);
		builder.append("}");
		addTab(builder, 0);
		builder.append("}");
		addTab(builder, 0);
		
		try {
			File fs = new File(codeDir);
			if(!fs.isDirectory()){
				fs.mkdirs();
			}

			OutputStreamWriter out1 = new OutputStreamWriter(new FileOutputStream(fs.getPath()+"/"+captureName(ts.name)+".cs"),"UTF-8");
			out1.write(builder.toString());
			out1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			return "Byte ";
		case Types.TINYINT:
			return "Int16 ";
		case Types.SMALLINT:
			return "Int16 ";
		case Types.INTEGER:
			return "Int32 ";
		case Types.BIGINT:
			return "Int64 ";
		case Types.REAL:
			return "Single ";
		case Types.DOUBLE:
		case Types.FLOAT:
			return "Double ";
		case Types.DECIMAL:
		case Types.NUMERIC:
			return "Decimal ";
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return "DateTime ";
		default:
			return "";
		}
	}

}
