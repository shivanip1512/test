<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="programList">
    
    <tags:simpleDialog id="drDialog"/>

    <c:set var="baseUrl" value="/dr/program/list"/>
    <%@ include file="programList.jspf" %>

</cti:standardPage>
