<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="adminSetup" page="lmMappings">
  <cti:includeScript link="/JavaScript/yukon.substation.mappings.js"/>

<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.interfaces.home.tab.title">
        <c:url value="/multispeak/setup/home" />
    </cti:linkTab>

    <cti:linkTab tabId="loadMgtTab" selectorKey="yukon.web.modules.adminSetup.lmMappings.tab.title" initiallySelected="${true}">
        <c:url value="/multispeak/setup/lmMappings/home" />
    </cti:linkTab>

    <cti:linkTab tabId="deviceGroupTab" selectorKey="yukon.web.modules.adminSetup.deviceGroupSyncHome.tab.title">
        <c:url value="/multispeak/setup/deviceGroupSync/home" />
    </cti:linkTab>
</cti:linkTabbedContainer>

    <tags:sectionContainer2 nameKey="setMappings">
        <tags:nameValueContainer2 tableClass="with-form-controls">
            <tags:nameValue2 nameKey=".strategy">
                <input type="text" class="js-mapping-input js-strategy">
                <span class="errors dn js-mapping-errors js-strategy"><i:inline key=".error.noStrategy"/></span>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".substation">
                <input type="text" class="js-mapping-input js-substation" >
                <span class="errors dn js-mapping-errors js-substation"><i:inline key=".error.noSubstation"/></span>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".pao" valueClass="pao-values">
                <cti:msg2  var="notFound" key=".error.notFound"/>
                <span class="mapped-pao-name" data-empty-text="${notFound}"></span>

                <cti:msg2 var="message" key=".changeMapping.message"/>
                <cti:msg2 var="title" key=".changeMapping.title"/>
                <cti:button nameKey="add" busy="true" classes="js-add-btn fn vat" data-ok-event="yukon_substation_mappings_add"
                    data-message="${message}" data-title="${title}"/>

                <%-- These inputs are used by the picker --%>
                <input type="hidden" id="mappedNameId" >
                <input id="mappedName" class="dn" >
                <tags:pickerDialog id="paoPicker"
                    type="lmProgramOrScenarioPicker"
                    destinationFieldId="mappedNameId"
                    extraDestinationFields="paoName:mappedName;" 
                    endAction="yukon.substation.mappings.setMappedNameId"
                    linkType="none"/>
            </tags:nameValue2>
        <div class="user-message error dn js-add-error"><i:inline key=".error.add"/></div>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>

    <%-- ALL MAPPINGS --%>
    <tags:sectionContainer2 nameKey="allMappings">
        <div class="user-message error dn js-delete-error"><i:inline key=".error.delete"/></div>
        <div class="user-message warning dn js-reload-error"><i:inline key=".error.reload"/></div>
        <div data-mappings-table data-url="reloadAllMappingsTable">
            <jsp:include page="allMappingsTable.jsp"/>
        </div>
    </tags:sectionContainer2>
</cti:standardPage>