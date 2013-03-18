<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage page="archivedValueExporterScheduleSetup" module="amr">
	
	<form:form id="scheduleForm" commandName="exportData" action="doSchedule">
		<tags:boxContainer2 nameKey="adeParameters">
			<tags:nameValueContainer2 id="adeParametersContainer">
				<c:if test="${not empty jobId}">
					<input type="hidden" name="jobId" value="${jobId}">
				</c:if>
				<input type="hidden" name="formatId" value="${exportFormat.formatId}">
				<input type="hidden" name="attribute" value="${attribute}">
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
				<c:if test="${not empty attribute}">
					<tags:nameValue2 nameKey=".attribute">
						<i:inline key="${attribute}"/>
					</tags:nameValue2>
				</c:if>
				<tags:nameValue2 nameKey=".dataRange">
					<tags:dataRange value="${dataRange}"/>
				</tags:nameValue2>
			</tags:nameValueContainer2>
		</tags:boxContainer2>
		
		<tags:boxContainer2 nameKey="exportParameters">
			<tags:nameValueContainer2 id="exportParametersContainer">
				<tags:nameValue2 nameKey=".scheduleName">
					<form:input path="scheduleName" />
				</tags:nameValue2>
				<tags:nameValue2 nameKey=".exportPath">
					<form:input path="exportPath" />
					<cti:url var="infoImg" value="/WebConfig/yukon/Icons/information.gif"/>
                    <img src="${infoImg}" title="<cti:msg2 key=".exportPathInfo"/>"/>
				</tags:nameValue2>
				<tags:nameValue2 nameKey=".exportFileName">
					<form:input path="exportFileName" />
					<img src="${infoImg}" title="<cti:msg2 key=".exportFileNameInfo"/>"/>
				</tags:nameValue2>
				<tags:nameValue2 nameKey=".appendDateToFileName" excludeColon="true">
					<form:checkbox path="appendDateToFileName" />
				</tags:nameValue2>
		
				<tags:nameValue2 nameKey=".scheduleCronString">
					<tags:cronExpressionData id="scheduleCronString" state="${cronExpressionTagState}" allowTypeChange="false" />
				</tags:nameValue2>
		
				<tags:nameValue2 nameKey=".notificationEmailAddresses">
					<form:input path="notificationEmailAddresses" />
				</tags:nameValue2>
			</tags:nameValueContainer2>
		</tags:boxContainer2>
		
		<cti:button nameKey="cancel" href="view"/>
		<cti:button nameKey="submit" type="submit"/>
	</form:form>

</cti:standardPage>