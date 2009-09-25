<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="loadGroupList">
    <cti:standardMenu menuSelection="details|loadGroups"/>

    <tags:simpleDialog id="drDialog"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.loadGroupList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.loadGroupList.breadcrumb.loadGroups"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.loadGroupList.loadGroups"/></h2>
    <br>

    <c:set var="baseUrl" value="/spring/dr/loadGroup/list"/>
    <%@ include file="loadGroupList.jspf" %>

</cti:standardPage>
