<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dr" page="setup.loadGroup.${mode}">
    
    <tags:setFormEditMode mode="${mode}" />
    
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <cti:url var="createUrl" value="/dr/setup/loadGroup/create" />
            <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" href="${createUrl}"/>
            <cti:url var="editUrl" value="/dr/setup/loadGroup/${loadGroup.id}/edit"/>
            <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" />
            <li class="divider"></li>
            <!-- TODO: Copy functionality to be implemented -->
            <cm:dropdownOption key="yukon.web.components.button.copy.label" icon="icon-disk-multiple" id="js-copy-option" href="#"/>
            <li class="divider"></li>
            <!-- TODO: Delete functionality to be implemented -->
            <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" id="js-delete-option" href="#"/>
        </div>
    </cti:displayForPageEditModes>
    
    
    <cti:url var="action" value="/dr/setup/loadGroup/save" />
    <form:form modelAttribute="loadGroup" action="${action}" method="post">
        <cti:csrfToken />
        <form:hidden path="id"/>
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    <tags:input path="name" size="25" maxlength="60" autofocus="autofocus"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".type">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                        <tags:selectWithItems items="${switchTypes}" path="type" defaultItemLabel="${selectLbl}" defaultItemValue=""/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <i:inline key="${loadGroup.type}"/>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".kWCapacity">
                    <tags:input path="kWCapacity"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".disableGroup">
                    <tags:switchButton path="disableGroup" offNameKey="yukon.common.no" onNameKey="yukon.common.yes" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".disableControl">
                    <tags:switchButton path="disableControl" offNameKey="yukon.common.no" onNameKey="yukon.common.yes" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" />
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url var="viewUrl" value="/dr/setup/loadGroup/${loadGroup.id}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button id="js-cancel-btn" nameKey="cancel" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.loadGroup.js" />
</cti:standardPage>