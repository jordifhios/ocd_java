/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bdigital.ocd.admission;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.holders.StringHolder;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bdigital.ocd.base.BaseAction;
import org.bdigital.ocd.model.Action;
import org.bdigital.ocd.model.Actions;
import org.bdigital.ocd.model.Admission;
import org.bdigital.ocd.model.AdmissionData;
import org.bdigital.ocd.model.AdmissionProgram;
import org.bdigital.ocd.model.AdmissionProtocol;
import org.bdigital.ocd.model.Case;
import org.bdigital.ocd.model.Task;
import org.bdigital.ocd.model.Tasks;
import org.bdigital.ocd.utils.UtilsString;
import org.bdigital.ocd.utils.UtilsWs;

/**
 *
 * @author jroda
 */
public class AdmissionDetailsNoAutoAction extends BaseAction {

    /**
     * This is the action called from the Struts framework.
     *
     * @param mapping The ActionMapping used to select this instance.
     * @param form The optional ActionForm bean for this request.
     * @param request The HTTP Request we are processing.
     * @param response The HTTP Response we are processing.
     * @throws java.lang.Exception
     * @return
     */
    @Override
    public ActionForward doExecute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // extract user data
    	AdmissionDetailsForm formBean = (AdmissionDetailsForm)form;
    	
    	String tokenLK = (String)request.getSession().getAttribute("tokenLK");
    	String admissionId=formBean.getIdAdmission();
    	
    	if(admissionId!=null){
    		StringHolder errorMsg = new StringHolder("");
        	StringHolder result = new StringHolder("");
        	proxy.admission_get(tokenLK,admissionId,result,errorMsg);
        	if (!"".equals(errorMsg.value)) {

                ActionMessages errors = new ActionMessages();
                errors.add("general",new ActionMessage("errors.detail",errorMsg.value));
                saveErrors(request, errors);
                return mapping.findForward(FAILURE);
            }else{
            	
            	Admission admissionObj = (Admission)UtilsWs.xmlToObject(result.value,
            			Admission.class, Case.class, 
            			AdmissionData.class, AdmissionProgram.class, AdmissionProtocol.class);
            	if(admissionObj.getData()!=null){
            		if(admissionObj.getData().getProgram()!=null){
            			formBean.setProgramName(admissionObj.getData().getProgram().getName());
            		}
		    		formBean.setEnrolDate(admissionObj.getData().getEnrolDate());
            	}
            	
            	errorMsg = new StringHolder("");
            	result = new StringHolder("");
            	proxy.action_list(tokenLK,"","CARE",admissionId,"","",result,errorMsg);
            	if (!"".equals(errorMsg.value)) {

                    ActionMessages errors = new ActionMessages();
                    errors.add("general",new ActionMessage("errors.detail",errorMsg.value));
                    saveErrors(request, errors);
                    return mapping.findForward(FAILURE);
                }else{
                	Actions actionsObj = (Actions)UtilsWs.xmlToObject(result.value,
                			Actions.class, Action.class);
                	formBean.setActions(actionsObj.getActions());
                	errorMsg = new StringHolder("");
                	result = new StringHolder("");
                	proxy.action_list(tokenLK,"","MNG",admissionId,"","",result,errorMsg);
                	if (!"".equals(errorMsg.value)) {

                        ActionMessages errors = new ActionMessages();
                        errors.add("general",new ActionMessage("errors.detail",errorMsg.value));
                        saveErrors(request, errors);
                        return mapping.findForward(FAILURE);
                    }else{
                    	actionsObj = (Actions)UtilsWs.xmlToObject(result.value,
                    			Actions.class, Action.class);
                    	formBean.getActions().addAll(actionsObj.getActions());

//                    	errorMsg = new StringHolder("");
//                    	result = new StringHolder("");
//                    	proxy.task_list_day_default(tokenLK, "ADMI", admissionId, result, errorMsg);
//                    	if (!"".equals(errorMsg.value)) {
//
//                            ActionMessages errors = new ActionMessages();
//                            errors.add("general",new ActionMessage("errors.detail",errorMsg.value));
//                            saveErrors(request, errors);
//                            return mapping.findForward(FAILURE);
//                        }else{
                        	errorMsg = new StringHolder("");
                        	result = new StringHolder("");
                        	Date admissionDate = null;
                        	Calendar admissionCal = new GregorianCalendar();
                        	Calendar todayCal = new GregorianCalendar();
                        	
                        	if(admissionObj.getData()!=null){
                        		if(admissionObj.getData().getProgram()!=null){
                        			admissionDate = UtilsString.stringtoDate(admissionObj.getData().getEnrolDate(),UtilsWs.FORMAT_DATEHOUR_WS);
                        			//String admissionDateString = UtilsString.dateToString(admissionDate, UtilsWs.FORMAT_DATE_WS);
                        			admissionCal = new GregorianCalendar();
                        			admissionCal.setTime(admissionDate);
                        		}
                        	}
                        	
                        	List<Task> tasks = new ArrayList<Task>();
                        	List<Task> proTasks = new ArrayList<Task>();
                        	for(int j=admissionCal.get(Calendar.YEAR);j<=todayCal.get(Calendar.YEAR)+1;j++){

                            	errorMsg = new StringHolder("");
                            	result = new StringHolder("");
                        		proxy.task_calendar_year(tokenLK, ""+j, "ADMI", admissionId, result, errorMsg);
                            	if (!"".equals(errorMsg.value)) {

                                    ActionMessages errors = new ActionMessages();
                                    errors.add("general",new ActionMessage("errors.detail",errorMsg.value));
                                    saveErrors(request, errors);
                                    return mapping.findForward(FAILURE);
                                }else{
                                	if(result.value!=null){
	                                	String[] datesArray = result.value.split("#");
	                                	for(int k=0;k<datesArray.length;k++){
	                                		String dateItem = datesArray[k];
	                                		String[] infoDateArray = dateItem.split("\\|");
	                                    	errorMsg = new StringHolder("");
	                                    	result = new StringHolder("");
	                                		proxy.task_list_date(tokenLK, infoDateArray[0], "ADMI", admissionId, result, errorMsg);
	                                    	if (!"".equals(errorMsg.value)) {
	
	                                            ActionMessages errors = new ActionMessages();
	                                            errors.add("general",new ActionMessage("errors.detail",errorMsg.value));
	                                            saveErrors(request, errors);
	                                            return mapping.findForward(FAILURE);
	                                        }else{
	                                        	Tasks tasksObj = (Tasks)UtilsWs.xmlToObject(result.value,
	                                        			Tasks.class, Task.class);
	                                        	if(tasksObj.getTasks()!=null){
	                                        		for(int i=0;i<tasksObj.getTasks().size();i++){
	            	                        			Task t = tasksObj.getTasks().get(i);
	            	                        			if("PRO_TASK".equals(t.getTaskClass())){
	            	                        				proTasks.add(t);
	            	                        			}
	            	                        			tasks.add(t);
	            	                            	}
	                                        	}
	                                        }
	                                	}
                                	}
                                }
                        	}

                        	formBean.setTasks(tasks);
                        	return mapping.findForward(SUCCESS);
//                        }
                    }
                	
                }
            }
    	}else{
    		return mapping.findForward(FAILURE);
    	}
    }
}
