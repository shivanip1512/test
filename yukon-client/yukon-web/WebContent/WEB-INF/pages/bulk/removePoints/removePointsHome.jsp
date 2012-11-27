<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:checkProperty property="DeviceActionsRole.ADD_REMOVE_POINTS">
<cti:msg var="pageTitle" key="yukon.common.device.bulk.removePointsHome.pageTitle" />

<cti:standardPage page="removePointsHome" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- remove points --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/addRemovePointsBulkOperation.js"/>
    
    <script type="text/javascript">

    	Event.observe (window, 'load', function() {
				var sharedPoints = ${sharedPoints};
				doToggleShowSharedPoints(sharedPoints);
			}
    	);
		
    </script>

    <h2>${pageTitle}</h2>
    <br>
                
    <tags:bulkActionContainer key="yukon.common.device.bulk.removePointsHome" deviceCollection="${deviceCollection}">
        
        <form id="executeRemovePointsForm" action="<cti:url value="/bulk/removePoints/execute" />">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- OPTIONS --%>
            <c:set var="optionNameWidth" value="150px" />
            <c:set var="selectInputWidth" value="300px" />
            <c:set var="optionsTableWidth" value="50%" />
            <table class="compactResultsTable" style="width:${optionsTableWidth}">
	            <tr><th>Options</th></tr>
	            <tr><td>
	            <tags:nameValueContainer>
	            
	            	<cti:msg var="sharedPointsOptionLabel" key="yukon.common.device.bulk.removePointsHome.sharedPointsOptionLabel"/>
            		<cti:msg var="sharedPointsFalseOptionText" key="yukon.common.device.bulk.removePointsHome.sharedPointsFalseOptionText"/>
		   			<cti:msg var="sharedPointsFalseOptionTooltip" key="yukon.common.device.bulk.removePointsHome.sharedPointsFalseOptionTooltip"/>
		   			<cti:msg var="sharedPointsTrueOptionText" key="yukon.common.device.bulk.removePointsHome.sharedPointsTrueOptionText"/>
		   			<cti:msg var="sharedPointsTrueOptionTooltip" key="yukon.common.device.bulk.removePointsHome.sharedPointsTrueOptionTooltip"/>
		   			<c:set var="sharedPointsFalseSelected" value="${sharedPoints ? '' : 'selected'}"/>
		   			<c:set var="sharedPointsTrueSelected" value="${sharedPoints ? 'selected' : ''}"/>
	            	<tags:nameValue name="${sharedPointsOptionLabel}" nameColumnWidth="${optionNameWidth}">
			            <select name="sharedPoints" style="width:${selectInputWidth};" onchange="toggleShowSharedPoints(this);">
			            	<option value="false" title="${sharedPointsFalseOptionTooltip}" ${sharedPointsFalseSelected}>${sharedPointsFalseOptionText}</option>
			            	<option value="true" title="${sharedPointsTrueOptionTooltip}" ${sharedPointsTrueSelected}>${sharedPointsTrueOptionText}</option>
			            </select> <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('sharedPointsOptionInfoPopup').toggle();">
	            	</tags:nameValue>
	            	
	            	<cti:msg var="maskMissingPointsOptionLabel" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsOptionLabel"/>
	            	<cti:msg var="maskMissingPointsOptionDescription" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsOptionDescription"/>
	            	<cti:msg var="maskMissingPointsFalseOptionText" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsFalseOptionText"/>
           			<cti:msg var="maskMissingPointsFalseOptionTooltip" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsFalseOptionTooltip"/>
           			<cti:msg var="maskMissingPointsTrueOptionText" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsTrueOptionText"/>
           			<cti:msg var="maskMissingPointsTrueOptionTooltip" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsTrueOptionTooltip"/>
           			<input type="hidden" name="maskMissingPoints" value="${maskMissingPoints}">
	            	<tags:nameValue name="${maskMissingPointsOptionLabel}">
	            		<c:choose>
           					<c:when test="${not maskMissingPoints}">
				            	<input type="submit" name="maskMissingPointsSubmitButton" value="${maskMissingPointsFalseOptionText}" title="${maskMissingPointsFalseOptionTooltip}" style="width:${selectInputWidth};"> <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('maskMissingPointsOptionInfoPopup').toggle();">
			           		</c:when>
				           	<c:otherwise>
					            	<input type="submit" name="maskMissingPointsSubmitButton" value="${maskMissingPointsTrueOptionText}" title="${maskMissingPointsTrueOptionTooltip}" style="width:${selectInputWidth};"> <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('maskMissingPointsOptionInfoPopup').toggle();">
				           	</c:otherwise>
	            		</c:choose>
	            	</tags:nameValue>
	            	
	            </tags:nameValueContainer>
	            </td></tr>
			</table>
			<br>
			
			<tags:simplePopup id="maskMissingPointsOptionInfoPopup" title="${maskMissingPointsOptionLabel}" onClose="$('maskMissingPointsOptionInfoPopup').toggle();">
			     <br>
			     ${maskMissingPointsOptionDescription}
			     <br><br>
			     <tags:nameValueContainer>
			     	<tags:nameValue name="${maskMissingPointsFalseOptionText}">${maskMissingPointsFalseOptionTooltip}<br><br></tags:nameValue>
			     	<tags:nameValue name="${maskMissingPointsTrueOptionText}">${maskMissingPointsTrueOptionTooltip}</tags:nameValue>
			     </tags:nameValueContainer><br>
			</tags:simplePopup>
			
			<tags:simplePopup id="sharedPointsOptionInfoPopup" title="${sharedPointsOptionLabel}" onClose="$('sharedPointsOptionInfoPopup').toggle();">
			     <br><tags:nameValueContainer>
			     	<tags:nameValue name="${sharedPointsFalseOptionText}">${sharedPointsFalseOptionTooltip}<br><br></tags:nameValue>
			     	<tags:nameValue name="${sharedPointsTrueOptionText}">${sharedPointsTrueOptionTooltip}</tags:nameValue>
			     </tags:nameValueContainer><br>
			</tags:simplePopup>
            
            <%-- SHARED POINTS --%>
            <div id="sharedPointsDiv" style="display:none;">
	            <tags:deviceTypePointsSection paoTypeMasks="${sharedPaoTypeMasks}" columnCount="4" />
            </div>
            
            <%-- TYPES --%>
            <div id="allPointsDiv">
            	<c:forEach var="paoTypeMasks" items="${paoTypeMasksList}">
	            	<c:set var="deviceType" value="${paoTypeMasks.paoType}"/>
            		<c:if test="${paoTypeMasks.paoType eq deviceType}">
            			<tags:deviceTypePointsSection deviceType="${deviceType}" 
	            									  deviceTypeDeviceCollection="${deviceTypeDeviceCollectionMap[deviceType]}" 
	            									  paoTypeMasks="${paoTypeMasks}" 
	            									  columnCount="4" />
            		</c:if>
	            </c:forEach>
            </div>
                    
            <%-- REMOVE POINTS BUTTON --%>
            <cti:msg var="removeButtonLabel" key="yukon.common.device.bulk.removePointsHome.removeButton"/>
            <tags:slowInput myFormId="executeRemovePointsForm" labelBusy="${removeButtonLabel}" label="${removeButtonLabel}" />

        </form>
        
    </tags:bulkActionContainer>
                    
</cti:standardPage>
</cti:checkProperty>