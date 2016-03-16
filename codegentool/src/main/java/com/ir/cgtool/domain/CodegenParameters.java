package com.ir.cgtool.domain;

import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;

import com.ir.util.StringUtil;

public class CodegenParameters {
 
	private String tableNames = null ;
	 
	private String srcFolder = null;

	private String srcPackage = null;

	private boolean createSpringService = false;
	
	private boolean createRestService = false;

	private boolean createSpringDao = false;

	private String modulePrefix = null;

	private String packagePath = null;

	private String basefolder = null;

	private String configFileDir = null;

	private String daoImplPkg = null;
	
	private String daoBaseImplPkg = null;
	
	private String daoPkg = null;
	
	private String daoBasePkg = null;

	private String svcImplPkg = null;
	
	private String svcBaseImplPkg = null;
	
	private String svcPkg = null;
	
	private String svcBasePkg = null;
	
	private String restSvcPkg  = null ; 
	
	private boolean overwriteAll = false;
	
	private Map<String,Document> configFileMap = null ; 
	
	
	public CodegenParameters(Properties cgToolProperties) {
		setTableNames(cgToolProperties.getProperty("tableNames"));
		setSrcFolder(StringUtil.isEmpty(cgToolProperties.getProperty("srcFolder"))
				? System.getProperty("user.dir") + "\\cgsrc" : cgToolProperties.getProperty("srcFolder"));
		setSrcPackage(StringUtil.isEmpty(cgToolProperties.getProperty("srcPackage")) ? "com\\ir"
				: cgToolProperties.getProperty("srcPackage"));
		setCreateSpringService(
				new Boolean(StringUtil.nullCheck(cgToolProperties.getProperty("createSpringService"), "FALSE")));
		setCreateRestService(
				new Boolean(StringUtil.nullCheck(cgToolProperties.getProperty("createRestService"), "FALSE")));
		
		setCreateSpringDao(new Boolean(StringUtil.nullCheck(cgToolProperties.getProperty("createSpringDao"), "FALSE")));
		setModulePrefix(cgToolProperties.getProperty("modulePrefix"));
		setPackagePath(srcPackage.replace("\\", "."));
		setBasefolder(cgToolProperties.getProperty("basefolder"));
		setConfigFileDir(cgToolProperties.getProperty("configFileDir"));
		
		setDaoImplPkg(StringUtil.isEmpty(cgToolProperties.getProperty("daoImplPkg")) ? ".dao.impl"
				: cgToolProperties.getProperty("daoImplPkg"));
		setDaoBaseImplPkg(StringUtil.isEmpty(cgToolProperties.getProperty("daoBaseImplPkg")) ? ".dao.cg.impl"
				: cgToolProperties.getProperty("daoBaseImplPkg"));
		setDaoPkg(StringUtil.isEmpty(cgToolProperties.getProperty("daoPkg")) ? ".dao"
				: cgToolProperties.getProperty("daoPkg"));
		setDaoBasePkg(StringUtil.isEmpty(cgToolProperties.getProperty("daoBasePkg")) ? ".dao.cg"
				: cgToolProperties.getProperty("daoBasePkg"));
		
		
		setSvcImplPkg(StringUtil.isEmpty(cgToolProperties.getProperty("svcImplPkg")) ? ".svc.impl"
				: cgToolProperties.getProperty("svcImplPkg"));
		setSvcBaseImplPkg(StringUtil.isEmpty(cgToolProperties.getProperty("svcBaseImplPkg")) ? ".svc.cg.impl"
				: cgToolProperties.getProperty("svcBaseImplPkg"));
		setSvcPkg(StringUtil.isEmpty(cgToolProperties.getProperty("svcPkg")) ? ".svc"
				: cgToolProperties.getProperty("svcPkg"));
		setSvcBasePkg(StringUtil.isEmpty(cgToolProperties.getProperty("svcBasePkg")) ? ".svc.cg"
				: cgToolProperties.getProperty("svcBasePkg"));
		
		setRestSvcPkg(StringUtil.isEmpty(cgToolProperties.getProperty("restSvcPkg")) ? ".web.svc"
				: cgToolProperties.getProperty("restSvcPkg"));
		
		
		
		setOverwriteAll(
				new Boolean(StringUtil.nullCheck(cgToolProperties.getProperty("overwriteAll"), "FALSE")));
	}
	
	public String getTableNames() {
		return tableNames;
	}

	public void setTableNames(String tableNames) {
		this.tableNames = tableNames;
	}

	public String getSrcFolder() {
		return srcFolder;
	}

	public void setSrcFolder(String srcFolder) {
		this.srcFolder = srcFolder;
	}

