<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">
   
   <tags:nameValue2 nameKey=".nmipaddressPort" nameColumnWidth="160px">
   
      	<select name="nmIpAddressPort" class="js-nmipaddressport" tabindex="4">
      		<c:set var="selectedOptionFound" value="false"/>
      		<c:forEach var="nmAddressPort" items="${nmIPAddressPorts}">
      			<c:set var="selected" value=""/>
      			<c:set var="ipAddress" value="${nmAddressPort.nmIpAddress}"/>
      			<c:set var="port" value="${nmAddressPort.nmPort}"/>
      			<c:if test="${ipAddress == settings.nmIpAddress && port == settings.nmPort}">
      				<c:set var="selected" value="selected"/>
      				<c:set var="selectedOptionFound" value="true"/>
      			</c:if>
      			<option ${selected} data-nmipaddress="${ipAddress}" data-nmport="${port}">${ipAddress}:${port}</option>
      		</c:forEach>
      		<c:set var="newSelected" value="${selectedOptionFound ? '' : 'selected'}"/>
      		<option ${newSelected} data-nmipaddress="NEW"><i:inline key="yukon.common.new"/></option>
      	</select>
      	<c:set var="newIPAddressPortClass" value="${selectedOptionFound ? 'dn' : ''}"/>
      	<span class="js-new-nmipaddressport ${newIPAddressPortClass}">
      	    <cti:msg2 var="nmIPAddressText" key=".nmipaddress"/>
      	    <spring:bind path="nmIpAddress">
      	        <c:set var="errorClass" value="${status.error ? 'error' : ''}"/>
      			<form:input path="nmIpAddress" placeholder="${nmIPAddressText}" maxlength="15" size="15" 
      				tabindex="4" cssClass="MR10 js-nmipaddress ${errorClass}"/>
      		</spring:bind>
      	    <cti:msg2 var="nmPortText" key=".nmport"/>
      	    <spring:bind path="nmPort">
      	        <c:set var="errorClass" value="${status.error ? 'error' : ''}"/>
      	    	<form:input path="nmPort" placeholder="${nmPortText}" maxlength="5" size="5" 
      	    		tabindex="5" cssClass="js-nmport ${errorClass}"/>
      	    </spring:bind>
      	</span>
      	<cti:msg2 var="nmIPAddressPortHelpTextTitle" key=".nmipaddressPort"/>
          <tags:helpInfoPopup title="${nmIPAddressPortHelpTextTitle}" classes="vam ML0">
              <cti:msg2 key=".nmipaddressPortHelpText"/>
          </tags:helpInfoPopup>
      	<span class="js-new-nmipaddressport ${newIPAddressPortClass}">
             	<spring:bind path="nmIpAddress">
                  <c:if test="${status.error}"><br><form:errors path="nmIpAddress" cssClass="error" /></c:if>
              </spring:bind>
              <spring:bind path="nmPort">
                  <c:if test="${status.error}"><br><form:errors path="nmPort" cssClass="error" /></c:if>
              </spring:bind>
      	</span>
      </tags:nameValue2>
      
</cti:msgScope>