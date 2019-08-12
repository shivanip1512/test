<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <tags:setFormEditMode mode="${mode}" />
    
    <cti:url var="action" value="/dr/setup/loadProgram/gear/save" />
    <div class="js-program-gear-container">
        <form:form modelAttribute="programGear" action="${action}" method="post" id="js-program-gear-form">
        <input type="hidden" name="tempGearId" id="temp-gear-id"/>
            <div class="js-help-section">
                <span class="fr js-help-btn-span dn">
                    <cti:button renderMode="image" icon="icon-help" classes="fr js-help-btn"/>
                </span>
    
                <cti:url var="simpleThermostatRampingGraph" value="/WebConfig/yukon/SimpleThermostatRampingGraph.png"/>
                <c:set var="simpleThermostatRampingGraphCss" value="width:250 px; height: 100px; display:block; margin-left:auto; margin-right:auto;"/>
    
                <cti:url value="/WebConfig/yukon/ThermostatRampingGraph.png" var="thermostatRampingGraph"/>
                <c:set var="thermostatRampingGraphCss" value="width:250 px; height: 100px; display:block; margin-left:auto; margin-right:auto;"/>
    
                <tags:alertBox type="info" classes="dn js-user-message js-simple-thermostat-ramping-alert-box" 
                               includeCloseButton="true" imgUrl="${simpleThermostatRampingGraph}" imgCss="${simpleThermostatRampingGraphCss}"/>
                <tags:alertBox type="info" classes="dn js-user-message js-thermostat-ramping-alert-box" 
                               includeCloseButton="true" imgUrl="${thermostatRampingGraph}" imgCss="${thermostatRampingGraphCss}"/>
            </div>
            <cti:csrfToken />
            <input type="hidden" name="programGear" value="${selectedGearType}">
            <input id="js-programType" type="hidden" name="programType" value="${programType}" />
            <tags:sectionContainer2 nameKey="general">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".gearName">
                        <tags:input id="gearName" path="gearName" size="25" maxlength="60" autofocus="autofocus" />
                        <br>
                        <cti:msg2 var="gearNameLbl" key=".gearName"/>
                        <span id="gearNameIsBlankError" class="error dn">
                            <i:inline key="yukon.web.error.fieldrequired" arguments="${gearNameLbl}"/>
                        </span>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".gearType">
                        <c:choose>
                            <c:when test="${showGearTypeOptions}">
                                <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl" />
                                <tags:selectWithItems items="${gearTypes}" id="controlMethod" path="controlMethod" defaultItemLabel="${selectLbl}"
                                    defaultItemValue="" />
                                <br>
                                <span id="gearTypeIsRequiredError" class="error dn">
                                    <cti:msg2 key=".gearType" var="gearTypeLbl"/>
                                    <i:inline key="yukon.web.error.fieldrequired" arguments="${gearTypeLbl}"/>
                                </span>
                            </c:when>
                            <c:otherwise>
                                <i:inline key="${programGear.controlMethod}"/>
                                <form:hidden path="controlMethod" value="${selectedGearType}" />
                            </c:otherwise>
                        </c:choose> 
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <div id='js-gear-fields-container'>
                <!-- Include jsp for Gear type -->
                <c:if
                    test="${selectedGearType == 'SmartCycle' ||
                        selectedGearType == 'TrueCycle' || selectedGearType == 'MagnitudeCycle' || selectedGearType == 'TargetCycle'}">
                    <%@ include file="smartCycle.jsp"%>
                </c:if>
                <c:if test="${selectedGearType == 'SepCycle'}">
                    <%@ include file="sepCycle.jsp"%>
                </c:if>
                <c:if test="${selectedGearType == 'MasterCycle'}">
                    <%@ include file="masterCycle.jsp"%>
                </c:if>
                <c:if test="${selectedGearType == 'TimeRefresh'}">
                    <%@ include file="timeRefresh.jsp"%>
                </c:if>
                <c:if test="${selectedGearType == 'EcobeeCycle'}">
                    <%@ include file="ecobeeCycle.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'HoneywellCycle'}">
                    <%@ include file="honeywellCycle.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'ItronCycle'}">
                    <%@ include file="itronCycle.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'NestStandardCycle'}">
                    <%@ include file="nestStandardCycle.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'ThermostatRamping'}">
                    <%@ include file="thermostatRamping.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'SimpleThermostatRamping'}">
                    <%@ include file="simpleThermostatRamping.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'SepTemperatureOffset'}">
                    <%@ include file="sepTempreatureOffset.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'Rotation'}">
                       <%@ include file="rotation.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'BeatThePeak'}">
                    <%@ include file="beatThePeak.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'Latching'}">
                    <%@ include file="latching.jsp" %>
                </c:if>
                <c:if test="${selectedGearType == 'NoControl'}">
                       <%@ include file="noControl.jsp" %>
                </c:if>
            </div>
        </form:form>
    </div>
</cti:msgScope>