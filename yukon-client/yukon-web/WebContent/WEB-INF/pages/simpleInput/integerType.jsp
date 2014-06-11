<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
<cti:displayForPageEditModes modes="VIEW">${status.value}<input type="hidden" value="${status.value}"/></cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE,EDIT">
    <input <tags:attributeHelper name="id" value="${param.id}"/> type="text" maxlength="30" size="15" id="${status.expression}" name="${status.expression}" value="${status.value}"  class="${inputClass}">
</cti:displayForPageEditModes>