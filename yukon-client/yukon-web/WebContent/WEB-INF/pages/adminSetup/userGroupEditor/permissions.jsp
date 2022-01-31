<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="adminSetup" page="auth.${page}.permissions">
<cti:msgScope paths="modules.adminSetup.auth.permissions">

<cti:includeScript link="/resources/js/pages/yukon.admin.permissions.js"/>

<cti:toJson id="permission-data" object="${data}"/>

<div class="column-12-12">
    <div class="column one">
        <tags:sectionContainer2 nameKey="lm">
            <div class="stacked">
                <span class="label label-info"><i:inline key="yukon.common.note"/></span>
                <span><i:inline key=".lm.note"/></span>
            </div>
            <div class="scroll-lg">
                <table id="lm-table" class="full-width striped with-form-controls"
                    data-permission="${lm}">
                    <c:forEach var="pao" items="${lmVisible}">
                        <tr data-id="${pao.liteID}">
                            <td>${fn:escapeXml(pao.paoName)}</td>
                            <td><i:inline key="${pao.paoType}"/></td>
                            <td class="remove-column">
                                <cti:button renderMode="buttonImage" classes="show-on-hover js-delete" icon="icon-remove"/>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
            <div class="action-area">
                <tags:pickerDialog
                    id="lmPicker"
                    type="lmDevicePicker"
                    endEvent="yukon:admin:permission:add"
                    multiSelectMode="true"
                    linkType="button"
                    icon="icon-add"
                    nameKey="add"/>
            </div>
        </tags:sectionContainer2>
    </div>
    
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="vv">
            <div class="stacked">
                <span class="label label-info"><i:inline key="yukon.common.note"/></span>
                <span><i:inline key=".vv.note"/></span>
            </div>
            <div class="scroll-lg">
                <table id="vv-table" class="full-width striped with-form-controls"
                    data-permission="${pao}">
                    <c:forEach var="pao" items="${paoVisible}">
                        <tr data-id="${pao.liteID}">
                            <td>${fn:escapeXml(pao.paoName)}</td>
                            <td><i:inline key="${pao.paoType}"/></td>
                            <td>
                                <cti:button renderMode="buttonImage" classes="fr show-on-hover js-delete" icon="icon-remove"/>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
            <div class="action-area">
                <tags:pickerDialog
                    id="vvPicker"
                    type="capControlAreaPicker"
                    endEvent="yukon:admin:permission:add"
                    multiSelectMode="true"
                    linkType="button"
                    icon="icon-add"
                    nameKey="add"/>
            </div>
        </tags:sectionContainer2>
    </div>
</div>

<table class="dn js-templates">
    <tr class="js-template-row">
        <td></td>
        <td></td>
        <td>
            <cti:button renderMode="buttonImage" classes="fr show-on-hover js-delete" icon="icon-remove"/>
        </td>
    </tr>
</table>

</cti:msgScope>
</cti:standardPage>