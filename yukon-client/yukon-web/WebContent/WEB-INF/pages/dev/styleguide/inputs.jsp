<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="inputs">
<tags:styleguide page="inputs">

<style>
.style-guide-example .one { line-height: 26px; }
.description { line-height: 22px; }
</style>

<cti:includeScript link="/resources/js/lib/fancytree/jquery.fancytree.min.js"/>
<!-- Include Fancytree skin and library -->
<cti:includeCss link="/resources/js/lib/fancytree/skin/skin-win8/ui.fancytree.css" />


<p class="description">
    Inputs can have special behavior simply by using some css classes.
</p>

<h2 id="focus-example">Focus</h2>

<p class="description">
    Inputs can be focused automatically using the <span class="label label-attr">.js-focus</span> class.
    When the page loads the element with this class will have focus initially.  Use this wisely on your pages
    to make the user's experience more streamlined.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <input type="text" name="foo" placeholder="don't look at me">
        <input type="text" name="bar" placeholder="please focus on me" class="js-focus">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;input type=&quot;text&quot; name=&quot;foo&quot; placeholder=&quot;don't look at me&quot;&gt;
&lt;input type=&quot;text&quot; name=&quot;bar&quot; placeholder=&quot;please focus on me&quot; class=&quot;js-focus&quot;&gt;
</pre>

<h2 id="format-phone-example">Formatting</h2>

<p class="description">
    Formatting a phone number is serious business but you can do it simply by using the 
    <span class="label label-attr">.js-format-phone</span> class.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <input class="js-format-phone" type="text" name="phone" value="15554443333">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;input class=&quot;js-format-phone&quot; type=&quot;text&quot; name=&quot;phone&quot; value=&quot;15554443333&quot;&gt;
</pre>

<h2 id="numeric-example">Numeric</h2>

<p class="description">
    The numeric tag can be used for formatting numeric fields. You can specify min, max, and a step value for the field.
    This tag uses the jQuery UI Spinner widget. The value is validated against the min/max values supplied when the field loses focus.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <form:form modelAttribute="numericInput">
            <tags:numeric path="temperature" minValue="-10" maxValue="40" stepValue="5"/>
        </form:form>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:numeric path=&quot;temperature&quot; minValue=&quot;-10&quot; maxValue=&quot;40&quot; stepValue=&quot;5&quot;&gt;
</pre>


<h2 id="toggle-with-checkbox-example">Toggle Groups</h2>

<p class="description">
    You can enable or disable groups of elements using the 
    <span class="label label-attr">[data-toggle]</span> and 
    <span class="label label-attr">[data-toggle-group]</span> attributes.
    This is also utilized by the <span class="label label-attr">&lt;tags:switch&gt;</span> and
    <span class="label label-attr">&lt;tags:switchButton&gt;</span> tags internally. 
    <a href="switches#toggle-group-example">See example</a>.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <label><input type="checkbox" name="enable" data-toggle="my-toggle-grp">Enable All The Things?</label>
        <div>
            <input type="text" name="name" placeholder="Name" data-toggle-group="my-toggle-grp" disabled>
            <select name="color" data-toggle-group="my-toggle-grp" disabled>
                <option>Red</option><option>Blue</option>
            </select>
            <cti:button nameKey="save" data-toggle-group="my-toggle-grp" disabled="true"/>
        </div>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;label&gt;&lt;input type=&quot;checkbox&quot; name=&quot;enable&quot; data-toggle=&quot;my-toggle-grp&quot;&gt;Enable All The Things?&lt;/label&gt;
&lt;div&gt;
    &lt;input type=&quot;text&quot; name=&quot;name&quot; placeholder=&quot;Name&quot; data-toggle-group=&quot;my-toggle-grp&quot; disabled&gt;
    &lt;select name=&quot;color&quot; data-toggle-group=&quot;my-toggle-grp&quot; disabled&gt;
        &lt;option&gt;Red&lt;/option&gt;&lt;option&gt;Blue&lt;/option&gt;
    &lt;/select&gt;
    &lt;cti:button nameKey=&quot;save&quot; data-toggle-group=&quot;my-toggle-grp&quot; disabled=&quot;true&quot;/&gt;
&lt;/div&gt;
</pre>

<h2 id="toggle-with-checkbox-example">File Uploads</h2>

