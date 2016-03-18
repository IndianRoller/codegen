package com.ir.cgtool.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ir.cgtool.CGConstants;
import com.ir.cgtool.util.CodeGenUtil;
import com.ir.util.StringUtil;

public class CgObject {

	private CodegenParameters cgParams  = null; 
	
	private String tableName = null;
	
	private String javaClassName = null;
	
	private Map<String,JavaSource> components = new LinkedHashMap<String,JavaSource>();
	
	private DBColumn pk = null ; 
	
	public CgObject(String tableName, CodegenParameters codegenParameters) {
		super();
		setTableName(tableName);
		setCgParams(codegenParameters);
		setJavaClassName(CodeGenUtil.getJavaClassName(tableName));
	}

	public CodegenParameters getCgParams() {
		return cgParams;
	}

	public void setCgParams(CodegenParameters cgParams) {
		this.cgParams = cgParams;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

	public Map<String, JavaSource> getComponents() {
		return components;
	}

	public void setComponents(Map<String, JavaSource> components) {
		this.components = components;
	}

	public DBColumn getPk() {
		return pk;
	}

	public void setPk(DBColumn pk) {
		this.pk = pk;
	}

	private void init() {
		
		prepareDomain();
 		
		prepareDomainHelper();
		
		prepareDao();
		
		prepareService();
		
		setImports();
	}

	private void prepareService() {
		createInterface(CGConstants.SERVICE_BASE,"ServiceBase", getCgParams().getSvcBasePkg());

	 	createInterface(CGConstants.SERVICE_EXT,"Service", getCgParams().getSvcPkg());
  	    getServiceExt().addSupperClass(getServiceBase());
	    getServiceExt().setOverwrite(false);
	    
	    
	    createClass(CGConstants.SERVICE_BASE_IMPL,"ServiceBaseImpl", getCgParams().getSvcBaseImplPkg());
	    getServiceBaseImpl().addSupperClass(getServiceBase());
 	    getServiceBaseImpl().addDependency(getDaoExt(), null, true);
 	    getServiceBaseImpl().applySort(true);
 	   
		 
	    createClass(CGConstants.SERVICE_EXT_IMPL,"ServiceImpl", getCgParams().getSvcImplPkg());
		getServiceImplExt().addSupperClass( getServiceBaseImpl());
		getServiceImplExt().getImplmentionList().add(getServiceExt().getName());
	
		getServiceImplExt().setOverwrite(false);
		
		List<JavaSource> dependenciesRef = new ArrayList<JavaSource>();
		List<JavaSource> dependenciesImpl = new ArrayList<JavaSource>();
		dependenciesRef.add(getDaoExt());
		dependenciesImpl.add(getDaoImplExt());

		if (getCgParams().isCreateSpringService()) {
			CodeGenUtil.addSpringBeans(getCgParams().getSvcConfigFile(),
					getServiceExt(), getServiceImplExt(), dependenciesRef,getCgParams());
		} else {
			getServiceBaseImpl().addConstructor(dependenciesRef, dependenciesImpl);
		}
 		
		createClass(CGConstants.REST_SERVICE,"RestService", getCgParams().getRestSvcPkg());
		getRestService().addAnnotation("Component", "org.springframework.stereotype.Component");
		getRestService().addAnnotation("Path(\"/"+CodeGenUtil.toLowerCase(getJavaClassName(), 0)+"s\")", "javax.ws.rs.Path");
		getRestService().addAnnotation("Produces({ \"application/json\" })", "javax.ws.rs.Produces");
		getRestService().addAnnotation("Consumes({ \"application/json\" })", "javax.ws.rs.Consumes");

		List<String> methodAnnoations = new ArrayList<String>();
		methodAnnoations.add("org.springframework.beans.factory.annotation.Autowired");
		
		getRestService().addDependency(getServiceExt(), methodAnnoations, false);
		getRestService().applySort(true);
 	     
	}
 
	private void setImports() {
		getDomainHelper().getImportList().add("java.sql.ResultSet");
  		getDomainHelper().getImportList().add("java.sql.PreparedStatement");
  		getDomainHelper().getImportList().add("java.sql.Types");
			
		getDaoBase().getImportList().add("java.util.List");
		getDaoBase().getImportList().add(getDomainExt().getFullName()); 
	   
		getDaoBaseImpl().getImportList().add("java.sql.Connection");
  		getDaoBaseImpl().getImportList().add("java.sql.ResultSet");
  		getDaoBaseImpl().getImportList().add("java.sql.PreparedStatement");
		getDaoBaseImpl().getImportList().add("java.util.List");
		getDaoBaseImpl().getImportList().add("java.util.ArrayList");
		getDaoBaseImpl().getImportList().add("com.ir.util.ConnectionUtil");
		getDaoBaseImpl().getImportList().add("com.ir.util.DBUtil");
		getDaoBaseImpl().getImportList().add(getDomainExt().getFullName());
 		
		getDaoImplExt().getImportList().add(getDaoExt().getFullName());
		
		getServiceBase().getImportList().add("java.util.List");
		getServiceBase().getImportList().add(getDomainExt().getFullName());
		 
		getServiceBaseImpl().getImportList().add("java.util.List");
		getServiceBaseImpl().getImportList().add(getDomainExt().getFullName());	    

		getServiceImplExt().getImportList().add(getServiceExt().getFullName());
		
		getRestService().getImportList().add("java.util.List");
		getRestService().getImportList().add("javax.ws.rs.GET");
		getRestService().getImportList().add("javax.ws.rs.POST");
		getRestService().getImportList().add("javax.ws.rs.PUT");
		getRestService().getImportList().add("javax.ws.rs.PathParam");
		getRestService().getImportList().add("javax.ws.rs.DELETE");
 		getRestService().getImportList().add(getDomainExt().getFullName());
	}

	private void prepareDao() {
	 	 createInterface(CGConstants.DAO_BASE,"DaoBase", getCgParams().getDaoBasePkg());
		 createInterface(CGConstants.DAO_EXT,"Dao", getCgParams().getDaoPkg());
	    getDaoExt().addSupperClass(getDaoBase());
	    getDaoExt().setOverwrite(false);
	     
	    createClass(CGConstants.DAO_BASE_IMPL,"DaoBaseImpl", getCgParams().getDaoBaseImplPkg());
		
 	    getDaoBaseImpl().addSupperClass(getDaoBase());
	    getDaoBaseImpl().addDependency(getDomainHelperExt(), null, true);
	    
	    List<JavaSource> dependenciesRef = new ArrayList<JavaSource>();
 	    dependenciesRef.add(getDomainHelperExt());
 	    getDaoBaseImpl().addConstructor(dependenciesRef,null);
 	    getDaoBaseImpl().applySort(true);
	     
 	    createClass(CGConstants.DAO_EXT_IMPL, "DaoImpl", getCgParams().getDaoImplPkg());
 	    getDaoImplExt().getImplmentionList().add(getDaoExt().getName());
	    getDaoImplExt().addSupperClass(getDaoBaseImpl());

 	    getDaoImplExt().setOverwrite(false);
 	    
		if (getCgParams().isCreateSpringDao()) {
			CodeGenUtil.addSpringBeans(getCgParams().getDaoConfigFile(), getDaoExt(), getDaoImplExt(), new ArrayList<JavaSource>(),getCgParams());
		}
 		
 	}

	
 
	private void prepareDomainHelper() {
		createClass(CGConstants.DOMAIN_HELPER,"HelperBase", getCgParams().getDomainHelperBasePkg());
		createClass(CGConstants.DOMAIN_HELPER_EXT,"Helper", getCgParams().getDomainHelperPkg());
	      
		getDomainHelperExt().addSupperClass(getDomainHelper());
		getDomainHelperExt().setOverwrite(false);
	}

	private void prepareDomain() {
		createClass(CGConstants.DOMAIN, "Base", getCgParams().getDomainBasePkg());
		createClass(CGConstants.DOMAIN_EXT, "", getCgParams().getDomainPkg());
	    
	    getDomainExt().addSupperClass(getDomain());
	    getDomainExt().setOverwrite(false);
 	}

	private void createClass(String componentName, String name, String packageName) {
		getComponents().put(componentName, new JavaSource("class", getJavaClassName()+name, packageName, getCgParams().getSrcFolderForPackage(packageName)));
	}

	private void createInterface(String componentName, String nameSuffix, String packageName) {
		getComponents().put(componentName, new JavaSource("interface", getJavaClassName()+nameSuffix, packageName, getCgParams().getSrcFolderForPackage(packageName)));
	}
	
	public JavaSource getDomain() {
		return getComponents().get(CGConstants.DOMAIN);
	}
 
	public JavaSource getDomainHelper() {
		return getComponents().get(CGConstants.DOMAIN_HELPER); 
	}
 
	public JavaSource getServiceBase() {
		return getComponents().get(CGConstants.SERVICE_BASE); 
	}

	public JavaSource getServiceBaseImpl() {
		return getComponents().get(CGConstants.SERVICE_BASE_IMPL); 
	}

	public JavaSource getDaoBase() {
		return getComponents().get(CGConstants.DAO_BASE);
	}

	public JavaSource getDomainExt() {
		return getComponents().get(CGConstants.DOMAIN_EXT); 
	}

	public JavaSource getDomainHelperExt() {
		return getComponents().get(CGConstants.DOMAIN_HELPER_EXT); 
	}

	public JavaSource getServiceExt() {
		return getComponents().get(CGConstants.SERVICE_EXT); 
	}

	public JavaSource getServiceImplExt() {
		return getComponents().get(CGConstants.SERVICE_EXT_IMPL); 
	}

	public JavaSource getDaoExt() {
		return getComponents().get(CGConstants.DAO_EXT);	 
	}
 
	public JavaSource getDaoImplExt() {
		  return getComponents().get(CGConstants.DAO_EXT_IMPL);	
	}
	
	public JavaSource getDaoBaseImpl() {
		 return getComponents().get(CGConstants.DAO_BASE_IMPL);	 
	}
 
	public JavaSource getRestService() {
		 return getComponents().get(CGConstants.REST_SERVICE);	  
	}
 
	public void generateCode() throws IOException {
		for (Entry<String, JavaSource> entry : getComponents().entrySet()) {
			entry.getValue().generateCode();
		}
	}

	public  void prepareCgObject(Map<String, DBColumn> dbColumnMap ) throws  Exception {
		init();
		
	 	boolean dateImportAdded = false;
     
	 	List<DBColumn> dbCols = new ArrayList<DBColumn>();
		
		Iterator<String> itr = dbColumnMap.keySet().iterator();
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
			
			getDomain().getVariableList().add(variable);
			getDomain().getMethodList().add(new Method("public", dbColumn.getColumnType(), dbColumn.getMethodName(), new ArrayList<Variable>(),getMethodBody.toString()));
			getDomain().getMethodList().add(new Method("public", "void", "set"+colName, params,setMethodBody.toString()));
		
		 
			
			if(!dateImportAdded && "Date".equalsIgnoreCase(dbColumn.getColumnType())) {
				getDomain().getImportList().add("java.sql.Date");
				dateImportAdded = true;
			}
			
			 //Service/DAO Methods
			 if(dbColumn.isPrimaryKeyColumn() || dbColumn.isUniqueKeyColumn() || dbColumn.isRefKeyColumn()) {
			  	 loadMethod(getTableName(),  dbColumn); 
				 deleteMethod(  dbColumn); 
			 }
			 
  			 if(dbColumn.isPrimaryKeyColumn()) {
				 setPk(dbColumn); 
			 }else {
				 dbCols.add(dbColumn);
			 }
 		}
		
		dbCols.add(getPk());
		int columnCnt=0;
		StringBuffer dhMapFromDB = new StringBuffer();
		StringBuffer dhMapToDB = new StringBuffer();
		
		StringBuffer sqlInsert = new StringBuffer("\" INSERT INTO ").append(getTableName()).append(" (");
		StringBuffer sqlUpdate = new StringBuffer("\" UPDATE ").append(getTableName()).append(" SET ");
		StringBuffer sqlDelete = new StringBuffer("\" DELETE FROM  ").append(getTableName());
		
		for(DBColumn dbCol : dbCols){
			String colName = CodeGenUtil.toUpperCase(dbCol.getColumnName(), 0);
			if("boolean".equals(dbCol.getColumnType())){
				dhMapFromDB.append("\t\t").append(getDomainExt().getNameForVariable()).append(".set"+colName+"(").append("\"Y\".equals(rs.getString(\"").append(dbCol.getDbColumnName()).append("\")));").append("\n\n");
				dhMapToDB.append("\t\t").append("psmt.setString(").append(columnCnt+1).append(",").append(getDomainExt().getNameForVariable()).append(".is").append(colName).append("()?\"Y\":\"N\"").append(");").append("\n\n");
			}else{
				dhMapFromDB.append("\t\t").append(getDomainExt().getNameForVariable()).append(".set"+colName+"(").append("rs.get").append(dbCol.getColumnType()).append("(\"").append(dbCol.getDbColumnName()).append("\"));").append("\n\n");
				dhMapToDB.append("\t\t").append("if( null == ").append(getDomainExt().getNameForVariable()).append(".get").append(colName).append("()){ ").append("\n");
				dhMapToDB.append("\t\t\t").append("psmt.setNull(").append(columnCnt+1).append(",").append(dbCol.getNullType()).append("); \n");
				dhMapToDB.append("\t\t").append("} else { ").append("\n");
				dhMapToDB.append("\t\t\t").append("psmt.set").append(dbCol.getColumnType()).append("(").append(columnCnt+1).append(",").append(getDomainExt().getNameForVariable()).append(".get").append(colName).append("());").append("\n");
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
		
		sqlUpdate.append(" WHERE ").append(getPk().getDbColumnName()).append(" =  ? \"");
		sqlDelete.append(" WHERE ").append(getPk().getDbColumnName()).append(" =  ? \"");
		
		
		
		sqlInsert.append(")\"");
		
		 
		Variable seqVar = new Variable("SEQ_KEY", "String", "public static final");
		seqVar.setInitVal("\"" + CodeGenUtil.getSeqName(getTableName())+  "\"");
		getDomainHelper().getVariableList().add(seqVar);
		
		Variable insertSQLVar = new Variable("INSERT_"+getDomainExt().getName()+"_SQL", "String", "public static final");
		insertSQLVar.setInitVal(sqlInsert.toString());
		getDomainHelper().getVariableList().add(insertSQLVar);
		

		Variable updateSQLVar = new Variable("UPDATE_"+getDomainExt().getName()+"_SQL", "String", "public static final");
		updateSQLVar.setInitVal(sqlUpdate.toString());
		getDomainHelper().getVariableList().add(updateSQLVar);
		
		Variable deletetSQLVar = new Variable("DELETE_"+getDomainExt().getName()+"_SQL", "String", "public static final");
		deletetSQLVar.setInitVal(sqlDelete.toString());
		getDomainHelper().getVariableList().add(deletetSQLVar);
		
		//load all records
		loadMethod(getTableName(),   null); 
		
		domainHelperMaptoDomain(  dhMapFromDB);
		
		domainHelperMapToDB(  dhMapToDB);

		saveMethod( false, "add");

		//saveMethod(true , "add");

		saveMethod( false , "update");
	}


	


	private void domainHelperMapToDB(StringBuffer dhMapToDB) {
		List<Variable> dhparams = new ArrayList<Variable>();
		dhparams.add(new Variable("psmt", "PreparedStatement", "private"));
		dhparams.add(new Variable(getDomainExt().getNameForVariable(), getDomainExt().getName(), "private"));
		
		StringBuffer dhMethodBody = new StringBuffer();
		//dhMethodBody.append("\t\t").append(getDomain().getName()).append(" ").append(getDomain().getNameForVariable()).append(" = new ").append(getDomain().getName()).append("();\n\n");
		dhMethodBody.append(dhMapToDB);
		//dhMethodBody.append("\t\t").append("return ").append(getDomain().getNameForVariable()).append(";").append("\n");
		Method dhMethod = new Method("public" , "void" , "set"+getDomainExt().getName(),dhparams, dhMethodBody.toString());
		dhMethod.getThrownExceptions().add("SQLException");
		//getDomainHelper().getImportList().add("java.sql.SQLException");
		getDomainHelper().getMethodList().add(dhMethod);
		
	}


	private void domainHelperMaptoDomain(StringBuffer dhLoadFromDB) {
		List<Variable> dhparams = new ArrayList<Variable>();
		dhparams.add(new Variable("rs", "ResultSet", "private"));
		StringBuffer dhMethodBody = new StringBuffer();
		dhMethodBody.append("\t\t").append(getDomainExt().getName()).append(" ").append(getDomainExt().getNameForVariable()).append(" = new ").append(getDomainExt().getName()).append("();\n\n");
		dhMethodBody.append(dhLoadFromDB);
		dhMethodBody.append("\t\t").append("return ").append(getDomainExt().getNameForVariable()).append(";").append("\n");
		Method dhMethod = new Method("public" , getDomainExt().getName(), "get"+getDomainExt().getName(),dhparams, dhMethodBody.toString());
		dhMethod.getThrownExceptions().add("SQLException");
		getDomainHelper().getImportList().add("java.sql.SQLException");
		getDomainHelper().getImportList().add(getDomainExt().getFullName());
		getDomainHelper().getMethodList().add(dhMethod);
	}


	private void saveMethod(boolean multiple,String type) {
		 List<Variable> params = new ArrayList<Variable>();
		 List<Variable> restSvcParams = new ArrayList<Variable>();
		 
		 String methodName = null ; 
 		 Variable variable =  null; 
 		 String returnType = null ; 
		 String returnTypeImpl = null ; 
		 String methodAnnotation = null; 
		  
		 
		 
		 if("update".equalsIgnoreCase(type)){
			  returnType =  "void" ;
			  methodName =  "update"+getDomainExt().getName() ;
			 
			  if(multiple){
				 variable = new Variable(getDomainExt().getName()+"s", "List<"+getDomainExt().getName()+">", "private");
				 params.add(variable);
				 returnType =   "List<Long>" ;
				 returnTypeImpl = "new ArrayList<Long>()";
			} else {
				variable = new Variable(getDomainExt().getName(), getDomainExt().getName(),"private");
				params.add(variable);
				Variable restParamVar = new Variable(getPk().getColumnName(), getPk().getColumnType(),"private");
				restParamVar.addAnnotation("PathParam(\"" + getPk().getColumnName() + "\")");
				restSvcParams.add(restParamVar);
				restSvcParams.add(variable);
				methodAnnotation = "PUT";
			}
		} else {
			methodName = "add" + getDomainExt().getName();
			if (multiple) {
				variable = new Variable(getDomainExt().getName() + "s", "List<" + getDomainExt().getName() + ">", "private");
				params.add(variable);
				returnType = "List<Long>";
				returnTypeImpl = "new ArrayList<Long>()";
			} else {
				variable = new Variable(getDomainExt().getName(), getDomainExt().getName(), "private");
				returnType = "Long";
				returnTypeImpl = getDomainExt().getNameForVariable() + "." + getPk().getMethodName() + "()";
				params.add(variable);
  
				restSvcParams.add(variable);
				methodAnnotation = "POST";
			}
		}
		 
		 StringBuffer methodBodyDaoImpl = getDaoImplMethodBody(returnTypeImpl,type,null);
		 StringBuffer methodBodyServiceImpl  = new StringBuffer();
		 methodBodyServiceImpl.append("\t").append("\t");
		 if(!"update".equalsIgnoreCase(type))    methodBodyServiceImpl.append("return ");
		 methodBodyServiceImpl.append(getDaoExt().getNameForVariable()).append(".").append(methodName).append("(")
		 					  .append(Method.getParamCode(params,Variable.PARAM_MODE_EXE)).append(")").append(";");
		 
		 	 
		 Method method = new Method("public",returnType, methodName, params  , null,true);
		 method.getThrownExceptions().add("Exception");
		 getDaoBase().getMethodList().add(method);
 		 getServiceBase().getMethodList().add(method);
		
 		 
 		 method = new Method("public",returnType, methodName, params  , methodBodyDaoImpl.toString(),false);
		 method.getThrownExceptions().add("Exception");
		 getDaoBaseImpl().getMethodList().add(method);
 
		 method = new Method("public",returnType, methodName, params  , methodBodyServiceImpl.toString(),false);
		 method.getThrownExceptions().add("Exception");
		 getServiceBaseImpl().getMethodList().add(method);
		 
		 if(!multiple){
			 DBColumn dbColumn = getPk();
			 String colName = CodeGenUtil.toUpperCase(dbColumn.getColumnName(), 0);
			 String getMethodName =  "get"+getDomainExt().getName();
			 
			 StringBuffer methodBodyRestSvc  = new StringBuffer();
			 methodBodyRestSvc.append("\t").append("\t");
			 if("update".equalsIgnoreCase(type)){ 
				 methodBodyRestSvc.append(getDomainExt().getNameForVariable()).append(".set").append(colName).append("(").append(dbColumn.getColumnName()).append(");").append(StringUtil.LINE_SEPARTOR);
				 methodBodyRestSvc.append("\t").append("\t");
			 }else{
				 methodBodyRestSvc.append(dbColumn.getColumnType()).append(" ").append(dbColumn.getColumnName()).append(" = ");
			 }
			
			 methodBodyRestSvc.append(getServiceExt().getNameForVariable()).append(".").append(methodName).append("(")
			 					  .append(Method.getParamCode(params,Variable.PARAM_MODE_EXE)).append(")").append(";").append(StringUtil.LINE_SEPARTOR);
			 
			 methodBodyRestSvc.append("\t").append("\t").append("return ").append(getServiceExt().getNameForVariable()).append(".").append(getMethodName).append("(")
			 .append(dbColumn!=null ? dbColumn.getColumnName():"")
			 .append(")").append(";");
			 
			 method = new Method("public",getDomainExt().getName(), methodName, restSvcParams  , methodBodyRestSvc.toString(),false);
			 method.addAnnotation(methodAnnotation);
			 if("update".equalsIgnoreCase(type)) method.addAnnotation("Path(\"/{"+getPk().getColumnName()+"}\")");
			 method.getThrownExceptions().add("Exception");
			 getRestService().getMethodList().add(method);
		 }
		
		 
	}


	private StringBuffer getDaoImplMethodBody(String  returnTypeImpl,String type,DBColumn dbColumn) {
		StringBuffer methodBodyDaoImpl;
		methodBodyDaoImpl = new StringBuffer();
		 
		String sql = "add".equalsIgnoreCase(type)?".INSERT_":"delete".equalsIgnoreCase(type)?".DELETE_":".UPDATE_" ; 
		 
		 methodBodyDaoImpl.append("\t\t").append("String sql = ").append(getDomainHelperExt().getName()).append(sql).append(getDomainExt().getName().toUpperCase()).append("_SQL").append(";\n")
						  .append("\t\t").append("Connection connection = null ; ").append("\n")
						  .append("\t\t").append("PreparedStatement pstmt = null ;").append("\n")
						  .append("\t\t").append("try { ").append("\n");
						  
							if ("add".equalsIgnoreCase(type)) {
								methodBodyDaoImpl.append("\t\t\t")
										.append(getDomainExt().getNameForVariable())
										.append(".").append(getPk().setMethodName())
										.append("(DBUtil.getId(")
										.append(getDomainHelperExt().getName())
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
										.append(getDomainHelperExt().getNameForVariable())
										.append(".set").append(getDomainExt().getName())
										.append("(pstmt,")
										.append(getDomainExt().getNameForVariable())
										.append(");").append("\n");
					
							}
							  
		 methodBodyDaoImpl.append("\t\t\t").append("pstmt.executeUpdate();").append("\n");
		 
		 						  
						  
		    methodBodyDaoImpl.append("\t\t\t").append(" ").append("\n")
						  .append("\t\t\t").append(" ").append("\n")
						  .append("\t  ").append("} catch(Exception ex) { ").append("\n\n")
						  .append("\t\t  ").append("throw ex; ").append("\n\n")
						  .append("\t  ").append("} finally {").append("\n")
						  .append("\t\t  ").append("ConnectionUtil.closeConnection(connection,pstmt);").append("\n")
						  .append("\t  ").append("}").append("\n");
		    if("add".equalsIgnoreCase(type))
		    methodBodyDaoImpl.append("\t").append("\t").append("return ").append(returnTypeImpl).append(";");
		return methodBodyDaoImpl;
	}


	private void deleteMethod(DBColumn dbColumn){
		Variable variable = new Variable(dbColumn.getColumnName(), dbColumn.getColumnType(), "private");
		List<Variable> params = new ArrayList<Variable>();
		params.add(variable);
		
		
		Variable restParamVar = new Variable(dbColumn.getColumnName(), dbColumn.getColumnType(), "private");
		restParamVar.addAnnotation("PathParam(\""+dbColumn.getColumnName()+"\")");
		List<Variable> restSvcParams = new ArrayList<Variable>();
		restSvcParams.add(restParamVar);
 
		String colName = CodeGenUtil.toUpperCase(dbColumn.getColumnName(), 0);
		 	 
		 //delete
		 String methodName = "delete"+getDomainExt().getName();
		 if(!dbColumn.isPrimaryKeyColumn() )methodName =  methodName.concat("By").concat(colName);
		 
		 Method method = new Method("public","void", methodName, params  , null,true);
		 method.getThrownExceptions().add("Exception");
		 getDaoBase().getMethodList().add(method);
		 getServiceBase().getMethodList().add(method);
		 
		 StringBuffer methodBodyDaoImpl =   getDaoImplMethodBody( "true;" , "delete",dbColumn);
		 Method daoImplMethod = new Method("public","void", methodName, params  , methodBodyDaoImpl.toString());
		 getDaoBaseImpl().getMethodList().add(daoImplMethod);
		 daoImplMethod.getThrownExceptions().add("Exception");
		 
		 StringBuffer methodBodyServiceImpl = new StringBuffer();
		 methodBodyServiceImpl.append("\t").append("\t").append(getDaoExt().getNameForVariable()).append(".").append(methodName).append("(").append(dbColumn.getColumnName()).append(")").append(";");
		 Method serviceMethod = new Method("public","void", methodName, params  , methodBodyServiceImpl.toString());
		 serviceMethod.getThrownExceptions().add("Exception");
		 getServiceBaseImpl().getMethodList().add(serviceMethod);
		 
		 
		 StringBuffer methodBodyRestService  = new StringBuffer();
		 methodBodyRestService.append("\t").append("\t").append(getServiceExt().getNameForVariable()).append(".").append(methodName).append("(").append(dbColumn.getColumnName()).append(")").append(";");
	
		 
		 Method restMethod = new Method("public","void", methodName , restSvcParams  , methodBodyRestService.toString());
		 restMethod.addAnnotation("DELETE");
		 
		 if ( dbColumn.isPrimaryKeyColumn()) {
				restMethod.addAnnotation("Path(\"/{" + dbColumn.getColumnName() + "}\")");
			} else if (dbColumn != null && (dbColumn.isRefKeyColumn() || dbColumn.isUniqueKeyColumn())) {
				restMethod.addAnnotation("Path(\"/" + dbColumn.getColumnName()+"/{"+dbColumn.getColumnName()+"}" + "\")");
			}
		 
		 restMethod.getThrownExceptions().add("Exception");
		 
	
		 
		 getRestService().getMethodList().add(restMethod);
		 
	}


	private void loadMethod(String tableName,  DBColumn dbColumn) {
		List<Variable> params = new ArrayList<Variable>();
		List<Variable> restSvcParams = new ArrayList<Variable>();
		String colName  = null ; 
		String returnType     = getDomainExt().getName();
		String returnTypeName =  getDomainExt().getNameForVariable();
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
			methodName = "get"+ getDomainExt().getName();
			if(!dbColumn.isPrimaryKeyColumn()) methodName = methodName +"By"+colName ;
			
		}else{
		    sql = "\" SELECT * FROM " + tableName + " \" " ; 
		    methodName = "get"+getDomainExt().getName()+"s" ;
		}
		
		 if(dbColumn==null || dbColumn.isRefKeyColumn()){
			 returnType = "List<"+returnType+">";
			 returnTypeName = returnTypeName+"List";
			 returnTypeInit = "new Array"+returnType+"()"; 
		 }
		 	
		 //load Method
		 Method method = new Method("public",returnType, methodName, params  , null,true);
		 method.getThrownExceptions().add("Exception");
		  
		 getDaoBase().getMethodList().add(method);
		 
		 Method methodService = new Method("public",returnType, methodName, params  , null,true);
		 methodService.getThrownExceptions().add("Exception");
		 getServiceBase().getMethodList().add(methodService);
		 
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
							                    .append(getDomainHelperExt().getNameForVariable()).append(".get").append(getDomainExt().getName())
							                    .append("(rs));").append("\n");
						   }else{
							   methodBodyDaoImpl.append("\t\t\t").append(returnTypeName).append(" = ")
							                    .append(getDomainHelperExt().getNameForVariable()).append(".get").append(getDomainExt().getName())
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
		 getDaoBaseImpl().getMethodList().add(m);
		 
		 StringBuffer methodBodyServiceImpl = new StringBuffer();
		 methodBodyServiceImpl.append("\t").append("\t").append("return ").append(getDaoExt().getNameForVariable()).append(".").append(methodName).append("(")
		 .append(dbColumn!=null ? dbColumn.getColumnName():"")
		 .append(")").append(";");
		 
		 Method serviceMethod =  new Method("public",returnType, methodName , params  , methodBodyServiceImpl.toString());
		 serviceMethod.getThrownExceptions().add("Exception");
		 getServiceBaseImpl().getMethodList().add(serviceMethod);
		 
		 
		 StringBuffer methodBodyRestSvc = new StringBuffer();
		 methodBodyRestSvc.append("\t").append("\t").append("return ").append(getServiceExt().getNameForVariable()).append(".").append(methodName).append("(")
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
		 
		 
		 getRestService().getMethodList().add(restMethod);
 	}
 

}
