<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="title" required="true" type="java.lang.String"%>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>

<img onclick="$('${uniqueId}').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
					
<tags:simplePopup id="${uniqueId}" title="${title}" onClose="$('${uniqueId}').toggle();">
     <jsp:doBody/>
</tags:simplePopup>