<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="name" required="true" type="java.lang.String"%>

<cti:includeScript link="/JavaScript/radioContentSelectorContainer.js"/>

<cti:uniqueIdentifier var="thisId" prefix="radioContentSelectorContainer_" />

<script type="text/javascript">

	function setup_${thisId}() {
		setupRadioControl('${thisId}', '${name}');
	}
		
	Event.observe (window, 'load', setup_${thisId});

</script>
        
<div class="titledContainer sectionContainer">

	<div class="titleBar sectionContainer_titleBar">
	
		<span class="titleBar sectionContainer_title">
	
			${label}${(not empty label) ? ':&nbsp;':''}
	
		</span>
		
		<span id="radioControl_${thisId}" style="white-space:nowrap;"></span>
		
	</div>
	
	<div id="contentContainer_${thisId}" class="content sectionContainer sectionContainer_content">
	
		<jsp:doBody/>
	
	</div>

</div>