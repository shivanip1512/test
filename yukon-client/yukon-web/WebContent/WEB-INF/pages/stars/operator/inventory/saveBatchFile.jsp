<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="operator" page="saveToFile">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    
    <script type="text/javascript">
        jQuery(document).ready(function() {
        	jQuery(document).on('change', '#routes input:radio', function(e){
        		var currentTarget = jQuery(this);
        		
        		currentTarget.closest('div').siblings().addClass('disabled').find('input[type!=radio], select, textarea, button').attr('disabled', 'disabled');
        		currentTarget.closest('div').removeClass('disabled').find('input,select,textarea,button').removeAttr('disabled');
        	});
        	jQuery("#routes input:radio:checked").trigger("change");
        	
        	var singleHwConfigType = ${uniformHardwareConfigType};
            if(!singleHwConfigType) {
                jQuery('#groups').addClass('disabled').find(':input').attr('disabled', 'disabled');
            } else {
            	jQuery(document).on('change', '#groups input:radio', function(e){
                    var currentTarget = jQuery(this);
                    
                    currentTarget.closest('div').siblings().addClass('disabled').find('input[type!=radio],select,textarea,button').attr('disabled', 'disabled');
                    currentTarget.closest('div').removeClass('disabled').find('input,select,textarea,button').removeAttr('disabled');
                });
        	    jQuery('#groups input:radio:checked').trigger('change');
            }
        });
    </script>
    
    <tags:boxContainer2 nameKey="title"> 
        <div class="containerHeader">
            <table>
                <tr>
                    <td valign="top" colspan="2">
                        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
                    </td>
                </tr>
                <tr>
                    <td class="smallBoldLabel errorMessage"><i:inline key=".instructionsLabel"/></td>
                    <td><i:inline key=".instructions"/></td>
                </tr>
                <c:if test="${empty task}">
                    <c:if test="${!uniformHardwareConfigType}">
                        <tr>
                            <td class="smallBoldLabel errorMessage"><i:inline key=".note"/></td>
                            <td><i:inline key=".multipleHardwareConfigTypes"/></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td><i:inline key=".enforceSingleHardwareConfigType"/></td>
                        </tr>
                    </c:if>
                </c:if>
            </table>
        </div>
        
        <c:if test="${empty task}">
            <form:form action="do" commandName="saveToBatchInfo">
                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                <form:hidden path="ecDefaultRoute"/>
                <div class="formElementContainer">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".route">
                            <div id="routes">
                                <div class="saveToBatchOption" id="currentRoute">
                                    <form:radiobutton id="useCurrentRoutes" path="useRoutes" value="current"/>
                                    <label for="useCurrentRoutes">
                                        <i:inline key=".useCurrentRoutes"/>
                                    </label>
                                </div>
                                <div class="saveToBatchOption" id="defaultRoute">
                                    <form:radiobutton id="useDefaultRoute" path="useRoutes" value="default"/>
                                    <label for="useDefaultRoute">
                                        <i:inline key=".useDefaultRoute"/>${fn:escapeXml(ecDefaultRoute)} 
                                    </label>
                                </div>
                                <div class="saveToBatchOption" id="newRoute">
                                    <form:radiobutton id="useNewRoute" path="useRoutes" value="new"/>
                                    <label for="useNewRoute">
                                        <i:inline key=".selectRoute"/>
                                    </label>
                                    <form:select path="routeId" id="routeId">
                                        <c:forEach var="route" items="${routes}">
                                            <form:option value="${route.paoIdentifier.paoId}">${fn:escapeXml(route.paoName)}</form:option>
                                        </c:forEach>
                                    </form:select>
                                </div>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".group">
                            <div id="groups"> 
                                <div class="saveToBatchOption">
                                    <form:radiobutton id="useCurrentGroup" path="useGroups" value="current"/>
                                    <label for="useCurrentGroup">
                                        <i:inline key=".useCurrentConfiguration"/>
                                    </label>
                                </div>
                                <div class="saveToBatchOption">
                                    <form:radiobutton id="useNewGroup" path="useGroups" value="new"/>
                                    <label for="useNewGroup">
                                        <i:inline key=".selectGroup"/>
                                    </label>
                                    <form:hidden path="groupId" id="groupId"/>
                                    <tags:pickerDialog type="lmGroupPaoPermissionCheckingPicker"
                                                       id="groupPicker" 
                                                       linkType="selection"
                                                       multiSelectMode="false"
                                                       immediateSelectMode="true"
                                                       selectionProperty="paoName"
                                                       destinationFieldId="groupId"/>
                                </div>
                            </div>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <cti:button nameKey="save" type="submit" name="save"/>
                <cti:button nameKey="cancel" type="submit" name="cancel"/>
            </form:form>
        </c:if>
        <c:if test="${not empty task}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".progress">
                    <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <br>
            <a href="/stars/operator/inventory/home"><i:inline key=".inventoryHome"/></a>
        </c:if>
    </tags:boxContainer2>
</cti:standardPage>