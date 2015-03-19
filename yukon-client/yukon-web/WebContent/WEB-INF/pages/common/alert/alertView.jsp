<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.alerts.table">
<div id="alertView" >
    <table id="alert-table" class="compact-results-table with-form-controls has-alerts separated no-stripes">
      <thead></thead>
      <tfoot></tfoot>
      <tbody>
        <c:forEach var="alert" items="${alerts}">
            <tr data-alert-id="${alert.id}">
                <cti:msg2 key="${alert.type}" var="iconTitle"/>
                <td><cti:icon icon="${alert.icon}" title="${iconTitle}" classes="push-down-4"/></td>
                <td class="full-width"><cti:formatTemplate message="${alert.message}" /></td>
                <td class="wsnw">
                    <cti:formatDate type="BOTH" value="${alert.date}"/>
                </td>
                <td>
                    <cti:msg2 key=".clear.tooltip" var="tt"/>
                    <cti:button classes="js-clear-yukon-alert M0" renderMode="buttonImage" icon="icon-tick" title="${tt}"/>
                </td>
            </tr>
        </c:forEach>
      </tbody>
    </table>
</div>
</cti:msgScope>