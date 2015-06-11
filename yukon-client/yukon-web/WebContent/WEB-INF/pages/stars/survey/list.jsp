<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="adminSetup" page="survey.list">

    <cti:includeScript link="/resources/js/pages/yukon.surveys.edit.js"/>

    <div class="stacked" data-url="listTable">
        <%@ include file="listTable.jsp" %>
    </div>

    <div class="action-area">
        <cti:url var="sampleXmlUrl" value="sampleXml"/>
        <cti:button nameKey="sampleXml" href="${sampleXmlUrl}" icon="icon-page-white-code"/>
        <cti:button nameKey="add" icon="icon-add" data-popup="#add-survey-popup"/>
        <div data-dialog id="add-survey-popup" data-url="editDetails" 
            data-title="<cti:msg2 key=".addTitle"/>" data-event="yukon.survey.details.edit"></div>
    </div>
    
<script type="text/javascript">
$(function () {
    yukon.surveys.edit.init({hasBeenTaken : '${hasBeenTaken}'});
});
</script>
</cti:standardPage>