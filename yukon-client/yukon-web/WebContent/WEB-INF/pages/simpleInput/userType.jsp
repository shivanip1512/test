<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
<input <tags:attributeHelper name="id" value="${param.id}"/> type="hidden" name="${status.expression}" value="${status.value}" class="${inputClass}">
<tags:pickerDialog
    id="userPicker"
    type="userPicker"
    destinationFieldId="${param.id}"
    linkType="selection"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="true"
    selectionProperty="userName"/>