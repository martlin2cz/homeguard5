package cz.martlin.hg5.web;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
/**
 * Provides some useful utilities for beans. 
 * @author martin
 *
 */
public class Utils {

	public static void info(String title, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, title, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public static void warn(String title, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, title, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public static void error(String title, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, title, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

}
