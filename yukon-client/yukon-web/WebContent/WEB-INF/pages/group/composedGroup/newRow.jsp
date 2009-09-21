<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
<tags:composedGroupItem groupDataJson="${groupDataJson}"/>
