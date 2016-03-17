package com.ir.cgtool;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ir.cgtool.domain.CgObject;
import com.ir.cgtool.domain.CodegenParameters;
import com.ir.cgtool.domain.DBColumn;
import com.ir.cgtool.domain.Method;
import com.ir.cgtool.domain.Variable;
import com.ir.cgtool.util.CodeGenUtil;
import com.ir.util.ConnectionUtil;
import com.ir.util.DBInfo;
import com.ir.util.StringUtil;

public class CodeGenerator {
	

	public static final String DAO_CONFIG_XML = "dao-config.xml";

	public static final String SERVICE_CONFIG_XML = "service-config.xml";

	public static final String ALL_TABLE_SQLSERVER = " SELECT TABLE_NAME  FROM  INFORMATION_SCHEMA.TABLES  WHERE TABLE_NAME <> 'SEQUENCES' " ;

	public static final String COLUMN_SQL_SQLSERVER = " SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? ";
 
	public static final String KEYS_SQL_SQLSERVER = 
			" SELECT INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_TYPE CONSTRAINT_TYPE, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE.CONSTRAINT_NAME  CONSTRAINT_NAME  ,  INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE.COLUMN_NAME COLUMN_NAME " +
		    " FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE " + 
		    " WHERE INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_NAME = INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE.CONSTRAINT_NAME AND INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_NAME = ? " ;
	
    public static final String ALL_TABLE_ORACLE    = " SELECT TNAME TABLE_NAME  FROM  TAB " ; 
	
	public static final String COLUMN_SQL_ORACLE = " SELECT * FROM USER_TAB_COLUMNS WHERE TABLE_NAME=?";

	public static final String KEYS_SQL_ORACLE =
			" SELECT A.COLUMN_NAME,C.CONSTRAINT_TYPE,C.CONSTRAINT_NAME FROM ALL_CONS_COLUMNS A, ALL_CONSTRAINTS C " +
			" WHERE A.CONSTRAINT_NAME = C.CONSTRAINT_NAME AND A.TABLE_NAME=? ";
	
	public void execute() throws Exception {
		Connection connection = null ;
		try {
			CodegenParameters cgparams = new CodegenParameters(CGToolInfo.getInstance().getCgToolProperties());
			
			System.setProperty(DBInfo.USE_DATASOURCE, DBInfo.USE_DATASOURCE_FALSE);
			
			connection = ConnectionUtil.getConnection();
			 
			File srcDir = new File(cgparams.getSrcFolder()+"//"+cgparams.getSrcPackage().replace(".", "//"));
			if (!srcDir.exists()) srcDir.mkdirs();
			  
			 List<String> tableList = new ArrayList<String>();
			if(StringUtil.isEmpty(cgparams.getTableNames())) {
				  tableList = getTableList(connection);
			}else{
				tableList.add(cgparams.getTableNames());
			}
			
			cgparams.setConfigFileMap(new HashMap<String, Document>());
			
			if(cgparams.isCreateSpringService()) cgparams.getConfigFileMap().put(SERVICE_CONFIG_XML,getNewDocument());
			if(cgparams.isCreateSpringDao()) cgparams.getConfigFileMap().put(DAO_CONFIG_XML,getNewDocument());
			
			for(String table : tableList){
				generateCode(connection, table, cgparams);
			}
			
			if(cgparams.isCreateSpringService()) {
				 TransformerFactory transformerFactory = TransformerFactory.newInstance();
			     Transformer transformer =  transformerFactory.newTransformer();
			     DOMSource source = new DOMSource(cgparams.getConfigFileMap().get(SERVICE_CONFIG_XML));
			     StreamResult result = new StreamResult(new File(cgparams.getConfigFileLocation(),SERVICE_CONFIG_XML));
			     transformer.transform(source, result);
		    }
			
			if(cgparams.isCreateSpringDao()) {
				 TransformerFactory transformerFactory = TransformerFactory.newInstance();
			     Transformer transformer =  transformerFactory.newTransformer();
			     DOMSource source = new DOMSource(cgparams.getConfigFileMap().get(DAO_CONFIG_XML));
			     StreamResult result = new StreamResult(new File(cgparams.getConfigFileLocation(),DAO_CONFIG_XML));
			     transformer.transform(source, result);
	 	 	}
		    
		} catch (Exception ex) {
			throw ex;
		} finally{
			ConnectionUtil.closeConnection(connection);
		}

	}




	private Document getNewDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document document = dBuilder.newDocument();

		Element rootElement = document.createElement("beans");

		Attr attr = document.createAttribute("xmlns");
		attr.setValue("http://www.springframework.org/schema/beans");
		rootElement.setAttributeNode(attr);

		attr = document.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(attr);

