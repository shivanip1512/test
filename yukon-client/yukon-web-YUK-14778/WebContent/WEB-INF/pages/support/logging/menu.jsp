<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dateTime" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="support" page="logMenu">
    <d:inline nameKey="custom" okEvent="customDateRangeSubmit" on="#customDateBtn">
        <form method="GET" id="customDateForm">
            <input type="hidden" name="file" value="${file}"/>
            <input type="hidden" name="sortBy" value="${sortBy}"/>
            <input type="hidden" name="show" value="custom"/>
            <dateTime:dateRange startName="customStart" endName="customEnd" startValue="${customStart}" endValue="${customEnd}" maxDate="${maxDate}" />
        </form>
    </d:inline>

    <cti:dataGrid cols="2">
        <cti:dataGridCell>
            <tags:sectionContainer2 nameKey="settings">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".show">
                        <a href="<tags:logPageLink showParam="today"/>" id="todayBtn"><i:inline key=".today"/></a>&nbsp;|&nbsp;
                        <a href="<tags:logPageLink showParam="lastWeek"/>"  id="lastWeekBtn"><i:inline key=".oneWeek"/></a>&nbsp;|&nbsp;
                        <a href="<tags:logPageLink showParam="lastMonth"/>"  id="lastMonthBtn"><i:inline key=".oneMonth"/></a>&nbsp;|&nbsp;
                        <a href="javascript:void(0);" id="customDateBtn"><i:inline key=".custom"/></a>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".sortBy">
                        <a href="<tags:logPageLink sortByParam="date"/>" id="dateBtn"><i:inline key=".date"/></a>&nbsp;|&nbsp;
                        <a href="<tags:logPageLink sortByParam="application"/>" id="applicationBtn" "><i:inline key=".application"/></a>
                    </tags:nameValue2>
                    <c:if test="${show eq 'custom'}">
                        <tags:nameValue2 nameKey=".showing">
                            <cti:formatDate type="LONG_DATE" value="${customStart}"/> - <cti:formatDate type="LONG_DATE" value="${customEnd}"/>
                        </tags:nameValue2>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </cti:dataGridCell>
        <cti:dataGridCell>
            <tags:sectionContainer2 nameKey="directories" >
                <ul class="tree-list">
                    <li>
                        <c:if test="${isSubDirectory}">
                            <a class="labeled_icon prev fn" href="<tags:logPageLink fileParam="${currentDirectory}"/>">${logBaseDir}${file}</a>
                        </c:if>
                        <c:if test="${!isSubDirectory}">
                            ${logBaseDir}
                        </c:if>
                    </li>
                    <c:forEach var="directory" items="${directories}">
                        <li><a class="fn" href="<tags:logPageLink fileParam="${file}${directory}/"/>"><cti:icon icon="icon-folder"/>${directory}</a></li>
                    </c:forEach>
                </ul>
            </tags:sectionContainer2>
        </cti:dataGridCell>
    </cti:dataGrid>

    <c:forEach var="logSection" items="${logList}">
        <div class="vat dib stacked">
            <tags:sectionContainer title="${logSection.key}" hideEnabled="true">
                <table class="contentTable row-highlighting">
                    <c:forEach var="logFile" items="${logSection.value}" varStatus="logFileIndex">
                        <cti:url value="view" var="url"><cti:param name="file" value="${file}${logFile.name}"/></cti:url>
                        <tr title="${logFile.name}">
                            <td><a href="${url}">${logFile.identifier}</a></td>
                            <td><cti:msg2 key="${logFile.size}"/></td>
                        </tr>
                    </c:forEach>
                </table>
            </tags:sectionContainer>
        </div>
    </c:forEach>

<script>
    $(function() {
        $("#${cti:escapeJavaScript(show)}Btn").replaceWith($("#${cti:escapeJavaScript(show)}Btn").html()); // Remove surrounding anchor tag
        $("#${cti:escapeJavaScript(sortBy)}Btn").replaceWith($("#${cti:escapeJavaScript(sortBy)}Btn").html()); // Remove surrounding anchor tag

          $(document).on("customDateRangeSubmit", function(event) {
             yukon.ui.blockPage();
             $("#customDateForm").submit();
        });
    });
</script>

</cti:standardPage>