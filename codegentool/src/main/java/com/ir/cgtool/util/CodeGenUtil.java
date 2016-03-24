package com.ir.cgtool.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ir.cgtool.CGConstants;
import com.ir.cgtool.domain.CodegenParameters;
import com.ir.cgtool.domain.DBColumn;
import com.ir.cgtool.domain.JavaSource;
import com.ir.util.ConnectionUtil;

public class CodeGenUtil {
	
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
	
	public static  String toUpperCase(String input,int index){
		StringBuffer sb = new StringBuffer();
		
		sb.append( (input.substring(index,index+1)).toUpperCase());
		sb.append( (input.substring(index+1)));
		
		return sb.toString();
	}
	
	
	public  static String toLowerCase(String input,int index){
		StringBuffer sb = new StringBuffer();
		sb.append( (input.substring(index,index+1)).toLowerCase());
		sb.append( (input.substring(index+1)));
		
		return sb.toString();
	}
	
	
	public static String getJavaColName(String input) {
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new  StringTokenizer(input,"_");
		int i=0;
		while(st.hasMoreTokens()){ 
			String str  = st.nextToken().toLowerCase();
			if(i>0){
				sb.append( (str.substring(0,1)).toUpperCase());
				sb.append( (str.substring(1)));
			}else sb.append(str);
			i++;
		}
		return sb.toString();
	}
 

	public static   String getJavaClassName(String input) {
		
		if(!input.contains("_")){
			return toUpperCase(input.toLowerCase(), 0);
		}
		
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new  StringTokenizer(input,"_");
		int i=0;
 		while(st.hasMoreTokens()){ 
			String str  = st.nextToken().toLowerCase();
	 		if(i>0) 
	 			sb.append(toUpperCase(str,0));
			i++;
		}
		return sb.toString();
	}


	public static   String getSeqName(String input) {
	
		return	input.concat("_SEQ");
     
	}

	public static void addSpringBeans(Document document, JavaSource base, JavaSource impl, List<JavaSource> dependenciesRef,CodegenParameters cgParams, List<Attr> optionalAttr) {
		Attr idAttr = createAttr(document, "id",base.getSpringBeanName(cgParams.getModulePrefix()));
		Attr classAttr = createAttr(document, "class",impl.getFullName());

		Element newBean = document.createElement("bean");
		newBean.setAttributeNode(idAttr);
		newBean.setAttributeNode(classAttr);
		
		if(optionalAttr!=null && !optionalAttr.isEmpty()) {
			for(Attr attr : optionalAttr)newBean.setAttributeNode(attr);
		}

		document.getDocumentElement().appendChild(newBean);

		for (JavaSource bean : dependenciesRef) {
			Element prop = document.createElement("property");
			Attr nameAttr = document.createAttribute("name");
			nameAttr.setValue(bean.getNameForVariable());
			prop.setAttributeNode(nameAttr);
			 
			if(bean.isSpringBean()){
				Attr refAttr  = document.createAttribute("ref");
				refAttr.setValue(bean.getSpringBeanName(cgParams.getModulePrefix()));
				prop.setAttributeNode(refAttr);
			}else {
				Element innerBean = document.createElement("bean");
				
				
				Attr iidAttr =  createAttr(document, "id",bean.getSpringBeanName(cgParams.getModulePrefix()));  
 				Attr iclassAttr =createAttr(document, "class",bean.getFullName()); 
				
				innerBean.setAttributeNode(iidAttr);
				innerBean.setAttributeNode(iclassAttr);
			    prop.appendChild(innerBean);
				
			}
			newBean.appendChild(prop);
		}
	}

	public static Attr createAttr(Document document, String name, String value) {
		Attr classAttr = document.createAttribute( name);
		classAttr.setValue(value);
		return classAttr;
	}
	
	 

	public static Document getNewDocument() throws ParserConfigurationException {
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
	
	public static void createConfig(Document config, File conigFile)
			throws TransformerException, TransformerConfigurationException, TransformerFactoryConfigurationError {
		TransformerFactory.newInstance().newTransformer().transform(new DOMSource(config), new StreamResult(conigFile));
	}

	
	public static Map<String, DBColumn> loadColumnMap(String tableName) throws  Exception {
		Connection connection = null ; 
		PreparedStatement psmt = null;
		ResultSet rs = null;
		
		PreparedStatement psmt1 = null;
		ResultSet rs1 = null;
		
		
		Map<String, DBColumn> dbColumnMap = new LinkedHashMap<String,DBColumn>();
		try {
			connection = ConnectionUtil.getConnection();
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
			ConnectionUtil.closeConnection(connection);
		}
		return dbColumnMap;
	}

	public static List<String> getTableList() throws Exception {
		Connection connection = null; 
		PreparedStatement psmt = null;
		ResultSet rs = null;
 		
		List<String> tableList = new ArrayList<String>();
		
		try {
			connection = ConnectionUtil.getConnection();
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
 			ConnectionUtil.closeConnection(connection, rs, psmt);
		}
		return tableList;

	}
	
	public static void createConfigFiles(CodegenParameters cgparams)
			throws TransformerException, TransformerConfigurationException, TransformerFactoryConfigurationError {
		File srcDir = new File(cgparams.getConfigFileLocation());
		if (!srcDir.exists()) srcDir.mkdirs();
		
		if (cgparams.isCreateSpringService()) CodeGenUtil.createConfig(cgparams.getSvcConfigFile(), new File(srcDir, CGConstants.SERVICE_CONFIG_XML));
		if (cgparams.isCreateSpringDao()) CodeGenUtil.createConfig(cgparams.getDaoConfigFile(), new File(srcDir, CGConstants.DAO_CONFIG_XML));
	}
 
}
