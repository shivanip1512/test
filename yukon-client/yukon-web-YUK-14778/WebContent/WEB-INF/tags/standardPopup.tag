<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="module" required="true" type="java.lang.String"%>
<%@ attribute name="pageName" required="true" type="java.lang.String"%>
<%@ attribute name="popupName" required="true" type="java.lang.String"%>

<cti:flashScopeMessages/>

<cti:standardMsgScopeHelper module="${module}" pageName="${pageName}" fragmentName="${popupName}">
    <div id="${popupName}_ContentWrapper">
        <jsp:doBody/>
    </div>
</cti:standardMsgScopeHelper>