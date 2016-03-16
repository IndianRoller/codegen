package com.ir.cgtool.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	private JavaSource domainExt = null;
	
	private JavaSource domainHelper = null;

	private JavaSource domainHelperExt = null;
	
	private JavaSource daoBase = null;

	private JavaSource daoBaseImpl = null;
 
	private JavaSource daoExt = null;

	private JavaSource daoImplExt = null;
	
	private JavaSource serviceBase = null;

	private JavaSource serviceBaseImpl = null;

 	private JavaSource serviceExt = null;

	private JavaSource serviceImplExt = null;
	
	private JavaSource restService = null;
 
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
		setServiceBase(new JavaSource("interface", javaClassName+"ServiceBase" ,   getSvcBasePackage(), getSrcFolderForPackage(getSvcBasePackage()) , false));
	    
	    setServiceExt(new JavaSource("interface", javaClassName+"Service" , getSvcPackage(), getSrcFolderForPackage(getSvcPackage()) ,  false));
 	    getServiceExt().addSupperClass(getServiceBase());
	    getServiceExt().setOverwrite(false);
	    
	    
	    setServiceBaseImpl(new JavaSource("class", javaClassName+"ServiceBaseImpl",getSvcBaseImplPackage(), getSrcFolderForPackage(getSvcBaseImplPackage()) ,   true));
	    getServiceBaseImpl().addSupperClass(getServiceBase());
 	    getServiceBaseImpl().addDependency(getDaoExt(), null, true);
 	    
 	   
 	   
		 
		
	    setServiceImplExt(new JavaSource("class", javaClassName+"ServiceImpl",  getSvcImplPackage(), getSrcFolderForPackage(getSvcImplPackage()) ,   true));
		getServiceImplExt().addSupperClass( getServiceBaseImpl());
		getServiceImplExt().getImplmentionList().add(getServiceExt().getName());
	
		getServiceImplExt().setOverwrite(false);
		
		List<JavaSource> dependenciesRef = new ArrayList<JavaSource>();
		List<JavaSource> dependenciesImpl = new ArrayList<JavaSource>();
		dependenciesRef.add(getDaoExt());
		dependenciesImpl.add(getDaoImplExt());

		if (isCreateSpringService()) {
			addSpringBeans(getCodegenParameters().getConfigFileMap().get(CodeGenerator.SERVICE_CONFIG_XML),
					getServiceExt(), getServiceImplExt(), dependenciesRef);
		} else {
			getServiceBaseImpl().addConstructor(dependenciesRef, dependenciesImpl);
		}
		
		
		setRestService(new JavaSource("class", javaClassName+"RestService" ,   getRestSvcPackage(), getSrcFolderForPackage(getRestSvcPackage()) , false));
		getRestService().addAnnotation("Component", "org.springframework.stereotype.Component");
		getRestService().addAnnotation("Path(\"/"+javaClassName+"s\")", "javax.ws.rs.Path");
		getRestService().addAnnotation("Produces({\"application/json\"})", "javax.ws.rs.Produces");
		getRestService().addAnnotation("Consumes({\"application/json\"})", "javax.ws.rs.Consumes");

		List<String> methodAnnoations = new ArrayList<String>();
		methodAnnoations.add("org.springframework.beans.factory.annotation.Autowired");
		
		getRestService().addDependency(getServiceExt(), methodAnnoations, false);
 	     
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
		getRestService().getImportList().add(getDomainExt().getFullName());
	}

	private void prepareDao() {
		 
	    setDaoBase(new JavaSource("interface", javaClassName+"DaoBase" , getDaoBasePackage(), getSrcFolderForPackage(getDaoBasePackage()) , false));
	   
	    
	    setDaoExt(new JavaSource("interface", javaClassName+"Dao" , getDaoPackage(), getSrcFolderForPackage(getDaoPackage()) , false));
  	    getDaoExt().addSupperClass(getDaoBase());
	    getDaoExt().setOverwrite(false);
	    
		
	    setDaoBaseImpl(new JavaSource("class", javaClassName+"DaoBaseImpl", getDaoBaseImplPackage(), getSrcFolderForPackage(getDaoBaseImplPackage()), true));
 
	    getDaoBaseImpl().addSupperClass(getDaoBase());
	    
	    getDaoBaseImpl().addDependency(getDomainHelperExt(), null, true);
	    
	    List<JavaSource> dependenciesRef = new ArrayList<JavaSource>();
 	    dependenciesRef.add(getDomainHelperExt());
 	    getDaoBaseImpl().addConstructor(dependenciesRef,null);
	  
	    
	    setDaoImplExt(new JavaSource("class", javaClassName+"DaoImpl", getDaoImplPackage(), getSrcFolderForPackage(getDaoImplPackage()) , true));
	    getDaoImplExt().getImplmentionList().add(getDaoExt().getName());
	    getDaoImplExt().addSupperClass(getDaoBaseImpl());
	    

 	    getDaoImplExt().setOverwrite(false);
 	    
		if (isCreateSpringDao()) {
			addSpringBeans(getCodegenParameters().getConfigFileMap().get(CodeGenerator.DAO_CONFIG_XML), getDaoExt(),
					getDaoImplExt(), new ArrayList<JavaSource>());
		}
 		
 	}

 
	private void prepareDomainHelper() {
		setDomainHelper(new JavaSource("class", javaClassName + "HelperBase", getPackagePath().concat(".domain.cg"), getSrcFolder() + "\\" + getSrcPackage() + "\\domain\\cg", false));
		
		setDomainHelperExt(new JavaSource("class", javaClassName + "Helper", getPackagePath().concat(".domain"), getSrcFolder() + "\\" + getSrcPackage() + "\\domain", false));
		getDomainHelperExt().addSupperClass(getDomainHelper());
		getDomainHelperExt().setOverwrite(false);
	}

	private void prepareDomain() {
	    setDomain(new JavaSource("class", javaClassName+"Base", getPackagePath().concat(".domain.cg"), getSrcFolder()+"\\"+getSrcPackage()+"\\domain\\cg", false));
	    
	    setDomainExt(new JavaSource("class", javaClassName, getPackagePath().concat(".domain"), getSrcFolder()+"\\"+getSrcPackage()+"\\domain", false));
	    
	    getDomainExt().addSupperClass(getDomain());
	    getDomainExt().setOverwrite(false);
	   
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

	public boolean isCreateRestService() {
		return codegenParameters.isCreateRestService();
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

	private String getDaoImplPackage() {
		return codegenParameters.getDaoImplPackage();
	}

	private String getDaoBaseImplPackage() {
		return codegenParameters.getDaoBaseImplPackage();
	}

	private String getDaoPackage() {
		return codegenParameters.getDaoPackage();
	}

	private String getDaoBasePackage() {
		return codegenParameters.getDaoBasePackage();
	}
	
	private String getSvcImplPackage() {
		return codegenParameters.getSvcImplPackage();
	}

	private String getSvcBaseImplPackage() {
		return codegenParameters.getSvcBaseImplPackage();
	}

	private String getSvcPackage() {
		return codegenParameters.getSvcPackage();
	}

	private String getSvcBasePackage() {
		return codegenParameters.getSvcBasePackage();
	}
	
	private String getRestSvcPackage() {
		return codegenParameters.getRestSvcPackage();
	}
	
	public String getSrcFolderForPackage(String packageName) {
		return codegenParameters.getSrcFolderForPackage(packageName);
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

	public JavaSource getRestService() {
		return restService;
	}

	public void setRestService(JavaSource restService) {
		this.restService = restService;
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

	public void generateCode() throws IOException {
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
		
		getRestService().generateCode();
		
 	}

	private void addSpringBeans(Document document, JavaSource base, JavaSource impl, List<JavaSource> dependenciesRef) {
		Attr idAttr = document.createAttribute("id");
		idAttr.setValue(getModulePrefix() + "-" + base.getNameForVariable());

		Attr classAttr = document.createAttribute("class");
		classAttr.setValue(impl.getSourcePackage() + "." + impl.getName());

		Element newBean = document.createElement("bean");
		newBean.setAttributeNode(idAttr);
		newBean.setAttributeNode(classAttr);

		document.getDocumentElement().appendChild(newBean);

		for (JavaSource bean : dependenciesRef) {

			Attr nameAttr = document.createAttribute("name");
			nameAttr.setValue(bean.getNameForVariable());

			Attr refAttr = document.createAttribute("ref");
			refAttr.setValue(getModulePrefix() + "-" + bean.getNameForVariable());

			Element prop = document.createElement("property");
			prop.setAttributeNode(nameAttr);
			prop.setAttributeNode(refAttr);

			newBean.appendChild(prop);
		}
	}
}
