<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="startPath" required="false" description="Spring binding path"%>
<%@ attribute name="startName" description="Name of the field in the supplied object"%>
<%@ attribute name="startValue" type="java.lang.Object" description="Default: null. Sets the initial value of the input." %>
<%@ attribute name="endPath" required="false" description="Spring binding path"%>
<%@ attribute name="endName" description="Name of the field in the supplied object"%>
<%@ attribute name="endValue" type="java.lang.Object" description="Default: null. Sets the initial value of the input." %>
<%@ attribute name="maxDate" required="false" type="java.lang.Object" description="Default: null. Sets the maxDate for this dateRange" %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="Default: false. Determines if the input is disabled." %>
<%@ attribute name="cssClass" type="java.lang.String" description="Class added to the input of the widget" %>
<%@ attribute name="cssDialogClass" type="java.lang.String" description="Class added to the outer dialog div" %>

<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<div class="f-dateRange oh">
	<dt:date path="${pageScope.startPath}"
			 name="${pageScope.startName}"
			 value="${pageScope.startValue}"
			 disabled="${pageScope.disabled}"
			 cssClass="${pageScope.cssClass} f-dateStart"
			 cssDialogClass="${pageScope.cssDialogClass}"
			 maxDate="${pageScope.endValue}"/>
	<jsp:doBody/>
<!-- Only force includes on the first input -->
	<dt:date path="${pageScope.endPath}"
			 name="${pageScope.endName}"
			 value="${pageScope.endValue}"
			 disabled="${pageScope.disabled}"
			 cssClass="${pageScope.cssClass} f-dateEnd"
			 cssDialogClass="${pageScope.cssDialogClass}"
			 minDate="${pageScope.startValue}" 
			 maxDate="${pageScope.maxDate}" />
</div>