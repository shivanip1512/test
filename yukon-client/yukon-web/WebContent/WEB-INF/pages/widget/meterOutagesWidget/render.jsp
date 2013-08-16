<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<tags:nameValueContainer>
    <cti:msg2 key="${attribute}" var="attributeName" />
    <tags:nameValue name="${attributeName}">
        <c:if test="${not isBlinkConfigured}"><i:inline key=".blinkCountNotConfigured"/></c:if>
        <c:if test="${isBlinkConfigured}"><tags:attributeValue device="${device}" attribute="${attribute}" /></c:if>
    </tags:nameValue>
</tags:nameValueContainer>

<c:if test="${isOutageSupported}">
	<tags:nameValueContainer2 tableClass="stacked">
        <tags:nameValue2 nameKey=".outagesLastRetrieved">
            <c:if test="${not isOutageConfigured}">
            	<i:inline key=".outageLogPointNotConfigured"/>
            </c:if>
            <c:if test="${isOutageConfigured}">
            	<c:if test="${data.readDate != null}">
            		<cti:formatDate value="${data.readDate}" type="BOTH"/>
                </c:if>
            </c:if>
        </tags:nameValue2>
	</tags:nameValueContainer2>

    <div class="widgetInternalSection">
        <c:if test="${not empty data.outageData}">
            <table class="compactResultsTable">
                <thead>
                    <tr>
                        <th><i:inline key=".log" /></th>
                        <th><i:inline key=".time" /></th>
                        <th><i:inline key=".duration" /></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:if test="${not empty data.outageData}">
                        <c:forEach items="${data.outageData}" var="outage">
                            <tr>
                                <td>${outage.outageLogIndex}</td>
                                <td><cti:formatDate value="${outage.timestamp.pointDataTimeStamp}" type="BOTH"/></td>
                                <td>${outage.duration}</td>
                            </tr>
                        </c:forEach>
                    </c:if>
                </tbody>
            </table>
        </c:if>
    </div>
</c:if>
<div class="actionArea">
    <tags:widgetActionRefresh hide="${!readable}" method="read" nameKey="read"/>
</div>
