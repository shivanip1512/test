<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="dataStreamingSupport">
    
    <table class="matrix">
        <tr>
            <th style="width: 130px;" class="firstCol"><cti:msg2 key=".devices" /></th>
            <c:forEach var="atrId" items="${attributes}">
                <th class="rtate30"><div><span>${atrId.getDescription() }</span></div></th>
            </c:forEach>
        </tr>
        <c:forEach var="device" items="${devices}">
            <tr>
            <td>${device.getDbString()}</td>
            <c:forEach var="attribute" items="${attributes}">
                <td>${dataStreamingDevices.get(device).contains(attribute)?"X":""}</td>
            </c:forEach>
            </tr>
        </c:forEach>
    </table>
    
</cti:standardPage>
