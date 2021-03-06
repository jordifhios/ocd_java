/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bdigital.ocd.base;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.bdigital.ocd.login.LoginAction;
import org.bdigital.ocd.login.LoginForm;
import org.bdigital.ocd.ws.LINKCAREException;

/**
 *
 * @author jroda
 */
public abstract class BaseAction extends org.apache.struts.action.Action {

    /* forward name="success" path="" */
    protected static final String SUCCESS = "success";
    protected final static String FAILURE = "failure";
    protected org.bdigital.ocd.ws.LINKCAREProxyWrapper proxy;
    private boolean autologin = false;

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
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
    	proxy = new org.bdigital.ocd.ws.LINKCAREProxyWrapper();
    	proxy.setEndpoint("http://dev3.linkcare.es/ws/ServerWSDL.php");
    	//proxy.setEndpoint("http://localhost:8081/BDIGITAL-LCWS/ServerWSDL.php");
    	HttpSession session = request.getSession();
    	String tokenLK = (String) session.getAttribute("tokenLK");
    	if(tokenLK == null && !(this instanceof LoginAction)){
    		if(autologin){
	    		LoginForm formBean = new LoginForm();
	            formBean.setUsername("atencio.integrada");
	            formBean.setPassword("xxxxxxxxxx");
	            request.setAttribute("LoginForm", formBean);
	            return mapping.findForward("loginAuto");
	    	}else{
	        	ActionMessages errors = new ActionMessages();
	            errors.add("general",
	                       new ActionMessage("errors.sessionexpired"));
	            saveErrors(request, errors);
	            return mapping.findForward("login");
	    	}
    	}
        try{
        	return doExecute(mapping, form, request, response);
        }catch(LINKCAREException e){
        	
        	ActionMessages errors = new ActionMessages();
            errors.add("general",new ActionMessage("errors.resultWS",e.getMessage()));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }catch(RemoteException e){
    		MessageResources msgResource = getResources(request);
        	String msgError="";
    		if(e.detail!=null && e.detail.getClass() == java.net.ConnectException.class){
            	String msg = msgResource.getMessage("errors.connection");
    			msgError = msg+": "+e.detail.getMessage();
    		}else{
    			msgError = e.getMessage();
    		}
    		
    		ActionMessages errors = new ActionMessages();
            errors.add("general",
                       new ActionMessage("errors.detail",msgError));
            saveErrors(request, errors);
            
    		return mapping.findForward(FAILURE);
    	}
    }
    
    public abstract ActionForward doExecute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception;
}
