package com.hexaid.struts2.junit;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Gabriel Belingueres
 *
 */
public class MyAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    @Override
    public String execute() {
        return SUCCESS;
    }
    
    public String someMethod() {
        return INPUT;
    }

}
