package com.cannontech.stars.web.action;

import javax.servlet.http.*;
import com.cannontech.stars.xml.serialize.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public abstract class ActionBase {

    public abstract StarsOperation build(HttpServletRequest req, HttpSession session);

    public abstract StarsOperation process(StarsOperation reqOper, HttpSession session);

    public abstract boolean parse(StarsOperation operation, HttpSession session);
}