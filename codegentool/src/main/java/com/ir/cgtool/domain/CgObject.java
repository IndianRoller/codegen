package com.ir.cgtool.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ir.cgtool.CodeGenerator;
import com.ir.cgtool.util.CodeGenUtil;

public class CgObject {

	private String tableName = null;
	
	private CodegenParameters codegenParameters  = null; 

	private String javaClassName = null;
	
	private JavaSource domain = null;

	private JavaSource domainHelper = null;

	private JavaSource serviceBase = null;

	private JavaSource serviceBaseImpl = null;

	private JavaSource daoBase = null;

	private JavaSource daoBaseImpl = null;
	
	private JavaSource domainExt = null;

	private JavaSource domainHelperExt = null;

	private JavaSource serviceExt = null;

	private JavaSource serviceImplExt = null;

	private JavaSource daoExt = null;

	private JavaSource daoImplExt = null;
 
	private String seqName = null;

	private DBColumn pk = null ; 

	
	public CgObject(String tableName, CodegenParameters codegenParameters) {
		super();
		setTableName(tableName);
		setCodegenParameters(codegenParameters);
 		setSeqName(CodeGenUtil.getSeqName(tableName));
 		setJavaClassName(CodeGenUtil.getJavaClassName(tableName));
  		
 		init();
	}

	private void init() {
		prepareDomain();
 		prepareDomainHelper();
		prepareDao();
		prepareService();
		setImports();
	}

	private void prepareService() {
	 	JavaSource serviceBase = new JavaSource("interface", javaClassName+"ServiceBase" , codegenParameters.getPackagePath().concat(".service.cg"), getSrcFolder()+"\\"+codegenParameters.getSrcPackage()+"\\service\\cg", false);
	    List<String> importList = new ArrayList<String>();
	    importList.add(getDomainExt().getFullName());
	    serviceBase.setImportList(importList);
	    setServiceBase(serviceBase);
	    
	    JavaSource serviceExt = new JavaSource("interface", javaClassName+"Service" , codegenParameters.getPackagePath().concat(".service"), getSrcFolder()+"\\"+codegenParameters.getSrcPackage()+"\\service", false);
	    serviceExt.setSuperClassAssociationType("extends");
	    serviceExt.setSuperClassName(serviceBase.getName());
	    serviceExt.getImportList().add(serviceBase.getFullName());
	    serviceExt.setOverwrite(false);
	    setServiceExt(serviceExt);
	    
	    
	    JavaSource serviceBaseImpl = new JavaSource("class", javaClassName+"ServiceBaseImpl", codegenParameters.getPackagePath().concat(".service.cg.impl"), getSrcFolder()+"\\"+codegenParameters.getSrcPackage()+"\\service\\cg\\impl", true);
	    importList = new ArrayList<String>();
	    //importList.add(getDomainExt().getFullName());
	    importList.add(getDaoBase().getFullName());	    
	    importList.add(getDaoBaseImpl().getFullName());	    
	    importList.add(getServiceBase().getFullName());	
	    
		
	    serviceBaseImpl.setImportList(importList);
	    serviceBaseImpl.setSuperClassAssociationType("implements");
	    serviceBaseImpl.setSuperClassName(serviceBase.getName());
 	    
	    Variable daoVar = new Variable(daoBase.getName(), daoBase.getName(), "private"); 
	    List<Variable> params = new ArrayList<Variable>();
		params.add(daoVar);
		
		StringBuffer setMethodBody = new StringBuffer();
		setMethodBody.append("\t").append("\t").append("this.").append(daoVar.getName()).append(" = ").append(daoVar.getName()).append(";");
		StringBuffer getMethodBody = new StringBuffer();
		getMethodBody.append("\t").append("\t").append("return ").append(daoVar.getName()).append(";");
		
		serviceBaseImpl.getVariableList().add(daoVar);
		serviceBaseImpl.getMethodList().add(new Method("public", daoBase.getName(), "get"+CodeGenUtil.toUpperCase(daoBase.getName(), 0), new ArrayList<Variable>(),getMethodBody.toString()));
		serviceBaseImpl.getMethodList().add(new Method("public", "void", "set"+CodeGenUtil.toUpperCase(daoBase.getName(), 0), params,setMethodBody.toString()));
		
		Method serviceConstructor = new Method();
	    serviceConstructor.setConstructor(true);
	    serviceConstructor.setName(serviceBaseImpl.getName());
	    serviceConstructor.setAccess("public");
	 
	    StringBuffer constBodyServiceImpl = new StringBuffer();
	    constBodyServiceImpl.append("\t").append("\t").append("this.").append(getDaoBase().getNameForVariable()).append("=").append("new ").append(getDaoBaseImpl().getName()).append("();");
	    serviceConstructor.setBody(constBodyServiceImpl.toString());
 	    
	    serviceBaseImpl.getMethodList().add(serviceConstructor);
	    serviceBaseImpl.getImportList().add(domainExt.getFullName());
	    //serviceBase.getImportList().add(domain.getFullName());
		setServiceBaseImpl(serviceBaseImpl);
		
		
		JavaSource serviceImplExt = new JavaSource("class", javaClassName+"ServiceImpl", codegenParameters.getPackagePath().concat(".service.impl"), getSrcFolder()+"\\"+codegenParameters.getSrcPackage()+"\\service\\impl", true);
		serviceImplExt.setSuperClassAssociationType("extends");
		serviceImplExt.setSuperClassName(serviceBaseImpl.getName());
		serviceImplExt.getImplmentionList().add(serviceExt.getName());
		serviceImplExt.getImportList().add(serviceBaseImpl.getFullName());
		serviceImplExt.getImportList().add(serviceExt.getFullName());
		serviceImplExt.setOverwrite(false);
	    setServiceImplExt(serviceImplExt);
	}

