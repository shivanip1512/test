<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table>
    <tr>
        <td><cti:paoDetailUrl yukonPao="${paoPointIdentifier.paoIdentifier}">${paoPointIdentifier.paoIdentifier}</cti:paoDetailUrl></td>
        <td>${paoPointIdentifier.pointIdentifier}</td>
        <td>${fn:escapeXml(pointName)}</td>
        <td>${pointId}<input type="hidden" name="pointId" value="${pointId}"></td>
        <td>
            <input name="date" value="<cti:formatDate type="DATE" value="${pointValue.pointDataTimeStamp}"/>" size="10" type="text">
            <cti:button classes="dateTimeClear" label="Now"/>
        </td>
        <td>
            <input name="time" value="<cti:formatDate type="TIME" value="${pointValue.pointDataTimeStamp}"/>" size="10" type="text">
            <cti:button classes="dateTimeClear" label="Now"/>
        </td>
        <td><tags:simpleSelect items="${qualities}" name="quality" selectedItem="${pointValue.pointQuality}"/></td>
        <td>
            <c:if test="${status}">
                <tags:simpleSelect items="${states}" name="value" itemLabel="stateText" itemValue="stateRawState" selectedValue="${statePointValue}"/>
            </c:if>
            <c:if test="${not status}">
                <input name="value" value="${decimalPointValue}" size="10" type="number">
                <cti:button classes="valueMinus" label="-"/>
                <cti:button classes="valuePlus" label="+"/>
            </c:if>
        </td>
        <c:choose>
            <c:when test="${not forceArchive}">
                <td><input type="checkbox" name="forceArchive" onclick="forceArchiveChecked(this, ${pointId})"></td>
            </c:when>
            <c:otherwise>
                <td><input type="checkbox" name="forceArchive" checked="yup" onclick="forceArchiveChecked(this, ${pointId})"></td>
            </c:otherwise>
        </c:choose>
        <td>
            <cti:button classes="sendData" label="Send"/>
            <cti:button classes="removeRow" label="Remove"/>
        </td>
    </tr>
</table>