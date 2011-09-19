<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="rfnTest">

<script type="text/javascript">
function useRandom() {
    var checked = $('random').checked;
    if (checked) {
        $('static').disable();
    } else {
        $('static').enable();
    }
}
</script>

    <tags:sectionContainer title="RFN Meter Archive Request Test">
        <form action="sendMeterArchiveRequest" method="post">
            <tags:nameValueContainer>
                <tags:nameValue name="Serial Number"><input name="serialFrom" type="text" value="1000"> to <input name="serialTo" type="text" value="1000"></tags:nameValue>
                <tags:nameValue name="Manufacturer"><input name="manufacturer" type="text" value="LGYR"></tags:nameValue>
                <tags:nameValue name="Model"><input name="model" type="text" value="FocuskWh"></tags:nameValue>
                <tags:nameValue name="Wh Value"><input id="static" name="value" type="text" value="34543.4">  <input type="checkbox" id="random" onclick="useRandom()">  <label for="random">Random</label></tags:nameValue>
            </tags:nameValueContainer>
            <div clas="actionArea"><cti:button nameKey="send" type="submit"/></div>
        </form>
    </tags:sectionContainer>

</cti:standardPage>