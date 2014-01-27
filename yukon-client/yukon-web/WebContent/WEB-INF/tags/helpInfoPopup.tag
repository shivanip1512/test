<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="title" required="true" type="java.lang.String"%>

<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>
<cti:icon icon="icon-help" id="icon_${uniqueId}" classes="cp fn"/>
<tags:simplePopup id="${uniqueId}" title="${title}" on="#icon_${uniqueId}" options="{'width': 600}"><jsp:doBody/></tags:simplePopup>