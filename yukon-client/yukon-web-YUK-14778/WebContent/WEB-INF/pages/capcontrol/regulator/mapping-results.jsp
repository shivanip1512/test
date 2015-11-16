<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol.regulator.setup">

<tags:sectionContainer2 nameKey="mapping.result" arguments="${task['start']}">
    <div class="column-6-18 clearfix js-task-results" data-task="${taskId}">
        <div class="column one">
            <div class="scroll-lg">
                <ul class="simple-list nav-list selectable js-mapping-result-devices">
                    <c:forEach var="pao" items="${regulators}" varStatus="status">
                        <c:set var="clazz" value="${status.first ? 'selected' : ''}"/>
                        <li class="${clazz}" data-id="${pao.liteID}">${fn:escapeXml(pao.paoName)}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
        <div class="column two nogutter">
            <div class="stacked dn js-automap-results">
                <span class="name"><i:inline key="yukon.common.result"/>:&nbsp;</span>
                <span class="label label-success js-automap-result"></span>
            </div>
            <table class="compact-results-table dashed">
                <thead>
                    <tr>
                        <th><i:inline key="yukon.common.attribute"/></th>
                        <th><i:inline key="yukon.common.point"/></th>
                        <th class="dn js-result-header"><i:inline key="yukon.common.result"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody class="js-mappings">
                    <c:forEach var="mapping" items="${regulator.mappings}">
                        <tr data-mapping="${mapping.key}">
                            <td><i:inline key="${mapping.key}"/></td>
                            <td>${fn:escapeXml(regulator.name)}-${fn:escapeXml(mapping.key.mappingString)}</td>
                            <td class="js-result"></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</tags:sectionContainer2>

</cti:msgScope>