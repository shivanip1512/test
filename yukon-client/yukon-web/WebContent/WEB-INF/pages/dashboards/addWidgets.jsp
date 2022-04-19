<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:msgScope paths="modules.dashboard">

    <div class="column-6-18">
        <div class="column one">
            <input type="text" class="js-search" placeholder="<cti:msg2 key='yukon.common.search.placeholder'/>"/>
            <br/><br/>
            <h4><i:inline key=".addWidgets.widgetCategory"/></h4>
            <table class="compact-results-table full-width">
                <tr class="cp js-show-all-row">
                    <td><input type="radio" name="js-widget-category" checked="checked"/></td>
                    <td><a class="js-show-all cp"><i:inline key=".widgetCategory.ALL"/></a></td>
                    <td class="fr txt-color-gray">${totalWidgets}</td>
                </tr>
                <c:forEach var="mapEntry" items="${widgetMap}">
                    <tr class="js-category-row cp" data-category="${mapEntry.key}">
                        <td><input type="radio" name="js-widget-category"/></td>
                        <td><a class="js-show-category cp" data-category="${mapEntry.key}"><i:inline key="${mapEntry.key.formatKey}"/></a></td>
                        <td class="fr txt-color-gray">${mapEntry.value.size()}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <div class="column two nogutter oa" style="padding-left:20px;border-left:1px solid #ccc;height: 350px;">
        <div class="js-no-widgets-found dn"><i:inline key=".addWidgets.noWidgetsFound"/></div>
            <table class="compact-results-table row-highlighting">
                <c:forEach var="mapEntry" items="${widgetMap}">
                    <c:forEach var="widgetType" items="${mapEntry.value}">
                        <tr style="border-bottom:1px solid #ccc" class="js-category-widgets js-${mapEntry.key}">
                            <td><cti:dashboardImage image="${widgetType.imageName}"/></td>
                            <td>
                                <b class="wsnw"><span class="js-name"><i:inline key="${widgetType.formatKey}"/></span></b>
                                <br/><br/><span class="js-description"><i:inline key="${widgetType.descriptionKey}"/></span>
                            </td>
                            <td style="width:25%;" class="wsnw vat">
                                <cti:button data-type="${widgetType}" classes="js-widget-add" icon="icon-plus-green" nameKey="addWidgets.addWidget"/>
                                <br/><br/>
                                <div class="success js-widget-added-msg-${widgetType} fl MT10 dn"><i:inline key=".addWidgets.widgetAdded"/></div>
                            </td>
                        </tr>
                    </c:forEach>
                 </c:forEach>
            </table>
        </div>
    </div>    

</cti:msgScope>

