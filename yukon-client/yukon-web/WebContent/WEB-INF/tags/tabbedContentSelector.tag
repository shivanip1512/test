<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/tabbedContentSelectorContainer.js"/>
<cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tabs.css"/>

<cti:uniqueIdentifier var="thisId" prefix="tabbedContentSelectorContainer_" />

<script type="text/javascript">

	function setup_${thisId}() {
		setupTabbedControl('${thisId}');
	}
		
	Event.observe (window, 'load', setup_${thisId});

</script>

<span id="tabbedControl_${thisId}"></span>

<div id="contentContainer_${thisId}">
	
	<jsp:doBody/>

</div>