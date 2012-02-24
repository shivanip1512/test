<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<script type="text/javascript">
jQuery(document).ready(flashYellow($('reloadedAt'), 2));
</script>

<cti:msgScope paths="widgets.rfnOutagesWidget">
<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".outageLog">
        <div id="reloadedAt" class="fl" style="margin-right: 5px;">
            <cti:formatDate type="BOTH" value="${logLoadedAt}"/>
        </div>
        <button id="refreshLogs" class="icon ui-icon refresh pointer "/>
    </tags:nameValue2>
</tags:nameValueContainer2>
<div class="smallDialogScrollArea">
    <table class="miniResultsTable boxContainer_miniResultsTable">
        <tr>
            <th><i:inline key=".start"/></th>
            <th><i:inline key=".end"/></th>
            <th><i:inline key=".duration"/></th>
        </tr>
        <c:choose>
            <c:when test="${empty logs}">
                <tr>
                    <td colspan="3" class="subtleGray fsi"><i:inline key=".noLogs"/></td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${logs}" var="log">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td><cti:formatDate value="${log.start}" type="BOTH"/></td>
                        <td><cti:formatDate value="${log.end}" type="BOTH"/></td>
                        <td><cti:formatDuration type="DHMS_REDUCED" startDate="${log.start}" endDate="${log.end}"/></td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </table>
</cti:msgScope>
</div>