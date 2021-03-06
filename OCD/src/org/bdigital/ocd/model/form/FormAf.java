package org.bdigital.ocd.model.form;

import java.lang.reflect.InvocationTargetException;

import org.bdigital.ocd.model.Form;

 
public class FormAf extends org.apache.struts.action.ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ref;
	String shortName;
	String status;
	String itemType;
	FormDataAf formData;
	
	public FormAf(Form obj) throws IllegalAccessException, InvocationTargetException {
		super();
		this.ref=obj.getRef();
		this.shortName=obj.getShortName();
		this.status=obj.getStatus();
		if(obj.getFormData()!=null){
			this.formData = new FormDataAf(obj.getFormData());
		}
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public FormDataAf getFormData() {
		return formData;
	}
	public void setFormData(FormDataAf formData) {
		this.formData = formData;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
}
