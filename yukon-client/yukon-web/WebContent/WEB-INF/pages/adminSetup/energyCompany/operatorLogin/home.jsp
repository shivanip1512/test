<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="operatorLogin.home">
    <script>
        function addUser(selectedItems, picker) {
            $('#userId').value = selectedItems[0][picker.idFieldName];
            $('#operatorLoginAddForm').submit();
        }
    
        $(document).on('click', '.js-remove', function() {                
             var userId = $(this).attr("userId");
             var ecId = $(this).attr("ecId");
             var data = {};
             data.userId = userId;
             data.ecId = ecId;
             $.post(yukon.url('/admin/energyCompany/operatorLogin/remove'), data).done(function(data) {  
                 $("#"+userId).hide();
           });
        });
        
        $(function() {
            $("a.toggle_status").click(function(event) {
                event.preventDefault();// We don't want the anchor href to submit a GET request
                var anchor = $(event.currentTarget);
                var icon = anchor.find('.icon');
                var originalClasses = icon.attr('class');
                icon.addClass("icon-spinner");
                $.ajax({
                    type: "POST",
                    url: anchor.attr("href"),
                    datatype: "json"
                }).done(function (jsonResponse, textStatus, jqXHR) {
                    icon.removeClass("icon-accept");
                    icon.removeClass("icon-delete");
                    icon.removeClass("icon-spinner");
                    icon.addClass(jsonResponse.icon);
                    $('.icon-accept').html(yg.iconSvg.iconAccept);
                    $('.icon-delete').html(yg.iconSvg.iconDelete);
                    icon.attr("title",jsonResponse.loginStatus);
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    icon.removeClass("icon-spinner");
                    icon.attr("class", originalClasses);
                });
            });
        });
</script>
    <div style="min-width: 400px;" class="fl">
            <div id="operatorLoginList" class="scroll-lg">
                <table class="compact-results-table row-highlighting with-form-controls clearfix">
                    <thead>
                    <th><i:inline key=".username" /></th>
                    <th><i:inline key=".loginEnabled" /></th>
                    <th></th>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                    <c:forEach items="${operatorLogins}" var="login">
                        <tr id="${login.userID}">
                            <td>
                                <cti:url var="operatorLoginViewUrl" value="${baseUrl}/view">
                                    <cti:param name="ecId" value="${ecId}" />
                                    <cti:param name="operatorLoginId" value="${login.userID}" />
                                </cti:url>
                                <a href="${operatorLoginViewUrl}">${fn:escapeXml(login)}</a></td>
                            <td>
                                <cti:url var="operatorLoginUpdateUrl" value="${baseUrl}/toggleOperatorLoginStatus">
                                    <cti:param name="ecId" value="${ecId}" />
                                    <cti:param name="operatorLoginId" value="${login.userID}" />
                                </cti:url>
                                <c:if test="${currentUserId != login.userID}">
                                    <c:choose>
                                        <c:when test="${login.loginStatus == 'DISABLED'}">
                                            <c:set var="cssClass" value="icon-delete" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="cssClass" value="icon-accept" />
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="${operatorLoginUpdateUrl}" class="button toggle_status" title="${login.loginStatus}"><cti:icon icon="${cssClass}"/></a>
                                </c:if>
                                <c:if test="${currentUserId == login.userID}">
                                    <a class="button" disabled="disabled" title="<i:inline key=".unableToDeleteCurrentUser"/>"><cti:icon icon="icon-accept"/></a>
                                </c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${currentUserId == login.userID}">
                                        <c:set var="disabled" value="true" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="disabled" value="false" />
                                    </c:otherwise>
                                </c:choose>
                                <cti:button id="remove_${login.userID}" name="remove" ecId="${ecId}" userId="${login.userID}" renderMode="buttonImage" icon="icon-remove" classes="fr js-remove" disabled="${disabled}" />
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <cti:url var="operatorLoginCreateUrl" value="${baseUrl}/new">
                <cti:param name="ecId" value="${ecId}" />
            </cti:url>
            <cti:url var="operatorLoginAddUrl" value="${baseUrl}/add" />
            <div class="action-area">
                <form id="operatorLoginAddForm" action="${operatorLoginAddUrl}">
                    <input type="hidden" name="ecId" value="${ecId}" /> <input type="hidden" name="userId" id="userId" />
                    <tags:pickerDialog type="ecOperatorCandidatePicker" id="ecOperatorCandidatePicker" destinationFieldId="userId" linkType="button" nameKey="add" icon="icon-add" endAction="addUser" immediateSelectMode="true" />
                </form>
                <cti:button nameKey="create" icon="icon-plus-green" href="${operatorLoginCreateUrl}" />
            </div>
    </div>

</cti:standardPage>