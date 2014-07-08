<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="estimatedLoad">
    <cti:includeScript link="/JavaScript/yukon.dr.formula.js"/>

    <cti:tabbedContentSelector mode="section">
        <cti:msg2 var="formulasTab" key='.formulas'/>
        <cti:tabbedContentSelectorContent selectorName="${formulasTab}">
           <%@ include file="_formulasTable.jsp" %>
            <div class="action-area">
               <cti:button icon="icon-plus-green" nameKey="newFormula" href="formula/create"/>
            </div>
        </cti:tabbedContentSelectorContent>

        <cti:msg2 var="assignmentsTab" key='.assignments'/>
        <cti:tabbedContentSelectorContent selectorName="${assignmentsTab}">
           <tags:sectionContainer2 nameKey="applianceCategories">
               <%@ include file="_appCatAssignmentsTable.jsp" %>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="gears">
               <%@ include file="_gearAssignmentsTable.jsp" %>
            </tags:sectionContainer2>
        </cti:tabbedContentSelectorContent>
    </cti:tabbedContentSelector>
</cti:standardPage>