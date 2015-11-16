<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:standardPage module="amr" page="phaseDetect.testSettings">

    <cti:url var="saveTestSettingsUrl" value="/amr/phaseDetect/saveTestSettings"/>
    <form action="${saveTestSettingsUrl}" method="post">
        <cti:csrfToken/>
        <tags:sectionContainer2 nameKey="testParameters">
            <tags:nameValueContainer2>
                <cti:msg2 var="substation" key=".substation"/>
                <cti:msg2 var="intervalLength" key=".intervalLength"/>
                <cti:msg2 var="deltaVoltage" key=".deltaVoltage"/>
                <cti:msg2 var="numOfIntervals" key=".numOfIntervals"/>
                <tags:nameValue2 nameKey=".substation">
                    ${fn:escapeXml(substationName)}
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".intervalLength">
                   <select id="intervalLength" name="intervalLength">
                       <option value="60">60</option>
                       <option value="45">45</option>
                       <option value="30" selected="selected">30</option>
                       <option value="15">15</option>
                   </select>
               </tags:nameValue2>
               <tags:nameValue2 nameKey=".deltaVoltage">
                   <select id="deltaVoltage" name="deltaVoltage">
                       <option value="4">+4</option>
                       <option value="3">+3</option>
                       <option value="2" selected="selected">+2</option>
                       <option value="1">+1</option>
                       <option value="-1">-1</option>
                       <option value="-2">-2</option>
                       <option value="-3">-3</option>
                       <option value="-4">-4</option>
                   </select>
               </tags:nameValue2>
               <tags:nameValue2 nameKey=".numOfIntervals">
                   <select id="numIntervals" name="numIntervals">
                       <option value="6" selected="selected">6</option>
                       <option value="5">5</option>
                       <option value="4">4</option>
                   </select>
               </tags:nameValue2>
           </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <div class="page-action-area">
            <cti:button nameKey="next" type="submit" name="next" classes="primary action"/>
            <cti:button nameKey="cancelTest" type="submit" name="cancel"/>
        </div>
    </form>
</cti:standardPage>