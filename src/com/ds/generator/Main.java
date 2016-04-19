package com.ds.generator;


public class Main {
	
	public static void main(String[] args) {
		try {
			String cmd_type = args[0];
			IGenerator generator = null;
			if(cmd_type.equals("cs")){
				generator = new GeneratorCSharp();
			}else if(cmd_type.equals("java")){
				generator = new GeneratorJava();
			}
			generator.build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
