<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<style>
#neighborsLegend {
    text-align:center;
    border:1px solid #ccc;
    padding:2px;
    margin-top:20px;
    font-size:11px;
    box-shadow: 0px 0px 2px #888;
}
#neighborsLegend hr {
    width:30px;
    display:inline-block;
    margin-bottom:3px;
    margin-top: 5px;
}
#neighborsLegend span {
    padding-left:5px;
    padding-right:10px;
}
</style>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">
       
    <div id="neighborsLegend" class="dn js-legend-neighbors">
        <span><b><i:inline key=".neighborsLineColor"/></b></span><br/>
        <hr class="js-etx-1"/><span><i:inline key=".etxBand1"/></span>
        <hr class="js-etx-2"/><span><i:inline key=".etxBand2"/></span>
        <hr class="js-etx-3"/><span><i:inline key=".etxBand3"/></span><br/>
        <hr class="js-etx-4"/><span><i:inline key=".etxBand4"/></span>
        <hr class="js-etx-5"/><span><i:inline key=".etxBand5"/></span><br/>              
        <span><b><i:inline key=".neighborsLineThickness"/></b></span><br/>
        <hr style="border-top:1px solid;"/><span><i:inline key=".numSamplesZeroToFifty"/></span>
        <hr style="border-top:2px solid;"/><span><i:inline key=".numSamplesFiftyOneToFiveHundred"/></span>
        <hr style="border-top:3px solid;"/><span><i:inline key=".numSamplesOverFiveHundred"/></span>
    </div>

</cti:msgScope>
