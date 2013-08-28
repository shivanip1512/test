<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="assignments">
    <cti:includeScript link="/JavaScript/drFormula.js"/>
    <tags:formElementContainer nameKey="applianceCategories">
	     <div class="f-drFormula-replaceViaAjax">
	        <%@ include file="_appCatAssignmentsTable.jsp" %>
	     </div>
     </tags:formElementContainer>
     <tags:formElementContainer nameKey="gears">
	     <div class="f-drFormula-replaceViaAjax">
	        <%@ include file="_gearAssignmentsTable.jsp" %>
	     </div>
     </tags:formElementContainer>
</cti:standardPage>
