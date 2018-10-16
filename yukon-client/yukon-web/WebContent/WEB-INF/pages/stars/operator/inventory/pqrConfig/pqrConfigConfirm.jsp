<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="pqrConfigConfirm">
<cti:msgScope paths=",modules.operator.pqrConfig">

    <div class="stacked-md">
        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
    </div>
    
    <div class="stacked-md">
        <i:inline key=".willBeSubmitted"/>
    </div>
    
    <cti:msg2 var="volts" key="yukon.common.units.VOLTS"/>
    <cti:msg2 var="microsec" key="yukon.common.units.MICROSECONDS_PERIOD"/>
    <cti:msg2 var="millis" key="yukon.common.units.MILLIS"/>
    <cti:msg2 var="secs" key="yukon.common.units.SECONDS"/>
    
    <cti:url var="submitUrl" value="/stars/operator/inventory/pqrConfig/submit"/>
    <form:form id="config-form" modelAttribute="config" action="${submitUrl}" method="post">
        <cti:csrfToken/>
        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
        
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="enable">
                    <tags:nameValueContainer2>
                        <div class="button-group button-group-toggle">
                            <c:choose>
                                <c:when test="${empty config.pqrEnable}">
                                    <i:inline key=".notSent"/>
                                </c:when>
                                <c:when test="${config.pqrEnable}">
                                    <i:inline key=".enableButton.label"/>
                                </c:when>
                                <c:otherwise>
                                    <i:inline key=".disableButton.label"/>
                                </c:otherwise>
                            </c:choose>
                            <form:hidden id="pqr-enable" path="pqrEnable"/>
                        </div>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="ovTriggers">
                    <c:if test="${config.hasLovParams()}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".ovTrigger">
                                ${config.lovTrigger} ${volts}
                                <form:hidden path="lovTrigger"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ovRestore">
                                ${config.lovRestore} ${volts}
                                <form:hidden path="lovRestore"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ovTriggerTime">
                                ${config.lovTriggerTime} ${millis}
                                <form:hidden path="lovTriggerTime"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ovRestoreTime">
                                ${config.lovRestoreTime} ${millis}
                                <form:hidden path="lovRestoreTime"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:if>
                    <c:if test="${not config.hasLovParams()}">
                        <i:inline key=".notSent"/>
                    </c:if>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="ovEventDuration">
                    <c:if test="${config.hasLovEventDurations()}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".ovMinEventDuration">
                                ${config.lovMinEventDuration} ${secs}
                                <form:hidden path="lovMinEventDuration"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ovMaxEventDuration">
                                ${config.lovMaxEventDuration} ${secs}
                                <form:hidden path="lovMaxEventDuration"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:if>
                    <c:if test="${not config.hasLovEventDurations()}">
                        <i:inline key=".notSent"/>
                    </c:if>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="ovRandomization">
                    <c:if test="${config.hasLovDelayDurations()}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".ovStartRandomTime">
                                ${config.lovStartRandomTime} ${millis}
                                <form:hidden path="lovStartRandomTime"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ovEndRandomTime">
                                ${config.lovEndRandomTime} ${millis}
                                <form:hidden path="lovEndRandomTime"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:if>
                    <c:if test="${not config.hasLovDelayDurations()}">
                        <i:inline key=".notSent"/>
                    </c:if>
                </tags:sectionContainer2>
            </div>
            
            <div class="column two nogutter">
                <tags:sectionContainer2 nameKey="eventSeparation">
                    <c:if test="${not empty config.minimumEventSeparation}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".minEventSeparation">
                                ${config.minimumEventSeparation} ${secs}
                                <form:hidden path="minimumEventSeparation"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:if>
                    <c:if test="${empty config.minimumEventSeparation}">
                        <i:inline key=".notSent"/>
                    </c:if>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="ofTriggers">
                    <c:if test="${config.hasLofParams()}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".ofTrigger">
                                ${config.lofTrigger} ${microsec}
                                <form:hidden path="lofTrigger"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ofRestore">
                                ${config.lofRestore} ${microsec}
                                <form:hidden path="lofRestore"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ovTriggerTime">
                                ${config.lofTriggerTime} ${millis}
                                <form:hidden path="lofTriggerTime"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ovRestoreTime">
                                ${config.lofRestoreTime} ${millis}
                                <form:hidden path="lofRestoreTime"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:if>
                    <c:if test="${not config.hasLofParams()}">
                        <i:inline key=".notSent"/>
                    </c:if>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="ofEventDuration">
                    <c:if test="${config.hasLofEventDurations()}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".ofMinEventDuration">
                                ${config.lofMinEventDuration} ${secs}
                                <form:hidden path="lofMinEventDuration"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ofMaxEventDuration">
                                ${config.lofMaxEventDuration} ${secs}
                                <form:hidden path="lofMaxEventDuration"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:if>
                    <c:if test="${not config.hasLofEventDurations()}">
                        <i:inline key=".notSent"/>
                    </c:if>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="ofRandomization">
                    <c:if test="${config.hasLofDelayDurations()}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".ofStartRandomTime">
                                ${config.lofStartRandomTime} ${millis}
                                <form:hidden path="lofStartRandomTime"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ofEndRandomTime">
                                ${config.lofEndRandomTime} ${millis}
                                <form:hidden path="lofEndRandomTime"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:if>
                    <c:if test="${not config.hasLofDelayDurations()}">
                        <i:inline key=".notSent"/>
                    </c:if>
                </tags:sectionContainer2>
            </div>
        </div>
        
        <div class="page-action-area">
            <cti:button nameKey="back" type="button" id="js-back-button"/>
            <cti:button nameKey="send" type="submit" classes="action primary"/>
        </div>
    </form:form>
    
    <cti:url var="backUrl" value="/stars/operator/inventory/pqrConfig/setup"/>
    <script>
        $('#js-back-button').click(function() {
            $('#config-form').attr('action', '${backUrl}');
            $('#config-form').submit();
        });
    </script>
    
</cti:msgScope>
</cti:standardPage>