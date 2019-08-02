<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dr" page="setup.constraint.${mode}" >
    <tags:setFormEditMode mode="${mode}" />
    
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <!-- Create -->
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
                <cti:url var="createUrl" value="/dr/setup/constraint/create" />
                <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" href="${createUrl}"/>
            </cti:checkRolesAndProperties>
            <!-- Edit -->
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="UPDATE">
                <cti:url var="editUrl" value="/dr/setup/constraint/${programConstraint.id}/edit"/>
                <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" />
            </cti:checkRolesAndProperties>
            <!-- Delete -->
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="OWNER">
                <li class="divider"></li>
                <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-hide-dropdown"
                               id="delete-option" data-ok-event="yukon:constraint:delete"/>
            
                <d:confirm on="#delete-option" nameKey="confirmDelete" argument="${programConstraint.name}" />
                <cti:url var="deleteUrl" value="/dr/setup/constraint/${programConstraint.id}/delete"/>
                <form:form id="delete-constraint-form" action="${deleteUrl}" method="delete" modelAttribute="programConstraint">
                    <tags:hidden path="name"/>
                    <cti:csrfToken/>
                </form:form>
            </cti:checkRolesAndProperties>
        </div>
    </cti:displayForPageEditModes>
    
    <cti:url var="action" value="/dr/setup/constraint/save" />
    <form:form modelAttribute="programConstraint" action="${action}" method="post">
        <cti:csrfToken />
        <form:hidden path="id"/>
        <cti:msg2 var="seconds" key="yukon.common.units.SECONDS"/>
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    <tags:input path="name" size="25" maxlength="60" autofocus="autofocus"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".seasonSchedule">
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <tags:selectWithItems items="${seasonSchedules}" id="type" path="seasonSchedule.id" defaultItemValue="0" itemLabel="name" itemValue="id" />
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <c:choose>
                            <c:when test="${programConstraint.seasonSchedule.id == 0}">
                                <i:inline key=".none"/>
                            </c:when>
                            <c:otherwise>
                                ${fn:escapeXml(programConstraint.seasonSchedule.name)}
                            </c:otherwise>
                        </c:choose>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".maxActivate">
                    <tags:numeric path="maxActivateSeconds" size="5" maxlength="5" units="${seconds}" minValue="0" maxValue="99999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".maxDailyOps">
                    <tags:numeric path="maxDailyOpsSeconds" size="5" maxlength="5" units="${seconds}" minValue="0" maxValue="99999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".minActivate">
                    <tags:numeric path="minActivateSeconds" size="5" maxlength="5" units="${seconds}" minValue="0" maxValue="99999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".minRestart">
                    <tags:numeric path="minRestartSeconds" size="5" maxlength="5" units="${seconds}" minValue="0" maxValue="99999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".daySelection">
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <div class="button-group">
                        <c:forEach var="daySelection" items="${daySelections}">
                            <tags:check id="js-${daySelection}" path="daySelection" value="${daySelection}" key="yukon.common.day.${daySelection}.short" classes="M0"/>
                        </c:forEach>
                    </div>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="VIEW">
                    <c:choose>
                        <c:when test="${empty daySelections}">
                            <i:inline key=".none"/>
                        </c:when>
                        <c:otherwise>
                            <div class="button-group">
                                <c:forEach var="daySelection" items="${daySelections}" varStatus="status">
                                    <i:inline key="yukon.common.day.${daySelection}.short"/>
                                    <c:if test="${!status.last}">
                                        <i:inline key="yukon.common.comma"/>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </cti:displayForPageEditModes>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".holidaySchedule">
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <tags:selectWithItems items="${holidaySchedules}" id="js-holiday-schedule" path="holidaySchedule.id" defaultItemValue="0" itemLabel="name" itemValue="id"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <c:choose>
                            <c:when test="${programConstraint.holidaySchedule.id == 0}">
                                <i:inline key=".none"/>
                            </c:when>
                            <c:otherwise>
                                ${fn:escapeXml(programConstraint.holidaySchedule.name)}
                            </c:otherwise>
                        </c:choose>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".holidayUsage">
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <div class="button-group">
                            <tags:radio path="holidayUsage" key=".modules.dr.setup.constraint.EXCLUDE" classes="left yes ML0" value="EXCLUDE"/>
                            <tags:radio path="holidayUsage" key=".modules.dr.setup.constraint.FORCE" classes="right yes ML0" value="FORCE"/>
                        </div>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <c:choose>
                            <c:when test="${programConstraint.holidayUsage == 'NONE'}">
                                <i:inline key=".none"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key="yukon.common.modules.dr.setup.constraint.${programConstraint.holidayUsage}"/>
                            </c:otherwise>
                        </c:choose>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <tags:sectionContainer2 nameKey="maxHourAllowance">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".maxHoursDaily">
                    <tags:numeric path="maxHoursDaily" size="5" maxlength="5" minValue="0" maxValue="99999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".maxHoursMonthly">
                    <tags:numeric path="maxHoursMonthly" size="5" maxlength="5" minValue="0" maxValue="99999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".maxHoursAnnually">
                    <tags:numeric path="maxHoursAnnually" size="5" maxlength="5" minValue="0" maxValue="99999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".maxHoursSeasonal">
                    <tags:numeric path="maxHoursSeasonal" size="5" maxlength="5" minValue="0" maxValue="99999"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" />
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url var="viewUrl" value="/dr/setup/constraint/${programConstraint.id}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button id="cancel-btn" nameKey="cancel" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.constraint.js" />
</cti:standardPage>