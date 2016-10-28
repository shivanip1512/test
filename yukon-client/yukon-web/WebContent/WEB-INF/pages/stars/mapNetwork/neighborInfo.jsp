<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer2 tableClass="name-collapse">    
    <tags:nameValue2 nameKey=".device" valueClass="js-device"></tags:nameValue2>
    <tags:nameValue2 nameKey=".type" valueClass="js-type"></tags:nameValue2>
    <tags:nameValue2 nameKey=".status" valueClass="js-status"></tags:nameValue2>
    <tags:nameValue2 nameKey=".nodeSN" valueClass="js-neighbor-serialNumber"></tags:nameValue2>
    <tags:nameValue2 nameKey=".serialNumber" valueClass="js-serial-number"></tags:nameValue2>
    <tags:nameValue2 nameKey=".macAddress" valueClass="js-address"></tags:nameValue2>
    <tags:nameValue2 nameKey=".etxBand" valueClass="js-etx-band"></tags:nameValue2>
    <tags:nameValue2 nameKey=".linkCost" valueClass="js-link-cost"></tags:nameValue2>
    <tags:nameValue2 nameKey=".numSamples" valueClass="js-num-samples"></tags:nameValue2>
    <tags:nameValue2 nameKey=".flags" valueClass="js-flags"></tags:nameValue2>
    <tags:nameValue2 nameKey=".distance" valueClass="js-distance"></tags:nameValue2>
</tags:nameValueContainer2>
