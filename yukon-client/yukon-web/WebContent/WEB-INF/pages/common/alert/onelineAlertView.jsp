<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<cti:msgScope paths="yukon.web.alerts.types">
<div id="alertView" >
    <table id="alertTable" cellspacing="0">
           <tr>
            <td></td>
            <td align="center" style="color:#9FBBAC; font-weight: bold; font-size: 16;">Alerts:</td>
            <td align="right">
                <a href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">x</a>
            </td>
            <td></td>
        </tr>
        <tr>
            <td colspan="4">
                <hr style="color: gray;"/>
            </td>
        </tr>
       	<tr style="font-size: 0.8em; color:#9FBBAC;">
            <th>Type</th>
            <th>Message</th>
            <th>Date</th>
            <th></th>
        </tr> 

        <c:forEach var="alert" items="${alerts}">
        		<tr id="alertTableRow_${alert.id}" style="color: #9FBBAC;">
                <td width="20%"><i:inline key="${alert.type}"/></td>
                <td><i:inline key="${alert.message}"/></td>
                <td><cti:formatDate type="BOTH" value="${alert.date}"/></td>
                <td>
                    <input type="hidden" name="alertId" value="${alert.id}">
                    <img style="cursor: pointer;" title="Click to clear the alert"
                         src="<cti:url value="/WebConfig/yukon/Icons/accept.gif"/>"
                         onclick="javascript:alert_clearAlert(${alert.id});">
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</cti:msgScope>