<p class="description">
    The <span class="label label-attr">&lt;tags:file/&gt;</span> tag is used to create a file input that looks and feels
    the same across all browsers. Always use this tag instead of raw html 
    <span class="label label-attr">&lt;input type="file"&gt;</span>.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:file/>
        <tags:file name="history.magnaCarta" id="my-input"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:file/&gt;
&lt;tags:file name="history.magnaCarta" id="my-input"/&gt;
</pre>

<h2 id="toggle-with-checkbox-example">Dynamic Tree using FancyTree</h2>
<p class="description">
    The dynamic tree is generated through Fancytree jquery.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one">
        <h4 class="subtle">Example:</h4>
        <div id="selectedinfo">
            <cti:button id="checkselected" label="checkselected"/>
            <cti:button id="checkselectall" label="checkselectall"/>
        </div>
    </div>
    <div class="column two nogutter">
        <div id="tree"/>
        <div id="activeNodeInfo"/>

        <script type="text/javascript">
            $(function(){
                $("#tree").fancytree({
                	source:[{
                		"metadata":{"groupName":"/","id":"Groups"},
                		"children":
                		[
                			{"metadata":{"groupName":"/cbc_tssl1","id":"cbctssl1"},
                			 "children":[],
                			 "id":"cbctssl1",
                			 "text":"cbc_tssl1",
                			 "href":"javascript:void(0);",
                			 "leaf":true,
                			 "title":"cbc_tssl1",
                			 "key":"cbctssl1",
                			 "info":{"groupName":"/cbc_tssl1","id":"cbctssl1"},
                			 "addClass":"null yukon"
                			},
                			{"metadata":{"groupName":"/Meters","id":"Meters"},
                		     "children":[
                                          {"metadata":{"groupName":"/Meters/<iframe><iframe>","id":"Metersiframeiframe"},
                                           "children":[
                                        	   {"metadata":{"groupName":"/Meters/<iframe><iframe>/<iframe src=javascript:alert(23)>",
                                        		"id":"Metersiframeiframeiframesrcjavascriptalert23"},
                                        		"children":[],
                                        		"id":"Metersiframeiframeiframesrcjavascriptalert23",
                                        		"text":"&lt;iframe src=javascript:alert(23)&gt;",
                                        		"href":"javascript:void(0);",
                                        		"leaf":true,
                                        		"title":"&lt;iframe src=javascript:alert(23)&gt;",
                                        		"key":"Metersiframeiframeiframesrcjavascriptalert23",
                                        		"info":{"groupName":"/Meters/<iframe><iframe>/<iframe src=javascript:alert(23)>",
                                        		"id":"Metersiframeiframeiframesrcjavascriptalert23"},
                                        		"addClass":"null yukon"
                                           }
                                         ],
                             "id":"Metersiframeiframe",
                             "text":"&lt;iframe&gt;&lt;iframe&gt;",
                             "href":"javascript:void(0);",
                             "leaf":false,
                             "title":"&lt;iframe&gt;&lt;iframe&gt;",
                             "key":"Metersiframeiframe",
                             "info":{"groupName":"/Meters/<iframe><iframe>","id":"Metersiframeiframe"},
                             "addClass":"null yukon"
                            },
                            {"metadata":{"groupName":"/Meters/Alternate","id":"MetersAlternate"},
                             "children":[],
                             "id":"MetersAlternate",
                             "text":"Alternate",
                             "href":"javascript:void(0);",
                             "iconCls":"ALTERNATE",
                             "leaf":true,
                             "title":"Alternate",
                             "key":"MetersAlternate",
                             "info":{"groupName":"/Meters/Alternate","id":"MetersAlternate"},
                             "addClass":"ALTERNATE yukon"
                            },
                            {"metadata":{"groupName":"/Meters/Billing","id":"MetersBilling"},
                             "children":[],
                             "id":"MetersBilling",
                             "text":"Billing",
                             "href":"javascript:void(0);",
                             "iconCls":"BILLING",
                             "leaf":true,
                             "title":"Billing",
                             "key":"MetersBilling",
                             "info":{"groupName":"/Meters/Billing","id":"MetersBilling"},
                             "addClass":"BILLING yukon"
                            },
                            {"metadata":{"groupName":"/Meters/CIS DeviceClass","id":"MetersCISDeviceClass"},
                             "children":[],
                             "id":"MetersCISDeviceClass",
                             "text":"CIS DeviceClass",
                             "href":"javascript:void(0);",
                             "iconCls":"CIS_DEVICECLASS",
                             "leaf":true,
                             "title":"CIS DeviceClass",
                             "key":"MetersCISDeviceClass",
                             "info":{"groupName":"/Meters/CIS DeviceClass","id":"MetersCISDeviceClass"},
                             "addClass":"CIS_DEVICECLASS yukon"
                            },
                            {"metadata":{"groupName":"/Meters/CIS Substation","id":"MetersCISSubstation"},
                             "children":[
                            	          {"metadata":{"groupName":"/Meters/CIS Substation/FORT PIERRE","id":"MetersCISSubstationFORTPIERRE"},
                            	           "children":[],
                            	           "id":"MetersCISSubstationFORTPIERRE",
                            	           "text":"FORT PIERRE",
                            	           "href":"javascript:void(0);",
                            	           "leaf":true,
                            	           "title":"FORT PIERRE",
                            	           "key":"MetersCISSubstationFORTPIERRE",
                            	           "info":{"groupName":"/Meters/CIS Substation/FORT PIERRE","id":"MetersCISSubstationFORTPIERRE"},
                            	           "addClass":"null yukon"
                            	           }
                            	        ],
                             "id":"MetersCISSubstation",
                             "text":"CIS Substation",
                             "href":"javascript:void(0);",
                             "iconCls":"CIS_SUBSTATION",
                             "leaf":false,
                             "title":"CIS Substation",
                             "key":"MetersCISSubstation",
                             "info":{"groupName":"/Meters/CIS Substation","id":"MetersCISSubstation"},
                             "addClass":"CIS_SUBSTATION yukon"
                            },
                            {"metadata":{"groupName":"/Meters/Collection","id":"MetersCollection"},
                        	 "children":[],
                        	 "id":"MetersCollection",
                        	 "text":"Collection",
                        	 "href":"javascript:void(0);",
                        	 "iconCls":"COLLECTION",
                        	 "leaf":true,
                        	 "title":"Collection",
                        	 "key":"MetersCollection",
                        	 "info":{"groupName":"/Meters/Collection","id":"MetersCollection"},
                        	 "addClass":"COLLECTION yukon"
                           },
                           {"metadata":{"groupName":"/Meters/Flags","id":"MetersFlags"},
                            "children":[
                        	   {"metadata":{"groupName":"/Meters/Flags/DisconnectedStatus","id":"MetersFlagsDisconnectedStatus"},
                        		"children":[],
                        		"id":"MetersFlagsDisconnectedStatus",
                        		"text":"DisconnectedStatus",
                        		"href":"javascript:void(0);",
                        		"iconCls":"DISCONNECTED_STATUS",
                        		"leaf":true,
                        		"title":"DisconnectedStatus",
                        		"key":"MetersFlagsDisconnectedStatus",
                        		"info":{"groupName":"/Meters/Flags/DisconnectedStatus","id":"MetersFlagsDisconnectedStatus"},
                        		"addClass":"DISCONNECTED_STATUS yukon"
                         },
                         {"metadata":{"groupName":"/Meters/Flags/Inventory","id":"MetersFlagsInventory"},
                          "children":[],
                          "id":"MetersFlagsInventory",
                          "text":"Inventory",
                          "href":"javascript:void(0);",
                          "iconCls":"INVENTORY",
                          "leaf":true,
                          "title":"Inventory",
                          "key":"MetersFlagsInventory",
                          "info":{"groupName":"/Meters/Flags/Inventory","id":"MetersFlagsInventory"},
                          "addClass":"INVENTORY yukon"
                         },
                         {"metadata":{"groupName":"/Meters/Flags/UsageMonitoring","id":"MetersFlagsUsageMonitoring"},
                          "children":[],
                          "id":"MetersFlagsUsageMonitoring",
                          "text":"UsageMonitoring",
                          "href":"javascript:void(0);",
                          "iconCls":"USAGE_MONITORING",
                          "leaf":true,
                          "title":"UsageMonitoring",
                          "key":"MetersFlagsUsageMonitoring",
                          "info":{"groupName":"/Meters/Flags/UsageMonitoring","id":"MetersFlagsUsageMonitoring"},
                          "addClass":"USAGE_MONITORING yukon"
                          }
                       ],
                     "id":"MetersFlags",
                     "text":"Flags",
                     "href":"javascript:void(0);",
                     "iconCls":"FLAGS",
                     "leaf":false,
                     "title":"Flags",
                     "key":"MetersFlags",
                     "info":{"groupName":"/Meters/Flags","id":"MetersFlags"},
                     "addClass":"FLAGS yukon"}
                   ],
                   "id":"Meters",
                   "text":"Meters",
                   "href":"javascript:void(0);",
                   "iconCls":"METERS",
                   "leaf":false,
                   "title":"Meters",
                   "key":"Meters",
                   "info":{"groupName":"/Meters","id":"Meters"},
                   "addClass":"METERS yukon"
                 },
                 {"metadata":{"groupName":"/Monitors","id":"Monitors"},
                  "children":[{"metadata":{"groupName":"/Monitors/DeviceData","id":"MonitorsDeviceData"},
                  "children":[{"metadata":{"groupName":"/Monitors/DeviceData/ddm 2","id":"MonitorsDeviceDataddm2"},
                  "children":[],
                  "id":"MonitorsDeviceDataddm2",
                  "text":"ddm 2",
                  "href":"javascript:void(0);",
                  "leaf":true,
                  "title":"ddm 2",
                  "key":"MonitorsDeviceDataddm2",
                  "info":{"groupName":"/Monitors/DeviceData/ddm 2","id":"MonitorsDeviceDataddm2"},
                  "addClass":"null yukon"
                },
                {"metadata":{"groupName":"/Monitors/DeviceData/device data monitor1","id":"MonitorsDeviceDatadevicedatamonitor1"},
                 "children":[],
                 "id":"MonitorsDeviceDatadevicedatamonitor1",
                 "text":"device data monitor1",
                 "href":"javascript:void(0);",
                 "leaf":true,
                 "title":"device data monitor1",
                 "key":"MonitorsDeviceDatadevicedatamonitor1",
                 "info":{"groupName":"/Monitors/DeviceData/device data monitor1","id":"MonitorsDeviceDatadevicedatamonitor1"},
                 "addClass":"null yukon"
               },
               {"metadata":{"groupName":"/Monitors/DeviceData/device data monitor1hgjhgjk",
            	"id":"MonitorsDeviceDatadevicedatamonitor1hgjhgjk"},
            	"children":[],
            	"id":"MonitorsDeviceDatadevicedatamonitor1hgjhgjk",
            	"text":"device data monitor1hgjhgjk",
            	"href":"javascript:void(0);",
            	"leaf":true,
            	"title":"device data monitor1hgjhgjk",
            	"key":"MonitorsDeviceDatadevicedatamonitor1hgjhgjk",
            	"info":{"groupName":"/Monitors/DeviceData/device data monitor1hgjhgjk",
                "id":"MonitorsDeviceDatadevicedatamonitor1hgjhgjk"},
                "addClass":"null yukon"
                },
                {"metadata":{"groupName":"/Monitors/DeviceData/Test","id":"MonitorsDeviceDataTest"},
                 "children":[],
                 "id":"MonitorsDeviceDataTest",
                 "text":"Test",
                 "href":"javascript:void(0);",
                 "leaf":true,
                 "title":"Test",
                 "key":"MonitorsDeviceDataTest",
                 "info":{"groupName":"/Monitors/DeviceData/Test","id":"MonitorsDeviceDataTest"},
                 "addClass":"null yukon"
                }
              ],
              "id":"MonitorsDeviceData",
              "text":"DeviceData",
              "href":"javascript:void(0);",
              "iconCls":"DEVICE_DATA",
              "leaf":false,
              "title":"DeviceData",
              "key":"MonitorsDeviceData",
              "info":{"groupName":"/Monitors/DeviceData","id":"MonitorsDeviceData"},
              "addClass":"DEVICE_DATA yukon"
              },
              {"metadata":{"groupName":"/Monitors/Outage","id":"MonitorsOutage"},
               "children":[{"metadata":{"groupName":"/Monitors/Outage/<iframe src=javascript:alert(347)>","id":"MonitorsOutageiframesrcjavascriptalert347"},
            	            "children":[],
            	            "id":"MonitorsOutageiframesrcjavascriptalert347",
            	            "text":"&lt;iframe src=javascript:alert(347)&gt;",
            	            "href":"javascript:void(0);",
            	            "leaf":true,
            	            "title":"&lt;iframe src=javascript:alert(347)&gt;",
            	            "key":"MonitorsOutageiframesrcjavascriptalert347",
            	            "info":{"groupName":"/Monitors/Outage/<iframe src=javascript:alert(347)>",
            	            "id":"MonitorsOutageiframesrcjavascriptalert347"},
            	            "addClass":"null yukon"
            	            },
            	            {"metadata":{"groupName":"/Monitors/Outage/outage2","id":"MonitorsOutageoutage2"},
            	             "children":[],"id":"MonitorsOutageoutage2",
            	             "text":"outage2",
            	             "href":"javascript:void(0);",
            	             "leaf":true,
            	             "title":"outage2",
            	             "key":"MonitorsOutageoutage2",
            	             "info":{"groupName":"/Monitors/Outage/outage2","id":"MonitorsOutageoutage2"},
            	             "addClass":"null yukon"
            	            }
            	          ],
              "id":"MonitorsOutage",
              "text":"Outage",
              "href":"javascript:void(0);",
              "iconCls":"OUTAGE",
              "leaf":false,
              "title":"Outage",
              "key":"MonitorsOutage",
              "info":{"groupName":"/Monitors/Outage","id":"MonitorsOutage"},
              "addClass":"OUTAGE yukon"
              },
              {"metadata":{"groupName":"/Monitors/Tamper Flag","id":"MonitorsTamperFlag"},
               "children":[
            	            {"metadata":{"groupName":"/Monitors/Tamper Flag/tamper1","id":"MonitorsTamperFlagtamper1"},
            	             "children":[],
            	             "id":"MonitorsTamperFlagtamper1",
            	             "text":"tamper1",
            	             "href":"javascript:void(0);",
            	             "leaf":true,
            	             "title":"tamper1",
            	             "key":"MonitorsTamperFlagtamper1",
            	             "info":{"groupName":"/Monitors/Tamper Flag/tamper1","id":"MonitorsTamperFlagtamper1"},
            	             "addClass":"null yukon"
            	             },
            	             {"metadata":{"groupName":"/Monitors/Tamper Flag/tamper 2","id":"MonitorsTamperFlagtamper2"},
            	              "children":[],
            	              "id":"MonitorsTamperFlagtamper2",
            	              "text":"tamper 2",
            	              "href":"javascript:void(0);",
            	              "leaf":true,
            	              "title":"tamper 2",
            	              "key":"MonitorsTamperFlagtamper2",
            	              "info":{"groupName":"/Monitors/Tamper Flag/tamper 2","id":"MonitorsTamperFlagtamper2"},
            	              "addClass":"null yukon"
            	             }
            	           ],
            	"id":"MonitorsTamperFlag",
            	"text":"Tamper Flag",
            	"href":"javascript:void(0);",
            	"iconCls":"TAMPER_FLAG",
            	"leaf":false,
            	"title":"Tamper Flag",
            	"key":"MonitorsTamperFlag",
            	"info":{"groupName":"/Monitors/Tamper Flag","id":"MonitorsTamperFlag"},
            	"addClass":"TAMPER_FLAG yukon"
            	}
              ],
              "id":"Monitors",
              "text":"Monitors",
              "href":"javascript:void(0);",
              "iconCls":"MONITORS",
              "leaf":false,
              "title":"Monitors",
              "key":"Monitors",
              "info":{"groupName":"/Monitors","id":"Monitors"},
              "addClass":"MONITORS yukon"
              },
              {"metadata":{"groupName":"/shikha-test","id":"shikhatest"},
               "children":[],
               "id":"shikhatest",
               "text":"shikha-test",
               "href":"javascript:void(0);",
               "leaf":true,
               "title":"shikha-test",
               "key":"shikhatest",
               "info":{"groupName":"/shikha-test","id":"shikhatest"},
               "addClass":"null yukon"
              }],
              "id":"groups",
              "text":"groups",
              "title":"Groups",
              "key":"groups",
              "info":{"groupName":"/groups","id":"shikhatest"},
              "addClass":"null yukon"
              }],
              activate: function(event, data) {
                  $("#activeNodeInfo").text("Active node: " + data.node.title);
              }
             });
            $(".fancytree-container").addClass("fancytree-connectors");
            var tree = $("#tree").fancytree("getTree");
            tree.visit(function(node){
                    node.setExpanded(true);
            });

            $("#checkselected").click(function() {
                var node = tree.getNodeByKey('groups');
                node.setSelected(true);
             });

            $("#checkselectall").click(function() {
                tree.selectAll();
             });
            });
        </script>
    </div>
</div>

</tags:styleguide>
</cti:standardPage>