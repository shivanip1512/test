<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:checkProperty property="DeviceActionsRole.ADD_REMOVE_POINTS">
<cti:msg var="pageTitle" key="yukon.common.device.bulk.updatePointsHome.pageTitle" />

<cti:standardPage page="updatePointsHome" module="amr">

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
        
        <%-- update points --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/addRemovePointsBulkOperation.js"/>
    
    <script type="text/javascript">

        Event.observe (window, 'load', function() {
                var sharedPoints = ${sharedPoints};
                doToggleShowSharedPoints(sharedPoints);
            }
        );

        function fieldToModifyChanged() {
        	new Ajax.Updater('fieldSpecificOptions', '/bulk/updatePoints/getSpecificOptions', {method: 'get'});
        }
        
    </script>

    <h2>${pageTitle}</h2>
    <br>
                
    <tags:bulkActionContainer key="yukon.common.device.bulk.updatePointsHome" deviceCollection="${deviceCollection}">
        
        <form id="executeUpdatePointsForm" action="<cti:url value="/bulk/updatePoints/execute" />">
        
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
                
                    <cti:msg var="sharedPointsOptionLabel" key="yukon.common.device.bulk.updatePointsHome.sharedPointsOptionLabel"/>
                    <cti:msg var="sharedPointsTrueOptionText" key="yukon.common.device.bulk.updatePointsHome.sharedPointsTrueOptionText"/>
                    <cti:msg var="sharedPointsTrueOptionTooltip" key="yukon.common.device.bulk.updatePointsHome.sharedPointsTrueOptionTooltip"/>
                    <cti:msg var="sharedPointsFalseOptionText" key="yukon.common.device.bulk.updatePointsHome.sharedPointsFalseOptionText"/>
                    <cti:msg var="sharedPointsFalseOptionTooltip" key="yukon.common.device.bulk.updatePointsHome.sharedPointsFalseOptionTooltip"/>
                    <c:set var="sharedPointsTrueSelected" value="${sharedPoints ? 'selected' : ''}"/>
                    <c:set var="sharedPointsFalseSelected" value="${sharedPoints ? '' : 'selected'}"/>
                    <tags:nameValue name="${sharedPointsOptionLabel}" nameColumnWidth="${optionNameWidth}">
                        <select name="sharedPoints" style="width:${selectInputWidth};" onchange="toggleShowSharedPoints(this);">
                            <option value="true" title="${sharedPointsTrueOptionTooltip}" ${sharedPointsTrueSelected}>${sharedPointsTrueOptionText}</option>
                            <option value="false" title="${sharedPointsFalseOptionTooltip}" ${sharedPointsFalseSelected}>${sharedPointsFalseOptionText}</option>
                        </select> <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('sharedPointsOptionInfoPopup').toggle();">
                    </tags:nameValue>
                
                    <cti:msg var="maskExistingPointsOptionLabel" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsOptionLabel"/>
                    <cti:msg var="maskExistingPointsOptionDescription" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsOptionDescription"/>
                    <cti:msg var="maskExistingPointsFalseOptionText" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsFalseOptionText"/>
                    <cti:msg var="maskExistingPointsFalseOptionTooltip" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsFalseOptionTooltip"/>
                    <cti:msg var="maskExistingPointsTrueOptionText" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsTrueOptionText"/>
                    <cti:msg var="maskExistingPointsTrueOptionTooltip" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsTrueOptionTooltip"/>
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
            
            <tags:simplePopup id="sharedPointsOptionInfoPopup" title="${sharedPointsOptionLabel}" onClose="$('sharedPointsOptionInfoPopup').toggle();">
                 <br><tags:nameValueContainer>
                    <tags:nameValue name="${sharedPointsFalseOptionText}">${sharedPointsFalseOptionTooltip}<br><br></tags:nameValue>
                    <tags:nameValue name="${sharedPointsTrueOptionText}">${sharedPointsTrueOptionTooltip}</tags:nameValue>
                 </tags:nameValueContainer><br>
            </tags:simplePopup>
            
            <tags:simplePopup id="maskExistingPointsOptionInfoPopup" title="${maskExistingPointsOptionLabel}" onClose="$('maskExistingPointsOptionInfoPopup').toggle();">
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
            
            <%-- UPDATE POINTS OPTIONS --%>
            <div id="updateOptionsDiv">
                <table>
                    <tr>
                        <td style="padding-right: 10px;">
                            <b>Select update type: </b>
                            <select id="fieldToModify" name="fieldToModify">
                                <c:forEach var="pointField" items="${pointFields}">
                                    <option value="${pointField}">${pointField.displayValue}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td>
                            <span style="font-weight: bold;">Value: </span>
                            <input type="text" name="setValue">
                        </td>
                        <td>
                            <cti:msg var="updateButtonLabel" key="yukon.common.device.bulk.updatePointsHome.updateButton"/>
                            <tags:slowInput myFormId="executeUpdatePointsForm" labelBusy="${updateButtonLabel}" label="${updateButtonLabel}" />
                            
                        </td>
                    </tr>
                </table>
            </div>
        </form>

    </tags:bulkActionContainer>
                    
</cti:standardPage>
</cti:checkProperty>