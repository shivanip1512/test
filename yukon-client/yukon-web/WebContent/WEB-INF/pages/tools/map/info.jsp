<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.map">
<tags:nameValueContainer2 tableClass="name-collapse">
    <tags:nameValue2 nameKey=".device"><cti:paoDetailUrl yukonPao="${pao}" newTab="true">${pao.name}</cti:paoDetailUrl></tags:nameValue2>
    <tags:nameValue2 nameKey=".type">${fn:escapeXml(pao.paoIdentifier.paoType.paoTypeName)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".latitude">${location.latitude}</tags:nameValue2>
    <tags:nameValue2 nameKey=".longitude">${location.longitude}</tags:nameValue2>
</tags:nameValueContainer2>
</cti:msgScope>