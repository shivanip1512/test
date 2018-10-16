<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="pqrConfig">
    
    <div class="stacked-md">
        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
    </div>
    
    <div class="stacked-md">
        <span class="label label-info"><i:inline key="yukon.common.note"/></span>&nbsp;
        <i:inline key=".note"/>
    </div>
    
    <cti:msg2 var="volts" key="yukon.common.units.VOLTS"/>
    <cti:msg2 var="microsec" key="yukon.common.units.MICROSECONDS_PERIOD"/>
    <cti:msg2 var="millis" key="yukon.common.units.MILLISECONDS"/>
    <cti:msg2 var="secs" key="yukon.common.units.SECONDS"/>
    
    <cti:url var="submitUrl" value="/stars/operator/inventory/pqrConfig/confirm"/>
    <form:form modelAttribute="config" action="${submitUrl}" method="post">
        <cti:csrfToken/>
        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
        
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="enable">
                    <tags:nameValueContainer2>
                        <div class="button-group button-group-toggle">
                            <c:set var="enableSelected" value="${config.pqrEnable eq 'true' ? 'on' : ''}"/>
                            <c:set var="doNotSendSelected" value="${empty config.pqrEnable ? 'on' : ''}"/>
                            <c:set var="disableSelected" value="${config.pqrEnable eq 'false' ? 'on' : ''}"/>
                            <cti:button nameKey="enableButton" data-input="#pqr-enable" data-value="true" classes="yes ${enableSelected}"/>
                            <cti:button nameKey="doNotSendButton" data-input="#pqr-enable" data-value="" classes="${doNotSendSelected}"/>
                            <cti:button nameKey="disableButton" data-input="#pqr-enable" data-value="false" classes="no ${disableSelected}"/>
                            <form:hidden id="pqr-enable" path="pqrEnable"/>
                        </div>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ovTriggers">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ovTrigger">
                            <tags:input path="lovTrigger" units="${volts}" placeholder="000.0" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovRestore">
                            <tags:input path="lovRestore" units="${volts}" placeholder="000.0" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovTriggerTime">
                            <tags:input path="lovTriggerTime" units="${millis}" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovRestoreTime">
                            <tags:input path="lovRestoreTime" units="${millis}" inputClass="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="ovEventDuration">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ovMinEventDuration">
                            <tags:input path="lovMinEventDuration" units="${secs}" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovMaxEventDuration">
                            <tags:input path="lovMaxEventDuration" units="${secs}" inputClass="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ovRandomization">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ovStartRandomTime">
                            <tags:input path="lovStartRandomTime" units="${millis}" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovEndRandomTime">
                            <tags:input path="lovEndRandomTime" units="${millis}" inputClass="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            <div class="column two nogutter">
                <tags:sectionContainer2 nameKey="eventSeparation">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".minEventSeparation">
                            <tags:input path="minimumEventSeparation" units="${secs}" inputClass="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ofTriggers">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ofTrigger">
                            <tags:input path="lofTrigger" units="${microsec}" placeholder="00000" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ofRestore">
                            <tags:input path="lofRestore" units="${microsec}" placeholder="00000" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ofTriggerTime">
                            <tags:input path="lofTriggerTime" units="${millis}" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ofRestoreTime">
                            <tags:input path="lofRestoreTime" units="${millis}" inputClass="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ofEventDuration">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ofMinEventDuration">
                            <tags:input path="lofMinEventDuration" units="${secs}" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ofMaxEventDuration">
                            <tags:input path="lofMaxEventDuration" units="${secs}" inputClass="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ofRandomization">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ofStartRandomTime">
                            <tags:input path="lofStartRandomTime" units="${millis}" inputClass="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ofEndRandomTime">
                            <tags:input path="lofEndRandomTime" units="${millis}" inputClass="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
        </div>
        
        <div class="page-action-area">
            <cti:button nameKey="next" classes="action primary" type="submit"/>
        </div>
    </form:form>
    
    <style>
        .skinny {
            width: 70px;
        }
    </style>
</cti:standardPage>