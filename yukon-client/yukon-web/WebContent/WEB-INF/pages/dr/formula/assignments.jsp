<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="dr" page="assignments">
    <cti:includeScript link="/JavaScript/drFormula.js"/>
    <tags:formElementContainer nameKey="applianceCategories">
	     <div class="f-replaceViaAjax">
	        <%@ include file="_appCatAssignmentsTable.jsp" %>
	     </div>
     </tags:formElementContainer>
     <tags:formElementContainer nameKey="gears">
	     <div class="f-replaceViaAjax">
	        <%@ include file="_gearAssignmentsTable.jsp" %>
	     </div>
     </tags:formElementContainer>
</cti:standardPage>
