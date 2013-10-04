<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="estimatedLoad">
    <cti:includeScript link="/JavaScript/drFormula.js"/>
    <cti:includeScript link="/JavaScript/yukon/ui/confirm_dialog_manager.js"/>

    <cti:tabbedContentSelector mode="section">
        <cti:msg2 var="formulasTab" key='.formulas'/>
        <cti:tabbedContentSelectorContent selectorName="${formulasTab}">
            <div class="f-drFormula-replaceViaAjax clearfix">
               <%@ include file="_formulasTable.jsp" %>
            </div>
            <div class="actionArea">
               <cti:button icon="icon-plus-green" nameKey="newFormula" href="formula/create"/>
            </div>
        </cti:tabbedContentSelectorContent>

        <cti:msg2 var="assignmentsTab" key='.assignments'/>
        <cti:tabbedContentSelectorContent selectorName="${assignmentsTab}">
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
        </cti:tabbedContentSelectorContent>
    </cti:tabbedContentSelector>
</cti:standardPage>