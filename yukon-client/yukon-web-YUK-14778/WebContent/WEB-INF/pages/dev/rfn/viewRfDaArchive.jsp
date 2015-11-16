<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="rfnTest">

<tags:sectionContainer title="RF DA Archive Request">
    <form action="sendRfDaArchiveRequest" method="post">
        <cti:csrfToken/>
        <tags:nameValueContainer>
            <tags:nameValue name="Serial Number">
                <input name="serial" type="text" />
            </tags:nameValue>
            <tags:nameValue name="Manufacturer">
                <select name="manufacturer">
                    <option value="CPS">CPS</option>
                </select>
            </tags:nameValue>
            <tags:nameValue name="Model">
                <select name="model">
                    <option value="VR-CL7">VR-CL7</option>
                    <option value="RECL-F4D">RECL-F4D</option>
                    <option value="CBC-8000">CBC-8000</option>
                </select>
            </tags:nameValue>
        </tags:nameValueContainer>
        <div class="page-action-area">
            <cti:button nameKey="send" type="submit" classes="js-blocker"/>
        </div>
    </form>
</tags:sectionContainer>

</cti:standardPage>