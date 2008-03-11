<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Energy Services Operations Center" module="amr">
<cti:standardMenu/>

Grid:

<!-- Add the device collection parameters to the data and edit urls so the 
     device collection can be recreated on the server side -->
<c:url var="dataUrl" value="/spring/bulk/action">
    <c:param name="action" value="gridData" />
    <c:forEach var="entry" items="${deviceCollection.collectionParameters}">
	    <c:param name="${entry.key}" value="${entry.value}" />
    </c:forEach>
</c:url>

<c:url var="editUrl" value="/spring/bulk/action">
    <c:param name="action" value="saveEdit" />
    <c:forEach var="entry" items="${deviceCollection.collectionParameters}">
	    <c:param name="${entry.key}" value="${entry.value}" />
    </c:forEach>
</c:url>

<tags:extEditorGrid metaObject="${metaObject}" dataUrl="${dataUrl}" editUrl="${editUrl}" />


</cti:standardPage>