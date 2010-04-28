<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ attribute name="formId" required="true" type="java.lang.String" description="The id attribute of the form to be submitted."%>
<%@ attribute name="key" required="true" type="java.lang.String" description="Base i18n key. Available settings: .label (required), .labelBusy (optional), .description (optional)"%>
<%@ attribute name="disableOtherButtons" required="false" type="java.lang.Boolean" description="Defaults to true. Disables all other buttons with formSubmit class on the page."%>
<%@ tag body-content="empty" %>

<cti:includeScript link="/JavaScript/slowInput2.js"/>

<cti:uniqueIdentifier var="uniqueId"/>

<c:if test="${empty pageScope.disableOtherButtons}">
	<c:set var="disableOtherButtons" value="true"/>
</c:if>

<span style="white-space:nowrap;">

	<cti:msgScope paths=".${key},components.slowInput.${key}">

		<%-- MAIN BUTTON --%>
	    <button id="slowInput2Button_${uniqueId}" type="button" class="formSubmit" onclick="slowInput2ButtonPress('${uniqueId}', '${formId}', ${disableOtherButtons})">
			<i:inline key=".label"/>
			<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
				<br>
				labelBusy = <i:inline key=".labelBusy"/>
				</br>
				description = <i:inline key=".description"/>
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

