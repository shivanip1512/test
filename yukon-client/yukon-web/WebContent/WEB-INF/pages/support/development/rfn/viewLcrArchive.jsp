<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="rfnTest">

<tags:sectionContainer title="RFN LCR Archive Request Test">
    <form action="sendLcrArchiveRequest" method="post">
        <tags:nameValueContainer>
            <tags:nameValue name="Serial Number"><input name="serialFrom" type="text" value="1000"> to <input name="serialTo" type="text" value="1000"></tags:nameValue>
            <tags:nameValue name="Manufacturer">
                <select name="manufacturer">
                    <option value="CPS">CPS</option>
                </select>
            </tags:nameValue>
            <tags:nameValue name="Model">
                <select name="model">
                    <option value="RFN-LCR6200">RFN-LCR6200</option>
                    <option value="RFN-LCR6600">RFN-LCR6600</option>
                </select>
            </tags:nameValue>
        </tags:nameValueContainer>
        <div class="pageActionArea">
            <cti:button nameKey="send" type="submit" styleClass="f_blocker"/>
        </div>
    </form>
</tags:sectionContainer>

</cti:standardPage>