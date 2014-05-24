<%@ tag trimDirectiveWhitespaces="true" dynamic-attributes="attrs"
    description="A light weight tag meant to build sortable links for table headers.  
                This tag is intended to have javascript do the heavy lifting so there is no url handling here. 
                Dynamic attributes are supported to provide easier behavior wiring." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="classes" description="CSS class names applied to the anchor element." %>
<%@ attribute name="column" required="true" type="com.cannontech.web.dev.StyleGuideController.SortableColumn" %>

<c:if test="${column.sortable}">
    <c:set var="classes" value="${classes} sortable"/>
</c:if>
<c:if test="${column.active}">
    <c:set var="classes" value="${classes} ${column.dir}"/>
</c:if>
<th>
<a class="${pageScope.classes}" <c:forEach items="${pageScope.attrs}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
${fn:escapeXml(column.text)}<cti:icon icon="icon-blank"/>
</a>
</th>