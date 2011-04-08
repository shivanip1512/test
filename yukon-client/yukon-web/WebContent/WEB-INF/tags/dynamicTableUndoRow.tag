<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ tag body-content="empty" description="Use inside a table inside :dynamicTable." %>
<%@ attribute name="columnSpan" type="java.lang.Integer" required="true" %>
<%@ attribute name="nameKey" required="true"
    description="Base i18n key. Required keys: .willBeRemoved, .undo"%>

<cti:includeScript link="/JavaScript/dynamicTable.js"/>
<cti:msgScope paths=".${nameKey},">
    <cti:msg2 var="willBeRemovedMsg" key=".willBeRemoved"/>
    <cti:msg2 var="undoMsg" key=".undo"/>
</cti:msgScope>

<tr class="undoRow" style="display: none;">
    <!-- TODO:  localize -->
    <td colspan="${columnSpan - 1}">${willBeRemovedMsg}</td>
    <!-- TODO:  localize; probably use button with image (fix row heights before doing that) -->
    <td class="actions"><span class="undoRemoveBtn">${undoMsg}</span></td>
</tr>
