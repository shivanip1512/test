<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="title" required="true" type="java.lang.String"%>

<cti:uniqueIdentifier var="uniqueId" prefix="helpInfoPopup_"/>
<cti:icon icon="icon-help" id="icon_${uniqueId}" classes="cp"/>
<tags:simplePopup id="${uniqueId}" title="${title}" on="#icon_${uniqueId}" options="{'width': 600}"><jsp:doBody/></tags:simplePopup>