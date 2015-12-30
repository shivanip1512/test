<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<table id="configList" class="compact-results-table has-actions dashed">
    <thead>
        <tr>
            <c:forEach var="column" items="${columns}">
                <tags:sort column="${column}" />
            </c:forEach>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="config" items="${configurations}">
            <c:set var="id" value="${config.configurationId}" />
            <tr>
                <td><cti:url var="url" value="config/view">
                        <cti:param name="configId" value="${id}" />
                    </cti:url> <a href="${url}">${fn:escapeXml(config.name)}</a></td>
                <td><span class="fl">${fn:escapeXml(config.numDevices)}</span> <c:if
                        test="${config.numDevices > 0}">
                        <c:url var="url" value="/bulk/selectedDevicesTableForGroupName">
                            <c:set var="fullName" value="/System/Device Configs/${config.name}" />
                            <c:param name="groupName" value="${fullName}" />
                        </c:url>
                        <cti:icon icon="icon-magnifier" classes="cp show-on-hover"
                            data-popup=".js-device-count-${id}" />
                        <div data-title="${fn:escapeXml(config.name)}" data-url="${url}"
                            data-width="450" data-height="300" class="dn js-device-count-${id}"></div>
                    </c:if> <cm:dropdown triggerClasses="fr">
                        <cti:url var="groupUrl" value="/group/editor/home">
                            <cti:param name="groupName"
                                value="/System/Device Configs/${config.name}" />
                        </cti:url>
                        <cm:dropdownOption icon="icon-folder-magnify" href="${groupUrl}"
                            key="yukon.web.modules.tools.configs.deviceGroup" />
                        <cti:url var="url" value="/bulk/collectionActions">
                            <cti:param name="collectionType" value="group" />
                            <cti:param name="group.name"
                                value="/System/Device Configs/${config.name}" />
                        </cti:url>
                        <cm:dropdownOption icon="icon-cog-go" href="${url}" key="yukon.web.modules.tools.configs.collectionAction" />
                    </cm:dropdown></td>
            </tr>
        </c:forEach>
    </tbody>
</table>