		attr = document.createAttribute("xmlns:context");
		attr.setValue("http://www.springframework.org/schema/context");
		rootElement.setAttributeNode(attr);

		attr = document.createAttribute("xsi:schemaLocation");
		attr.setValue(" http://www.springframework.org/schema/beans  http://www.springframework.org/schema/beans/spring-beans-3.0.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd http://cxf.apache.org/jaxrs http://cxf.apache.org/schemas/jaxrs.xsd");
		rootElement.setAttributeNode(attr);
		
		document.appendChild(rootElement);

		return document;
	}


	private List<String> getTableList(Connection connection) throws Exception {
		 
		PreparedStatement psmt = null;
		ResultSet rs = null;
 		
		List<String> tableList = new ArrayList<String>();
		
		try {
			
			String dbProductName = connection.getMetaData().getDatabaseProductName();
			 
			String tableSQL = null ; 
			if("Oracle".equalsIgnoreCase(dbProductName)){
				  tableSQL = ALL_TABLE_ORACLE ; 
			}else if("Microsoft SQL Server".equalsIgnoreCase(dbProductName)){
				tableSQL = ALL_TABLE_SQLSERVER ; 
			}	
			
			psmt = connection.prepareStatement(tableSQL);
			rs = psmt.executeQuery();
			while (rs.next()) {
				tableList.add(rs.getString("TABLE_NAME")); 
		     }

			 			
		} catch(Exception ex){
			throw ex;
		} finally {
 			ConnectionUtil.closeConnection(rs, psmt);
		}
		return tableList;

	}


	protected void generateCode(Connection connection, String tableName, CodegenParameters codegenParameters) throws  SQLException, IOException {
		
		
		CgObject cgObject = new CgObject(tableName,codegenParameters );	 
		
 		
		Map<String,DBColumn> dbColumnMap = loadColumnMap(tableName,connection);
		if(dbColumnMap==null || dbColumnMap.isEmpty()) return;
		
		Iterator<String> itr = dbColumnMap.keySet().iterator();
		
		//List<String> importList = new ArrayList<String>();
		boolean dateImportAdded = false;
	 	
		StringBuffer dhMapFromDB = new StringBuffer();
		StringBuffer dhMapToDB = new StringBuffer();
		
		StringBuffer sqlInsert = new StringBuffer("\" INSERT INTO ").append(tableName).append(" (");
		StringBuffer sqlUpdate = new StringBuffer("\" UPDATE ").append(tableName).append(" SET "); 
		StringBuffer sqlDelete = new StringBuffer("\" DELETE FROM  ").append(tableName);
		
		
	
		
		List<DBColumn> dbCols = new ArrayList<DBColumn>();
		
		while (itr.hasNext()) {
			//TODO move this to JavaSource class
			DBColumn dbColumn = dbColumnMap.get(itr.next());
			String colName = CodeGenUtil.toUpperCase(dbColumn.getColumnName(), 0);
			
			Variable variable = new Variable(dbColumn.getColumnName(), dbColumn.getColumnType(), "private");
			
			List<Variable> params = new ArrayList<Variable>();
			params.add(variable);
			StringBuffer setMethodBody = new StringBuffer();
			setMethodBody.append("\t").append("\t").append("this.").append(variable.getName()).append(" = ").append(variable.getName()).append(";");
			StringBuffer getMethodBody = new StringBuffer();
			getMethodBody.append("\t").append("\t").append("return ").append(variable.getName()).append(";");
			
			cgObject.getDomain().getVariableList().add(variable);
			cgObject.getDomain().getMethodList().add(new Method("public", dbColumn.getColumnType(), dbColumn.getMethodName(), new ArrayList<Variable>(),getMethodBody.toString()));
			cgObject.getDomain().getMethodList().add(new Method("public", "void", "set"+colName, params,setMethodBody.toString()));
		
		 
			
			if(!dateImportAdded && "Date".equalsIgnoreCase(dbColumn.getColumnType())) {
				cgObject.getDomain().getImportList().add("java.sql.Date");
				dateImportAdded = true;
			}
			
			
			 //Service/DAO Methods
			
			 if(dbColumn.isPrimaryKeyColumn() || dbColumn.isUniqueKeyColumn() || dbColumn.isRefKeyColumn()) {
			  	 loadMethod(tableName, cgObject, dbColumn); 
				 deleteMethod(cgObject, dbColumn); 
			 }
			 
  			 if(dbColumn.isPrimaryKeyColumn()) {
				 cgObject.setPk(dbColumn); 
			 }else {
				 dbCols.add(dbColumn);
			 }
			 
			
		}
		
		 
		
		dbCols.add(cgObject.getPk());
		int columnCnt=0;
		for(DBColumn dbCol : dbCols){
			String colName = CodeGenUtil.toUpperCase(dbCol.getColumnName(), 0);
			if("boolean".equals(dbCol.getColumnType())){
				dhMapFromDB.append("\t\t").append(cgObject.getDomainExt().getNameForVariable()).append(".set"+colName+"(").append("\"Y\".equals(rs.getString(\"").append(dbCol.getDbColumnName()).append("\")));").append("\n\n");
				dhMapToDB.append("\t\t").append("psmt.setString(").append(columnCnt+1).append(",").append(cgObject.getDomainExt().getNameForVariable()).append(".is").append(colName).append("()?\"Y\":\"N\"").append(");").append("\n\n");
			}else{
				dhMapFromDB.append("\t\t").append(cgObject.getDomainExt().getNameForVariable()).append(".set"+colName+"(").append("rs.get").append(dbCol.getColumnType()).append("(\"").append(dbCol.getDbColumnName()).append("\"));").append("\n\n");
				dhMapToDB.append("\t\t").append("if( null == ").append(cgObject.getDomainExt().getNameForVariable()).append(".get").append(colName).append("()){ ").append("\n");
				dhMapToDB.append("\t\t\t").append("psmt.setNull(").append(columnCnt+1).append(",").append(dbCol.getNullType()).append("); \n");
				dhMapToDB.append("\t\t").append("} else { ").append("\n");
				dhMapToDB.append("\t\t\t").append("psmt.set").append(dbCol.getColumnType()).append("(").append(columnCnt+1).append(",").append(cgObject.getDomainExt().getNameForVariable()).append(".get").append(colName).append("());").append("\n");
				dhMapToDB.append("\t\t").append("}").append("\n\n");
			}
			
	    if(columnCnt>0) {
			 sqlInsert.append(" ,");
			 if(!dbCol.isPrimaryKeyColumn()) sqlUpdate.append(" , ");
		 }
	    
	    
		 sqlInsert.append(dbCol.getDbColumnName());
		 
		 if(!dbCol.isPrimaryKeyColumn()) sqlUpdate.append(dbCol.getDbColumnName()).append(" = ? ");
		 
		 columnCnt++;
			
		}
		
		
		sqlInsert.append(" ) VALUES ( "); 
		for(int i=0; i<columnCnt;i++){
			if(i>0) sqlInsert.append(" ,");
			sqlInsert.append("?");
		}
		
		sqlUpdate.append(" WHERE ").append(cgObject.getPk().getDbColumnName()).append(" =  ? \"");
		sqlDelete.append(" WHERE ").append(cgObject.getPk().getDbColumnName()).append(" =  ? \"");
		
		
		
		sqlInsert.append(")\"");
		
		 
		Variable seqVar = new Variable("SEQ_KEY", "String", "public static final");
		seqVar.setInitVal("\"" + cgObject.getSeqName()+  "\"");
		cgObject.getDomainHelper().getVariableList().add(seqVar);
		
		Variable insertSQLVar = new Variable("INSERT_"+cgObject.getDomainExt().getName()+"_SQL", "String", "public static final");
		insertSQLVar.setInitVal(sqlInsert.toString());
		cgObject.getDomainHelper().getVariableList().add(insertSQLVar);
		

		Variable updateSQLVar = new Variable("UPDATE_"+cgObject.getDomainExt().getName()+"_SQL", "String", "public static final");
		updateSQLVar.setInitVal(sqlUpdate.toString());
		cgObject.getDomainHelper().getVariableList().add(updateSQLVar);
		
		Variable deletetSQLVar = new Variable("DELETE_"+cgObject.getDomainExt().getName()+"_SQL", "String", "public static final");
		deletetSQLVar.setInitVal(sqlDelete.toString());
		cgObject.getDomainHelper().getVariableList().add(deletetSQLVar);
		
		
		//load all records
		loadMethod(tableName, cgObject, null); 
		
		domainHelperMaptoDomain(cgObject, dhMapFromDB);
		
		domainHelperMapToDB(cgObject, dhMapToDB);

		saveMethod(cgObject,false, "add");

		//saveMethod(cgObject,true , "add");

		saveMethod(cgObject,false , "update");
		cgObject.generateCode();
	}


	


	private void domainHelperMapToDB(CgObject cgObject, StringBuffer dhMapToDB) {
		List<Variable> dhparams = new ArrayList<Variable>();
		dhparams.add(new Variable("psmt", "PreparedStatement", "private"));
		dhparams.add(new Variable(cgObject.getDomainExt().getNameForVariable(), cgObject.getDomainExt().getName(), "private"));
		
		StringBuffer dhMethodBody = new StringBuffer();
		//dhMethodBody.append("\t\t").append(cgObject.getDomain().getName()).append(" ").append(cgObject.getDomain().getNameForVariable()).append(" = new ").append(cgObject.getDomain().getName()).append("();\n\n");
		dhMethodBody.append(dhMapToDB);
		//dhMethodBody.append("\t\t").append("return ").append(cgObject.getDomain().getNameForVariable()).append(";").append("\n");
		Method dhMethod = new Method("public" , "void" , "set"+cgObject.getDomainExt().getName(),dhparams, dhMethodBody.toString());
		dhMethod.getThrownExceptions().add("SQLException");
		//cgObject.getDomainHelper().getImportList().add("java.sql.SQLException");
		cgObject.getDomainHelper().getMethodList().add(dhMethod);
		
	}


	protected void domainHelperMaptoDomain(CgObject cgObject, StringBuffer dhLoadFromDB) {
		List<Variable> dhparams = new ArrayList<Variable>();
		dhparams.add(new Variable("rs", "ResultSet", "private"));
		StringBuffer dhMethodBody = new StringBuffer();
		dhMethodBody.append("\t\t").append(cgObject.getDomainExt().getName()).append(" ").append(cgObject.getDomainExt().getNameForVariable()).append(" = new ").append(cgObject.getDomainExt().getName()).append("();\n\n");
		dhMethodBody.append(dhLoadFromDB);
		dhMethodBody.append("\t\t").append("return ").append(cgObject.getDomainExt().getNameForVariable()).append(";").append("\n");
		Method dhMethod = new Method("public" , cgObject.getDomainExt().getName(), "get"+cgObject.getDomainExt().getName(),dhparams, dhMethodBody.toString());
		dhMethod.getThrownExceptions().add("SQLException");
		cgObject.getDomainHelper().getImportList().add("java.sql.SQLException");
		cgObject.getDomainHelper().getImportList().add(cgObject.getDomainExt().getFullName());
		cgObject.getDomainHelper().getMethodList().add(dhMethod);
	}


	protected void saveMethod(CgObject cgObject,boolean multiple,String type) {
		 List<Variable> params = new ArrayList<Variable>();
		 List<Variable> restSvcParams = new ArrayList<Variable>();
		 
		 String methodName = null ; 
 		 Variable variable =  null; 
 		 String returnType = null ; 
		 String returnTypeImpl = null ; 
		 String methodAnnotation = null; 
		  
		 
		 
		 if("update".equalsIgnoreCase(type)){
			  returnType =  "void" ;
			  methodName =  "update"+cgObject.getDomainExt().getName() ;
			 
			  if(multiple){
				 variable = new Variable(cgObject.getDomainExt().getName()+"s", "List<"+cgObject.getDomainExt().getName()+">", "private");
				 params.add(variable);
				 returnType =   "List<Long>" ;
				 returnTypeImpl = "new ArrayList<Long>()";
			} else {
				variable = new Variable(cgObject.getDomainExt().getName(), cgObject.getDomainExt().getName(),"private");
				params.add(variable);
				Variable restParamVar = new Variable(cgObject.getPk().getColumnName(), cgObject.getPk().getColumnType(),"private");
				restParamVar.addAnnotation("PathParam(\"" + cgObject.getPk().getColumnName() + "\")");
				restSvcParams.add(restParamVar);
				restSvcParams.add(variable);
				methodAnnotation = "PUT";
			}
		} else {
			methodName = "add" + cgObject.getDomainExt().getName();
			if (multiple) {
				variable = new Variable(cgObject.getDomainExt().getName() + "s", "List<" + cgObject.getDomainExt().getName() + ">", "private");
				params.add(variable);
				returnType = "List<Long>";
				returnTypeImpl = "new ArrayList<Long>()";
			} else {
				variable = new Variable(cgObject.getDomainExt().getName(), cgObject.getDomainExt().getName(), "private");
				returnType = "Long";
				returnTypeImpl = cgObject.getDomainExt().getNameForVariable() + "." + cgObject.getPk().getMethodName() + "()";
				params.add(variable);
  
				restSvcParams.add(variable);
				methodAnnotation = "POST";
			}
		}
		 
		 StringBuffer methodBodyDaoImpl = getDaoImplMethodBody(cgObject, returnTypeImpl,type,null);
		 StringBuffer methodBodyServiceImpl  = new StringBuffer();
		 methodBodyServiceImpl.append("\t").append("\t");
		 if(!"update".equalsIgnoreCase(type))    methodBodyServiceImpl.append("return ");
		 methodBodyServiceImpl.append(cgObject.getDaoExt().getNameForVariable()).append(".").append(methodName).append("(")
		 					  .append(Method.getParamCode(params,Variable.PARAM_MODE_EXE)).append(")").append(";");
		 
		 	 
		 Method method = new Method("public",returnType, methodName, params  , null,true);
		 method.getThrownExceptions().add("Exception");
		 cgObject.getDaoBase().getMethodList().add(method);
 		 cgObject.getServiceBase().getMethodList().add(method);
		
 		 
 		 method = new Method("public",returnType, methodName, params  , methodBodyDaoImpl.toString(),false);
		 method.getThrownExceptions().add("Exception");
		 cgObject.getDaoBaseImpl().getMethodList().add(method);
 
		 method = new Method("public",returnType, methodName, params  , methodBodyServiceImpl.toString(),false);
		 method.getThrownExceptions().add("Exception");
		 cgObject.getServiceBaseImpl().getMethodList().add(method);
		 
		 if(!multiple){
			 DBColumn dbColumn = cgObject.getPk();
			 String colName = CodeGenUtil.toUpperCase(dbColumn.getColumnName(), 0);
			 String getMethodName =  "get"+cgObject.getDomainExt().getName();
			 
			 StringBuffer methodBodyRestSvc  = new StringBuffer();
			 methodBodyRestSvc.append("\t").append("\t");
			 if("update".equalsIgnoreCase(type)){ 
				 methodBodyRestSvc.append(cgObject.getDomainExt().getNameForVariable()).append(".set").append(colName).append("(").append(dbColumn.getColumnName()).append(");").append(StringUtil.LINE_SEPARTOR);
				 methodBodyRestSvc.append("\t").append("\t");
			 }else{
				 methodBodyRestSvc.append(dbColumn.getColumnType()).append(" ").append(dbColumn.getColumnName()).append(" = ");
			 }
			
			 methodBodyRestSvc.append(cgObject.getServiceExt().getNameForVariable()).append(".").append(methodName).append("(")
			 					  .append(Method.getParamCode(params,Variable.PARAM_MODE_EXE)).append(")").append(";").append(StringUtil.LINE_SEPARTOR);
			 
			 methodBodyRestSvc.append("\t").append("\t").append("return ").append(cgObject.getServiceExt().getNameForVariable()).append(".").append(getMethodName).append("(")
			 .append(dbColumn!=null ? dbColumn.getColumnName():"")
			 .append(")").append(";");
			 
			 method = new Method("public",cgObject.getDomainExt().getName(), methodName, restSvcParams  , methodBodyRestSvc.toString(),false);
			 method.addAnnotation(methodAnnotation);
			 if("update".equalsIgnoreCase(type)) method.addAnnotation("PathParam(\"/{"+cgObject.getPk().getColumnName()+"}\")");
			 method.getThrownExceptions().add("Exception");
			 cgObject.getRestService().getMethodList().add(method);
		 }
		
		 
	}


	protected StringBuffer getDaoImplMethodBody(CgObject cgObject,String  returnTypeImpl,String type,DBColumn dbColumn) {
		StringBuffer methodBodyDaoImpl;
		methodBodyDaoImpl = new StringBuffer();
		 
		String sql = "add".equalsIgnoreCase(type)?".INSERT_":"delete".equalsIgnoreCase(type)?".DELETE_":".UPDATE_" ; 
		 
		 methodBodyDaoImpl.append("\t\t").append("String sql = ").append(cgObject.getDomainHelperExt().getName()).append(sql).append(cgObject.getDomainExt().getName().toUpperCase()).append("_SQL").append(";\n")
						  .append("\t\t").append("Connection connection = null ; ").append("\n")
						  .append("\t\t").append("PreparedStatement pstmt = null ;").append("\n")
						  .append("\t\t").append("try { ").append("\n");
						  
							if ("add".equalsIgnoreCase(type)) {
								methodBodyDaoImpl.append("\t\t\t")
										.append(cgObject.getDomainExt().getNameForVariable())
										.append(".").append(cgObject.getPk().setMethodName())
										.append("(DBUtil.getId(")
										.append(cgObject.getDomainHelperExt().getName())
										.append(".SEQ_KEY));").append("\n");
							}
							
							
						  
							methodBodyDaoImpl.append("\t\t\t").append("connection = ConnectionUtil.getConnection();").append("\n")
							.append("\t\t\t").append("pstmt=connection.prepareStatement(sql);").append("\n");
						
							if ("delete".equalsIgnoreCase(type)) {
								methodBodyDaoImpl.append("\t\t\t").append("pstmt.set")
										.append(dbColumn.getColumnType()).append("(1,")
										.append(dbColumn.getColumnName()).append(");")
										.append("\n");
							} else {
								methodBodyDaoImpl.append("\t\t\t")
										.append(cgObject.getDomainHelperExt().getNameForVariable())
										.append(".set").append(cgObject.getDomainExt().getName())
										.append("(pstmt,")
										.append(cgObject.getDomainExt().getNameForVariable())
										.append(");").append("\n");
					
							}
							  
		 methodBodyDaoImpl.append("\t\t\t").append("pstmt.executeUpdate();").append("\n");
		 
		 						  
						  
		    methodBodyDaoImpl.append("\t\t\t").append(" ").append("\n")
						  .append("\t\t\t").append(" ").append("\n")
			//			  .append("\t\t\t").append("}").append("\n")
						  .append("\t  ").append("} catch(Exception ex) { ").append("\n\n")
						  .append("\t\t  ").append("throw ex; ").append("\n\n")
						  .append("\t  ").append("} finally {").append("\n")
						  .append("\t\t  ").append("ConnectionUtil.closeConnection(connection,pstmt);").append("\n")
						  .append("\t  ").append("}").append("\n");
		    if("add".equalsIgnoreCase(type))
		    methodBodyDaoImpl.append("\t").append("\t").append("return ").append(returnTypeImpl).append(";");
		return methodBodyDaoImpl;
	}


	protected void deleteMethod(CgObject cgObject, DBColumn dbColumn){
		Variable variable = new Variable(dbColumn.getColumnName(), dbColumn.getColumnType(), "private");
		List<Variable> params = new ArrayList<Variable>();
		params.add(variable);
		
		
		Variable restParamVar = new Variable(dbColumn.getColumnName(), dbColumn.getColumnType(), "private");
		restParamVar.addAnnotation("PathParam(\""+dbColumn.getColumnName()+"\")");
		List<Variable> restSvcParams = new ArrayList<Variable>();
		restSvcParams.add(restParamVar);
 
		String colName = CodeGenUtil.toUpperCase(dbColumn.getColumnName(), 0);
		 	 
		 //delete
		 String methodName = "delete"+cgObject.getDomainExt().getName();
		 if(!dbColumn.isPrimaryKeyColumn() )methodName =  methodName.concat("By").concat(colName);
		 
		 Method method = new Method("public","void", methodName, params  , null,true);
		 method.getThrownExceptions().add("Exception");
		 cgObject.getDaoBase().getMethodList().add(method);
		 cgObject.getServiceBase().getMethodList().add(method);
		 
		 StringBuffer methodBodyDaoImpl =   getDaoImplMethodBody(cgObject, "true;" , "delete",dbColumn);
		 Method daoImplMethod = new Method("public","void", methodName, params  , methodBodyDaoImpl.toString());
		 cgObject.getDaoBaseImpl().getMethodList().add(daoImplMethod);
		 daoImplMethod.getThrownExceptions().add("Exception");
		 
		 StringBuffer methodBodyServiceImpl = new StringBuffer();
		 methodBodyServiceImpl.append("\t").append("\t").append(cgObject.getDaoExt().getNameForVariable()).append(".").append(methodName).append("(").append(dbColumn.getColumnName()).append(")").append(";");
		 Method serviceMethod = new Method("public","void", methodName, params  , methodBodyServiceImpl.toString());
		 serviceMethod.getThrownExceptions().add("Exception");
		 cgObject.getServiceBaseImpl().getMethodList().add(serviceMethod);
		 
		 
		 StringBuffer methodBodyRestService  = new StringBuffer();
		 methodBodyRestService.append("\t").append("\t").append(cgObject.getServiceExt().getNameForVariable()).append(".").append(methodName).append("(").append(dbColumn.getColumnName()).append(")").append(";");
	
		 
		 Method restMethod = new Method("public","void", methodName , restSvcParams  , methodBodyRestService.toString());
		 restMethod.addAnnotation("DELETE");
		 
		 if ( dbColumn.isPrimaryKeyColumn()) {
				restMethod.addAnnotation("Path(\"/{" + dbColumn.getColumnName() + "}\")");
			} else if (dbColumn != null && (dbColumn.isRefKeyColumn() || dbColumn.isUniqueKeyColumn())) {
				restMethod.addAnnotation("Path(\"/" + dbColumn.getColumnName()+"/{"+dbColumn.getColumnName()+"}" + "\")");
			}
		 
		 restMethod.getThrownExceptions().add("Exception");
		 
	
		 
		 cgObject.getRestService().getMethodList().add(restMethod);
		 
	}


	protected void loadMethod(String tableName, CgObject cgObject,DBColumn dbColumn) {
		
		
		List<Variable> params = new ArrayList<Variable>();
		List<Variable> restSvcParams = new ArrayList<Variable>();
		String colName  = null ; 
		String returnType     = cgObject.getDomainExt().getName();
		String returnTypeName = cgObject.getDomainExt().getNameForVariable();
		String returnTypeInit = "null" ; 
		String sql = null ;
		String methodName = null;
 
		if(dbColumn!=null) {
			params.add(new Variable(dbColumn.getColumnName(), dbColumn.getColumnType(), "private"));
			
			Variable restParamVar = new Variable(dbColumn.getColumnName(), dbColumn.getColumnType(), "private");
			restParamVar.addAnnotation("PathParam(\""+dbColumn.getColumnName()+"\")");
			restSvcParams.add(restParamVar);
			
			colName = CodeGenUtil.toUpperCase(dbColumn.getColumnName(), 0);
			sql = "\" SELECT * FROM " + tableName + " WHERE " +  dbColumn.getDbColumnName() + " = ?  \" " ;
			methodName = "get"+cgObject.getDomainExt().getName();
			if(!dbColumn.isPrimaryKeyColumn()) methodName = methodName +"By"+colName ;
			
		}else{
		    sql = "\" SELECT * FROM " + tableName + " \" " ; 
		    methodName = "get"+cgObject.getDomainExt().getName()+"s" ;
		}
		
		 if(dbColumn==null || dbColumn.isRefKeyColumn()){
			 returnType = "List<"+returnType+">";
			 returnTypeName = returnTypeName+"List";
			 returnTypeInit = "new Array"+returnType+"()"; 
		 }
		 	
		 //load Method
		 Method method = new Method("public",returnType, methodName, params  , null,true);
		 method.getThrownExceptions().add("Exception");
		  
		 cgObject.getDaoBase().getMethodList().add(method);
		 
		 Method methodService = new Method("public",returnType, methodName, params  , null,true);
		 methodService.getThrownExceptions().add("Exception");
		 cgObject.getServiceBase().getMethodList().add(methodService);
		 
		 StringBuffer methodBodyDaoImpl = new StringBuffer();
		 methodBodyDaoImpl.append("\t\t").append("String sql = ").append(sql).append(";\n")
						  .append("\t\t").append("Connection connection = null ; ").append("\n")
						  .append("\t\t").append("PreparedStatement pstmt = null ;").append("\n")
						  .append("\t\t").append("ResultSet rs = null ;").append("\n")
						  .append("\t\t").append(returnType).append(" ").append(returnTypeName).append(" = ").append(returnTypeInit).append(";").append("\n")
						  .append("\t\t").append("try { ").append("\n")
						  .append("\t\t\t").append("connection = ConnectionUtil.getConnection();").append("\n")
						  .append("\t\t\t").append("pstmt=connection.prepareStatement(sql);").append("\n");
		if(null !=dbColumn) {
			methodBodyDaoImpl.append("\t\t\t").append("pstmt.").append("set").append(dbColumn.getColumnType()).append("(1,").append(dbColumn.getColumnName()).append(");").append("\n");
				
		}
						  
		methodBodyDaoImpl.append("\t\t\t").append("rs=pstmt.executeQuery();").append("\n\n")
						  .append("\t\t\t").append("while(rs.next()){").append("\n");
						   if(dbColumn==null || dbColumn.isRefKeyColumn()){
							   methodBodyDaoImpl.append("\t\t\t").append(returnTypeName).append(".add(")
							                    .append(cgObject.getDomainHelperExt().getNameForVariable()).append(".get").append(cgObject.getDomainExt().getName())
							                    .append("(rs));").append("\n");
						   }else{
							   methodBodyDaoImpl.append("\t\t\t").append(returnTypeName).append(" = ")
							                    .append(cgObject.getDomainHelperExt().getNameForVariable()).append(".get").append(cgObject.getDomainExt().getName())
							                    .append("(rs);").append("\n");   
						   }
						  
						  
	     methodBodyDaoImpl.append("\t\t\t").append(" ").append("\n")
						  .append("\t\t\t").append(" ").append("\n")
						  .append("\t\t\t").append("}").append("\n")
						  .append("\t  ").append("} catch(Exception ex) { ").append("\n\n")
						  .append("\t\t  ").append("throw ex; ").append("\n\n")
						  .append("\t  ").append("} finally {").append("\n")
						  .append("\t\t  ").append("ConnectionUtil.closeConnection(connection,rs,pstmt);").append("\n")
						  .append("\t  ").append("}").append("\n");
		 methodBodyDaoImpl.append("\t").append("\t").append("return ").append(returnTypeName).append(";");
		 
		 Method m = new Method("public",returnType, methodName, params  , methodBodyDaoImpl.toString());
		 m.getThrownExceptions().add("Exception");
		 cgObject.getDaoBaseImpl().getMethodList().add(m);
		 
		 StringBuffer methodBodyServiceImpl = new StringBuffer();
		 methodBodyServiceImpl.append("\t").append("\t").append("return ").append(cgObject.getDaoExt().getNameForVariable()).append(".").append(methodName).append("(")
		 .append(dbColumn!=null ? dbColumn.getColumnName():"")
		 .append(")").append(";");
		 
		 Method serviceMethod =  new Method("public",returnType, methodName , params  , methodBodyServiceImpl.toString());
		 serviceMethod.getThrownExceptions().add("Exception");
		 cgObject.getServiceBaseImpl().getMethodList().add(serviceMethod);
		 
		 
		 StringBuffer methodBodyRestSvc = new StringBuffer();
		 methodBodyRestSvc.append("\t").append("\t").append("return ").append(cgObject.getServiceExt().getNameForVariable()).append(".").append(methodName).append("(")
		 .append(dbColumn!=null ? dbColumn.getColumnName():"")
		 .append(")").append(";");
		 
		 Method restMethod =  new Method("public",returnType, methodName , restSvcParams  , methodBodyRestSvc.toString());
		 restMethod.getThrownExceptions().add("Exception");
		
		restMethod.addAnnotation("GET");
		if (dbColumn != null && dbColumn.isPrimaryKeyColumn()) {
			restMethod.addAnnotation("Path(\"/{" + dbColumn.getColumnName() + "}\")");
		} else if (dbColumn != null && (dbColumn.isRefKeyColumn() || dbColumn.isUniqueKeyColumn())) {
			restMethod.addAnnotation("Path(\"/" + dbColumn.getColumnName()+"/{"+dbColumn.getColumnName()+"}" + "\")");
		}
		 
		 
		 cgObject.getRestService().getMethodList().add(restMethod);
 	}

 
	public Map<String, DBColumn> loadColumnMap(String tableName,Connection connection) throws  SQLException {
		PreparedStatement psmt = null;
		ResultSet rs = null;
		
		PreparedStatement psmt1 = null;
		ResultSet rs1 = null;
		
		
		Map<String, DBColumn> dbColumnMap = new LinkedHashMap<String,DBColumn>();
		try {
			
			String dbProductName = connection.getMetaData().getDatabaseProductName();
			
			String columnSql = null ; 
			String keysSql = null  ; 
			if("Oracle".equalsIgnoreCase(dbProductName)){
				  columnSql = COLUMN_SQL_ORACLE ; 
				  keysSql = KEYS_SQL_ORACLE  ; 
			}else if("Microsoft SQL Server".equalsIgnoreCase(dbProductName)){
				  columnSql = COLUMN_SQL_SQLSERVER ; 
				  keysSql = KEYS_SQL_SQLSERVER  ; 
			}	
			
			psmt = connection.prepareStatement(columnSql);
			psmt.setString(1, tableName);
			rs = psmt.executeQuery();
			while (rs.next()) {
				DBColumn dbColumn = new DBColumn();
				String dbColumnName = rs.getString("COLUMN_NAME"); 
				String dbColumnType = rs.getString("DATA_TYPE");
				if("Oracle".equalsIgnoreCase(dbProductName)){
					dbColumn.setDataPrecision(new Integer(rs.getInt("DATA_PRECISION")));
					dbColumn.setDataScale(new Integer(rs.getInt("DATA_SCALE")));
				}
				dbColumn.setDbColumnName(dbColumnName); 
				dbColumn.setDbColumnType(dbColumnType);
				dbColumn.setColumnName(CodeGenUtil.getJavaColName(dbColumnName.toLowerCase()) );
				dbColumn.setColumnType(dbColumn.getJavaColType(dbProductName) );
				dbColumnMap.put(dbColumnName, dbColumn);
			 }

			
			psmt1 = connection.prepareStatement(keysSql);
			psmt1.setString(1, tableName);
			rs1 = psmt1.executeQuery();
			DBColumn pkColumn = null; 
			while (rs1.next()) {
				String keyType = rs1.getString("CONSTRAINT_TYPE");
				DBColumn dbColumn = dbColumnMap.get(rs1.getString("COLUMN_NAME"));
				if("PRIMARY KEY".equalsIgnoreCase(keyType) || "P".equalsIgnoreCase(keyType) ) {
					dbColumn.setPrimaryKeyColumn(true);
					pkColumn = dbColumn;
				}
				if("UNIQUE".equalsIgnoreCase(keyType) || "U".equalsIgnoreCase(keyType)) dbColumn.setUniqueKeyColumn(true);
				if("FOREIGN KEY".equalsIgnoreCase(keyType) || "R".equalsIgnoreCase(keyType)) dbColumn.setRefKeyColumn(true);
			}
			
			if(pkColumn==null ||  !"Long".equalsIgnoreCase(pkColumn.getJavaColType(dbProductName))){
				dbColumnMap.clear();
			}
		} catch(Exception ex){
			throw ex;
		} finally {
			ConnectionUtil.closeConnection(rs1, psmt1);
			ConnectionUtil.closeConnection(rs, psmt);
		}
		return dbColumnMap;
	}

	public static void main(String[] args) throws Exception {
		new CodeGenerator().execute();
		System.out.println("Successfully generated the source");
	}

}
