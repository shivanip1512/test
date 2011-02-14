<%@ attribute name="popupId" required="true" %>
<%@ attribute name="defaultFilterInput" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url var="filter" value="/WebConfig/yukon/Icons/filter.gif"/>
<c:url var="filterOver" value="/WebConfig/yukon/Icons/filter_over.gif"/>

<a href="javascript:void(0);" onclick="showSimplePopup('${popupId}', '${pageScope.defaultFilterInput}');" style="text-decoration:none;color:#06C;">
	<img src="${filter}" class="logoImage" onmouseover="javascript:this.src='${filterOver}'" onmouseout="javascript:this.src='${filter}'">
	<span class="clickableText">Filter</span>
</a>
