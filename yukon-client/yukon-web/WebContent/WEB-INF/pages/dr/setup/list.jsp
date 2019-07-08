<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="dr" page="setup">
    
    <cti:msgScope paths="components, modules.dr.setup">
        <!-- Actions dropdown -->
        <div id="page-actions" class="dn">
            <cm:dropdownOption icon="icon-plus-green" key=".button.create.label" id="js-create-option" data-popup="#js-create-dr-objects-popup"/>
        </div>
        
        <div id="js-create-dr-objects-popup" class="dn" data-title="<i:inline key=".createObject.title"/>" data-width="300">
            <div class="column-12-12">
                <div class="column one">
                    <div class="stacked">
                        <cti:url var="createLoadGroupUrl" value="/dr/setup/loadGroup/create"/>
                        <a href="${createLoadGroupUrl}"><i:inline key=".loadGroup.title"/></a>
                    </div>
                </div>
                <div class="column two nogutter">
                    <div class="stacked">
                        <cti:url var="createMacroLoadGroupUrl" value="/dr/setup/macroLoadGroup/create"/>
                        <a href="${createMacroLoadGroupUrl}"><i:inline key=".macroLoadGroup.title"/></a>
                    </div>
                </div>
            </div>
        </div>
    </cti:msgScope>
    
</cti:standardPage>