	private void setImports() {
		getDaoBase().getImportList().add("java.util.List");
		getDaoBaseImpl().getImportList().add("java.sql.Connection");
  		getDaoBaseImpl().getImportList().add("java.sql.ResultSet");
  		
  		getDomainHelper().getImportList().add("java.sql.ResultSet");
  		getDomainHelper().getImportList().add("java.sql.PreparedStatement");
  		getDomainHelper().getImportList().add("java.sql.Types");
  		
		getDaoBaseImpl().getImportList().add("java.sql.PreparedStatement");
		getDaoBaseImpl().getImportList().add("java.util.List");
		getDaoBaseImpl().getImportList().add("java.util.ArrayList");
		
		getDaoBaseImpl().getImportList().add("com.ir.util.ConnectionUtil");
		getDaoBaseImpl().getImportList().add("com.ir.util.DBUtil");

		getServiceBase().getImportList().add("java.util.List");
		getServiceBaseImpl().getImportList().add("java.util.List");
	}

	private JavaSource prepareDao() {
		JavaSource daoBase = new JavaSource("interface", javaClassName+"DaoBase" , getPackagePath().concat(".dao.cg"), getSrcFolder()+"\\"+getSrcPackage()+"\\dao\\cg", false);
	    setDaoBase(daoBase);
	    getDaoBase().getImportList().add(domainExt.getFullName()); 
	    
	    
	    JavaSource daoExt = new JavaSource("interface", javaClassName+"Dao" , getPackagePath().concat(".dao"), getSrcFolder()+"\\"+getSrcPackage()+"\\dao", false);
	    daoExt.setSuperClassAssociationType("extends");
	    daoExt.setSuperClassName(daoBase.getName());
	    daoExt.getImportList().add(daoBase.getFullName());
	    daoExt.setOverwrite(false);
	    setDaoExt(daoExt);	    
	    
		
	    JavaSource daoImpl = new JavaSource("class", javaClassName+"DaoBaseImpl", getPackagePath().concat(".dao.cg.impl"), getSrcFolder()+"\\"+getSrcPackage()+"\\dao\\cg\\impl", true);
	    daoImpl.setSuperClassAssociationType("implements");
	    daoImpl.setSuperClassName(daoBase.getName());
	    setDaoBaseImpl(daoImpl);
	    getDaoBaseImpl().getImportList().add(domainExt.getFullName());
	    //getDaoBaseImpl().getImportList().add(domainHelper.getFullName());
	    getDaoBaseImpl().getImportList().add(daoBase.getFullName());
	    getDaoBaseImpl().getImportList().add(getDomainHelperExt().getFullName());
		
	    
	    Method daoImplConstructor = new Method();
	    daoImplConstructor.setConstructor(true);
	    daoImplConstructor.setName(daoImpl.getName());
	    daoImplConstructor.setAccess("public");
	 
	    StringBuffer constBodyDaoImpl = new StringBuffer();
	    constBodyDaoImpl.append("\t").append("\t").append("this.").append(getDomainHelperExt().getNameForVariable()).append("=").append("new ").append(getDomainHelperExt().getName()).append("();");
	    daoImplConstructor.setBody(constBodyDaoImpl.toString());
 	    
	    getDaoBaseImpl().getMethodList().add(daoImplConstructor);
	  	
	    
	    
	    StringBuffer setMethodBody = new StringBuffer();
		setMethodBody.append("\t").append("\t").append("this.").append(domainHelperExt.getNameForVariable()).append(" = ").append(domainHelperExt.getNameForVariable()).append(";");
		StringBuffer getMethodBody = new StringBuffer();
		getMethodBody.append("\t").append("\t").append("return ").append(domainHelperExt.getNameForVariable()).append(";");
		
	 
		List<Variable> params = new ArrayList<Variable>();
		params.add(new Variable(domainHelperExt.getName(), domainHelperExt.getName(), "private"));
		
	    getDaoBaseImpl().getVariableList().add(new Variable(domainHelperExt.getNameForVariable(), domainHelperExt.getName(), "private"));
 		getDaoBaseImpl().getMethodList().add(new Method("public", domainHelperExt.getName(), "get"+domainHelperExt.getName(), new ArrayList<Variable>(),getMethodBody.toString()));
	    getDaoBaseImpl().getMethodList().add(new Method("public", "void", "set"+domainHelperExt.getName(), params,setMethodBody.toString()));
		
	    
	    JavaSource daoImplExt = new JavaSource("class", javaClassName+"DaoImpl", getPackagePath().concat(".dao.impl"), getSrcFolder()+"\\"+getSrcPackage()+"\\dao\\impl", true);
	    
	    daoImplExt.getImplmentionList().add(daoExt.getName());
	    
	    daoImplExt.setSuperClassAssociationType("extends");
	    daoImplExt.setSuperClassName(daoImpl.getName());
	    daoImplExt.getImportList().add(daoExt.getFullName());
	    daoImplExt.getImportList().add(daoImpl.getFullName());
	    
	    daoImplExt.setOverwrite(false);
	    setDaoImplExt(daoImplExt);
		return daoBase;
	}

