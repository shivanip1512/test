<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<c:url var="completeHistoryUrl" value="/spring/stars/consumer/controlhistory/completeHistoryView"/>

<cti:standardPage module="consumer" title="Consumer Energy Services">
    <cti:standardMenu />
    
<script type="text/javascript">
function viewCompleteControlHistory(programId) {
    var url = '${completeHistoryUrl}?programId=' + programId;
    location.href = url;
}
</script>
    
    <table class="contentTable">
        <tr>
            <td class="leftColumn">
                <h3><cti:msg key="yukon.dr.consumer.controlhistory.header" /></h3>
                <br>
                <div id="programs">
                    <cti:msg key="yukon.dr.consumer.controlhistory.enrolledProgramsTitle" var="programsTitle" />
                    <ct:boxContainer title="${programsTitle}" hideEnabled="false">
                        <c:choose>
                            <c:when test="${isNotEnrolled}">
                                <cti:msg key="yukon.dr.consumer.controlhistory.notEnrolledMessage"></cti:msg>
                            </c:when>
                            <c:otherwise>
                                <c:set var="count" value="0"/>
                                <table cellspacing="0" class="enrolledPrograms">
                                    <c:forEach var="displayableProgram" items="${displayablePrograms}">
                                        <c:set var="program" value="${displayableProgram.program}"/>
                                        <c:set var="count" value="${count + 1}"/>

                                        <c:set var="isBordered" value="${fn:length(displayablePrograms) != count}"/>
                                        <c:set var="logoClass" value="${isBordered ? 'logoBordered' : 'logo'}"/>
                                        <c:set var="tableDataClass" value="${isBordered ? 'bordered' : ''}"/>
                                        <c:set var="viewClass" value="${isBordered ? 'viewBordered' : 'view'}"/>                                        
                                        
                                        <tr>
                                            <td class="${logoClass}" width="5%">
                                                <img src="../../../WebConfig/${program.logoLocation}"></img>
                                            </td>
                                            <td class="${tableDataClass}">
                                                <b><cti:msg key="${program.displayName}"></cti:msg></b>
                                                
                                                <c:if test="${program.chanceOfControl != 'NONE'}">
                                                    <cti:msg key="yukon.dr.consumer.chanceOfControl.${program.chanceOfControl}"/>
                                                </c:if>
                                                
                                                <table>
                                                    <c:forEach var="displayableControlHistory" items="${displayableProgram.displayableControlHistoryList}">
                                                        <tr>
                                                            <td></td>
                                                            <c:choose>
                                                                
                                                                <c:when test="${displayableControlHistory.controlStatusDisplay}">
                                                                    <td colspan="3"><cti:msg key="${displayableControlHistory.controlHistory.currentStatus}"/></td>
                                                                </c:when>
                                                                
                                                                <c:when test="${displayableControlHistory.deviceLabelControlStatusDisplay}">
                                                                    <td>${displayableControlHistory.controlHistory.displayName}</td>
                                                                    <td>-</td>
                                                                    <td><cti:msg key="${displayableControlHistory.controlHistory.currentStatus}"/></td>
                                                                </c:when>
                                                            </c:choose>
                                                        </tr>
                                                    </c:forEach>
                                                </table>
                                            </td>
                                            <td width="25%" align="right" class="${viewClass}">
                                                <input type="button" value="View Complete History" onclick="viewCompleteControlHistory(${program.programId})"/>
                                                <c:set var="totalDuration" value="${totalDurationMap[program.programId]}"/>
                                                <br>
                                                <br>
                                                <c:choose>
                                                    <c:when test="${totalDuration > 0}">
                                                        <cti:formatDuration var="formattedDuration" type="HM" value="${totalDuration}"/>
                                                        <cti:msg key="yukon.dr.consumer.controlhistory.todaysProgramControl" argument="${formattedDuration}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <cti:msg key="yukon.dr.consumer.controlhistory.noProgramControl"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </ct:boxContainer>
                </div>                              
            </td>
            <td class="rightColumn">
                <cti:customerAccountInfoTag accountNumber="${customerAccount.accountNumber}" />
                <br>
                <img src="<cti:theme key="yukon.dr.consumer.controlhistory.rightColumnLogo" default="/WebConfig/yukon/Family.jpg" url="true"/>"></img>
            </td>
        </tr>
    </table> 
</cti:standardPage>    