package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public interface ActionBase {

    public SOAPMessage build(HttpServletRequest req, HttpSession session);

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session);

    public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session);
}