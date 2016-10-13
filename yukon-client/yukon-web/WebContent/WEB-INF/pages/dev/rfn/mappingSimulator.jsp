<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="dev" page="rfnTest.viewMappingSimulator">

<script>
yukon.namespace('yukon.dev.mappingSimulator');

yukon.dev.mappingSimulator = (function() {
    var _initialized = false,

    mod = {
        init : function() {
            if (_initialized) return;
            
            $(document).on('click', '#populateDatabase', function () {
                var form = $('#mapping-form');
                form.attr("action", "populateMappingDatabase");
                form.submit();
            });
            
            $(document).on('click', '#updateSettings', function () {
                var form = $('#mapping-form');
                form.attr("action", "updateMappingSettings");
                form.submit();
            });
            
            $(document).on('click', '#stopSimulator', function () {
                var form = $('#mapping-form');
                form.attr("action", "stopMappingSimulator");
                form.submit();
            });
            
            _initialized = true;
        },

    };
    return mod;
}());

$(function() {
    yukon.dev.mappingSimulator.init();
});

</script>

    <form:form id="mapping-form" action="startMappingSimulator" commandName="currentSettings" method="POST">
        <cti:csrfToken/>
    
    <div class="column-12-12 clearfix">
        <div class="column one">

    <tags:sectionContainer title="Parent Node Settings">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Node SN">
                <tags:input path="parentData.nodeSN"/>
            </tags:nameValue>
            <tags:nameValue name="Node Mac Address">
                <tags:input path="parentData.nodeMacAddress"/>
            </tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>
    
    <tags:sectionContainer title="Neighbor Data Settings">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Address">
                <tags:input path="neighborData.neighborAddress"/>
            </tags:nameValue>
            <tags:nameValue name="Flags">
                <c:forEach var="flag" items="${neighborFlags}">
                    <c:set var="checked" value=""/>
                    <c:if test="${fn:contains(currentSettings.neighborData.neighborFlags, flag)}">
                        <c:set var="checked" value="checked"/>
                    </c:if>
                    <input type="checkbox" name="neighborFlag_${flag}" ${checked}/><i:inline key="yukon.web.modules.operator.mapNetwork.neighborFlagType.${flag}"/><br>
                </c:forEach>
            </tags:nameValue>
            <tags:nameValue name="Link Cost">
                <tags:input path="neighborData.neighborLinkCost"/>
            </tags:nameValue>
            <tags:nameValue name="Number of Samples">
                <tags:input path="neighborData.numSamples"/>
            </tags:nameValue>
            <tags:nameValue name="ETX Band">
                <tags:input path="neighborData.etxBand"/>
            </tags:nameValue>
            <tags:nameValue name="Link Rate">
                <tags:input path="neighborData.linkRate"/>
            </tags:nameValue>
            <tags:nameValue name="Link Power">
                <tags:input path="neighborData.linkPower"/>
            </tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>
    
    </div>
    
    <div class="column two nogutter">
    
    <tags:sectionContainer title="Primary Route Settings">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Destination Address">
                <tags:input path="routeData.destinationAddress"/>
            </tags:nameValue>
            <tags:nameValue name="Next Hop Address">
                <tags:input path="routeData.nextHopAddress"/>
            </tags:nameValue>
            <tags:nameValue name="Total Cost">
                <tags:input path="routeData.totalCost"/>
            </tags:nameValue>
            <tags:nameValue name="Hop Count">
                <tags:input path="routeData.hopCount"/>
            </tags:nameValue>
            <tags:nameValue name="Flags">
                <c:forEach var="flag" items="${routeFlags}">
                    <c:set var="checked" value=""/>
                    <c:if test="${fn:contains(currentSettings.routeData.routeFlags, flag)}">
                        <c:set var="checked" value="checked"/>
                    </c:if>
                    <input type="checkbox" name="routeFlag_${flag}" ${checked}/><i:inline key="yukon.web.modules.operator.mapNetwork.routeFlagType.${flag}"/><br>
                </c:forEach>
            </tags:nameValue>
            <tags:nameValue name="Route Color">
                <tags:input path="routeData.routeColor"/>
            </tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>
    
    </div>
    </div>

        <cti:button id="populateDatabase" busy="true" label="Populate Database"/>
    
        <c:if test="${simulatorRunning}">
            <cti:button id="updateSettings" busy="true" label="Update Settings"/>
        </c:if>
        
    <c:if test="${not simulatorRunning}">
        <cti:button label="Start Simulator" busy="true" type="submit"/>
    </c:if>
    
    </form:form>
    
    <c:if test="${simulatorRunning}">
        <cti:button id="stopSimulator" busy="true" label="Stop Simulator"/>
    </c:if>
</cti:standardPage>