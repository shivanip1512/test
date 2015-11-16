<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest">
    <tags:sectionContainer title="RFN Meter Location Archive Request Test">
        <form action="sendLocationArchiveRequest" method="post">
            <cti:csrfToken/>
            <tags:nameValueContainer>
                
                <tags:nameValue name="Serial Number">
                    <input name="serialFrom" type="text" value="1000"> 
                    to 
                    <input name="serialTo" type="text" value="1000">
                </tags:nameValue>
                
                <tags:nameValue name="Manufacturer">
                    <select name="manufacturer">
                        <option value="LGYR">LGYR</option>
                        <option value="Eka">Eka</option>
                        <option value="EE">EE</option>
                        <option value="GE">GE</option>
                        <option value="ELO">ELO</option>
                        <option value="ITRN">ITRN</option>
                    </select>
                </tags:nameValue>
                
                <tags:nameValue name="Model">
                    <select name="model">
                        <option value="FocuskWh">FocuskWh</option>
                        <option value="water_sensor">water_sensor</option>
                        <option value="water_sensor">water_node</option>
                        <option value="A3R">A3R</option>
                        <option value="Centron">Centron</option>
                        <option value="kV2">kV2</option>
                        <option value="FocusAXD">FocusAXD</option>
                        <option value="FocusAXR">FocusAXR</option>
                        <option value="2131T">2131T</option>
                        <option value="2131xT">2131xT</option>
                        <option value="C2SX">C2SX</option>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Latitude"><input name="latitude" type="text" value="45.091807"></tags:nameValue>
                <tags:nameValue name="Longitude"><input name="longitude" type="text" value="-93.37958"></tags:nameValue>
            </tags:nameValueContainer>
            <div class="page-action-area">
                <cti:button nameKey="send" type="submit" classes="js-blocker"/>
            </div>
        </form>
    </tags:sectionContainer>
    <tags:sectionContainer title="RFN LCR Location Archive Request Test">
        <form action="sendLocationArchiveRequest" method="post">
            <cti:csrfToken/>
            <tags:nameValueContainer>
                <tags:nameValue name="Serial Number"><input name="serialFrom" type="text" value="1000"> to <input name="serialTo" type="text" value="1000"></tags:nameValue>
                <tags:nameValue name="Manufacturer">
                    <select name="manufacturer">
                        <option value="CPS">CPS</option>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Model">
                    <select name="model">
                        <option value="1077">LCR6200</option>
                        <option value="1082">LCR6600</option>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Latitude"><input name="latitude" type="text" value="44.971387"></tags:nameValue>
                <tags:nameValue name="Longitude"><input name="longitude" type="text" value="-93.514173"></tags:nameValue>
            </tags:nameValueContainer>
            <div class="page-action-area">
                <cti:button nameKey="send" type="submit" classes="js-blocker"/>
            </div>
        </form>
    </tags:sectionContainer>
</cti:standardPage>