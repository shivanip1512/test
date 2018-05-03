<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.tools.bulk.addPointsHome">

    <cti:checkRolesAndProperties value="ADD_REMOVE_POINTS">
    
        <cti:msgScope paths="yukon.common.device.bulk.addPointsHome"> 
    
            <tags:bulkActionContainer key="yukon.common.device.bulk.addPointsHome" deviceCollection="${deviceCollection}">
            
                <%-- ERROR MSG --%>
                <c:if test="${not empty errorMsg}">
                    <div class="user-message error stacked">${errorMsg}</div>
                </c:if>
            
                <form action="<cti:url value="/bulk/addPoints/execute" />" method="POST">
                    <c:forEach var="pointTypeOffset" items="${preselectedPointIdentifiers}">
                        <input type="hidden" name="preselectedPointIdentifiers" value="${pointTypeOffset}"/>
                    </c:forEach>
                    <cti:csrfToken/>
                    <%-- DEVICE COLLECTION --%>
                    <cti:deviceCollection deviceCollection="${deviceCollection}" />
                    
                    <%-- OPTIONS --%>
                    <c:set var="selectInputWidth" value="300px" />
                    <tags:sectionContainer2 nameKey="options">
                        <tags:nameValueContainer2>
                            <cti:msg2 var="sharedPointsOptionLabel" key=".sharedPointsOptionLabel"/>
                            <cti:msg2 var="sharedPointsTrueOptionText" key=".sharedPointsTrueOptionText"/>
                            <cti:msg2 var="sharedPointsTrueOptionTooltip" key=".sharedPointsTrueOptionTooltip"/>
                            <cti:msg2 var="sharedPointsFalseOptionText" key=".sharedPointsFalseOptionText"/>
                            <cti:msg2 var="sharedPointsFalseOptionTooltip" key=".sharedPointsFalseOptionTooltip"/>
                            <c:set var="sharedPointsTrueSelected" value="${sharedPoints ? 'selected' : ''}"/>
                            <c:set var="sharedPointsFalseSelected" value="${sharedPoints ? '' : 'selected'}"/>
                            <tags:nameValue2 nameKey=".sharedPointsOptionLabel">
                                <select id="sharedPoints" name="sharedPoints" style="width:${selectInputWidth};" class="fl">
                                    <option value="true" title="${sharedPointsTrueOptionTooltip}" ${sharedPointsTrueSelected}>${sharedPointsTrueOptionText}</option>
                                    <option value="false" title="${sharedPointsFalseOptionTooltip}" ${sharedPointsFalseSelected}>${sharedPointsFalseOptionText}</option>
                                </select> 
                                <cti:icon icon="icon-help" id="shared_help" classes="cp"/>
                            </tags:nameValue2>
                            <cti:msg2 var="updatePointsOptionLabel" key=".updatePointsOptionLabel"/>
                            <cti:msg2 var="updatePointsFalseOptionText" key=".updatePointsFalseOptionText"/>
                            <cti:msg2 var="updatePointsFalseOptionTooltip" key=".updatePointsFalseOptionTooltip"/>
                            <cti:msg2 var="updatePointsTrueOptionText" key=".updatePointsTrueOptionText"/>
                            <cti:msg2 var="updatePointsTrueOptionTooltip" key=".updatePointsTrueOptionTooltip"/>
                            <c:set var="updatePointsFalseSelected" value="${updatePoints ? '' : 'selected'}"/>
                            <c:set var="updatePointsTrueSelected" value="${updatePoints ? 'selected' : ''}"/>
                            <tags:nameValue2 nameKey=".updatePointsOptionLabel" id="updatePointsSelectTr">
                                <select name="updatePoints" style="width:${selectInputWidth};" class="fl">
                                    <option value="false" title="${updatePointsFalseOptionTooltip}" ${updatePointsFalseSelected}>${updatePointsFalseOptionText}</option>
                                    <option value="true" title="${updatePointsTrueOptionTooltip}" ${updatePointsTrueSelected}>${updatePointsTrueOptionText}</option>
                                </select>
                               <cti:icon icon="icon-help" id="update_help" classes="cp"/>
                            </tags:nameValue2>
                            
                            <cti:msg2 var="maskExistingPointsOptionLabel" key=".maskExistingPointsOptionLabel"/>
                            <cti:msg2 var="maskExistingPointsOptionDescription" key=".maskExistingPointsOptionDescription"/>
                            <cti:msg2 var="maskExistingPointsFalseOptionText" key=".maskExistingPointsFalseOptionText"/>
                            <cti:msg2 var="maskExistingPointsFalseOptionTooltip" key=".maskExistingPointsFalseOptionTooltip"/>
                            <cti:msg2 var="maskExistingPointsTrueOptionText" key=".maskExistingPointsTrueOptionText"/>
                            <cti:msg2 var="maskExistingPointsTrueOptionTooltip" key=".maskExistingPointsTrueOptionTooltip"/>
                            <input type="hidden" name="maskExistingPoints" value="${maskExistingPoints}">
                            <tags:nameValue2 nameKey=".maskExistingPointsOptionLabel" valueClass="PL0">
                                <c:choose>
                                    <c:when test="${not maskExistingPoints}">
                                        <cti:button type="submit"
                                            name="maskExistingPointsSubmitButton"
                                            label="${maskExistingPointsFalseOptionText}"
                                            value="${maskExistingPointsFalseOptionText}"
                                            title="${maskExistingPointsFalseOptionTooltip}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:button type="submit" 
                                            name="maskExistingPointsSubmitButton" 
                                            label="${maskExistingPointsTrueOptionText}" 
                                            value="${maskExistingPointsTrueOptionText}" 
                                            title="${maskExistingPointsTrueOptionTooltip}"/>
                                    </c:otherwise>
                                </c:choose>
                                <cti:icon icon="icon-help" id="existing_help" classes="cp"/>
                            </tags:nameValue2>
                        
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                    
                    <tags:simplePopup id="sharedPointsOptionInfoPopup" title="${sharedPointsOptionLabel}" on="#shared_help" options="{width: 600}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".sharedPointsFalseOptionText">${sharedPointsFalseOptionTooltip}</tags:nameValue2>
                             <tags:nameValue2 nameKey=".sharedPointsTrueOptionText">${sharedPointsTrueOptionTooltip}</tags:nameValue2>
                         </tags:nameValueContainer2>
                    </tags:simplePopup>
                    
                    <tags:simplePopup id="updatePointsOptionInfoPopup" title="${updatePointsOptionLabel}" on="#update_help" options="{width: 600}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".updatePointsFalseOptionText">${updatePointsFalseOptionTooltip}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".updatePointsTrueOptionText">${updatePointsTrueOptionTooltip}</tags:nameValue2>
                        </tags:nameValueContainer2>
                    </tags:simplePopup>
                    
                    <tags:simplePopup id="maskExistingPointsOptionInfoPopup" title="${maskExistingPointsOptionLabel}" on="#existing_help" options="{width: 600}">
                        <div class="warning stacked"><i:inline key="yukon.common.warningMessage" arguments="${maskExistingPointsOptionDescription}"/></div>
                         <tags:nameValueContainer2>
                             <tags:nameValue2 nameKey=".maskExistingPointsFalseOptionText">${maskExistingPointsFalseOptionTooltip}</tags:nameValue2>
                             <tags:nameValue2 nameKey=".maskExistingPointsTrueOptionText">${maskExistingPointsTrueOptionTooltip}</tags:nameValue2>
                         </tags:nameValueContainer2>
                    </tags:simplePopup>
                    
                    <%-- SHARED POINTS --%>
                    <c:set var="sharedPointsClass" value="${sharedPoints ? '' : 'dn'}"/>
                    <div id="sharedPointsDiv" class="${sharedPointsClass}">
                        <tags:deviceTypePointsSection paoTypeMasks="${sharedPaoTypeMasks}" columnCount="4" />
                    </div>
                    
                    <%-- TYPES --%>
                    <c:set var="allPointsClass" value="${sharedPoints ? 'dn' : ''}"/>
                    <div id="allPointsDiv" class="${allPointsClass}">
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
                    <cti:button nameKey="add" classes="action primary js-action-submit" busy="true"/>
                    
                </form>
        
            </tags:bulkActionContainer>
        
        </cti:msgScope>
                        
    </cti:checkRolesAndProperties>
        
</cti:msgScope>