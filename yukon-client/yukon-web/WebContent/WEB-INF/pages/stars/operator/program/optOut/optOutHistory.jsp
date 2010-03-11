<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>

<cti:standardPage module="operator" page="optOut">
    <cti:standardMenu />

<%-- 
	<i18n:sectionContainer titleKey=".optOutHistory">
--%>
    <i18n:boxContainer titleKey=".optOutHistory">
        <dr:optOutHistory previousOptOutList="${previousOptOutList}" />
    </i18n:boxContainer>

<%--
	</i18n:sectionContainer>
--%>
</cti:standardPage>