	public String getSrcPackage() {
		return srcPackage;
	}

	public void setSrcPackage(String srcPackage) {
		this.srcPackage = srcPackage;
	}

	public boolean isCreateSpringService() {
		return createSpringService;
	}

	public void setCreateSpringService(boolean createSpringService) {
		this.createSpringService = createSpringService;
	}

	public boolean isCreateRestService() {
		return createRestService;
	}

	public void setCreateRestService(boolean createRestService) {
		this.createRestService = createRestService;
	}

	public boolean isCreateSpringDao() {
		return createSpringDao;
	}

	public void setCreateSpringDao(boolean createSpringDao) {
		this.createSpringDao = createSpringDao;
	}

	public String getModulePrefix() {
		return modulePrefix;
	}

	public void setModulePrefix(String modulePrefix) {
		this.modulePrefix = modulePrefix;
	}
 	 
	public String getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	public String getBasefolder() {
		return basefolder;
	}

	public void setBasefolder(String basefolder) {
		this.basefolder = basefolder;
	}

	public String getConfigFileDir() {
		return configFileDir;
	}

	public void setConfigFileDir(String configFileDir) {
		this.configFileDir = configFileDir;
	}
	
	public String getDaoImplPkg() {
		return daoImplPkg;
	}

	public void setDaoImplPkg(String daoImplPkg) {
		this.daoImplPkg = daoImplPkg;
	}

	public String getDaoBaseImplPkg() {
		return daoBaseImplPkg;
	}

	public void setDaoBaseImplPkg(String daoBaseImplPkg) {
		this.daoBaseImplPkg = daoBaseImplPkg;
	}

	public String getDaoPkg() {
		return daoPkg;
	}

	public void setDaoPkg(String daoPkg) {
		this.daoPkg = daoPkg;
	}

	public String getDaoBasePkg() {
		return daoBasePkg;
	}

	public void setDaoBasePkg(String daoBasePkg) {
		this.daoBasePkg = daoBasePkg;
	}

	public String getSvcImplPkg() {
		return svcImplPkg;
	}

	public void setSvcImplPkg(String svcImplPkg) {
		this.svcImplPkg = svcImplPkg;
	}

	public String getSvcBaseImplPkg() {
		return svcBaseImplPkg;
	}

	public void setSvcBaseImplPkg(String svcBaseImplPkg) {
		this.svcBaseImplPkg = svcBaseImplPkg;
	}

	public String getSvcPkg() {
		return svcPkg;
	}

	public void setSvcPkg(String svcPkg) {
		this.svcPkg = svcPkg;
	}

	public String getSvcBasePkg() {
		return svcBasePkg;
	}

	public void setSvcBasePkg(String svcBasePkg) {
		this.svcBasePkg = svcBasePkg;
	}
	
	public String getRestSvcPkg() {
		return restSvcPkg;
	}

	public void setRestSvcPkg(String restSvcPkg) {
		this.restSvcPkg = restSvcPkg;
	}

	public boolean isOverwriteAll() {
		return overwriteAll;
	}

	public void setOverwriteAll(boolean overwriteAll) {
		this.overwriteAll = overwriteAll;
	}

	public Map<String, Document> getConfigFileMap() {
		return configFileMap;
	}

	public void setConfigFileMap(Map<String, Document> configFileMap) {
		this.configFileMap = configFileMap;
	}

	public String getConfigFileLocation() {
		return  getBasefolder()+"\\"+ getConfigFileDir();
	}

	public String getDaoImplPackage() {
		return getFullPackagePath(getDaoImplPkg());
	}

	public String getDaoBaseImplPackage() {
		return getFullPackagePath(getDaoBaseImplPkg());
	}

	public String getDaoPackage() {
		return getFullPackagePath(getDaoPkg());
	}

	public String getDaoBasePackage() {
		return getFullPackagePath(getDaoBasePkg());
	}
	
	public String getSvcImplPackage() {
		return getFullPackagePath(getSvcImplPkg());
	}

	public String getSvcBaseImplPackage() {
		return getFullPackagePath(getSvcBaseImplPkg());
	}

	public String getSvcPackage() {
		return getFullPackagePath(getSvcPkg());
	}

	public String getSvcBasePackage() {
		return getFullPackagePath(getSvcBasePkg());
	}
	
	public String getRestSvcPackage() {
		return getFullPackagePath(getRestSvcPkg());
	}

	public String getFullPackagePath(String subPackage) {
 		return getPackagePath().concat(subPackage); 
	}
	
	public String getSrcFolderForPackage(String packageName) {
 		return getSrcFolder().concat("\\").concat(packageName).replace(".", "\\");
	}
	
	
}
