<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<table><tr>
<td><cti:paoDetailUrl yukonPao="${paoPointIdentifier.paoIdentifier}">${paoPointIdentifier.paoIdentifier}</cti:paoDetailUrl></td>
<td>${paoPointIdentifier.pointIdentifier}</td>
<td>${pointName}</td>
<td>${pointId}<input type="hidden" name="pointId" value="${pointId}"></td>
<td><input name="date" value="<cti:formatDate type="DATE" value="${pointValue.pointDataTimeStamp}"/>" size="10"> <button class="dateTimeClear">Now</button></td>
<td><input name="time" value="<cti:formatDate type="TIME" value="${pointValue.pointDataTimeStamp}"/>" size="10"> <button class="dateTimeClear">Now</button></td>
<td><tags:simpleSelect items="${qualities}" name="quality" selectedItem="${pointValue.pointQuality}"/><td>
<c:if test="${status}">
<tags:simpleSelect items="${states}" name="value" itemLabel="stateText" itemValue="stateRawState" selectedValue="${statePointValue}"/>
</c:if>
<c:if test="${not status}">
  <input name="value" value="${decimalPointValue}" size="10">
  <button class="valueMinus">-</button>
  <button class="valuePlus">+</button>
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
<td><button class="sendData">Send</button><button class="removeRow">Remove</button></td>
</tr></table>