<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="adminSetup" page="survey.list">
    <cti:includeScript link="/JavaScript/yukon.dialog.ajax.js"/>
    <cti:includeScript link="/JavaScript/yukon.surveys.list.js"/>

    <div id="ajaxDialog"></div>

    <div class="stacked" data-reloadable>
        <%@ include file="listTable.jsp" %>
    </div>

    <div class="action-area">
        <cti:url var="sampleXmlUrl" value="sampleXml"/>
        <cti:button nameKey="sampleXml" href="${sampleXmlUrl}" icon="icon-page-white-code"/>
        <cti:button id="addSurveyBtn" nameKey="add" classes="f-blocker" icon="icon-add"/>
    </div>
</cti:standardPage>