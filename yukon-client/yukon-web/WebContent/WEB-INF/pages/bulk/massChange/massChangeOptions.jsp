<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.massChangeOptions">

    <c:if test="${not empty statusMsg}">
        <div class="fwb success stacked">${statusMsg}</div>
    </c:if>
    
    <cti:url var="massChangeAction" value="/bulk/massChangeOptions"/>
    <form:form method="post" action="${massChangeAction}" modelAttribute="massChangeOptions">
        <cti:csrfToken/>
        <%-- DEVICE COLLECTION --%>
        <cti:deviceCollection deviceCollection="${deviceCollection}" />
        <%-- NAME OF FIELD --%>
        <input type="hidden" name="massChangeBulkFieldName" value="${massChangeBulkFieldName}">
        <%-- INCLUDE JSP FOR SELECTED CHANGE FIELD --%>
        <c:forEach var="input" items="${inputRoot.inputList}">
            <cti:renderInput input="${input}" />
        </c:forEach>
        <%-- SUBMIT --%>
        <div class="page-action-area">
            <cti:button nameKey="submit" busy="true" classes="primary action js-action-submit"/>
        </div>
   </form:form>
    
</cti:msgScope>