	private JavaSource prepareDomainHelper() {
		JavaSource domainHelper = new JavaSource("class", javaClassName+"HelperBase", getPackagePath().concat(".domain.cg"), getSrcFolder()+"\\"+getSrcPackage()+"\\domain\\cg", false);
	    setDomainHelper(domainHelper);
	    
	    JavaSource domainHelperExt = new JavaSource("class", javaClassName+"Helper", getPackagePath().concat(".domain"), getSrcFolder()+"\\"+getSrcPackage()+"\\domain", false);
	    domainHelperExt.setSuperClassAssociationType("extends");
	    domainHelperExt.setSuperClassName(domainHelper.getName());
	    domainHelperExt.getImportList().add(domainHelper.getFullName());
	    domainHelperExt.setOverwrite(false);
	    setDomainHelperExt(domainHelperExt);
		return domainHelperExt;
	}

	private JavaSource prepareDomain() {
		JavaSource domain = new JavaSource("class", javaClassName+"Base", getPackagePath().concat(".domain.cg"), getSrcFolder()+"\\"+getSrcPackage()+"\\domain\\cg", false);
	    setDomain(domain);

	    JavaSource domainExt = new JavaSource("class", javaClassName, getPackagePath().concat(".domain"), getSrcFolder()+"\\"+getSrcPackage()+"\\domain", false);
	    domainExt.setSuperClassAssociationType("extends");
	    domainExt.setSuperClassName(domain.getName());
	    domainExt.getImportList().add(domain.getFullName());
	    domainExt.setOverwrite(false);
	    setDomainExt(domainExt);
		return domainExt;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
	 
	
	

	public CodegenParameters getCodegenParameters() {
		return codegenParameters;
	}

	public void setCodegenParameters(CodegenParameters codegenParameters) {
		this.codegenParameters = codegenParameters;
	}

	public String getSrcFolder() {
		return codegenParameters.getSrcFolder();
	}
	 
	public String getSrcPackage() {
		return codegenParameters.getSrcPackage();
	}

	public boolean isCreateSpringService() {
		return codegenParameters.isCreateSpringService();
	}


	public boolean isCreateSpringDao() {
		return codegenParameters.isCreateSpringDao();
	}

	
	public String getPackagePath() {
		return codegenParameters.getPackagePath();
	}

	public String getModulePrefix() {
		return codegenParameters.getModulePrefix();
	}
	 
	public JavaSource getDomain() {
		return domain;
	}

	public void setDomain(JavaSource domain) {
		this.domain = domain;
	}
 
	public JavaSource getDomainHelper() {
		return domainHelper;
	}

	public void setDomainHelper(JavaSource domainHelper) {
		this.domainHelper = domainHelper;
	}

	public JavaSource getServiceBase() {
		return serviceBase;
	}

	public void setServiceBase(JavaSource serviceBase) {
		this.serviceBase = serviceBase;
	}

	public JavaSource getServiceBaseImpl() {
		return serviceBaseImpl;
	}

	public void setServiceBaseImpl(JavaSource serviceBaseImpl) {
		this.serviceBaseImpl = serviceBaseImpl;
	}

	public JavaSource getDaoBase() {
		return daoBase;
	}

	public void setDaoBase(JavaSource daoBase) {
		this.daoBase = daoBase;
	}

	
	public JavaSource getDomainExt() {
		return domainExt;
	}

	public void setDomainExt(JavaSource domainExt) {
		this.domainExt = domainExt;
	}

	public JavaSource getDomainHelperExt() {
		return domainHelperExt;
	}

	public void setDomainHelperExt(JavaSource domainHelperExt) {
		this.domainHelperExt = domainHelperExt;
	}

	public JavaSource getServiceExt() {
		return serviceExt;
	}

	public void setServiceExt(JavaSource serviceExt) {
		this.serviceExt = serviceExt;
	}

	public JavaSource getServiceImplExt() {
		return serviceImplExt;
	}

	public void setServiceImplExt(JavaSource serviceImplExt) {
		this.serviceImplExt = serviceImplExt;
	}

	public JavaSource getDaoExt() {
		return daoExt;
	}

	public void setDaoExt(JavaSource daoExt) {
		this.daoExt = daoExt;
	}

	public JavaSource getDaoImplExt() {
		return daoImplExt;
	}

	public void setDaoImplExt(JavaSource daoImplExt) {
		this.daoImplExt = daoImplExt;
	}

	
	public JavaSource getDaoBaseImpl() {
		return daoBaseImpl;
	}

	public void setDaoBaseImpl(JavaSource daoBaseImpl) {
		this.daoBaseImpl = daoBaseImpl;
	}

	public String getSeqName() {
		return seqName;
	}

	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}

 	
	public DBColumn getPk() {
		return pk;
	}

