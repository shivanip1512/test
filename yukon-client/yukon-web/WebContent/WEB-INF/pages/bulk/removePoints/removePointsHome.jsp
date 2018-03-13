<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:checkRolesAndProperties value="ADD_REMOVE_POINTS">
    <cti:standardPage module="tools" page="bulk.removePointsHome">
    
        <cti:msgScope paths="yukon.common.device.bulk.removePointsHome"> 
    
            <tags:bulkActionContainer key="yukon.common.device.bulk.removePointsHome" deviceCollection="${deviceCollection}">
                
                <form action="<cti:url value="/bulk/removePoints/execute" />" method="POST">
                    <cti:csrfToken/>
                    <%-- DEVICE COLLECTION --%>
                    <cti:deviceCollection deviceCollection="${deviceCollection}" />
                    
                    <%-- OPTIONS --%>
                    <c:set var="selectInputWidth" value="300px" />
                    <tags:sectionContainer2 nameKey="options">
                        <tags:nameValueContainer2>
                        
                            <cti:msg2 var="sharedPointsOptionLabel" key=".sharedPointsOptionLabel"/>
                            <cti:msg2 var="sharedPointsFalseOptionText" key=".sharedPointsFalseOptionText"/>
                            <cti:msg2 var="sharedPointsFalseOptionTooltip" key=".sharedPointsFalseOptionTooltip"/>
                            <cti:msg2 var="sharedPointsTrueOptionText" key=".sharedPointsTrueOptionText"/>
                            <cti:msg2 var="sharedPointsTrueOptionTooltip" key=".sharedPointsTrueOptionTooltip"/>
                            <c:set var="sharedPointsFalseSelected" value="${sharedPoints ? '' : 'selected'}"/>
                            <c:set var="sharedPointsTrueSelected" value="${sharedPoints ? 'selected' : ''}"/>
                            <tags:nameValue2 nameKey=".sharedPointsOptionLabel">
                                <select id="sharedPoints" name="sharedPoints" style="width:${selectInputWidth};" class="fl">
                                    <option value="false" title="${sharedPointsFalseOptionTooltip}" ${sharedPointsFalseSelected}>${sharedPointsFalseOptionText}</option>
                                    <option value="true" title="${sharedPointsTrueOptionTooltip}" ${sharedPointsTrueSelected}>${sharedPointsTrueOptionText}</option>
                                </select><cti:icon icon="icon-help" id="shared_help" classes="cp"/>
                            </tags:nameValue2>
                            
                            <cti:msg2 var="maskMissingPointsOptionLabel" key=".maskMissingPointsOptionLabel"/>
                            <cti:msg2 var="maskMissingPointsOptionDescription" key=".maskMissingPointsOptionDescription"/>
                            <cti:msg2 var="maskMissingPointsFalseOptionText" key=".maskMissingPointsFalseOptionText"/>
                            <cti:msg2 var="maskMissingPointsFalseOptionTooltip" key=".maskMissingPointsFalseOptionTooltip"/>
                            <cti:msg2 var="maskMissingPointsTrueOptionText" key=".maskMissingPointsTrueOptionText"/>
                            <cti:msg2 var="maskMissingPointsTrueOptionTooltip" key=".maskMissingPointsTrueOptionTooltip"/>
                            <input type="hidden" name="maskMissingPoints" value="${maskMissingPoints}">
                            <tags:nameValue2 nameKey=".maskMissingPointsOptionLabel" valueClass="PL0">
                                <c:choose>
                                    <c:when test="${not maskMissingPoints}">
                                        <cti:button type="submit" 
                                            name="maskMissingPointsSubmitButton" 
                                            label="${maskMissingPointsFalseOptionText}" 
                                            value="${maskMissingPointsFalseOptionText}" 
                                            title="${maskMissingPointsFalseOptionTooltip}"/><cti:icon icon="icon-help" id="missing_help" classes="cp"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:button type="submit" 
                                            name="maskMissingPointsSubmitButton" 
                                            label="${maskMissingPointsTrueOptionText}" 
                                            value="${maskMissingPointsTrueOptionText}" 
                                            title="${maskMissingPointsTrueOptionTooltip}"/><cti:icon icon="icon-help" id="missing_help" classes="cp"/>
                                    </c:otherwise>
                                </c:choose>
                            </tags:nameValue2>
                            
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                    
                    <tags:simplePopup id="maskMissingPointsOptionInfoPopup" title="${maskMissingPointsOptionLabel}" on="#missing_help" options="{width: 600}">
                        <div class="warning stacked"><i:inline key="yukon.common.warningMessage" arguments="${maskMissingPointsOptionDescription}"/></div>
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".maskMissingPointsFalseOptionText">${maskMissingPointsFalseOptionTooltip}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".maskMissingPointsTrueOptionText">${maskMissingPointsTrueOptionTooltip}</tags:nameValue2>
                        </tags:nameValueContainer2>
                    </tags:simplePopup>
                    
                    <tags:simplePopup id="sharedPointsOptionInfoPopup" title="${sharedPointsOptionLabel}" on="#shared_help" options="{width: 600}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".sharedPointsFalseOptionText">${sharedPointsFalseOptionTooltip}</tags:nameValue2>
                            <tags:nameValue2 nameKey=".sharedPointsTrueOptionText">${sharedPointsTrueOptionTooltip}</tags:nameValue2>
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
                            
                    <%-- REMOVE POINTS BUTTON --%>
                    <cti:button nameKey="remove" classes="action primary" type="submit"/>
        
                </form>
                
            </tags:bulkActionContainer>
        
        </cti:msgScope>
        
        <cti:includeScript link="/resources/js/pages/yukon.bulk.point.js"/>
                        
    </cti:standardPage>
</cti:checkRolesAndProperties>