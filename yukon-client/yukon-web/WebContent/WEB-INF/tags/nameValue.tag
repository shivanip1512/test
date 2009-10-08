<%@ attribute name="name" required="true" %>
<%@ attribute name="rowHighlight" required="false" %>
<%@ attribute name="nameColumnWidth" required="false" %>
<%@ attribute name="id" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:uniqueIdentifier var="trId" prefix="nameValueTr_"/>
<c:if test="${not empty pageScope.id}">
    <c:set var="trId" value="${pageScope.id}" />
</c:if>

<c:choose>
  <c:when test="${empty nameValueNest}">
      <c:set var="nameValueNest" value="0" scope="request"/>
  </c:when>
  <c:otherwise>
      <c:set var="nameValueNest" value="${nameValueNest + 1}" scope="request"/>
  </c:otherwise>
</c:choose>

<c:set var="indentStyle" value="indent${nameValueNest}" scope="request"/>

<c:choose>
	<c:when test="${nameValueContainter}">
        
        <c:if test="${pageScope.nameValueNest > -1}">
            </td></tr>
        </c:if>
  
  
		<c:choose>
			<c:when test="${pageScope.rowHighlight}">
				<tr class="highlighted ${indentStyle}" id="${trId}">
			</c:when>
			<c:when test="${altRowOn && altRow}">
				<tr class="altRow ${indentStyle}" id="${trId}">
			</c:when>
			<c:otherwise>
				<tr class="${indentStyle}" id="${trId}">
			</c:otherwise>
		</c:choose>
		<c:set var="altRow" value="${!altRow}" scope="request"/>

			<td class="name" <c:if test="${not empty pageScope.nameColumnWidth}">style="width:${pageScope.nameColumnWidth};"</c:if>>${name}${(not empty name)? ':':'&nbsp;'}</td>
			<td class="value"><jsp:doBody/></td>
		</tr>
	</c:when>
	<c:otherwise>
		<div class="errorRed" style="font-weight: bold">
			ERROR: The &lt;nameValue&gt; tag must be enclosed in a &lt;nameValueContainer&gt; tag
		</div>
	</c:otherwise>
</c:choose>

<c:if test="${nameValueNest > -1}">
    <c:set var="nameValueNest" value="${nameValueNest - 1}" scope="request"/>
</c:if>