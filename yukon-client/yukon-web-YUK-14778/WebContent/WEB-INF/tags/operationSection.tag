<%@ attribute name="sectionName" required="true" %>
<%@ attribute name="sectionImageName" required="true" %>

<div>
	<div class="sectionHeader">${sectionName}</div>
	<div id="${sectionImageName}"></div>
	<div class="sectionLinks">
		
		<jsp:doBody/>
	
		<div style="clear:both"></div>
	</div>
</div>
<div style="clear:both"></div>
	