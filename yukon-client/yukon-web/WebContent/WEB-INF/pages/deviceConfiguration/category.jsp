<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="tools" page="configs.category.${mode}">
    <tags:setFormEditMode mode="${mode}"/>
    
    <form:form commandName="categoryEditBean" id="categoryForm" action="saveCategory">
        <form:hidden path="categoryType"/>
        <form:hidden path="categoryId"/>
    
        <h3 class="stacked"><i:inline key=".${categoryTemplate.categoryName}.title"/></h3>
    
        <span style="line-height: 26px;">
            <tags:nameValueContainer2 tableClass="stacked">
                <tags:inputNameValue nameKey=".categoryName" path="categoryName" size="50" maxlength="60"/>
            </tags:nameValueContainer2>
        </span>
        
        <div class="stacked">
            <h3><i:inline key=".categoryFields"/></h3>
        </div>
        
        <c:forEach var="field" items="${categoryTemplate.fields}">
            <div class="setting box">
                <div class="setting_name box fl">${field.displayName}</div>
                <div class="setting_details box detail fl">
                    <c:choose>
                        <c:when test="${empty field.inputType}">
                            <!-- InputType is empty for fields that represent maps -->
                            <table>
                                <c:forEach var="rate" items="${field.inputTypes}" varStatus="loopStatus">
                                    <tr>
                                        <td>
                                            <i:inline key=".${categoryTemplate.categoryName}.${rate.field}"/>
                                        </td>
                                        <td>
                                            <c:set var="isMidnight" value="${loopStatus.index == 0}"/>
                                            <c:choose>
                                                <c:when test="${!isMidnight}">
                                                    <cti:displayForPageEditModes modes="VIEW">
                                                        ${categoryEditBean.scheduleInputs[(field.fieldName)].rateInputs[(rate.field)].time}
                                                    </cti:displayForPageEditModes>
                                                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                                                        <tags:simpleInputType input="${rate.timeType}" path="scheduleInputs[${field.fieldName}].rateInputs[${rate.field}].time"/>
                                                    </cti:displayForPageEditModes>
                                                </c:when>
                                                <c:otherwise>
                                                    <i:inline key=".${categoryTemplate.categoryName}.midnightTime"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <i:inline key="yukon.web.modules.tools.configs.rate"/>
                                        </td>
                                        <td>
                                            <cti:displayForPageEditModes modes="VIEW">
                                                ${categoryEditBean.scheduleInputs[(field.fieldName)].rateInputs[(rate.field)].rate}
                                            </cti:displayForPageEditModes>
                                            <cti:displayForPageEditModes modes="CREATE,EDIT">
                                                <tags:simpleInputType input="${rate.rateType}" path="scheduleInputs[${field.fieldName}].rateInputs[${rate.field}].rate"/>
                                            </cti:displayForPageEditModes>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:choose>
                                    <c:when test="${field.isEnum}">
                                        <c:forEach var="option" items="${field.inputType.optionList}">
                                            <c:if test="${option.value == categoryEditBean.categoryInputs[(field.fieldName)]}">
                                                <cti:msg2 key="${option}"/>
                                            </c:if>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        ${categoryEditBean.categoryInputs[(field.fieldName)]}
                                    </c:otherwise>
                                </c:choose>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="CREATE,EDIT">
                                <tags:simpleInputType id="categoryInputs[${field.fieldName}]" input="${field.inputType}" path="categoryInputs[${field.fieldName}]"/>
                            </cti:displayForPageEditModes>
                        </c:otherwise>
                    </c:choose>
                    <div id="fieldErrors">
                        <spring:bind path="categoryInputs[${field.fieldName}]">
                            <c:if test="${status.error}">
                                <form:errors path="categoryInputs[${field.fieldName}]" cssClass="errorMessage"/>
                            </c:if>
                        </spring:bind>
                    </div>
                    <div class="description">
                        <span class="detail">${field.description}</span>
                    </div>
                </div>
            </div>
        </c:forEach>
        
        <cti:displayForPageEditModes modes="VIEW">
            <div class="pageActionArea">
                <cti:checkRolesAndProperties value="${editingRoleProperty}">
                    <cti:url value="editCategory" var="editUrl">
                        <cti:param name="categoryId" value="${categoryEditBean.categoryId}"/>
                    </cti:url>
                    <cti:button nameKey="edit" href="${editUrl}" icon="icon-pencil"/>
                </cti:checkRolesAndProperties>
                <cti:button nameKey="back" href="home"/>
            </div>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="EDIT">
            <cti:checkRolesAndProperties value="${editingRoleProperty}">
                <div class="pageActionArea">
                    <dialog:confirm on="#save" nameKey="confirmSave"/>
                    <cti:button nameKey="save" id="save" type="submit" classes="primary action"/>
                    <c:if test="${isDeletable}">
                        <dialog:confirm on="#remove" nameKey="confirmRemove"/>
                        <cti:url var="deleteUrl" value="deleteCategory">
                            <cti:param name="categoryId" value="${categoryEditBean.categoryId}"/>
                        </cti:url>
                        <cti:button nameKey="remove" id="remove" href="${deleteUrl}"/>
                    </c:if>
                    <cti:url var="viewUrl" value="viewCategory">
                        <cti:param name="categoryId" value="${categoryEditBean.categoryId}"/>
                    </cti:url>
                    <cti:button nameKey="cancel" href="${viewUrl}"/>
                </div>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="CREATE">
            <cti:checkRolesAndProperties value="${editingRoleProperty}">
                <div class="pageActionArea">
                    <cti:button nameKey="create" type="submit" classes="primary action"/>
                    <cti:button nameKey="cancel" href="home"/>
                </div>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
    </form:form>
</cti:standardPage>