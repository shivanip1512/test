<%@ attribute name="popupId" required="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url var="filter" value="/WebConfig/yukon/Icons/filter.gif"/>
<c:url var="filterOver" value="/WebConfig/yukon/Icons/filter_over.gif"/>

<div style="padding-bottom:5px;">
	<a href="javascript:void(0);" onclick="$('${popupId}').toggle();" style="text-decoration:none;color:#06C;">
		<img src="${filter}" onmouseover="javascript:this.src='${filterOver}'" onmouseout="javascript:this.src='${filter}'">
		Filter
	</a>
</div>