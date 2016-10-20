<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="rfnTest">

<tags:sectionContainer title="RFN Relay Archive Request Test">
    <form action="sendRelayArchiveRequest" method="post">
        <cti:csrfToken/>
        <tags:nameValueContainer>
            <tags:nameValue name="Serial Number"><input name="serialFrom" type="text" value="1000"> to <input name="serialTo" type="text" value="1000"></tags:nameValue>
            <tags:nameValue name="Manufacturer">
                <select name="manufacturer">
                    <option value="EATON">EATON</option>
                </select>
            </tags:nameValue>
            <tags:nameValue name="Model">
                <select name="model">
                    <option value="RFRelay">RFRelay</option>
                </select>
            </tags:nameValue>
        </tags:nameValueContainer>
        <div class="page-action-area">
            <cti:button nameKey="send" type="submit" classes="js-blocker"/>
        </div>
    </form>
</tags:sectionContainer>

</cti:standardPage>