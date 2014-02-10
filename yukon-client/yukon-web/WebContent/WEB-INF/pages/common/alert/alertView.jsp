<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div id="alertView" >
    <table id="alertTable" class="compact-results-table">
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
                    <cti:msg key="yukon.web.alerts.table.clear.tooltip" var="tt"/>
                    <cti:icon icon="icon-accept" title="${tt}" onclick="yukon.Alerts.clearAlert(${alert.id});" classes="cp"/>
                </td>
            </tr>
        </c:forEach>
      </tbody>
    </table>
</div>