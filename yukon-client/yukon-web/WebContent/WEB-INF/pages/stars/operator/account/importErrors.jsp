<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.accountImport">

    <tags:boxContainer2 nameKey="importErrorsContainer" hideEnabled="true" showInitially="true">
        <table class="compactResultsTable">
            <tr>
                <th nowrap="nowrap">Line #</th>
                <th>Error Message</th>
                <th>Import Account</th>
            </tr>
            <c:forEach var="importError" items="${importErrors}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>${importError.lineNumber}</td> 
                    <td>${importError.errorMessage}</td> 
                    <td>${importError.importAccount}</td>
                </tr>
            </c:forEach>
        </table>
    </tags:boxContainer2>
    
</cti:msgScope>