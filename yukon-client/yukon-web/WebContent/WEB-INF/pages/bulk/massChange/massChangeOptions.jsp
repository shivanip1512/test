<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="tools" page="bulk.massChangeOptions">

    <tags:bulkActionContainer key="yukon.common.device.bulk.massChangeOptions.${massChangeBulkFieldName}" deviceCollection="${deviceCollection}">
        <c:if test="${not empty statusMsg}">
            <div class="fwb success stacked">${statusMsg}</div>
        </c:if>
        
        <cti:url var="massChangeAction" value="/bulk/massChangeOptions"/>
        <form:form id="massChangeOptionsForm" method="post" action="${massChangeAction}" commandName="massChangeOptions">
            <cti:csrfToken/>
                <%-- DEVICE COLLECTION --%>
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
                <%-- NAME OF FIELD --%>
                <input type="hidden" id="massChangeBulkFieldName" name="massChangeBulkFieldName" value="${massChangeBulkFieldName}">
                <%-- INCLUDE JSP FOR SELECTED CHANGE FIELD --%>
                <c:forEach var="input" items="${inputRoot.inputList}">
                    <cti:renderInput input="${input}" />
                </c:forEach>
                <%-- SUBMIT --%>
                <cti:button nameKey="submit" busy="true" type="submit" classes="primary action"/>
       </form:form>
    </tags:bulkActionContainer>
</cti:standardPage>