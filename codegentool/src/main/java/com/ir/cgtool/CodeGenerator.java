package com.ir.cgtool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ir.cgtool.domain.CgObject;
import com.ir.cgtool.domain.CodegenParameters;
import com.ir.cgtool.domain.DBColumn;
import com.ir.cgtool.util.CodeGenUtil;
import com.ir.util.DBInfo;
import com.ir.util.StringUtil;

public class CodeGenerator {
 
	public static void main(String[] args) throws Exception {
		new CodeGenerator().execute();
		System.out.println("Successfully generated the source");
	}

	
	public void execute() throws Exception {
 		CodegenParameters cgParams = new CodegenParameters(CGToolInfo.getInstance().getCgToolProperties());
		
		System.setProperty(DBInfo.USE_DATASOURCE, DBInfo.USE_DATASOURCE_FALSE);

		List<String> tableList = getTableNames(cgParams);

		if (tableList.isEmpty()) return;

 		for (String tableName : tableList) {
 			Map<String, DBColumn> dbColumnMap = CodeGenUtil.loadColumnMap(tableName);

			if (dbColumnMap == null || dbColumnMap.isEmpty()) continue;

			CgObject cgObject = new CgObject(tableName, cgParams);

			cgObject.prepareCgObject(dbColumnMap);

			cgObject.generateCode();
 		}
 		CodeGenUtil.createConfigFiles(cgParams);
 	}


	private List<String> getTableNames(CodegenParameters cgParams) throws Exception {
		List<String> tableList = new ArrayList<String>();
		if (StringUtil.isEmpty(cgParams.getTableNames())) {
			tableList = CodeGenUtil.getTableList();
		} else {
			tableList.add(cgParams.getTableNames());
		}
		return tableList;
	}
 
	
}
