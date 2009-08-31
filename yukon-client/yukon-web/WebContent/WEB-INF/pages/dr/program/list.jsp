<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="programList">
    <cti:standardMenu menuSelection="details|programs"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.programList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.programList.breadcrumb.programs"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.programList.programs"/></h2>

    <%@ include file="programList.jspf" %>

</cti:standardPage>
