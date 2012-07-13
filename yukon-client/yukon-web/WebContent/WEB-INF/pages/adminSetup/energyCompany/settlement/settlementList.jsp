<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="settlements.list">

    <div class="listContainer">
        <tags:boxContainer2 nameKey="settlements" >
    
            <c:forEach var="settlementType" items="${settlementTypes.yukonListEntries}">
                <cti:url value="edit" var="editUrl">
                    <cti:param name="ecId" value="${ecId}" />
                </cti:url>
        
                <a href="${editUrl}">
                    <spring:escapeBody htmlEscape="true" >${settlementType.entryText} </spring:escapeBody>
                </a>
                <br>
            
            </c:forEach>
        </tags:boxContainer2>
    </div>
    
</cti:standardPage>