<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.accountImport">

    <tags:boxContainer2 nameKey="importErrorsContainer" hideEnabled="true" showInitially="true">
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th nowrap="nowrap"><i:inline key="yukon.web.modules.operator.accountImport.lineNo"/></th>
                    <th><i:inline key="yukon.web.modules.operator.accountImport.errorMessage"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="importError" items="${importErrors}">
                    <tr>
                        <td>${importError.lineNumber}</td> 
                        <td>
                            <div style="border-bottom: 1px dashed #ccc"><strong><i:inline key="yukon.web.modules.operator.accountImport.errorMessage"/>:</strong>${importError.errorMessage}</div> 
                            <div><strong><i:inline key="yukon.web.modules.operator.accountImport.importAccount"/>:</strong>${fn:escapeXml(importError.importAccount)}</div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </tags:boxContainer2>
    
</cti:msgScope>