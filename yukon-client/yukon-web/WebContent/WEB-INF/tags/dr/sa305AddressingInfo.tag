<%@ tag body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<script type="text/javascript">
function rateChanged() {
    // There is Java code doing this also in SA305.java.
    var newRate = jQuery('#rate').val(),
        rateMember = '',
        rateFamily = '';
    if (!isNaN(newRate) && newRate > 0) {
        rateMember = newRate % 16;
        rateFamily = Math.floor(newRate / 16);
    }
    jQuery('#rateMember').html(rateMember);
    jQuery('#rateFamily').html(rateFamily);
}
</script>

<tags:sectionContainer2 nameKey="sa305Addresses">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".utility">
            <tags:input path="addressingInfo.utility" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".group">
            <tags:input path="addressingInfo.group" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".division">
            <tags:input path="addressingInfo.division" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".substation">
            <tags:input path="addressingInfo.substation" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".rate">
            <tags:input id="rate" path="addressingInfo.rate" size="6" maxlength="15"
                onkeyup="rateChanged()" onblur="rateChanged()"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".rateMember">
            <span id="rateMember">${rateMember}</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".rateFamily">
            <span id="rateFamily">${rateFamily}</span>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<script type="text/javascript">rateChanged();</script>
