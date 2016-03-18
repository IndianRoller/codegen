package com.ir.cgtool.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.ir.cgtool.CGConstants;
import com.ir.cgtool.util.CodeGenUtil;
import com.ir.util.StringUtil;

public class CodegenParameters {

	private Map<String, Document> configFileMap = new HashMap<String, Document>();
	private String baseFolder = null;
	private String configFileDir = null;
	private String srcFolder = null;
	private String srcPackage = null;
	private String modulePrefix = null;
	private boolean createSpringDao = false;
	private boolean createSpringService = false;
	private boolean createRestService = false;
	private String domainBasePkg = null;
	private String domainPkg = null;
	private String domainHelperBasePkg = null;
	private String domainHelperPkg = null;
	private String daoBasePkg = null;
	private String daoBaseImplPkg = null;
	private String daoPkg = null;
	private String daoImplPkg = null;
	private String svcBasePkg = null;
	private String svcBaseImplPkg = null;
	private String svcPkg = null;
	private String svcImplPkg = null;
	private String restSvcPkg = null;
	private boolean overwriteAll = false;
	private String tableNames = null;
	private String excludeTableNames = null;

	public CodegenParameters(Properties cgToolProperties) throws ParserConfigurationException {
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
		// setPackagePath(srcPackage.replace("\\", "."));
		setBaseFolder(cgToolProperties.getProperty("baseFolder"));
		setConfigFileDir(cgToolProperties.getProperty("configFileDir"));

		
		
		setDomainBasePkg(getPackage(cgToolProperties, "domainBasePackage", ".domain.cg"));
		setDomainPkg(getPackage(cgToolProperties, "domainPackage", ".domain"));
		
		setDomainHelperBasePkg(getPackage(cgToolProperties, "domainHelperBasePackage", ".domain.cg"));
		setDomainHelperPkg(getPackage(cgToolProperties, "domainHelperBasePackage", ".domain"));
		
		setDaoBasePkg(getPackage(cgToolProperties, "daoBasePkg", ".dao.cg"));
		setDaoBaseImplPkg(getPackage(cgToolProperties, "daoBaseImplPkg", ".dao.cg.impl"));
 
		setDaoPkg(getPackage(cgToolProperties, "daoPkg", ".dao"));
		setDaoImplPkg(getPackage(cgToolProperties, "daoImplPkg", ".dao.impl"));

		setSvcBasePkg(getPackage(cgToolProperties, "svcBasePkg", ".svc.cg"));
		setSvcBaseImplPkg(getPackage(cgToolProperties, "svcBaseImplPkg", ".svc.cg.impl"));
 
		setSvcPkg(getPackage(cgToolProperties, "svcPkg", ".svc"));
		setSvcImplPkg(getPackage(cgToolProperties, "svcImplPkg", ".svc.impl"));
  
		setRestSvcPkg(getPackage(cgToolProperties, "restSvcPkg", ".web.svc"));
		  

		setOverwriteAll(new Boolean(StringUtil.nullCheck(cgToolProperties.getProperty("overwriteAll"), "FALSE")));

		if (isCreateSpringService())
			getConfigFileMap().put(CGConstants.SERVICE_CONFIG_XML, CodeGenUtil.getNewDocument());
		if (isCreateSpringDao())
			getConfigFileMap().put(CGConstants.DAO_CONFIG_XML, CodeGenUtil.getNewDocument());
	}

	private String getPackage(Properties cgToolProperties, String propName, String defValue) {
		return getSrcPackage().concat(StringUtil.isEmpty(cgToolProperties.getProperty(propName)) ? defValue
		: cgToolProperties.getProperty(propName));
	}

	public Map<String, Document> getConfigFileMap() {
		return configFileMap;
	}

	public void setConfigFileMap(Map<String, Document> configFileMap) {
		this.configFileMap = configFileMap;
	}

	public String getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	public String getConfigFileDir() {
		return configFileDir;
	}

	public void setConfigFileDir(String configFileDir) {
		this.configFileDir = configFileDir;
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

	public String getModulePrefix() {
		return modulePrefix;
	}

	public void setModulePrefix(String modulePrefix) {
		this.modulePrefix = modulePrefix;
	}

	public boolean isCreateSpringDao() {
		return createSpringDao;
	}

	public void setCreateSpringDao(boolean createSpringDao) {
		this.createSpringDao = createSpringDao;
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

	public String getDomainBasePkg() {
		return domainBasePkg;
	}

	public void setDomainBasePkg(String domainBasePkg) {
		this.domainBasePkg = domainBasePkg;
	}

	public String getDomainPkg() {
		return domainPkg;
	}

	public void setDomainPkg(String domainPkg) {
		this.domainPkg = domainPkg;
	}

	public String getDomainHelperBasePkg() {
		return domainHelperBasePkg;
	}

	public void setDomainHelperBasePkg(String domainHelperBasePkg) {
		this.domainHelperBasePkg = domainHelperBasePkg;
	}

	public String getDomainHelperPkg() {
		return domainHelperPkg;
	}

	public void setDomainHelperPkg(String domainHelperPkg) {
		this.domainHelperPkg = domainHelperPkg;
	}

	public String getDaoBasePkg() {
		return daoBasePkg;
	}

	public void setDaoBasePkg(String daoBasePkg) {
		this.daoBasePkg = daoBasePkg;
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

	public String getDaoImplPkg() {
		return daoImplPkg;
	}

	public void setDaoImplPkg(String daoImplPkg) {
		this.daoImplPkg = daoImplPkg;
	}

	public String getSvcBasePkg() {
		return svcBasePkg;
	}

	public void setSvcBasePkg(String svcBasePkg) {
		this.svcBasePkg = svcBasePkg;
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

	public String getSvcImplPkg() {
		return svcImplPkg;
	}

	public void setSvcImplPkg(String svcImplPkg) {
		this.svcImplPkg = svcImplPkg;
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

	public String getTableNames() {
		return tableNames;
	}

	public void setTableNames(String tableNames) {
		this.tableNames = tableNames;
	}

	public String getExcludeTableNames() {
		return excludeTableNames;
	}

	public void setExcludeTableNames(String excludeTableNames) {
		this.excludeTableNames = excludeTableNames;
	}

	public String getConfigFileLocation() {
		return getBaseFolder() + "\\" + getConfigFileDir();
	}
 
	public String getSrcFolderForPackage(String packageName) {
		return getBaseFolder().concat("\\").concat(getSrcFolder()).concat("\\").concat(packageName).replace(".", "\\");
	}

	public Document getDaoConfigFile() {
		return getConfigFileMap().get(CGConstants.DAO_CONFIG_XML);
	}

	public Document getSvcConfigFile() {
		return getConfigFileMap().get(CGConstants.SERVICE_CONFIG_XML);
	}

}
