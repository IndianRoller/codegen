package com.ir.cgtool.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ir.cgtool.CGConstants;
import com.ir.cgtool.util.CodeGenUtil;
import com.ir.util.StringUtil;

public class JavaSource {

	private String type = null;

	private String name = null;

	private String sourcePackage = null;

	private String sourceFolder = null;

	private String superClassName = null;

	private String superClassAssociationType = null;

	private List<String> implmentionList = new ArrayList<String>();

	private List<String> importList = new ArrayList<String>();

	private List<Variable> variableList = new ArrayList<Variable>();

	private List<Method> methodList = new ArrayList<Method>();

	private boolean overwrite = true;

	private boolean testClassRequired = false;
	
	private List<String> annotations = new ArrayList<String>();
	
	private boolean sortImports = true; 
	
	private boolean sortMethods = false; 
	
	private boolean sortVariables = false; 
	
	private boolean isSpringBean = false; 
	 
	public JavaSource(String type, String name, String sourcePackage, String sourceFolder) {
		super();
		setType(type);
		setName(name);
		setSourcePackage(sourcePackage);
		setSourceFolder(sourceFolder);
		setTestClassRequired(false);
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSourcePackage() {
		return sourcePackage;
	}

	public void setSourcePackage(String sourcePackage) {
		this.sourcePackage = sourcePackage;
	}

	public String getSourceFolder() {
		return sourceFolder;
	}

	public void setSourceFolder(String sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public String getSuperClassAssociationType() {
		return superClassAssociationType;
	}

	public void setSuperClassAssociationType(String superClassAssociationType) {
		this.superClassAssociationType = superClassAssociationType;
	}

	public List<String> getImplmentionList() {
		return implmentionList;
	}

	public void setImplmentionList(List<String> implmentionList) {
		this.implmentionList = implmentionList;
	}

	public List<String> getImportList() {
		return importList;
	}

	public void setImportList(List<String> importList) {
		this.importList = importList;
	}

	public List<Variable> getVariableList() {
		return variableList;
	}

	public void setVariableList(List<Variable> variableList) {
		this.variableList = variableList;
	}

	public List<Method> getMethodList() {
		return methodList;
	}

	public void setMethodList(List<Method> methodList) {
		this.methodList = methodList;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean isTestClassRequired() {
		return testClassRequired;
	}

	public void setTestClassRequired(boolean testClassRequired) {
		this.testClassRequired = testClassRequired;
	}

	public List<String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}
	
	public boolean isSortImports() {
		return sortImports;
	}

	public void setSortImports(boolean sortImports) {
		this.sortImports = sortImports;
	}

	public boolean isSortMethods() {
		return sortMethods;
	}

	public void setSortMethods(boolean sortMethods) {
		this.sortMethods = sortMethods;
	}

	public boolean isSortVariables() {
		return sortVariables;
	}

	public void setSortVariables(boolean sortVariables) {
		this.sortVariables = sortVariables;
	}
	
	public boolean isSpringBean() {
		return isSpringBean;
	}

	public void setSpringBean(boolean isSpringBean) {
		this.isSpringBean = isSpringBean;
	}

	public void addAnnotation(String annotation, String annotationImport){
		annotations.add(annotation);
		getImportList().add(annotationImport);
 	}
	
	public String getFullName() {
		return getSourcePackage().concat(".").concat(getName());
	}

	public String getNameForVariable() {
		return CodeGenUtil.toLowerCase(getName(), 0);
	}

	public String getSpringBeanName(String pefix) {
		return  pefix + "-" +  CodeGenUtil.toLowerCase(getName(), 0);
	}

	public String getPackageStatementCode() {
		StringBuffer content = new StringBuffer();
		content.append("package ").append(getSourcePackage()).append(";")
				.append(StringUtil.LINE_SEPARTOR)
				.append(StringUtil.LINE_SEPARTOR);
		return content.toString();
	}

	public String getImportStatementCode(List<String> importList) {
		if (importList.isEmpty())
			return "";

		StringBuffer content = new StringBuffer();
		for (String s : importList)
			content.append("import ").append(s).append(";")
					.append(StringUtil.LINE_SEPARTOR);

		content.append(StringUtil.LINE_SEPARTOR);
		return content.toString();
	}

	
	
	public String getSourceNameCode() {
		StringBuffer content = new StringBuffer();
		
		for(String annotation : annotations )content.append("@").append(annotation).append(StringUtil.LINE_SEPARTOR);
		
		content.append("public ").append(getType()).append(" ")
				.append(getName()).append(" ");

		if (!StringUtil.isEmpty(getSuperClassAssociationType())) {
			content.append(
					getSuperClassAssociationType() + " " + getSuperClassName())
					.append(" ");
		}

		int i = 0;
		for (String implClass : implmentionList) {
			if (i > 0)
				content.append(", ");
			content.append("implements " + implClass).append(" ");
			i++;
		}

		content.append("{").append(StringUtil.LINE_SEPARTOR)
				.append(StringUtil.LINE_SEPARTOR);
		return content.toString();
	}

	public String getTestNameCode() {
		StringBuffer content = new StringBuffer();
		content.append("public ").append(getType()).append(" ").append(getTestName()).append(" ");
		content.append("extends" + " " + "TestCase").append(" ");
 		content.append("{").append(StringUtil.LINE_SEPARTOR).append(StringUtil.LINE_SEPARTOR);
		return content.toString();
	}
	
	

	public void generateCode() throws IOException {
		
		if(isSortImports()) Collections.sort(getImportList());

		if(isSortMethods()) Collections.sort(getMethodList(), Method.METHOD_NAME_COMPARATOR);
		
		if(isSortVariables()) Collections.sort(getVariableList(), Variable.VARIABLE_NAME_COMPARATOR);
		
		generateSrcCode();

		generateTestCode();
	}

	private void generateSrcCode() throws IOException {
		if (getName() == null){ 
			return;
		}

		File fileDirectory = new File(getSourceFolder());
		if (!fileDirectory.exists()) fileDirectory.mkdirs();
		
		File srcFile = new File(getSourceFolder() + "\\" + getName() + ".java");
		System.out.println("Writing to : "+ srcFile.getAbsolutePath());

		if (!isOverwrite() && srcFile.exists()) return;

		BufferedWriter output = new BufferedWriter(new FileWriter(srcFile));

 		StringBuffer content = new StringBuffer();
		content.append(getPackageStatementCode());
		content.append(getImportStatementCode(getImportList()));
		content.append(getSourceNameCode());

		content.append(getInstanceVariablesCode());
		content.append(getMethodsCode());

		content.append(CGConstants.END_OF_SRC);
		output.write(content.toString());
		output.close();
	}

	private void generateTestCode() throws IOException {
		if (getName() == null || !isTestClassRequired()) return;

		File dir = new File(getTestFolder());

		if (!dir.exists()) dir.mkdirs();
		
		File testFile = new File(getTestFolder() + "\\" + getTestName() + ".java");

		if (!isOverwrite() && testFile.exists()) return;

		
		StringBuffer content = new StringBuffer();
		content.append(getPackageStatementCode());
		
		List<String> testImport = new ArrayList<String>(1);
		testImport.add("junit.framework.TestCase");
		
		content.append(getImportStatementCode(testImport));
		content.append(getTestNameCode());

		//content.append(getInstanceVariablesTestCode());
        content.append(getMethodsTestCode());

		content.append(CGConstants.END_OF_SRC);

		BufferedWriter output = new BufferedWriter(new FileWriter(testFile));
		output.write(content.toString());
		output.close();
	}

	private String getMethodsCode() {

		if (getMethodList().isEmpty())
			return "";

		StringBuffer content = new StringBuffer();

		for (Method method : getMethodList()) {
			content.append(method.getMethodText());
		}

		return content.toString();
	}

	private String getMethodsTestCode() {

		if (getMethodList().isEmpty())
			return "";

		StringBuffer content = new StringBuffer();

		for (Method method : getMethodList()) {
			if(method.isConstructor() || method.isAbStract()) continue; 
			content.append(method.getMethodTestText());
		}

		return content.toString();
	}
	
	private Object getInstanceVariablesCode() {
		if (getVariableList().isEmpty()) return "";

		StringBuffer content = new StringBuffer();

		for (Variable variable : getVariableList()) {
			content.append(variable.getInstanceVariableCode());
		}
		return content.toString();
	}

//	private Object getInstanceVariablesTestCode() {
//		if (getVariableList().isEmpty()) return "";
//
//		StringBuffer content = new StringBuffer("public void testGetterSetters() {").append(StringUtil.LINE_SEPARTOR);
//
//		for (Variable variable : getVariableList()) {
//			content.append(variable.getInstanceVariableTestCode(getName()));
//		}
//		
//		content.append(" } ").append(StringUtil.LINE_SEPARTOR);
//		return content.toString();
//	}
	
	public BufferedWriter getBufferdWriter() throws IOException {
		File serviceDirectory = new File(getSourceFolder());
		if (!serviceDirectory.exists())
			serviceDirectory.mkdirs();
		File serviceClass = new File(getSourceFolder() + "\\" + getName()
				+ ".java");
		BufferedWriter output = new BufferedWriter(new FileWriter(serviceClass));
		return output;
	}

	@Override
	public String toString() {
		return "JavaSource [type=" + type + ", name=" + name
				+ ", sourcePackage=" + sourcePackage + ", sourceFolder="
				+ sourceFolder + "]";
	}
	
	public String getTestFolder() {
		String testFolder = sourceFolder;
		return testFolder.replace("main", "test");
	}
	
	public String getTestName() {
		String testName = getName();
		return testName + "Test" ;  	
    }

	public boolean isInterface(){
		return "interface".equals(getType());
	}
	
	public void addSupperClass(JavaSource superClass) {
		if(superClass.isInterface() &&  isInterface()){
			setSuperClassAssociationType("extends" );
		}else {
			setSuperClassAssociationType(superClass.isInterface()?"implements":"extends" );
		}
		 
		setSuperClassName(superClass.getName());
		getImportList().add(superClass.getFullName());
	}
	
	public void addSupperClass(String className, String fullPath) {
	    setSuperClassAssociationType("extends" );
	 	setSuperClassName(className);
		getImportList().add(fullPath);
	}
	
	public void addImplementation(String className, String fullPath) {
		getImplmentionList().add(className);
		getImportList().add(fullPath);
	}
	
	public void addImplementation(JavaSource superClass) {
 	 	getImplmentionList().add(superClass.getName());
		getImportList().add(superClass.getFullName());
	}

	public void addDependency(JavaSource bean, List<String> methodAnnotations, boolean accessModifiersRequired) {
		Variable beanInstanceRef = new Variable(bean.getName(), bean.getName(), "private"); 
	    List<Variable> params = new ArrayList<Variable>();
		params.add(beanInstanceRef);
		
		
		getVariableList().add(beanInstanceRef);
		
		if(accessModifiersRequired){
			 StringBuffer setMethodBody = new StringBuffer();
			 setMethodBody.append("\t").append("\t").append("this.").append(bean.getNameForVariable()).append(" = ").append(bean.getNameForVariable()).append(";");
			
			 StringBuffer getMethodBody = new StringBuffer();
			 getMethodBody.append("\t").append("\t").append("return ").append(bean.getNameForVariable()).append(";");
			
			 getMethodList().add(new Method("public", bean.getName(), "get"+CodeGenUtil.toUpperCase(bean.getName(), 0), new ArrayList<Variable>(),getMethodBody.toString()));
			 getMethodList().add(new Method("public", "void", "set"+CodeGenUtil.toUpperCase(bean.getName(), 0), params,setMethodBody.toString()));
		}
		
		if (methodAnnotations != null) {
			for (String methodAnnotation : methodAnnotations) {
				beanInstanceRef.addAnnotation(methodAnnotation.substring(methodAnnotation.lastIndexOf(".") + 1));
				getImportList().add(methodAnnotation);
			}
		}
		
		getImportList().add(bean.getFullName());
	}

	public void addConstructor(List<JavaSource> dependenciesRef,List<JavaSource> dependenciesImpl) {
		Method constructor = new Method();
	    constructor.setConstructor(true);
	    constructor.setName(getName());
	    constructor.setAccess("public");
	 
	    StringBuffer constBody = new StringBuffer();
	    int i=0;
	    for(JavaSource beanRef : dependenciesRef) {
	    	JavaSource bean =  dependenciesImpl==null || dependenciesImpl.isEmpty() ? beanRef : dependenciesImpl.get(i);
	    	if(i>1) constBody.append(StringUtil.LINE_SEPARTOR);
	    	constBody.append("\t").append("\t").append("this.").append(beanRef.getNameForVariable()).append(" = ").append("new ").append(bean.getName()).append("();");
	     
	    	if( dependenciesImpl !=null && !dependenciesImpl.isEmpty() ) getImportList().add(bean.getFullName());
	    	
	    	i++;
	    }
	    constructor.setBody(constBody.toString());
 	    
	    getMethodList().add(constructor);
	    
	}


	public void applySort(boolean b) {
		 setSortImports(b);
		 setSortMethods(b);
		 setSortVariables(b);
	}
}
