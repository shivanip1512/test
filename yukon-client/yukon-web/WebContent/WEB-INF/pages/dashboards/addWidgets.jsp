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
                <tr>
                    <td><a class="js-show-all cp"><i:inline key=".widgetCategory.ALL"/></a></td>
                    <td class="fr"><span class="badge">${totalWidgets}</span></td>
                </tr>
                <c:forEach var="mapEntry" items="${widgetMap}">
                    <tr>
                        <td><a class="js-show-category cp" data-category="${mapEntry.key}"><i:inline key="${mapEntry.key.formatKey}"/></a></td>
                        <td class="fr"><span class="badge">${mapEntry.value.size()}</span></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <div class="column two nogutter" style="padding-left:20px;border-left:1px solid #ccc;">
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
                            <td style="width:25%;" class="wsnw">
                                <cti:button data-type="${widgetType}" classes="js-widget-add" icon="icon-plus-green" nameKey="addWidgets.addWidget"/>
                                <br/><br/>
                                <div class="success js-widget-added-msg-${widgetType} dn"><i:inline key=".addWidgets.widgetAdded"/></div>
                            </td>            
                        </tr>
                    </c:forEach>
                 </c:forEach>
            </table>
        </div>
    </div>    

</cti:msgScope>

