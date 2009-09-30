<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="title" required="true" type="java.lang.String"%>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>

<a href="javascript:void(0);" onclick="$('${uniqueId}').toggle();">
<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
</a>

<tags:simplePopup id="${uniqueId}" title="${title}">
     <jsp:doBody/>
</tags:simplePopup>