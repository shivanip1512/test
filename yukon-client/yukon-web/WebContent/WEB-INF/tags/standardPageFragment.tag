<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="module" required="true" %>
<%@ attribute name="pageName" required="true" %>
<%@ attribute name="fragmentName" required="true" %>

<cti:standardMsgScopeHelper module="${module}" pageName="${pageName}" fragmentName="${fragmentName}">
    <jsp:doBody/>
</cti:standardMsgScopeHelper>