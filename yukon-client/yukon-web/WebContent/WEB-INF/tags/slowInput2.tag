<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ attribute name="id" required="false" type="java.lang.String" description="The id of the main button."%>
<%@ attribute name="formId" required="true" type="java.lang.String" description="The id attribute of the form to be submitted."%>
<%@ attribute name="key" required="true" type="java.lang.String" description="Base i18n key. Available settings: .label (required), .labelBusy (optional), .description (optional)"%>
<%@ attribute name="disableOtherButtons" required="false" type="java.lang.Boolean" description="Defaults to true. Disables all other buttons with formSubmit class on the page."%>
<%@ attribute name="onsubmit" required="false" type="java.lang.String" description="Reference to a JavaScript function to call before form submission. Function MUST have return value of true for the form to submit, otherwise the button will revert and the form will remain unsubmitted."%>
<%@ attribute name="type" %>
<%@ tag body-content="empty" %>

<cti:includeScript link="/JavaScript/slowInput2.js"/>

<cti:uniqueIdentifier var="uniqueId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="uniqueId" value="${pageScope.id}"/>
</c:if>

<c:if test="${empty pageScope.disableOtherButtons}">
	<c:set var="disableOtherButtons" value="true"/>
</c:if>

<c:if test="${empty pageScope.onsubmit}">
	<c:set var="onsubmit" value="null"/>
</c:if>

<c:if test="${empty pageScope.type}">
	<c:set var="type" value="button"/>
</c:if>

<span style="white-space:nowrap;">

	<cti:msgScope paths=".${key},components.slowInput.${key}">

		<%-- MAIN BUTTON --%>
	    <button id="slowInput2Button_${uniqueId}" type="${pageScope.type}" class="formSubmit" onclick="slowInput2ButtonPress('${uniqueId}', '${formId}', ${disableOtherButtons}, ${pageScope.onsubmit})">
			<cti:checkGlobalRolesAndProperties value="!I18N_DESIGN_MODE">
				<cti:msg2 key=".label"/>
			</cti:checkGlobalRolesAndProperties>
			<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
				<span class="i18nInline">
					<cti:msg2 key=".label" debug="true" fallback="true"/>
					<span class="i18nInlineDebug">
						label = 
						<cti:msg2 key=".label" debug="true" fallback="true"/>
						<c:forEach items="${msg2TagDebugMap}" var="entry">${entry.key}=${entry.value}<br></c:forEach>
						<br>
						labelBusy = 
						<cti:msg2 key=".labelBusy" debug="true" fallback="true"/>
						<c:forEach items="${msg2TagDebugMap}" var="entry">${entry.key}=${entry.value}<br></c:forEach>
						<br>
						description = 
						<cti:msg2 key=".description" debug="true" fallback="true"/>
						<c:forEach items="${msg2TagDebugMap}" var="entry">${entry.key}=${entry.value}<br></c:forEach>
					</span>
				</span>
			</cti:checkGlobalRolesAndProperties>
	    </button>
	    
	    <%-- BUSY BUTTON --%>
	    <button id="slowInput2ButtonBusy_${uniqueId}" type="button" class="formSubmit" style="display:none;" disabled>
	    	<cti:msg2 var="labelBusy" key=".labelBusy" blankIfMissing="true"/>
	    	<c:choose>
	    		<c:when test="${not empty labelBusy}">
	    			<i:inline key=".labelBusy"/>
	    		</c:when>
	    		<c:otherwise>
	    			<i:inline key=".label"/>
	    		</c:otherwise>
	    	</c:choose>
	    </button>
	    
	    <%-- BUSY SPINNER ICON --%>
	    <img id="slowInput2SpinnerImg_${uniqueId}" src="/WebConfig/yukon/Icons/indicator_arrows.gif" style="display:none;">
	    
	    <%-- BUSY DESCRIPTION TEXT --%>
	   	<span id="slowInput2DescriptionSpan_${uniqueId}" style="display:none;"><cti:msg2 key=".description" blankIfMissing="true"/></span>
    
    </cti:msgScope>
    
</span>

