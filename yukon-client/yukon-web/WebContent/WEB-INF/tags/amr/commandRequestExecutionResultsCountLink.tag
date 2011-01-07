<%@ attribute name="commandRequestExecutionId" required="true" type="java.lang.Integer"%>
<%@ attribute name="commandRequestExecutionUpdaterType" required="true" type="java.lang.String"%>
<%@ attribute name="linkedInitially" required="true" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:uniqueIdentifier var="id" prefix="commandRequestResultsCountLinkTagId_"/>

<script type="text/javascript">

	function setCount_${id}() {
		
		return function(data) {
			var deviceCount = data.get('deviceCount');

			var countWithLinkName = 'countWithLink_' + '${id}';
			var countWithoutLinkName = 'countWithoutLink_' + '${id}';
			
			if ($(countWithLinkName) != undefined && $(countWithoutLinkName) != undefined) { // function is called before these may exists, avoid js error
			
				if (deviceCount > 0) {
					$(countWithLinkName).show();
					$(countWithoutLinkName).hide();
				} else {
					$(countWithLinkName).hide();
					$(countWithoutLinkName).show();
				}
			}
        };
	}
	function processDevices_${id}() {
		$('processDevices_${id}').submit();
	}

</script>

<form id="processDevices_${id}" action="/spring/common/commandRequestExecutionResults/processDevices" method="get">
	<input type="hidden" name="commandRequestExecutionId" value="${commandRequestExecutionId}">
	<input type="hidden" name="commandRequestExecutionUpdaterType" value="${commandRequestExecutionUpdaterType}">
</form>

<c:choose>
	<c:when test="${commandRequestExecutionId > 0}">
	
		<cti:dataUpdaterCallback function="setCount_${id}()" initialize="true" deviceCount="COMMAND_REQUEST_EXECUTION/${commandRequestExecutionId}/${commandRequestExecutionUpdaterType}" />
	
		<div id="countWithLink_${id}" <c:if test="${not linkedInitially}">style="display:none;"</c:if>>
		
			<a href="javascript:void(0);" onclick="processDevices_${id}();">
				<cti:dataUpdaterValue type="COMMAND_REQUEST_EXECUTION" identifier="${commandRequestExecutionId}/${commandRequestExecutionUpdaterType}"/>
			</a>
			
		</div>
		
		<div id="countWithoutLink_${id}" <c:if test="${linkedInitially}">style="display:none;"</c:if>>
			
			<cti:dataUpdaterValue type="COMMAND_REQUEST_EXECUTION" identifier="${commandRequestExecutionId}/${commandRequestExecutionUpdaterType}"/>
		
		</div>
		
	</c:when>
	<c:otherwise>
		N/A
	</c:otherwise>
</c:choose>