package com.cannontech.stars.web.action;

import javax.servlet.http.*;
import javax.xml.soap.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public abstract class ActionBase {

    public abstract SOAPMessage build(HttpServletRequest req, HttpSession session);

    public abstract SOAPMessage process(SOAPMessage reqMsg, HttpSession session);

    public abstract int parse(SOAPMessage respMsg, HttpSession session);
}