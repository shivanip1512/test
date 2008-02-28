<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:standardPage title="${param.title}" module="blank">
	<cti:standardMenu />
	
    <cti:breadCrumbs>
        <cti:crumbLink url="javascript:history.back();" title="Return" />
    </cti:breadCrumbs>
    
    <jsp:include page="${param.page}"/>
    
</cti:standardPage>
