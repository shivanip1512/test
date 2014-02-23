<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage module="tools" page="bulk.analysis.list">
    <cti:includeScript link="/JavaScript/yukon.data.analysis.js"/>
    
    <div class="f-table-container" data-reloadable>
        <%@ include file="listFragment.jsp" %>
    </div>
</cti:standardPage>