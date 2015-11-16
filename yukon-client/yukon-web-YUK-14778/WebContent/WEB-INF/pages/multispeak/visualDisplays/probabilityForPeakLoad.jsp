<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vdTags" tagdir="/WEB-INF/tags/visualDisplays" %>

<cti:standardPage module="dr" page="probabilityForPeakLoad">
<cti:includeScript link="/resources/js/pages/yukon.dr.psd.js"/>

<%-- LAST TRANSMITTED --%>
<div class="stacked">
    <span class="name"><i:inline key=".lastTransmitted"/>&nbsp;</span>
    <span id="last-transmitted" class="value"><cti:formatDate type="BOTH" value="${now}"/></span>
</div>

<%-- HOURLY DATA TABLE: Current Day vs Peak Day --%>
<div class="oa">
    <table class="results-table row-highlighting">
        <%-- header row (hr + power supplier names) --%>
        <thead>
            <tr>
                <th>&nbsp;</th>
                <c:forEach var="powerSupplier" items="${powerSuppliers}">
                    <th colspan="2" class="tac">${powerSupplier.powerSupplierType.description}</th>
                </c:forEach>
                <th>&nbsp;</th>
            </tr>
            
            <%-- current vs peak day per power supplier row--%>
            <tr>
                <th class="wsnw vab"><i:inline key=".hrEndLabel"/></th>
                
                <c:forEach var="powerSupplier" items="${powerSuppliers}">
                    <th class="tac wsnw">
                        <span class="fwb"><cti:msg2 key=".todayLabel"/></span>
                        <br>
                        <cti:pointValue format="{time|MM/dd/yyyy}" 
                            pointId="${powerSupplier.todayLoadControlPredictionPointIdList[0]}"/>
                    </th>
                    <th class="tac wsnw">
                        <span class="fwb"><cti:msg2 key=".tomorrowLabel"/></span>
                        <br>
                        <cti:pointValue format="{time|MM/dd/yyyy}" 
                            pointId="${powerSupplier.tomorrowLoadControlPredictionPointIdList[0]}"/>
                    </th>
                </c:forEach>
                
                <th class="wsnw vab"><i:inline key=".hrEndLabel"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        
        <tbody>
            <%-- hourly data rows --%>
            <c:forEach var="i" begin="1" end="24">
                <tr>
                    <td class="tar fwb">${i}</td>
                    <c:forEach var="powerSupplier" items="${powerSuppliers}">
                        <vdTags:valueTodayTomorrowPercentages 
                            todayPointId="${powerSupplier.todayLoadControlPredictionPointIdList[i-1]}" 
                            tomorrowPointId="${powerSupplier.tomorrowLoadControlPredictionPointIdList[i-1]}"/>
                    </c:forEach>
                    <td class="tar fwb">${i}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

</cti:standardPage>