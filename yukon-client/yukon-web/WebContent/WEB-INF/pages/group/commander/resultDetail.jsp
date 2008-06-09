<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Commander Results" module="amr">

    <cti:standardMenu menuSelection="devicegroups|commander"/>

	<%-- BREAD CRUMBS --%>
	<cti:breadCrumbs>
    
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- commander from location --%>
        <cti:crumbLink url="/spring/group/commander/resultList" title="All Results" />
        
        <%-- this result --%>
	    &gt; Command Executing
	
    </cti:breadCrumbs>
    
    <script type="text/javascript">
        
        function updateProgress(totalItems) {
    
            return function(data) {
            
                var completedItems = data['completedItems'];
                
                var percentDone = Math.round((completedItems / totalItems) * 100);
                
                $('completedItems${result.key}').innerHTML = data['completedItems']; 
                
                $('progressInner${result.key}').style.width = percentDone + 'px';
                $('percentComplete${result.key}').innerHTML = percentDone + '%';  
                
                if (completedItems < totalItems) {
                    $('progressDescription${result.key}').innerHTML = 'In Progress...   ';
                }
                else {
                    $('progressDescription${result.key}').innerHTML = 'Complete.   ';
                }
    
            };
        }
    
        function refreshResults(kind, theDiv) {

            if (theDiv.visible()) {
            
                var url = '/spring/group/commander/' + kind;
                
                var params = $H();
                params['resultKey'] = '${result.key}';
            
                var updater = new Ajax.Updater (theDiv, url, {
              
                  'parameters': params,
                  
                  'onSuccess': function(response) {
                               },
                  
                  'onException': function(response) {
                               }
                });
            }
        }
    
    </script>
    
    <h2>Results</h2>
    <br>
  
    <tags:boxContainer id="commanderResultsContainer" hideEnabled="false">
    
        <jsp:attribute name="title">
            Executing '${result.command}' on <cti:msg key="${result.deviceCollection.description}"/>
        </jsp:attribute>
        
        <jsp:body>
    
        <%-- NOTE --%>
        <table>
            <tr>
                <td valign="top" class="smallBoldLabel">Note:</td>
                <td style="font-size:11px;">
                    Progress is updated every 10 seconds. Processing will continue if you wish to navigate away from this page at any time.<br>
                    You may view the progress of all recent and ongoing processes from the main Commander page.<br><br>
                </td>
            </tr>
        </table>

        <%-- PROGRESS --%>
        <span class="normalBoldLabel">Progress: </span><span id="progressDescription${result.key}">In Progress...</span>
        
        <div style="padding:10px;">
            <table cellpadding="0px" border="0px">
                <tr>
                    <td>
                        <div id="progressBorder${result.key}" style="height:12px; width:100px; border:1px solid black; padding:0px; background-color:#CCCCCC;" align="left">
                            <div id="progressInner${result.key}" style="height: 10px; width: 0px; padding:1px; overflow:hidden; background-color:#006633; ">
                            </div>
                        </div>
                    </td>
                    <td>
                        <span id="percentComplete${result.key}" style="padding-left:6px;padding-right:10px;display:inline;font-size:11px;">0%</span>
                    </td>
                    <td>
                        <span style="font-size:11px;display:inline;"><span id="completedItems${result.key}"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/COMPLETED_ITEMS"/></span>/${result.deviceCollection.deviceCount}</span>
                    </td>
                </tr>
            </table>
            
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.allResults" class="small">
                <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
            </cti:link>
           
        </div>
    
    
        <%-- SUCCESS --%>
        <br>
        <div class="normalBoldLabel">Successfully Executed: <span style="color:#006633;"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/></span></div>
        
    	<div style="padding:10px;">
        
    	   <%-- device collection action --%>
           <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.successResults" class="small">
                <cti:mapParam value="${result.successCollection.collectionParameters}"/>
            </cti:link>
            
            <%-- success list --%>
            <div style="height:8px;"></div>
            <a href="javascript:void(0);" onclick="$('successResultsDiv${result.key}').toggle();refreshResults('successList', $('successResultsDiv${result.key}'));" class="small">View Results</a>
            <div id="successResultsDiv${result.key}" style="display:none;"></div>
            
        </div>
    
    
    
        <%-- PROCESSING EXCEPTION --%>
        <br>
        <div class="normalBoldLabel">Failed: <span style="color:#CC0000;"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/></span></div>
        
        <div style="padding:10px;">
            
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.failureResults" class="small">
                <cti:mapParam value="${result.failureCollection.collectionParameters}"/>
            </cti:link>
            
            <%-- errors list --%>
            <div style="height:8px;"></div>
            <a href="javascript:void(0);" onclick="$('errorsResultsDiv${result.key}').toggle();refreshResults('errorsList', $('errorsResultsDiv${result.key}'));" class="small">View Failure Reasons</a>
            <div id="errorsResultsDiv${result.key}" style="display:none;"></div>
        
        </div> 

        </jsp:body>
        
    </tags:boxContainer>
	
    <cti:dataUpdaterCallback function="updateProgress(${result.deviceCollection.deviceCount})" initialize="true" completedItems="COMMANDER/${result.key}/COMPLETED_ITEMS" />
    
</cti:standardPage>