package com.ir.cgtool.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ir.cgtool.CGConstants;
import com.ir.cgtool.util.CodeGenUtil;
import com.ir.util.StringUtil;

public class Method {
	
	private String access ; 
	
	private String returnType ; 
	
	private String name; 
	
	private List<Variable>  params = new ArrayList<Variable>() ;
	
	private String body ; 

	private boolean abStract ; 

	private boolean constructor ;
	
	private List<String> thrownExceptions = new ArrayList<String>();
	
	private List<String> annotations = new ArrayList<String>();
	
	private int sortOrder = 0 ; 
	
	public Method(String access, String returnType, String name, List<Variable> params, String body) {
		super();
		this.access = access;
		this.returnType = returnType;
		this.name = name;
		this.params = params;
		this.body = body;
	}

	public Method() {
		// TODO Auto-generated constructor stub
	}

	public Method(String access, String returnType, String name, List<Variable> params, String body, boolean abStract) {
		super();
		this.access = access;
		this.returnType = returnType;
		this.name = name;
		this.params = params;
		this.body = body;
		setAbStract(abStract);
	}
	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Variable> getParams() {
		return params;
	}

	public void setParams(List<Variable> params) {
		this.params = params;
	} 
	
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	 
	public boolean isAbStract() {
		return abStract;
	}

	public void setAbStract(boolean abStract) {
		this.abStract = abStract;
	}

	
	public boolean isConstructor() {
		return constructor;
	}

	public void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}
	
	public List<String> getThrownExceptions() {
		return thrownExceptions;
	}

	public void setThrownExceptions(List<String> thrownExceptions) {
		this.thrownExceptions = thrownExceptions;
	}

	public List<String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}

	public void addAnnotation(String annotation){
		annotations.add(annotation);
  	}

 	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String generateSrcCode() {
		StringBuffer sb = new StringBuffer();
		for(String annotation : annotations )sb.append("\t@").append(annotation).append(StringUtil.LINE_SEPARTOR);
		sb.append("\t").append(getAccess()).append(" ");
		if(!isConstructor()) sb.append(getReturnType()).append(" ");
		sb.append(getName())
		  .append("(").append(getParamCode()).append(") ");
		
		if(!getThrownExceptions().isEmpty()) {
			sb.append("throws").append(" ");
			int i=0;
			for(String excep : getThrownExceptions()) {
			    if(i>0)sb.append(", ");
				sb.append(excep);
			    i++;
			}
			sb.append(" ");
		}
		
		 if(isAbStract()){
			 sb.append(";");
		 }else{
			 sb.append(CGConstants.START_OF_SRC)
			  .append(StringUtil.LINE_SEPARTOR)
			  .append(StringUtil.isEmpty(getBody())?"":getBody())
			  .append(StringUtil.LINE_SEPARTOR)
			  .append("\t").append(CGConstants.END_OF_SRC);
		 }
		 
		sb.append(StringUtil.LINE_SEPARTOR).append(StringUtil.LINE_SEPARTOR);
 		return sb.toString();
	}

	public String generateTestCode() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t").append(getAccess()).append(" ");
		if(!isConstructor()) sb.append("void").append(" ");
		sb.append(getTestName())
		  .append("() ");
		
		if(!getThrownExceptions().isEmpty()) {
			sb.append("throws").append(" ");
			int i=0;
			for(String excep : getThrownExceptions()) {
			    if(i>0)sb.append(", ");
				sb.append(excep);
			    i++;
			}
			sb.append(" ");
		}
		
		 if(isAbStract()){
			 sb.append(";");
		 }else{
			 sb.append("{")
			  .append(StringUtil.LINE_SEPARTOR)
			  .append("\t").append("\t").append("assert(true);")
			  .append(StringUtil.LINE_SEPARTOR)
			  .append("\t").append("}");
		 }
		 
		    sb.append(StringUtil.LINE_SEPARTOR).append(StringUtil.LINE_SEPARTOR);
 		return sb.toString();
	}

	
	public String getParamCode(){
		return getParamCode(getParams(),Variable.PARAM_MODE_DECL);
	}
	
	public static String getParamCode(List<Variable> params, String mode) {
		StringBuffer sb = new StringBuffer();
		if(params!=null && !params.isEmpty()){
			  int i=0;
			  for(Variable  variable : params){
				if(i>0)sb.append(", ");  
				sb.append(variable.getParamCode(mode));
			    i++;
			  }
		  }
		return sb.toString();
	}
	
	
	public String getTestName() {
		String testName = name; 
		CodeGenUtil.toUpperCase(testName, 0);
		testName = "test"+testName;
		return testName;
	}

	public final static Comparator<Method> METHOD_NAME_COMPARATOR = new Comparator<Method>(){
		@Override
		public int compare(Method o1, Method o2) {
			if (o1 == null && o2 == null) return 0;
			if (o1 == null) return -1;
			if (o2 == null) return 1;
			
			if (o1.getName() == null && o2.getName() == null) return 0;
			if (o1.getName() == null) return -1;
			if (o2.getName() == null) return 1;

			return o1.getName().compareTo(o2.getName());
		}
	};
	 

}
