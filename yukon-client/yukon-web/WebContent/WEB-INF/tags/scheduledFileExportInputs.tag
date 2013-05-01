<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="cronExpressionTagState" required="true" rtexprvalue="true" type="com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState" %>

<cti:msgScope paths="yukon.web.modules.amr.scheduledFileExport.inputs">

	<tags:inputNameValue nameKey=".scheduleName" path="scheduleName" maxlength="100"/>
	
	<tags:nameValue2 nameKey=".exportPath">
		<tags:input path="exportPath" />
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

</cti:msgScope>