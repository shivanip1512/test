<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope  paths="modules.dr.home, modules.dr">
    <tags:sectionContainer2 nameKey="nest">
        <div class="stacked">
            <tags:nameValueContainer2 naturalWidth="false">
                <tags:nameValue2 nameKey=".nest.issues">
                    <c:set var="deviceClass" value="${discrepancies > 0 ? 'label-danger' : 'label-default'}"/>
                    <span class="label ${deviceClass}">${discrepancies}</span>&nbsp;
                    <i:inline key=".nest.discrepancies"/>&nbsp;
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
        <a href="<cti:url value="/dr/nest"/>"><i:inline key=".details"/></a>
    </tags:sectionContainer2>
</cti:msgScope>
