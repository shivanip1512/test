<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="module" required="true" type="java.lang.String"%>
<%@ attribute name="pageName" required="true" type="java.lang.String"%>
<%@ attribute name="fragmentName" required="true" type="java.lang.String"%>

<cti:standardMsgScopeHelper module="${module}" pageName="${pageName}" fragmentName="${fragmentName}">
	<jsp:doBody/>
</cti:standardMsgScopeHelper>