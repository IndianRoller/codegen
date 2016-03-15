package com.ir.cgtool.domain;

import java.util.Properties;

import com.ir.util.StringUtil;

public class CodegenParameters {
 
	private String tableNames = null ;
	 
	private String srcFolder = null;

	private String srcPackage = null;

	private boolean createSpringService = false;

	private boolean createSpringDao = false;

	private String modulePrefix = null;
 
	private String packagePath = null;
	
	private String basefolder = null ; 
	
	private String configFileDir = null;
	
	public CodegenParameters(Properties cgToolProperties) {
		setTableNames(cgToolProperties.getProperty("tableNames"));
  		setSrcFolder(StringUtil.isEmpty(cgToolProperties.getProperty("srcFolder")) ? System.getProperty("user.dir") + "\\cgsrc" : cgToolProperties.getProperty("srcFolder"));
		setSrcPackage(StringUtil.isEmpty(cgToolProperties.getProperty("srcPackage")) ? "com\\ir"   : cgToolProperties.getProperty("srcPackage"));
		setCreateSpringService(
				new Boolean(StringUtil.nullCheck(cgToolProperties.getProperty("createSpringService"), "FALSE")));
		setCreateSpringDao(new Boolean(StringUtil.nullCheck(cgToolProperties.getProperty("createSpringDao"), "FALSE")));
		setModulePrefix(cgToolProperties.getProperty("modulePrefix"));
		setPackagePath(srcPackage.replace("\\", "."));
		setBasefolder(cgToolProperties.getProperty("basefolder"));
		setConfigFileDir(cgToolProperties.getProperty("configFileDir"));
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
	
	public String getConfigFileLocation() {
		return  getBasefolder()+"\\"+ getConfigFileDir();
	}
	

}
