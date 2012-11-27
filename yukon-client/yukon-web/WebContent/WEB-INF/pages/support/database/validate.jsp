<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
<cti:standardMenu menuSelection="database|compare"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	<cti:crumbLink url="/support/" title="Support" />
    <cti:crumbLink>Database Validate</cti:crumbLink>
</cti:breadCrumbs>

<c:choose>
	<c:when test="${not empty msgError}">
		${msgError}
	</c:when>
	<c:otherwise>

		<script>
            jQuery(function() {
				new Ajax.Updater('compareResults', '/support/database/validate/results');
			});
		</script>
		
		<div id="compareResults">
			<c:if test="${displayOracleWarning}">
				Validation of your database schema is in progress (this process may take several minutes).
			</c:if>
			<br>
			loading... <img id="slowInputProcessImg${uniqueId}" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
		</div>

	</c:otherwise>
</c:choose>
</cti:standardPage>
