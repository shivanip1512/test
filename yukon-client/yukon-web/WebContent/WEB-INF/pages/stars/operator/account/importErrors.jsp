<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="modules.operator.accountImport">

    <tags:boxContainer2 nameKey="importErrorsContainer" hideEnabled="true" showInitially="true">
        <table class="compact-results-table">
            <tr>
                <th nowrap="nowrap"><i:inline key="yukon.web.modules.operator.accountImport.lineNo"/></th>
                <th><i:inline key="yukon.web.modules.operator.accountImport.errorMessage"/></th>
                <th><i:inline key="yukon.web.modules.operator.accountImport.importAccount"/></th>
            </tr>
            <c:forEach var="importError" items="${importErrors}">
                <tr>
                    <td>${importError.lineNumber}</td> 
                    <td>${importError.errorMessage}</td> 
                    <td><spring:escapeBody htmlEscape="true">${importError.importAccount}</spring:escapeBody></td>
                </tr>
            </c:forEach>
        </table>
    </tags:boxContainer2>
    
</cti:msgScope>