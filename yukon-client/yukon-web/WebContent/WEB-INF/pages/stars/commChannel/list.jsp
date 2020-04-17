<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="commChannel">
    <!-- Actions dropdown -->
    <!-- TODO : Replace the create url with create url -->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/stars/device/commChannel/list" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="create-option" href="${createUrl}"/>
    </div>

    <c:choose>
        <c:when test="${not empty commChannelList}">
            <cti:url var="listUrl" value = "/stars/device/commChannel/list"/>
            <div data-url="${listUrl}" data-static>
                <table class="compact-results-table row-highlighting">
                    <thead>
                        <tr>
                            <tags:sort column="${name}" />
                            <tags:sort column="${type}" />
                            <tags:sort column="${status}" />
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="commChannel" items="${commChannelList}">
                            <c:set var="cssClass" value="error" />
                            <cti:msg2 var="commChannelStatus" key="yukon.common.disabled"/>
                            <c:if test="${commChannel.enable}">
                                <c:set var="cssClass" value="success" />
                                <cti:msg2 var="commChannelStatus" key="yukon.common.enabled"/>
                            </c:if>
                            <tr>
                                <td width="50%">
                                    <c:choose>
                                        <c:when test="${commChannel.webSupportedType}">
                                            <!-- TODO : Replace the view url with comm channel view url -->
                                            <cti:url var="viewUrl" value="/stars/device/commChannel/list"/>
                                            <a href="${viewUrl}">${fn:escapeXml(commChannel.name)}</a>
                                        </c:when>
                                        <c:otherwise>
                                            ${fn:escapeXml(commChannel.name)}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><i:inline key="${commChannel.type}"/></td>
                                <td class="${cssClass}">${commChannelStatus}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:otherwise>
    </c:choose>
</cti:standardPage>