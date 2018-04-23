<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="pqrConfig">
    
    <div class="stacked-md">
        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
    </div>
    
    <div class="stacked-md">
        <span class="label label-info"><i:inline key="yukon.common.note"/></span>&nbsp;
        <i:inline key=".note"/>
    </div>
    
    <cti:url var="submitUrl" value="/stars/operator/inventory/pqrConfig/submit"/>
    <form:form commandName="config" action="${submitUrl}">
        <cti:csrfToken/>
        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
        <form:hidden id="pqr-enable" path="pqrEnable"/>
        
        <div class="column-8-8-8 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="enable">
                    <tags:nameValueContainer2>
                        <div class="button-group button-group-toggle">
                            <cti:button nameKey="enableButton" data-input="#pqr-enable" data-value="true" classes="yes"/>
                            <cti:button nameKey="doNotSendButton" data-input="#pqr-enable" data-value="" classes="on"/>
                            <cti:button nameKey="disableButton" data-input="#pqr-enable" data-value="false" classes="no"/>
                        </div>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="eventSeparation">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".minEventSeparation">
                            <form:input type="number" min="0" path="minimumEventSeparation" class="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            <div class="column two">
                <tags:sectionContainer2 nameKey="ovTriggers">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ovTrigger">
                            <form:input type="number" step="0.1" min="0" maxlength="5" path="lovTrigger" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovRestore">
                            <form:input type="number" step="0.1" min="0" maxlength="5" path="lovRestore" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovTriggerTime">
                            <form:input type="number" path="lovTriggerTime" min="0" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovRestoreTime">
                            <form:input type="number" path="lovRestoreTime" min="0" class="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ovEventDuration">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ovMinEventDuration">
                            <form:input type="number" path="lovMinEventDuration" min="0" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovMaxEventDuration">
                            <form:input type="number" path="lovMaxEventDuration" min="0" class="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ovRandomization">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ovStartRandomTime">
                            <form:input type="number" path="lovStartRandomTime" min="0" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovEndRandomTime">
                            <form:input type="number" path="lovEndRandomTime" min="0" class="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            <div class="column three nogutter">
                <tags:sectionContainer2 nameKey="ofTriggers">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ofTrigger">
                            <form:input type="number" path="lofTrigger" min="0" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ofRestore">
                            <form:input type="number" path="lofRestore" min="0" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovTriggerTime">
                            <form:input type="number" path="lofTriggerTime" min="0" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ovRestoreTime">
                            <form:input type="number" path="lofRestoreTime" min="0" class="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ofEventDuration">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ofMinEventDuration">
                            <form:input type="number" path="lofMinEventDuration" min="0" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ofMaxEventDuration">
                            <form:input type="number" path="lofMaxEventDuration" min="0" class="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="ofRandomization">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".ofStartRandomTime">
                            <form:input type="number" path="lofStartRandomTime" min="0" class="skinny"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ofEndRandomTime">
                            <form:input type="number" path="lofEndRandomTime" min="0" class="skinny"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
        </div>
        
        <div class="page-action-area">
            <cti:button nameKey="send" classes="action primary" type="submit"/>
        </div>
    </form:form>
    
    <style>
        .skinny {
            width: 70px;
        }
    </style>
</cti:standardPage>