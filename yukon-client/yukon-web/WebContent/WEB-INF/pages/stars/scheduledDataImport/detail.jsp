<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="operator" page="scheduledDataImportDetail.${mode}">

    <tags:setFormEditMode mode="${mode}" />
    
    <cti:displayForPageEditModes modes="VIEW">
        <c:set var="disableJobEdit" value="${scheduledImportData.jobState eq 'RUNNING' or  scheduledImportData.jobState eq 'DELETED'}"/>
        
        <div id="page-actions" class="dn">
            <!-- Create -->
            <cti:url var="createUrl" value="/stars/scheduledDataImport/create" />
            <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                               id="create-option" href="${createUrl}"/>
            <li class="divider"></li>
            <!-- Edit -->
            <cti:url var="editUrl" value="/stars/scheduledDataImport/${scheduledImportData.jobId}/edit" />
            <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}"
                               disabled="${disableJobEdit}" />
            <li class="divider"></li>
            <!-- Delete -->
            <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-hide-dropdown"
                               id="delete-option" data-ok-event="yukon:scheduledDataImport:delete" disabled="${disableJobEdit}"/>
            <d:confirm on="#delete-option" nameKey="confirmDelete" argument="${scheduledImportData.scheduleName}" />
            <cti:url var="deleteUrl" value="/stars/scheduledDataImport/${scheduledImportData.jobId}/delete" />
            <form:form id="delete-scheduledDataImport-form" action="${deleteUrl}" method="delete">
                <cti:csrfToken/>
            </form:form>
        </div>
    </cti:displayForPageEditModes>
    
    <cti:msg2 var="sectionTitle" key=".info"/>
    <tags:sectionContainer title="${sectionTitle}">
        <cti:url var="action" value="/stars/scheduledDataImport/save" />
        <form:form modelAttribute="scheduledImportData" action="${action}" method="post">
            <cti:csrfToken />
            <tags:hidden path="jobId" />
            <tags:hidden path="importType"/>
        
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".scheduleName">
                    <tags:input path="scheduleName" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".importPath">
                    <tags:selectWithItems path="importPath" items="${importPaths}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".errorFileOutputPath">
                    <tags:selectWithItems path="errorFileOutputPath" items="${errorFileOutputPaths}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".schedule">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <tags:cronExpressionData id="cronExpression" state="${cronExpressionTagState}" allowManual="${false}"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        ${scheduledImportData.scheduleDescription}
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <cti:displayForPageEditModes modes="EDIT,VIEW">
                    <tags:nameValue2 nameKey=".nextRun">
                        <cti:dataUpdaterValue type="JOB" identifier="${scheduledImportData.jobId}/NEXT_RUN_DATE"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".status">
                        <cti:dataUpdaterValue type="JOB" identifier="${scheduledImportData.jobId}/STATE_TEXT"/>
                    </tags:nameValue2>
                </cti:displayForPageEditModes>
            </tags:nameValueContainer2>
        
            <div class="page-action-area">
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <cti:button nameKey="save" type="submit" classes="primary action" disabled="${disableJobEdit}" busy="true"/>
                </cti:displayForPageEditModes>
            
                <cti:displayForPageEditModes modes="EDIT">
                    <cti:url var="viewUrl" value="/stars/scheduledDataImport/${scheduledImportData.jobId}/view"/>
                    <cti:button nameKey="cancel" href="${viewUrl}"/>
                </cti:displayForPageEditModes>
            
                <cti:displayForPageEditModes modes="CREATE">
                    <cti:button id="cancel-btn" nameKey="cancel" />
                </cti:displayForPageEditModes>
            </div>
        </form:form>
    </tags:sectionContainer>
    <cti:includeScript link="/resources/js/pages/yukon.assets.scheduleddataimport.js" />
</cti:standardPage>