<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vdTags" tagdir="/WEB-INF/tags/visualDisplays" %>

<cti:standardPage module="dr" page="loadManagement">
<cti:includeScript link="/JavaScript/yukon.dr.psd.js"/>

<%-- LAST TRANSMITTED --%>
<div class="stacked">
    <span class="name"><i:inline key=".lastTransmitted"/>&nbsp;</span>
    <span id="last-transmitted" class="value"><cti:formatDate type="BOTH" value="${now}"/></span>
</div>

<%-- POWER SUPPLIER DATA TABLE: Current Load, Currnt IH, Load To Peak, Peak IH Load --%>
<table class="results-table row-highlighting stacked-lg">
    
    <thead>
        <%-- header row --%>
        <tr>
            <th><i:inline key=".powerSupplier"/></th>
            <th colspan="2"><cti:msg2 key=".dataTypeEnum.CURRENT_LOAD"/></th>
            <th colspan="2"><cti:msg2 key=".dataTypeEnum.CURRENT_IH"/></th>
            <th colspan="2"><cti:msg2 key=".dataTypeEnum.LOAD_TO_PEAK"/></th>
            <th colspan="2"><cti:msg2 key=".dataTypeEnum.PEAK_IH_LOAD"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <%-- power supplier rows --%>
        <c:forEach var="powerSupplier" items="${powerSuppliers}">
            <tr>
                <td>${powerSupplier.powerSupplierType.description}</td>
                <vdTags:valueQuality pointId="${powerSupplier.currentLoadPointId}"/>
                <vdTags:valueQuality pointId="${powerSupplier.currentIhPointId}"/>
                <vdTags:valueQuality pointId="${powerSupplier.loadToPeakPointId}"/>
                <vdTags:valueQuality pointId="${powerSupplier.peakIhLoadPointId}"/>
            </tr>
        </c:forEach>
    </tbody>
</table>

<%-- HOURLY DATA TABLE: Current Day vs Peak Day --%>
<div class="oa">
    <table class="results-table row-highlighting">
        <thead>
            <%-- header row (hr + power supplier names) --%>
            <tr>
                <th>&nbsp;</th>
                
                <c:forEach var="powerSupplier" items="${powerSuppliers}">
                    <th colspan="4" class="supplier-loads-supplier tac">${powerSupplier.powerSupplierType.description}</th>
                </c:forEach>
                
                <th>&nbsp;</th>
            </tr>
            
            <%-- current vs peak day per power supplier row--%>
            <tr>
                <th class="wsnw vab tar" style="width: 60px;">
                    <cti:msg2 key=".hrEndLabel"/>
                </th>
                
                <c:forEach var="powerSupplier" items="${powerSuppliers}">
                    
                    <th colspan="2" class="tac wsnw supplier-loads-supplier">
                        <span class="fwb"><cti:msg2 key=".currentDayLabel"/></span>
                    </th>
                    
                    <th colspan="2" class="tac wsnw">
                        <span class="fwb"><cti:msg2 key=".peakDayLabel"/></span>
                        <br>
                        <cti:pointValue format="{time|MM/dd/yyyy}" pointId="${powerSupplier.peakIhLoadPointId}"/>
                        <br>
                        <cti:msg2 key=".hrLabel"/>&nbsp;
                        <cti:pointValue format="{time|HH zz}" pointId="${powerSupplier.peakIhLoadPointId}"/>
                    </th>
                    
                </c:forEach>
                
                <th class="wsnw vab tar" style="width: 60px;">
                    <cti:msg2 key=".hrEndLabel"/>
                </th>
            </tr>
        </thead>
        <tfoot></tfoot>
        
        <tbody>
            <%-- hourly data rows --%>
            <c:forEach var="i" begin="1" end="24">
                <tr>
                    <td class="tar fwb">${i}</td>
                    <c:forEach var="powerSupplier" items="${powerSuppliers}">
                        <vdTags:valueQuality pointId="${powerSupplier.todayIntegratedHourlyDataPointIdList[i-1]}"/>
                        <vdTags:valueQuality pointId="${powerSupplier.peakDayIntegratedHourlyDataPointIdList[i-1]}"/>
                    </c:forEach>
                    <td class="tar fwb">${i}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

</cti:standardPage>