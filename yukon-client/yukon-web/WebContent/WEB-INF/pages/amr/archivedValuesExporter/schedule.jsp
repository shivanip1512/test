<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage page="bulk.archivedValueExporterScheduleSetup" module="tools">
	
	<form:form id="scheduleForm" commandName="exportData" action="doSchedule">
		<tags:boxContainer2 nameKey="adeParameters">
			<tags:nameValueContainer2 id="adeParametersContainer">
				<c:if test="${not empty jobId}">
					<input type="hidden" name="jobId" value="${jobId}">
				</c:if>
				<input type="hidden" name="formatId" value="${exportFormat.formatId}">
				<cti:deviceCollection deviceCollection="${deviceCollection}"/>
				
				<tags:nameValue2 nameKey=".devices">
					<tags:selectedDevices id="deviceCollection" deviceCollection="${deviceCollection}" />
				</tags:nameValue2>
				<tags:nameValue2 nameKey=".formatName">
					${fn:escapeXml(exportFormat.formatName)}
				</tags:nameValue2>
				<tags:nameValue2 nameKey=".formatType">
					<i:inline key="${exportFormat.formatType.formatKey}"/>
				</tags:nameValue2>
				<c:if test="${not empty attributes}">
					<c:forEach var="attribute" items="${attributes}">
						<input type="hidden" name="attributes" value="${attribute}">
					</c:forEach>
					<tags:nameValue2 nameKey=".attribute">
						<c:forEach var="attribute" items="${attributes}" varStatus="status">
							<i:inline key="${attribute}"/><c:if test="${not status.last}">, </c:if>
						</c:forEach>
					</tags:nameValue2>
				</c:if>
				<tags:nameValue2 nameKey=".dataRange">
					<tags:dataRange value="${dataRange}"/>
				</tags:nameValue2>
			</tags:nameValueContainer2>
		</tags:boxContainer2>
		
		<tags:boxContainer2 nameKey="exportParameters">
			<tags:nameValueContainer2 id="exportParametersContainer">
				<tags:inputNameValue nameKey=".scheduleName" path="scheduleName" maxlength="100"/>
				
				<tags:nameValue2 nameKey=".exportPath">
					<tags:input path="exportPath"/>
					<cti:url var="infoImg" value="/WebConfig/yukon/Icons/information.gif"/>
                    <img src="${infoImg}" title="<cti:msg2 key=".exportPathInfo"/>"/>
				</tags:nameValue2>
				
				<tags:nameValue2 nameKey=".exportFileName">
					<tags:input path="exportFileName" maxlength="100"/>
					<img src="${infoImg}" title="<cti:msg2 key=".exportFileNameInfo"/>"/>
				</tags:nameValue2>
				
				<tags:nameValue2 nameKey=".appendDateToFileName" excludeColon="true">
					<form:checkbox path="appendDateToFileName" />
				</tags:nameValue2>
		
				<tags:nameValue2 nameKey=".scheduleCronString">
					<tags:cronExpressionData id="scheduleCronString" state="${cronExpressionTagState}" allowTypeChange="false" />
				</tags:nameValue2>
				
				<tags:inputNameValue nameKey=".notificationEmailAddresses" path="notificationEmailAddresses"/>
			</tags:nameValueContainer2>
		</tags:boxContainer2>
		
		<cti:button nameKey="cancel" href="view"/>
		<cti:button nameKey="submit" type="submit"/>
	</form:form>

</cti:standardPage>