<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="estimatedLoad">
    <cti:includeScript link="JQUERY_COOKIE"/>
    <cti:includeScript link="/JavaScript/drFormula.js"/>
    <cti:includeScript link="/JavaScript/yukon/ui/confirm_dialog_manager.js"/>

    <div id="display_tabs" style="display:none">
        <ul>
            <li><a href="#formulasTab"><cti:msg2 key='.formulas'/></a></li>
            <li><a href="#assignmentsTab"><cti:msg2 key='.assignments'/></a></li>
        </ul>
        <div id="formulasTab">
            <div class="f-drFormula-replaceViaAjax clearfix">
               <%@ include file="_formulasTable.jsp" %>
            </div>
            <div class="actionArea">
               <cti:button icon="icon-plus-green" nameKey="newFormula" href="formula/create"/>
            </div>
        </div>
        <div id="assignmentsTab">
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
        </div>
    </div>
        
</cti:standardPage>