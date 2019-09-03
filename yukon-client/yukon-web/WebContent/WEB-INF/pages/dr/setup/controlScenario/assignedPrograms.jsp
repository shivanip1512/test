<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.controlScenario">
    <h3 class="dib"><i:inline key=".assignedPrograms.title"/></h3>
    <div class="bordered-div oa" style="height:90%">
        <table id="js-assigned-programs-table" class="compact-results-table dashed">
            <thead>
                <tr>
                    <th width="5%"/>
                    <th width="40%"><i:inline key=".name"/></th>
                    <th width="20%"><i:inline key=".startOffset"/></th>
                    <th width="20%"><i:inline key=".stopOffset"/></th>
                    <th width="15%"><i:inline key=".startGear"/></th>
                </tr>
            </thead>
            <tbody>
                <form:form modelAttribute="controlScenario">
                    <c:forEach var="program" items="${controlScenario.allPrograms}" varStatus="status">
                        <tr id ="js-assigned-program-${program.programId}" data-id="${program.programId}">
                            <tags:hidden path="allPrograms[${status.count-1}].programId"/>
                            <td><cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove"/></td>
                            <td class="wbba vam">
                                <cti:deviceName deviceId="${program.programId}"/>
                            </td>
                            <td><dt:timeOffset path="allPrograms[${status.count-1}].startOffsetInMinutes"/></td>
                            <td><dt:timeOffset path="allPrograms[${status.count-1}].stopOffsetInMinutes"/></td>
                            <td class="wbba">
                                <tags:selectWithItems items="${program.gears}" path="allPrograms[${status.count-1}].gears[0].id"
                                                      itemLabel="name" itemValue="id"/>
                            </td>
                        </tr>
                    </c:forEach>
               </form:form>
            </tbody>
        </table>
    </div>
</cti:msgScope>