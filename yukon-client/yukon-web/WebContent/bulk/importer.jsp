<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<c:set var="pageTitle" value="The Bulk Importer Tool Has Moved!" />

<cti:standardPage title="${pageTitle}" module="blank">

	<cti:standardMenu menuSelection=""/>
	
    <br>
    <tags:boxContainer title="${pageTitle}" id="container" hideEnabled="false">

		<cti:url var="redirectUrl" value="/amr/bulkimporter/home"/>

		<br>
		${pageTitle} <u><a href="${redirectUrl}">Click here</a></u> to be redirected.
		<br><br>
		Please update your bookmark to the new location.
		<br><br>
	    
	</tags:boxContainer>

</cti:standardPage>