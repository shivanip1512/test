<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="loadGroupList">

    <tags:simpleDialog id="drDialog"/>
    <dr:favoriteIconSetup/>

    <c:set var="baseUrl" value="/dr/loadGroup/list"/>
    <%@ include file="loadGroupList.jspf" %>

</cti:standardPage>
