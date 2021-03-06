package com.ir.cgtool.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ir.cgtool.util.CodeGenUtil;
import com.ir.util.StringUtil;

public class Variable {

	public static final String PARAM_MODE_EXE = "EXE";

	public static final String PARAM_MODE_DECL = "DECL";

	private String name = null;

	private String type = null;

	private String access = null;

	private String initVal = null;

	private List<String> annotations = new ArrayList<String>(1);
	
	public Variable(String name, String type, String access) {
		super();

		setName(name);

		this.type = type;
		this.access = access;
	}

	public String getName() {
		if (getAccess().equalsIgnoreCase("public static final"))
			return name.toUpperCase();
		return name;
	}

	public void setName(String name) {
		this.name = CodeGenUtil.toLowerCase(name, 0);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;

	}

	public String getInitVal() {
		return initVal;
	}

	public void setInitVal(String initVal) {
		this.initVal = initVal;
	}
	
	public List<String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}

	public void addAnnotation(String annotation) {
		getAnnotations().add(annotation);
	}

	public String getInstanceVariableCode() {
		StringBuffer sb = new StringBuffer();
		for(String annotation : annotations )sb.append("\t@").append(annotation).append(StringUtil.LINE_SEPARTOR);
		sb.append("\t").append(getAccess()).append(" ").append(getType()).append(" ").append(getName()).append(" = ")
				.append(getColInitialization()).append(";").append(StringUtil.LINE_SEPARTOR)
				.append(StringUtil.LINE_SEPARTOR);
		return sb.toString();

	}

	public String getInstanceVariableTestCode(String className) {
		StringBuffer sb = new StringBuffer();
		sb.append("\t").append("assertNull(").append(" ").append(className).append(".").append(getName()).append(");")
				// .append(getColInitialization()).append("; ")
				.append(StringUtil.LINE_SEPARTOR).append(StringUtil.LINE_SEPARTOR);
		return sb.toString();
	}

	private String getColInitialization() {

		if (!StringUtil.isEmpty(getInitVal()))
			return getInitVal();

		if (getType().equals("Long") || getType().equals("String"))
			return "null";

		if (getType().equals("boolean"))
			return "false";

		return "null";
	}

	public String getParamCode(String mode) {
		StringBuffer sb = new StringBuffer();
		for(String annotation : annotations )sb.append("@").append(annotation).append(" ");
		if (PARAM_MODE_DECL.equalsIgnoreCase(mode))
			sb.append(getType()).append(" ");
		sb.append(getName());
		return sb.toString();
	}

	public final static Comparator<Variable> VARIABLE_NAME_COMPARATOR = new Comparator<Variable>(){
		@Override
		public int compare(Variable o1, Variable o2) {
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
