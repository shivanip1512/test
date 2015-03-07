<%@ tag body-content="empty" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="path" description="A base for the spring binding path." %>
<cti:default var="path" value=""/>

<script type="text/javascript">
function rateChanged() {
    // There is Java code doing this also in SA305.java.
    var newRate = $('#rate').val(),
        rateMember = '',
        rateFamily = '';
    if (!isNaN(newRate) && newRate > 0) {
        rateMember = newRate % 16;
        rateFamily = Math.floor(newRate / 16);
    }
    $('#rateMember').html(rateMember);
    $('#rateFamily').html(rateFamily);
}
</script>

<tags:sectionContainer2 nameKey="sa305Addresses">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".utility">
            <tags:input path="${path}addressingInfo.utility" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".group">
            <tags:input path="${path}addressingInfo.group" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".division">
            <tags:input path="${path}addressingInfo.division" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".substation">
            <tags:input path="${path}addressingInfo.substation" size="6" maxlength="15"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".rate">
            <tags:input id="rate" path="${path}addressingInfo.rate" size="6" maxlength="15"
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