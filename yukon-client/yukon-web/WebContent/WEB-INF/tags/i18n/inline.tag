<%@ attribute name="key" required="true" type="java.lang.String"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE"><span title="<cti:msg2 key="${key}" debug="true"/>" style="outline: red dotted thin"><cti:msg2 key="${key}" fallback="true"/></span></cti:checkGlobalRolesAndProperties>
<cti:checkGlobalRolesAndProperties value="!I18N_DESIGN_MODE"><cti:msg2 key="${key}"/></cti:checkGlobalRolesAndProperties>