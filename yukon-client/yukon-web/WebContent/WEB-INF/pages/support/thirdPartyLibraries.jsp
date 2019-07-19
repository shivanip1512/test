<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="support" page="thirdPartyLibraries">

    <tags:boxContainer2 nameKey="cpp">
        <c:set var="libraryList" value="${libraries.cppLibraries}" scope="request" />
        <jsp:include page="thirdPartyLibraryTable.jsp" />
    </tags:boxContainer2>

    <tags:boxContainer2 nameKey="java">
        <c:set var="libraryList" value="${libraries.javaLibraries}" scope="request" />
        <jsp:include page="thirdPartyLibraryTable.jsp" />
    </tags:boxContainer2>

    <tags:boxContainer2 nameKey="js">
        <c:set var="libraryList" value="${libraries.jsLibraries}" scope="request" />
        <jsp:include page="thirdPartyLibraryTable.jsp" />
    </tags:boxContainer2>

    <tags:boxContainer2 nameKey="icon">
        <c:set var="libraryList" value="${libraries.iconLibraries}" scope="request" />
        <jsp:include page="thirdPartyLibraryTable.jsp" />
    </tags:boxContainer2>

</cti:standardPage>