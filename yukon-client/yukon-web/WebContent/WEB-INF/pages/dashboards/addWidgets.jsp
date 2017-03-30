<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:msgScope paths="modules.dashboard">

    <div class="column-6-18">
        <div class="column one">
            <input type="text" placeholder="<cti:msg2 key='yukon.common.search.placeholder'/>"/>
            <br/><br/>
            <h3><i:inline key=".addWidgets.widgetType"/></h3>
            <table class="compact-results-table full-width">
                <c:forEach var="widgetCategory" items="${widgetCategories}">
                    <tr>
                    <td><a><i:inline key="${widgetCategory.formatKey}"/></a></td>
                    <td><span class="badge">15</span></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <div class="column two nogutter" style="padding-left:20px;border-left:1px solid #ccc;">
            <table class="compact-results-table row-highlighting">
                <c:forEach var="widgetType" items="${widgetTypes}">
                    <tr style="border-bottom:1px solid #ccc">
                        <td><cti:dashboardImage image="${widgetType.imageName}"/></td>    
                        <td><b class="wsnw"><i:inline key="${widgetType.formatKey}"/></b><br/><br/><i:inline key="${widgetType.descriptionKey}"/></td>    
                        <td style="width:25%;" class="wsnw"><cti:button icon="icon-plus-green" nameKey="addWidgets.addWidget"/></td>            
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>

</cti:msgScope>

