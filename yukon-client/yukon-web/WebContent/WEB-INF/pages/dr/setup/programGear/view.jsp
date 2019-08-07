<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <tags:setFormEditMode mode="${mode}" />
    <cti:url var="action" value="/dr/setup/loadProgram/gear/save" />
    <div class="js-program-gear-container">
        <form:form modelAttribute="programGear" action="${action}" method="post" id="js-program-gear-form">
            <cti:csrfToken />
            <input type="hidden" name="programGear" value="${selectedGearType}">
            <input type="hidden" name="programType" value="${programType}" />
            <tags:sectionContainer2 nameKey="general">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".gearName">
                        <tags:input id="gearName" path="gearName" size="25" maxlength="60" autofocus="autofocus" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".gearType">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl" />
                            <tags:selectWithItems items="${gearTypes}" id="controlMethod" path="controlMethod" defaultItemLabel="${selectLbl}"
                                defaultItemValue="" />
                        </cti:displayForPageEditModes>
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
            </div>
        </form:form>
    </div>

</cti:msgScope>