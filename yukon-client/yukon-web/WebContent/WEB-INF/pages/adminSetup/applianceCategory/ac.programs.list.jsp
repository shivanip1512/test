<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:msgScope paths="modules.adminSetup.applianceCategory">
    <table class="compact-results-table with-form-controls dashed">
        <thead>
            <tr>
                <c:choose>
                    <c:when test="${applianceCategory.consumerSelectable}">
                        <th>${orderColumn.text}</th>
                        <th>${nameColumn.text}</th>
                        <th></th>
                    </c:when>
                    <c:otherwise>
                        <tags:sort column="${nameColumn}" />
                        <th></th>
                    </c:otherwise>
                </c:choose>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="program" items="${assignedPrograms.resultList}" varStatus="status">
                <tr data-program="${program.assignedProgramId}" data-order="${program.programOrder}">
                    <c:if test="${applianceCategory.consumerSelectable}">
                        <td>${program.programOrder}</td>
                    </c:if>
                    <td><dr:assignedProgramName assignedProgram="${program}" /></td>
                    <td>
                        <div class="button-group fr wsnw oh">
                            <c:if test="${isEditable}">
                                <cti:button classes="js-edit fn" icon="icon-pencil"
                                    renderMode="buttonImage" />
                                <cti:button classes="js-remove fn" icon="icon-remove"
                                    renderMode="buttonImage" />
                            </c:if>
                            <c:if test="${isEditable && applianceCategory.consumerSelectable}">
                                <c:set var="upDisabled" value="${status.first ? 'true' : 'false'}" />
                                <c:set var="downDisabled" value="${status.last ? 'true' : 'false'}" />
                                <cti:button classes="js-up fn" icon="icon-bullet-go-up"
                                    renderMode="buttonImage" disabled="${upDisabled}" />
                                <cti:button classes="js-down fn" icon="icon-bullet-go-down"
                                    renderMode="buttonImage" disabled="${downDisabled}" />
                            </c:if>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:msgScope>