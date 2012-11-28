<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<script type="text/javascript">
jQuery(document).ready(flashYellow(jQuery('#reloadedAt')[0], 2));
</script>

<cti:msgScope paths="widgets.rfnOutagesWidget">

<tags:sectionContainer2 nameKey="outageLog">
    <div class="smallDialogScrollArea">
        <c:choose>
            <c:when test="${empty logs}"><i><i:inline key=".noLogs"/></i></c:when>
            <c:otherwise>
                <table class="miniResultsTable boxContainer_miniResultsTable">
                	<thead>
                    <tr>
                        <th><i:inline key=".start"/></th>
                        <th><i:inline key=".end"/></th>
                        <th><i:inline key=".duration"/></th>
                    </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                    <c:forEach items="${logs}" var="log">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <c:choose>
    	                        <c:when test="${!log.invalid}">
    		                        <td><cti:formatDate value="${log.start}" type="BOTH"/></td>
    		                        <td><cti:formatDate value="${log.end}" type="BOTH"/></td>
    		                        <td><cti:formatDuration type="DHMS_REDUCED" startDate="${log.start}" endDate="${log.end}"/></td>
    	                        </c:when>
    	                        <c:otherwise>
                                    <td><i:inline key=".unknown"/></td>
                                    <td><cti:formatDate value="${log.end}" type="BOTH"/></td>
                                    <td><i:inline key=".unknown"/></td>
    	                        </c:otherwise>
                            </c:choose>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</tags:sectionContainer2>
</cti:msgScope>