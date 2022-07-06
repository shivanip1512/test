<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>



<cti:msgScope paths="yukon.web.modules.operator.routes">
    <cti:standardPage module="operator" page="routes.${mode}">
        <tags:nameValueContainer2>
        
            <tags:sectionContainer2 nameKey="general">
                <tags:nameValueContainer2>
                <cti:url var="action" value="/stars/device/routes/create" />
            <form:form modelAttribute="communicationRoute" action="${action}" method="post" id="js-comm-route-form">
            
                    <tags:nameValue2 nameKey=".name">
                        <tags:input path="deviceName" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
                    </tags:nameValue2>
                
                    <tags:nameValue2 nameKey=".selectSignalTransmitter">
                        <tags:pickerDialog id="signalTransmitterPicker${id}" 
                                           type="signalTransmitterPicker" 
                                           destinationFieldName="signalTransmitterIds"
                                           linkType="selection" 
                                           selectionProperty="paoName"
                                           multiSelectMode="true"
                                           allowEmptySelection="false" 
                                           initialIds="${signalTransmitterIds}" 
                                           endEvent='okClicked'/>
                     </tags:nameValue2>
                
                    <tags:nameValue2 nameKey=".defaultRoute">
                        <tags:switchButton path="defaultRoute" onNameKey=".yes.label" offNameKey=".no.label"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            
            
            <!-- Buttons on page -->
            <%-- <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" classes="primary action js-save" busy="true"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url var="viewUrl" value="/dr/setup/controlArea/${controlArea.controlAreaId}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:url var="setupUrl" value="/dr/setup/filter?filterByType=CONTROL_AREA" />
                <cti:button nameKey="cancel" href="${setupUrl}" />
            </cti:displayForPageEditModes> --%>
            
                <div class="page-action-area">
                    <cti:button nameKey="save" classes="primary action js-save" busy="true"/>
                
                    <cti:url var="viewUrl" value="/stars/device/routes/list" />
                    <cti:button nameKey="cancel" href="${viewUrl}"/>
                
                </div>
            </form:form>
            </tags:sectionContainer2>
        </tags:nameValueContainer2>
    </cti:standardPage>
</cti:msgScope>
    

    