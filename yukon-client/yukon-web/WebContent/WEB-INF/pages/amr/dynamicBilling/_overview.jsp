<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.amr.billing.HOME">
<form method="get" id="formatForm" name="formatForm" action="">

    <h3><cti:msg2 key=".availableFormats"/></h3>
    <div>
        <div>
            <select style="width:275px;" id="availableFormat" size="10" 
             name="availableFormat">
        
                <c:forEach var="elem" items="${allRows}">
                    <option value="${elem.formatId}" isSystem="${elem.isSystem}"> 
                        <c:out value="${elem.name}" /> 
                    </option>
                </c:forEach>
        
             </select>
        </div>
        <div class="page-action-area">
            <cti:button id="btnCreateFormat" nameKey="create"/>
            <cti:button id="btnEditFormat" nameKey="edit" disabled="true"/>
            <cti:button id="btnCopyFormat" nameKey="copy" disabled="true"/>
            <cti:button id="btnDeleteFormat" nameKey="delete" disabled="true"/>
        </div>
    </div>
</form>

</cti:msgScope>