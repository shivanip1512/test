<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="general">
    <cti:standardMenu />

<cti:msg var="emailWarning" key="yukon.dr.consumer.general.oddsForControlEmailWarning" />
<script type="text/javascript">

    function checkOddsForControlEmail() {

	    var enabled = $F('oddsForControlNotification');
	    var email = $F('oddsForControlEmail');
	    if(enabled && email.empty()) {
		    alert('${emailWarning}');
		    return false;
	    } else {
		    return true;
	    }
	}
	
</script>


    <h3><cti:msg key="yukon.dr.consumer.general.header" /></h3>
    <br>
    <c:if test="${not empty optOutDisabledKey}">
        <tags:boxContainer2 nameKey="optOutsDisabledWarning" hideEnabled="false">
            <cti:msg2 key=".${optOutDisabledKey}" htmlEscape="false"/>
        </tags:boxContainer2>
        <br>
        <br>
    </c:if>
    <div id="description"><cti:msg key="yukon.dr.consumer.general.description" /></div>
    <br>
    <br>
    <div id="programs">
        <cti:msg key="yukon.dr.consumer.general.enrolledProgramsTitle" var="programsTitle" />
        <tags:boxContainer title="${programsTitle}" hideEnabled="false">
            <c:choose>
                <c:when test="${isNotEnrolled}">
                    <span id="notEnrolledMessageSpan">
                        <cti:msg key="yukon.dr.consumer.general.notEnrolledMessage"></cti:msg>
                    </span>
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
                            
                            <tr>
                                <td class="${logoClass}" width="5%">
                                    <img src="../../../WebConfig/${program.applianceCategoryLogo}">
                                </td>
                                <td class="${tableDataClass}">
                                    <b><cti:msg key="${program.displayName}"></cti:msg></b>
                                    
                                    <c:if test="${!empty program.chanceOfControl}">
                                        <spring:escapeBody>${program.chanceOfControl}</spring:escapeBody>
                                    </c:if>
                                    
                                    <table>
                                        <c:forEach var="displayableControlHistory" items="${displayableProgram.displayableControlHistoryList}">
                                            <c:set var="controlHistory" value="${displayableControlHistory.controlHistory}" />
                                            
                                            <tr>
                                                <td></td>
                                                <c:choose>
                                                    <c:when test="${displayableControlHistory.controlStatusDisplay or
                                                                    displayableControlHistory.deviceLabelControlStatusDisplay}">
                                                        <td><spring:escapeBody htmlEscape="true">${displayableControlHistory.controlHistory.displayName}</spring:escapeBody></td>
                                                        <td>-</td>
                                                        <td>
															<c:choose>
																<c:when test="${not empty controlHistory.lastControlHistoryEvent.endDate}">
																	<cti:formatDate type="DATEHM" var="lastControledEndDate" value="${controlHistory.lastControlHistoryEvent.endDate}"/>
																	<i:inline key="${controlHistory.currentStatus.formatKey}" arguments="${lastControledEndDate}" /> 
																</c:when>
																<c:otherwise>
																	<i:inline key="${controlHistory.currentStatus.formatKey}" /> 
																</c:otherwise>
															</c:choose>
                                                        </td>
                                                    </c:when>
                                                    
                                                </c:choose>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:otherwise>
            </c:choose>
        </tags:boxContainer>
        <br>
        <br>
        
        <c:if test="${showNotification}">
	        <cti:msg key="yukon.dr.consumer.general.oddsForControlTitle" var="oddsTitle" />
	        <tags:boxContainer title="${oddsTitle}" hideEnabled="false">
	            <form action="/spring/stars/consumer/general/updateOddsForControlNotification" method="post" onsubmit="checkOddsForControlEmail()">
	                <input id="oddsForControlNotification" type="checkbox" name="oddsForControlNotification" ${(emailEnabled)?'checked':''}/>
	                <label for="oddsForControlNotification">
				        <cti:msg key="yukon.dr.consumer.general.enableOddsForControl"/>
	                </label>
	                <br>
			        <cti:msg key="yukon.dr.consumer.general.oddsForControlEmail"/>
			        <c:set var="emailText">
			            <spring:escapeBody htmlEscape="true">${email}</spring:escapeBody>
			        </c:set>
		            <input type="text" name="oddsForControlEmail" value="${emailText}" size="50"/>
			        <br><br>
			        <center>
				        <cti:msg key="yukon.dr.consumer.general.updateOddsForControl" var="oddsUpdate" />
	    		        <input type="submit" value="${oddsUpdate}"/>
			        </center>
	            </form>
	        </tags:boxContainer>        
	        <br>
	        <br>
	    </c:if>
        
        <c:if test="${not empty scheduledOptOuts}">
            <i>
	            <c:forEach var="event" items="${scheduledOptOuts}">
	                <cti:formatDate value="${event.startDate}" type="DATE" var="startDate" />
	                <cti:formatDate value="${event.stopDate}" type="DATE" var="stopDate" />
	                <cti:msg key="yukon.dr.consumer.general.scheduledoptout.fromMessage" arguments="${startDate},${stopDate}"/>
	                <br>
	            </c:forEach>
            </i>
        </c:if>
    </div>
            
</cti:standardPage>