<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:url var="imageUrl" value="/common/images/${status.value}"/>
<c:choose>
    <c:when test="${empty param.mode or param.mode != 'VIEW'}">
        <input type="hidden" <tags:attributeHelper name="id" value="${param.id}"/> name="${status.expression}" value="${status.value}">
        <div class="edit simple-input-image" <tags:attributeHelper name="id" value="${param.id}"/>>
            <cti:url var="url" value="/admin/config/themes/imagePicker"><!-- TODO: put this somewhere common -->
                <cti:param name="selected" value="${status.value}" />
                <cti:param name="category" value="${inputType.category}" />
            </cti:url>
            <a data-image-picker="${param.id}" href="${url}" data-image-category="${inputType.category}">
                <img alt="${status.value}" src="${imageUrl}">
            </a>
            <!-- This div will be the popup -->
            <cti:msg2 key="yukon.common.imagePicker.title" var="title"/>
            <div title="${title}" class="dn" data-image-picker="${param.id}"></div>
        </div>
        
    </c:when>
    <c:otherwise>
        <div class="simple-input-image" <tags:attributeHelper name="id" value="${param.id}"/>>
            <a href="<cti:url value="/common/images/${status.value}"/>"><img alt="${status.value}" src="${imageUrl}"></a>
        </div>
    </c:otherwise>
</c:choose>