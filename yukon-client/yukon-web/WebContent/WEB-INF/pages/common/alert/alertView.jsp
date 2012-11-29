<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<div id="alertView" >
    <table id="alertTable" class="compactResultsTable">
      <thead>
        <tr class="wsnw">
            <th><cti:msg key="yukon.web.alerts.table.type"/></th>
            <th><cti:msg key="yukon.web.alerts.table.message"/></th>
            <th><cti:msg key="yukon.web.alerts.table.date"/></th>
            <th></th>
        </tr> 
      </thead>
      <tfoot></tfoot>
      <tbody>
        <c:forEach var="alert" items="${alerts}">
            <tr id="alertTableRow_${alert.id}" class="wsnw">
                <td width="20%"><cti:msg key="${alert.type}"/></td>
                <td id="msg"><cti:formatTemplate message="${alert.message}" /></td>
                <td><cti:formatDate type="BOTH" value="${alert.date}"/></td>
                <td>
                    <input type="hidden" name="alertId" value="${alert.id}">
                    <img style="cursor: pointer;" title="<cti:msg key="yukon.web.alerts.table.clear.tooltip"/>"
                         src="<cti:url value="/WebConfig/yukon/Icons/accept.gif"/>"
                         onclick="javascript:alert_clearAlert(${alert.id});">
                </td>
            </tr>
        </c:forEach>
      </tbody>
    </table>
</div>
