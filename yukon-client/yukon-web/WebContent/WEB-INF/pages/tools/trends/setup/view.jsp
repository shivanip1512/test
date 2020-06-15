<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="trend.${mode}">
    <cti:msgScope paths="yukon.common,modules.tools.trend">
        <table class="js-point-setup-template-table dn">
            <tr class="js-template-row">
                <td class="js-device-name wbba">
                    <input type="hidden" name="trendSeries[?]" class="js-row-data" />
                    <span></span>
                </td>
                <td class="js-point-name wbba" />
                <td class="js-label wbba" />
                <td class="js-color">
                    <div class="small-rectangle dib"></div>
                    <span></span>
                </td>
                <td class="js-axis" />
                <td class="js-type" />
                <td class="js-multiplier" />
                <td class="js-style" />
                <td class="js-actions">
                    <div class="button-group">
                        <cti:button icon="icon-pencil" renderMode="buttonImage" classes="js-edit-point" />
                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove-point" />
                    </div>
                </td>
            </tr>
        </table>
        <cti:url value="/tools/trend/save" var="saveUrl" />
        <form:form modelAttribute="trendModel" method="POST" action="${saveUrl}">
            <cti:csrfToken />
            <cti:tabs>
                <cti:msg2 key=".setup" var="setupTxt" />
                <cti:tab title="${setupTxt}">
                    <tags:sectionContainer2 nameKey="general">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".name">
                                <tags:input path="name" maxlength="40" autofocus="autofocus" inputClass="w300" />
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                    <tags:sectionContainer2 nameKey="pointSetup">
                        <table class="compact-results-table dashed with-form-controls" id="js-point-setup-table">
                            <thead>
                                <tr>
                                    <th style="width: 18%"><i:inline key=".device"/></th>
                                    <th style="width: 18%"><i:inline key=".pointName"/></th>
                                    <th style="width: 18%"><i:inline key=".label"/></th>
                                    <th style="width: 11%"><i:inline key=".color"/></th>
                                    <th style="width: 5%"><i:inline key=".axis"/></th>
                                    <th style="width: 15%"><i:inline key=".type"/></th>
                                    <th style="width: 2%"><i:inline key=".multiplier"/></th>
                                    <th style="width: 2%"><i:inline key=".style"/></th>
                                    <th style="width: 11%"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${fn:length(trendModel.trendSeries) > 0}">
                                    <c:forEach var="trendSeries" items="${trendModel.trendSeries}" varStatus="status">
                                        <tr>
                                            <td class="js-device-name wbba">
                                                <cti:toJson object="${trendSeries}" var="seriesJson"/>
                                                <input type="hidden" name="trendSeries[${status.index}]" class="js-row-data" value="${seriesJson}"/>
                                                <span><cti:deviceNameFromPointId pointId="${trendSeries.pointId}"/></span>
                                            </td>
                                            <td class="js-point-name wbba">
                                                <cti:pointName pointId="${trendSeries.pointId}"/>
                                            </td>
                                            <td class="js-label wbba">
                                                ${fn:escapeXml(trendSeries.label)}
                                            </td>
                                            <td class="js-color">
                                                <div class="small-rectangle dib" style="background-color: ${trendSeries.color}"></div>
                                                <span><i:inline key="${trendSeries.color}"/></span>
                                            </td>
                                            <td class="js-axis">
                                                <i:inline key="${trendSeries.axis}" />
                                            </td>
                                            <td class="js-type">
                                                <i:inline key="${trendSeries.type}" />
                                            </td>
                                            <td class="js-multiplier">${trendSeries.multiplier}</td>
                                            <td class="js-style">
                                                <i:inline key="${trendSeries.style}" />
                                            </td>
                                            <td class="js-actions">
                                                <div class="button-group">
                                                    <cti:button icon="icon-pencil" renderMode="buttonImage" classes="js-edit-point" />
                                                    <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove-point" />
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:if>
                            </tbody>
                        </table>
                        <div class="action-area">
                            <cti:button nameKey="add" icon="icon-add" data-popup="#js-add-point-dialog" />
                        </div>
                        <cti:url value="/tools/trend/renderAddPointPopup" var="addPointUrl" />
                        <div id="js-add-point-dialog"
                                data-dialog data-title="<i:inline key=".addPoint" />"
                                data-url="${addPointUrl}"
                                data-event="yukon:trend:setup:addPoint"
                                data-load-event="yukon:trend:setup:pointPopupLoaded"></div>
                    </tags:sectionContainer2>
                </cti:tab>
                <cti:msg2 key=".additionalOptions" var="additionalOptionsTxt" />
                <cti:tab title="${additionalOptionsTxt}">
                    TODO: Will work on it in other YUK...
                </cti:tab>
            </cti:tabs>
            <div class="page-action-area">
                <cti:button type="submit" nameKey="save" classes="primary action" busy="true" />
                <cti:url value="/tools/trends" var="cancelUrl" />
                <cti:button nameKey="cancel" href="${cancelUrl}" />
            </div>
        </form:form>
    </cti:msgScope>
    <cti:includeScript link="/resources/js/pages/yukon.tools.trend.setup.js" />
    <dt:pickerIncludes />
</cti:standardPage>