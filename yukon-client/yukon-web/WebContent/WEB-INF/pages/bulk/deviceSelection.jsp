<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="bulk.deviceSelection">
  <c:if test="${not empty errorMsg}">
    <div class="error">${errorMsg}</div>
  </c:if>

  <cti:msg var="headerTitle" key="yukon.common.device.bulk.deviceSelection.header" />
  <tags:sectionContainer title="${headerTitle}" id="updateConfirmContainer">

    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
    <tags:deviceSelection action="/bulk/deviceSelectionGetDevices" groupDataJson="${groupDataJson}" pickerType="devicePicker" />

  </tags:sectionContainer>

</cti:standardPage>