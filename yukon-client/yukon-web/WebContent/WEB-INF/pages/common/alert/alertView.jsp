<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<div id="alertView" >
    <table id="alertTable" cellspacing="0">
      <thead>
        <tr style="font-size: 0.8em;">
            <th><cti:msg key="yukon.web.alerts.table.type"/></th>
            <th><cti:msg key="yukon.web.alerts.table.message"/></th>
            <th><cti:msg key="yukon.web.alerts.table.date"/></th>
            <th></th>
        </tr> 
      </thead>
      <tbody>
        <c:forEach var="alert" items="${alerts}">
            <tr id="alertTableRow_${alert.id}">
                <td width="20%"><cti:msg key="${alert.type}"/></td>
                <td><cti:formatTemplate message="${alert.message}" /></td>
                <td><cti:formatDate type="BOTH" value="${alert.date}"/></td>
                <td>
                    <input type="hidden" name="alertId" value="${alert.id}">
                    <img style="cursor: pointer;" title="<cti:msg key="yukon.web.alerts.table.clear.tooltip"/>"
                         src="<c:url value="/WebConfig/yukon/Icons/accept.gif"/>"
                         onclick="javascript:alert_clearAlert(${alert.id});">
                </td>
            </tr>
        </c:forEach>
      </tbody>
    </table>
</div>
