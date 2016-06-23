<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="modules.operator.hardware.shedLoad">

<form:form id="shed-load-form" method="post" commandName="shedLoadBean" onsubmit="return false;">
  <cti:csrfToken/>
  <form:hidden path="inventoryId" value="${inventoryId}"/>
  
  <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="true">
    <tags:selectNameValue nameKey=".duration" items="${duration}" path="duration" id="duration"/>
    <tags:selectNameValue nameKey=".relayNo" items="${relayNo}" path="relayNo" id="relayNo"/>
  </tags:nameValueContainer2>
</form:form>

</cti:msgScope>