	public void setPk(DBColumn pk) {
		this.pk = pk;
	}

	

	 
	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

 

	@Override
	public String toString() {
		return "CgObject [tableName=" + tableName +   ", domain=" + domain
				+ ", domainBase=" +  ", service=" + serviceBase
				+ ", serviceImpl=" + serviceBaseImpl + ", dao=" + daoBase
				+ ", daoImpl=" + daoBaseImpl + "]";
	}

	public void generateCode(Map<String,Document> configFileMap) throws IOException {
		getDomain().generateCode();
		getDomainExt().generateCode();
		
		getDomainHelper().generateCode();
		
		getDomainHelperExt().generateCode();
		
		
		getDaoBase().generateCode();
		getDaoExt().generateCode();
		
		getDaoBaseImpl().generateCode();
		getDaoImplExt().generateCode();

		getServiceBase().generateCode();
		getServiceExt().generateCode();
		
		getServiceBaseImpl().generateCode();
		getServiceImplExt().generateCode();
		
		if(isCreateSpringService()){
			addSpringBeans( configFileMap.get(CodeGenerator.SERVICE_CONFIG_XML),getServiceExt(),getServiceImplExt());
		}
		
		if(isCreateSpringDao()){
			addSpringBeans( configFileMap.get(CodeGenerator.DAO_CONFIG_XML),getDaoExt(),getDaoImplExt());
		}
	}

	private void addSpringBeans( Document document, JavaSource base , JavaSource impl) {
		  Attr idAttr =  document.createAttribute("id");
          idAttr.setValue(getModulePrefix()+"-"+base.getNameForVariable());
          
          Attr classAttr =  document.createAttribute("class");
          classAttr.setValue(impl.getSourcePackage()+"."+impl.getName());
          
          Element newBean = document.createElement("bean");
          newBean.setAttributeNode(idAttr);
          newBean.setAttributeNode(classAttr);
          
          document.getDocumentElement().appendChild(newBean);
 	}
}
