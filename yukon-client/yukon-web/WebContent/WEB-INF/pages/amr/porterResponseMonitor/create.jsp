<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

	<%-- CREATE FORM --%>
	<form:form commandName="monitor" id="basicInfoForm"
		action="/spring/amr/porterResponseMonitor/create" method="post">

        <input type="hidden" name="stateGroup" value="${monitor.stateGroup}">

		<tags:formElementContainer nameKey="setup">
			<tags:nameValueContainer2
				style="border-collapse:separate;border-spacing:5px;">

				<%-- name --%>
				<tags:inputNameValue nameKey=".name" path="name" size="50" maxlength="50" />

            </tags:nameValueContainer2>
        </tags:formElementContainer>

		<%-- create / cancel --%>
		<div class="pageActionArea">
			<tags:slowInput2 formId="basicInfoForm" key="create" />
            <cti:button key="cancel" type="submit" name="cancel"/>
		</div>

	</form:form>

	<script type="text/javascript">
	    Event.observe(window, 'load', function() {
	        $$('#basicInfoForm input')[1].focus();
	    });
    </script>
</cti:standardPage>