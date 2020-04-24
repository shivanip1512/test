<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="commChannelDetail">
    <!-- Actions dropdown -->
    <!-- TODO : Replace the list urls with create and delete url -->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/stars/device/commChannel/list" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="create-option" href="${createUrl}"/>
    </div>
    <tags:widgetContainer deviceId="${id}" identify="false">
        <div class="column-12-12">
            <div class="one column">
                <tags:widget bean="commChannelInfoWidget"/>
            </div>
        </div>
    </tags:widgetContainer>
</cti:standardPage>