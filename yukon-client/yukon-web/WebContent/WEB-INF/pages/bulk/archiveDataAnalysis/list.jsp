<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage module="tools" page="bulk.analysis.list">
    <cti:includeScript link="/JavaScript/yukon.data.analysis.js"/>
    
    <div data-url="<cti:url value="page"/>">
        <%@ include file="listFragment.jsp" %>
    </div>
</cti:standardPage>