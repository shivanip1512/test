<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="capcontrol" page="dmvTest.${mode}">

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <tags:setFormEditMode mode="${mode}" />
    <cti:displayForPageEditModes modes="VIEW">
        <div class="js-page-additional-actions dn">
            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <li class="divider" />
                <cti:url var="editUrl"
                    value="/capcontrol/dmvTest/${dmvTest.dmvTestId}/edit" />
                <cm:dropdownOption key="components.button.edit.label"
                    icon="icon-pencil" href="${editUrl}" />
            </cti:checkRolesAndProperties>
        </div>
    </cti:displayForPageEditModes>
    <cti:url var="action" value="/capcontrol/dmvTest" />
    <form:form id="dmvTest-form" commandName="dmvTest" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="dmvTestId" />
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="info" styleClass="stacked-lg">
                    <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                        <tags:nameValue2 nameKey="yukon.common.name">
                            <tags:input path="name" maxlength="100"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".pollingInterval">
                            <tags:input path="pollingInterval" size="4"/>
                            <i:inline key="yukon.common.units.SECONDS"/>                                                        
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".dataGatheringDuration">
                            <tags:input path="dataGatheringDuration" size="4"/>
                            <i:inline key="yukon.common.units.MINUTES"/>                                                        
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".stepSize">
                            <tags:input path="stepSize" size="4"/>
                            <i:inline key="yukon.common.units.VOLTS"/>                                                        
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".commSuccPercentage">
                            <tags:input path="commSuccPercentage"/>
                            <i:inline key="yukon.common.units.PERCENT"/>                            
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
        </div>
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:msg2 var="deleteTitle" key=".dmvTest.delete"/>
                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:dmvtest:delete"
                    title="${deleteTitle}"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${dmvTest.name}"/>
                
                <cti:url var="viewUrl" value="/capcontrol/dmvTest/${dmvTest.dmvTestId}" />
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    
    <cti:url var="url" value="/capcontrol/dmvTest/${dmvTest.dmvTestId}"/>
    <form:form id="delete-dmvtest" method="DELETE" action="${url}">
        <cti:csrfToken/>
    </form:form>
    
    <cti:includeScript link="/resources/js/pages/yukon.da.dmvtest.js"/>
    
</cti:standardPage>