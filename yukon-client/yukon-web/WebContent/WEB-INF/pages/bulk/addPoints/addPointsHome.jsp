<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:checkProperty property="DeviceActionsRole.ADD_REMOVE_POINTS">
<cti:msg var="pageTitle" key="yukon.common.device.bulk.addPointsHome.pageTitle" />

<cti:standardPage page="addPointsHome" module="amr">

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
        
        <%-- add points --%>
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
                
    <tags:bulkActionContainer key="yukon.common.device.bulk.addPointsHome" deviceCollection="${deviceCollection}">
        
        <form id="executeAddPointsForm" action="<cti:url value="/bulk/addPoints/execute" />">
        
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
	            
	            	<cti:msg var="sharedPointsOptionLabel" key="yukon.common.device.bulk.addPointsHome.sharedPointsOptionLabel"/>
            		<cti:msg var="sharedPointsTrueOptionText" key="yukon.common.device.bulk.addPointsHome.sharedPointsTrueOptionText"/>
		   			<cti:msg var="sharedPointsTrueOptionTooltip" key="yukon.common.device.bulk.addPointsHome.sharedPointsTrueOptionTooltip"/>
		   			<cti:msg var="sharedPointsFalseOptionText" key="yukon.common.device.bulk.addPointsHome.sharedPointsFalseOptionText"/>
		   			<cti:msg var="sharedPointsFalseOptionTooltip" key="yukon.common.device.bulk.addPointsHome.sharedPointsFalseOptionTooltip"/>
		   			<c:set var="sharedPointsTrueSelected" value="${sharedPoints ? 'selected' : ''}"/>
		   			<c:set var="sharedPointsFalseSelected" value="${sharedPoints ? '' : 'selected'}"/>
	            	<tags:nameValue name="${sharedPointsOptionLabel}" nameColumnWidth="${optionNameWidth}">
                        <span class="nonwrapping">
    			            <select name="sharedPoints" style="width:${selectInputWidth};" onchange="toggleShowSharedPoints(this);">
    			            	<option value="true" title="${sharedPointsTrueOptionTooltip}" ${sharedPointsTrueSelected}>${sharedPointsTrueOptionText}</option>
    			            	<option value="false" title="${sharedPointsFalseOptionTooltip}" ${sharedPointsFalseSelected}>${sharedPointsFalseOptionText}</option>
    			            </select> <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('sharedPointsOptionInfoPopup').toggle();">
                        </span>
	            	</tags:nameValue>
	            
	            	<cti:msg var="updatePointsOptionLabel" key="yukon.common.device.bulk.addPointsHome.updatePointsOptionLabel"/>
	            	<cti:msg var="updatePointsFalseOptionText" key="yukon.common.device.bulk.addPointsHome.updatePointsFalseOptionText"/>
           			<cti:msg var="updatePointsFalseOptionTooltip" key="yukon.common.device.bulk.addPointsHome.updatePointsFalseOptionTooltip"/>
           			<cti:msg var="updatePointsTrueOptionText" key="yukon.common.device.bulk.addPointsHome.updatePointsTrueOptionText"/>
           			<cti:msg var="updatePointsTrueOptionTooltip" key="yukon.common.device.bulk.addPointsHome.updatePointsTrueOptionTooltip"/>
           			<c:set var="updatePointsFalseSelected" value="${updatePoints ? '' : 'selected'}"/>
		   			<c:set var="updatePointsTrueSelected" value="${updatePoints ? 'selected' : ''}"/>
	             	<tags:nameValue name="${updatePointsOptionLabel}" id="updatePointsSelectTr">
                        <span class="nonwrapping">
    			            <select id="updatePointsSelectEl" name="updatePoints" style="width:${selectInputWidth};">
    			            	<option value="false" title="${updatePointsFalseOptionTooltip}" ${updatePointsFalseSelected}>${updatePointsFalseOptionText}</option>
    			            	<option value="true" title="${updatePointsTrueOptionTooltip}" ${updatePointsTrueSelected}>${updatePointsTrueOptionText}</option>
    			            </select> <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('updatePointsOptionInfoPopup').toggle();">
                        </span>
	            	</tags:nameValue>
	            	
	            	<cti:msg var="maskExistingPointsOptionLabel" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsOptionLabel"/>
	            	<cti:msg var="maskExistingPointsOptionDescription" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsOptionDescription"/>
	            	<cti:msg var="maskExistingPointsFalseOptionText" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsFalseOptionText"/>
           			<cti:msg var="maskExistingPointsFalseOptionTooltip" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsFalseOptionTooltip"/>
           			<cti:msg var="maskExistingPointsTrueOptionText" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsTrueOptionText"/>
           			<cti:msg var="maskExistingPointsTrueOptionTooltip" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsTrueOptionTooltip"/>
           			<input type="hidden" name="maskExistingPoints" value="${maskExistingPoints}">
	            	<tags:nameValue name="${maskExistingPointsOptionLabel}">
	            		<c:choose>
           					<c:when test="${not maskExistingPoints}">
				            	<input type="submit" name="maskExistingPointsSubmitButton" value="${maskExistingPointsFalseOptionText}" title="${maskExistingPointsFalseOptionTooltip}" style="width:${selectInputWidth};"> <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('maskExistingPointsOptionInfoPopup').toggle();">
			           		</c:when>
				           	<c:otherwise>
					            <input type="submit" name="maskExistingPointsSubmitButton" value="${maskExistingPointsTrueOptionText}" title="${maskExistingPointsTrueOptionTooltip}" style="width:${selectInputWidth};"> <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('maskExistingPointsOptionInfoPopup').toggle();">
				           	</c:otherwise>
	            		</c:choose>
	            	</tags:nameValue>
	            
	            </tags:nameValueContainer>
	            </td></tr>
			</table>
			<br>
			
			<tags:simplePopup id="sharedPointsOptionInfoPopup" title="${sharedPointsOptionLabel}">
			     <br><tags:nameValueContainer>
			     	<tags:nameValue name="${sharedPointsFalseOptionText}">${sharedPointsFalseOptionTooltip}<br><br></tags:nameValue>
			     	<tags:nameValue name="${sharedPointsTrueOptionText}">${sharedPointsTrueOptionTooltip}</tags:nameValue>
			     </tags:nameValueContainer><br>
			</tags:simplePopup>
			
			<tags:simplePopup id="updatePointsOptionInfoPopup" title="${updatePointsOptionLabel}">
			     <br><tags:nameValueContainer>
			     	<tags:nameValue name="${updatePointsFalseOptionText}">${updatePointsFalseOptionTooltip}<br><br></tags:nameValue>
			     	<tags:nameValue name="${updatePointsTrueOptionText}">${updatePointsTrueOptionTooltip}</tags:nameValue>
			     </tags:nameValueContainer><br>
			</tags:simplePopup>
			
			<tags:simplePopup id="maskExistingPointsOptionInfoPopup" title="${maskExistingPointsOptionLabel}">
			     <br><tags:nameValueContainer>
			     	<tags:nameValue name="${maskExistingPointsFalseOptionText}">${maskExistingPointsFalseOptionTooltip}<br><br></tags:nameValue>
			     	<tags:nameValue name="${maskExistingPointsTrueOptionText}">${maskExistingPointsTrueOptionTooltip}</tags:nameValue>
			     </tags:nameValueContainer><br>
			     <div class="error">${maskExistingPointsOptionDescription}</div>
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
                    
            <%-- ADD POINTS BUTTON --%>
            <cti:msg var="addButtonLabel" key="yukon.common.device.bulk.addPointsHome.addButton"/>
            <tags:slowInput myFormId="executeAddPointsForm" labelBusy="${addButtonLabel}" label="${addButtonLabel}" />
            
        </form>
        
    </tags:bulkActionContainer>
                    
</cti:standardPage>
</cti:checkProperty>