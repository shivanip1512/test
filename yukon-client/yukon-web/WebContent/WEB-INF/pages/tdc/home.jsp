<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="tdc.home">

<cti:includeScript link="JQUERY_COOKIE"/>

<script type="text/javascript">
jQuery(function() {
    jQuery('#display_tabs').tabs({'class': 'section', 'cookie' : {}});
    
    jQuery('#b_mute, #b_unmute').click (function(e) {
        if (e.currentTarget.id == 'b_mute') {
            jQuery('#b_mute').hide();
            jQuery('#b_unmute').show();
        } else {
            jQuery('#b_mute').show();
            jQuery('#b_unmute').hide();
        }
    });
});
</script>

<style type="text/css">
.alarm-table tr:hover .f-dropdown_outer_container {visibility: visible;}
.alarm-text {}
.display-list li {padding: 2px;}
.display-list .divider {padding:none; margin:2px; list-style: none;}
</style>

<div class="column_8_16">
    <div class="column one">
        
        <div id="display_tabs" class="section">
            <ul>
                <li><a href="#custom_displays">Custom Displays</a></li>
                <li><a href="#events_displays">Event Displays</a></li>
            </ul>
            
            <div id="events_displays" class="clearfix scrollingContainer_large liteContainer">
                <ul class="simple-list display-list">
                    <li><a href="#">All Alarms</a></li>
                    <li><a href="#">Event Viewer</a></li>
                    <li><a href="#">SOE Log</a></li>
                    <li><a href="#">TAG Log</a></li>
                </ul>
                <hr>
                <ul class="simple-list display-list">
                    <li><a href="#">Alarm</a></li>
                    <li><a href="#">Load Control Status</a></li>
                    <li><a href="#">Category 3</a></li>
                    <li><a href="#">Category 4</a></li>
                    <li><a href="#">Category 5</a></li>
                    <li><a href="#">Category 6</a></li>
                    <li><a href="#">Category 7</a></li>
                    <li><a href="#">Category 8</a></li>
                    <li><a href="#">Category 9</a></li>
                    <li><a href="#">Category 10</a></li>
                    <li><a href="#">Category 11</a></li>
                    <li><a href="#">Category 12</a></li>
                    <li><a href="#">Category 13</a></li>
                    <li><a href="#">Category 14</a></li>
                    <li><a href="#">Category 15</a></li>
                    <li><a href="#">Category 16</a></li>
                    <li><a href="#">Category 17</a></li>
                    <li><a href="#">Category 18</a></li>
                    <li><a href="#">Category 19</a></li>
                    <li><a href="#">Category 20</a></li>
                    <li><a href="#">Category 21</a></li>
                    <li><a href="#">Category 22</a></li>
                    <li><a href="#">Category 23</a></li>
                    <li><a href="#">Category 24</a></li>
                    <li><a href="#">Category 25</a></li>
                    <li><a href="#">Category 26</a></li>
                    <li><a href="#">Category 27</a></li>
                    <li><a href="#">Category 28</a></li>
                    <li><a href="#">Category 29</a></li>
                    <li><a href="#">Category 30</a></li>
                    <li><a href="#">Category 31</a></li>
                    <li><a href="#">Category 32</a></li>
                    <li><a href="#">Category 33</a></li>
                </ul>
            </div>
            
            <div id="custom_displays" class="clearfix scrollingContainer_large">
                <ul class="display-list simple-list">
                    <li><a href="#">109129453</a></li>
                    <li><a href="#">88638107 RFN-420 CL</a></li>
                    <li><a href="#">Fairmont PUC Data</a></li>
                    <li><a href="#">Lift Station Gallons</a></li>
                </ul>
            </div>
        
        </div>
        
    </div>
    <div class="column two nogutter">
        
        <tags:sectionContainer title="Active Alarms (2)">
            <table class="compactResultsTable alarm-table">
                <thead></thead>
                <tfoot></tfoot>
                <tbody>
                    <tr>
                        <td class="alarm-text"><a href="#" title="88638107 RFN-420 CL">Limit 1 Exceeded High. 140.000 > 130.000</a></td>
                        <td>
                            <span class="error">Unacknowledged</span>
                            <cm:dropdown containerCssClass="fr vh">
                                <li><a class="clearfix"><cti:icon icon="icon-tick"/><span>Acknowledge</span></a></li>
                                <li class="divider"></li>
                                <li><a class="clearfix"><cti:icon icon="icon-pencil"/><span>Manual Entry</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-wrench"/><span>Manual Control</span></a></li>
                                <li class="divider"></li>
                                <li><a class="clearfix"><cti:icon icon="icon-tag-blue"/><span>Tags...</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-accept"/><span>Enable/Disable...</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-chart-line"/><span>Trend</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-transmit-blue"/><span>Force Alt Scan</span></a></li>
                            </cm:dropdown>
                        </td>
                    </tr>
                    <tr>
                        <td class="alarm-text"><a href="#" title="Sub Alexandria Voltage">Limit 1 Exceeded High. 1540.000 > 1200.000</a></td>
                        <td>
                            <span>Acknowledged</span>
                            <cm:dropdown containerCssClass="fr vh">
                                <li><a class="clearfix"><cti:icon icon="icon-pencil"/><span>Manual Entry</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-wrench"/><span>Manual Control</span></a></li>
                                <li class="divider"></li>
                                <li><a class="clearfix"><cti:icon icon="icon-tag-blue"/><span>Tags...</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-accept"/><span>Enable/Disable...</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-chart-line"/><span>Trend</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-transmit-blue"/><span>Force Alt Scan</span></a></li>
                            </cm:dropdown>
                        </td>
                    </tr>
                    <tr>
                        <td class="alarm-text"><a href="#" title="Sub Vila Voltage">Limit 1 Exceeded High. 1540.000 > 1200.000</a></td>
                        <td>
                            <span>Acknowledged</span>
                            <cm:dropdown containerCssClass="fr vh">
                                <li><a class="clearfix"><cti:icon icon="icon-pencil"/><span>Manual Entry</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-wrench"/><span>Manual Control</span></a></li>
                                <li class="divider"></li>
                                <li><a class="clearfix"><cti:icon icon="icon-tag-blue"/><span>Tags...</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-accept"/><span>Enable/Disable...</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-chart-line"/><span>Trend</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-transmit-blue"/><span>Force Alt Scan</span></a></li>
                            </cm:dropdown>
                        </td>
                    </tr>
                    <tr>
                        <td class="alarm-text"><a href="#" title="Sub Bob Voltage">Limit 1 Exceeded High. 1540.000 > 1200.000</a></td>
                        <td>
                            <span>Acknowledged</span>
                            <cm:dropdown containerCssClass="fr vh">
                                <li><a class="clearfix"><cti:icon icon="icon-pencil"/><span>Manual Entry</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-wrench"/><span>Manual Control</span></a></li>
                                <li class="divider"></li>
                                <li><a class="clearfix"><cti:icon icon="icon-tag-blue"/><span>Tags...</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-accept"/><span>Enable/Disable...</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-chart-line"/><span>Trend</span></a></li>
                                <li><a class="clearfix"><cti:icon icon="icon-transmit-blue"/><span>Force Alt Scan</span></a></li>
                            </cm:dropdown>
                        </td>
                    </tr>
                </tbody>
            </table>
            <hr>
            <div id="f-page-buttons" class="dn">
                <cti:button id="b_mute" nameKey="mute" icon="icon-sound-mute" classes="dn" renderMode="buttonImage"/>
                <cti:button id="b_unmute" nameKey="unmute" icon="icon-sound" renderMode="buttonImage"/>
            </div>
        </tags:sectionContainer>
        
        </div>
        
    </div>
</div>

</cti:standardPage>