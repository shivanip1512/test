<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

	<cti:standardMenu menuSelection="meters" />

	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink url="/spring/meter/start" title="Metering" />
		<cti:crumbLink><i:inline key=".title" /></cti:crumbLink>
	</cti:breadCrumbs>

	<h2><i:inline key=".title" /></h2>
	<br>

	<%-- CREATE FORM --%>
	<form:form commandName="monitor" id="basicInfoForm"
		action="/spring/amr/porterResponseMonitor/create" method="post">

		<form:hidden path="${monitor.monitorId}" />

		<tags:formElementContainer nameKey="setup">
			<tags:nameValueContainer2
				style="border-collapse:separate;border-spacing:5px;">

				<%-- name --%>
				<tags:inputNameValue nameKey=".name" path="name" size="50" maxlength="50" />

                <%-- state group --%>
                <tags:nameValue2 nameKey=".stateGroup">
                    <select name="stateGroup">
                        <c:forEach items="${stateGroups}" var="stateGroup">
                            <option value="${stateGroup}">${stateGroup.stateGroupName}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>

			</tags:nameValueContainer2>
		</tags:formElementContainer>

		<%-- create / cancel --%>
		<div class="pageActionArea">
			<tags:slowInput2 formId="basicInfoForm" key="create" />
			<input type="submit" name="cancel" class="formSubmit" value="<cti:msg2 key=".cancel"/>">
		</div>

	</form:form>

	<script type="text/javascript">
	    Event.observe(window, 'load', function() {
	        $$('#basicInfoForm input')[1].focus();
	    });
    </script>
</cti:standardPage>