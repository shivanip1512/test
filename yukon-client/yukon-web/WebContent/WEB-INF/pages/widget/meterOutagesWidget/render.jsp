<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<ct:nameValueContainer>
<cti:msg2 key="${attribute}" var="attributeName" />
  <ct:nameValue name="${attributeName}">
    <c:if test="${not isBlinkConfigured}">
    	<i:inline key=".blinkCountNotConfigured"/>
	</c:if>
  	<c:if test="${isBlinkConfigured}">
    	<ct:attributeValue device="${device}" attribute="${attribute}" />
	</c:if>
  </ct:nameValue>
  <br>
</ct:nameValueContainer>
<div style="text-align: right">
  <ct:widgetActionRefresh hide="${!readable}" method="read" nameKey="read"/>
</div>
<br>

<c:if test="${isOutageSupported}">
	<ct:nameValueContainer2>
	  <ct:nameValue2 nameKey=".outagesLastRetrieved">
	    <c:if test="${not isOutageConfigured}">
	    	<i:inline key=".outageLogPointNotConfigured"/>
	    </c:if>
	    <c:if test="${isOutageConfigured}">
	    	<c:if test="${data.readDate != null}">
				<cti:formatDate value="${data.readDate}" type="BOTH" var="formattedReadDate" />
	        	${formattedReadDate}
	        </c:if>
	    </c:if>
	  </ct:nameValue2>
	</ct:nameValueContainer2>

    <div class="widgetInternalSection">
        <table class="resultsTable">
            <thead>
                <tr>
                    <th><i:inline key=".log" /></th>
                    <th><i:inline key=".time" /></th>
                    <th><i:inline key=".duration" /></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:if test="${empty data.outageData}">
                    <c:forEach items="1">
                        <tr>
                            <td><i:inline key="yukon.web.defaults.na" /></td>
                            <td><i:inline key="yukon.web.defaults.na" /></td>
                            <td><i:inline key="yukon.web.defaults.na" /></td>
                        </tr>
                    </c:forEach>
                </c:if>
                <c:if test="${not empty data.outageData}">
                    <c:forEach items="${data.outageData}" var="outage">
                        <tr>
                            <td>${outage.outageLogIndex}</td>
                            <td><cti:formatDate value="${outage.timestamp.pointDataTimeStamp}"
                                    type="BOTH" var="formattedTimestamp" /> ${formattedTimestamp}</td>
                            <td>${outage.duration}</td>
                        </tr>
                    </c:forEach>
                </c:if>
            </tbody>
        </table>
    </div>
</c:if>
<br>