<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage page="webServices.account" module="dev">
    
  
    <%-- RESULT AREA --%>
    <c:if test="${not empty results || not empty errorReasons}">
    
        <tags:nameValueContainer2>
            
            <c:forEach items="${errorReasons}" var="errorReason">
                <tags:nameValue2 nameKey=".error">
                    <div class="error">${fn:escapeXml(errorReason)}</div>
                </tags:nameValue2>
            </c:forEach>
            
            <c:forEach items="${results}" var="result">
                <tags:nameValue2 nameKey=".result">
                    <div class="success">${fn:escapeXml(result)}</div>
                </tags:nameValue2>
            </c:forEach>
        
        </tags:nameValueContainer2>
    
    <br><br>
    </c:if>
    
    <table class="results-table">
    
        <tr>
            <th class="functionality"><i:inline key=".functionality"/></th>
            <th class="doc"><i:inline key=".bgeIntegrationReqDocRef"/></th>
            <th><i:inline key=".inputTest"/></th>
        </tr>
        
        <%-- DELETE ACCOUNT --%>
        <tr>
            <td class="functionality"><i:inline key=".deleteAccount"/></td>
            <td class="doc"><i:inline key=".section3"/></td>
            <td class="inputTest">
            <cti:url var="deleteAccountUrl" value="/debug/accountService/inputs/deleteAccount"/>
                <form action="${deleteAccountUrl}" method="post">
                    <cti:csrfToken/>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="delete_accountNumber" value="${fn:escapeXml(delete_accountNumber)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".operator" >
                            <input type="text" name="delete_user" value="${fn:escapeXml(delete_user)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                         <cti:button nameKey="testBtn" type="submit" classes="js-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- ADD ACCOUNT --%>
        <tr>
            <td class="functionality"><i:inline key=".addAccount"/></td>
            <td class="doc"></td>
            <td class="inputTest">
            <cti:url var="addAccountUrl" value="/debug/accountService/inputs/addAccount"/>
                <form action="${addAccountUrl}" method="post">
                    <cti:csrfToken/>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".operator" >
                            <input type="text" name="add_user" value="${fn:escapeXml(add_user)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="add_accountNumber" value="${fn:escapeXml(add_accountNumber)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".lastName" >
                            <input type="text" name="add_lastName" value="${fn:escapeXml(add_lastName)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".firstName" >
                            <input type="text" name="add_firstName" value="${fn:escapeXml(add_firstName)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".homePhone" >
                            <input type="text" name="add_homePhone" value="${fn:escapeXml(add_homePhone)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".workPhone" >
                            <input type="text" name="add_workPhone" value="${fn:escapeXml(add_workPhone)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".emailAddress" >
                            <input type="text" name="add_emailAddress" value="${fn:escapeXml(add_emailAddress)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altTracking" >
                            <input type="text" name="add_altTrackingNum" value="${fn:escapeXml(add_altTrackingNum)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".map" >
                            <input type="text" name="add_mapNum" value="${fn:escapeXml(add_mapNum)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".commercial" >
                            <input type="text" name="add_isCommercial" value="${fn:escapeXml(add_isCommercial)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".companyName" >
                            <input type="text" name="add_companyName" value="${fn:escapeXml(add_companyName)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".address1" >
                            <input type="text" name="add_streetAddress1" value="${fn:escapeXml(add_streetAddress1)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".address2" >
                            <input type="text" name="add_streetAddress2" value="${fn:escapeXml(add_streetAddress2)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".city" >
                            <input type="text" name="add_city" value="${fn:escapeXml(add_city)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".state" >
                            <input type="text" name="add_state" value="${fn:escapeXml(add_state)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".zip" >
                            <input type="text" name="add_zip" value="${fn:escapeXml(add_zip)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".county" >
                            <input type="text" name="add_county" value="${fn:escapeXml(add_county)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".billAddress1" >
                            <input type="text" name="add_billingAddress1" value="${fn:escapeXml(add_billingAddress1)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billAddress2" >
                            <input type="text" name="add_billingAddress2" value="${fn:escapeXml(add_billingAddress2)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billCity" >
                            <input type="text" name="add_billingCity" value="${fn:escapeXml(add_billingCity)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billState" >
                            <input type="text" name="add_billingState" value="${fn:escapeXml(add_billingState)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billZip" >
                            <input type="text" name="add_billingZip" value="${fn:escapeXml(add_billingZip)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billCounty" >
                            <input type="text" name="add_billingCounty" value="${fn:escapeXml(add_billingCounty)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".substationName" >
                            <input type="text" name="add_substation" value="${fn:escapeXml(add_substation)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".feeder" >
                            <input type="text" name="add_feeder" value="${fn:escapeXml(add_feeder)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".pole" >
                            <input type="text" name="add_pole" value="${fn:escapeXml(add_pole)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".transformerSize" >
                            <input type="text" name="add_transformerSize" value="${fn:escapeXml(add_transformerSize)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serviceVoltage" >
                            <input type="text" name="add_serviceVoltage" value="${fn:escapeXml(add_serviceVoltage)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".username" >
                            <input type="text" name="add_username" value="${fn:escapeXml(add_username)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".password" >
                            <input type="text" name="add_password" value="${fn:escapeXml(add_password)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".userGroup" >
                            <input type="text" name="add_loginGroup" value="${fn:escapeXml(add_loginGroup)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="js-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
        
        <%-- UPDATE ACCOUNT --%>
        <tr>
            <td class="functionality"><i:inline key=".updateAccount"/></td>
            <td class="doc"></td>
            <td class="inputTest">
            <cti:url var="updateAccountUrl" value="/debug/accountService/inputs/updateAccount"/>
                <form action="${updateAccountUrl}" method="post">
                    <cti:csrfToken/>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".operator" >
                            <input type="text" name="update_user" value="${fn:escapeXml(update_user)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="update_accountNumber" value="${fn:escapeXml(update_accountNumber)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".lastName" >
                            <input type="text" name="update_lastName" value="${fn:escapeXml(update_lastName)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".firstName" >
                            <input type="text" name="update_firstName" value="${fn:escapeXml(update_firstName)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".homePhone" >
                            <input type="text" name="update_homePhone" value="${fn:escapeXml(update_homePhone)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".workPhone" >
                            <input type="text" name="update_workPhone" value="${fn:escapeXml(update_workPhone)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".emailAddress" >
                            <input type="text" name="update_emailAddress" value="${fn:escapeXml(update_emailAddress)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altTracking" >
                            <input type="text" name="update_altTrackingNum" value="${fn:escapeXml(update_altTrackingNum)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".map" >
                            <input type="text" name="update_mapNum" value="${fn:escapeXml(update_mapNum)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".commercial" >
                            <input type="text" name="update_isCommercial" value="${fn:escapeXml(update_isCommercial)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".companyName" >
                            <input type="text" name="update_companyName" value="${fn:escapeXml(update_companyName)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".address1" >
                            <input type="text" name="update_streetAddress1" value="${fn:escapeXml(update_streetAddress1)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".address2" >
                            <input type="text" name="update_streetAddress2" value="${fn:escapeXml(update_streetAddress2)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".city" >
                            <input type="text" name="update_city" value="${fn:escapeXml(update_city)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".state" >
                            <input type="text" name="update_state" value="${fn:escapeXml(update_state)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".zip" >
                            <input type="text" name="update_zip" value="${fn:escapeXml(update_zip)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".county" >
                            <input type="text" name="update_county" value="${fn:escapeXml(update_county)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".billAddress1" >
                            <input type="text" name="update_billingAddress1" value="${fn:escapeXml(update_billingAddress1)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billAddress2" >
                            <input type="text" name="update_billingAddress2" value="${fn:escapeXml(update_billingAddress2)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billCity" >
                            <input type="text" name="update_billingCity" value="${fn:escapeXml(update_billingCity)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billState" >
                            <input type="text" name="update_billingState" value="${fn:escapeXml(update_billingState)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billZip" >
                            <input type="text" name="update_billingZip" value="${fn:escapeXml(update_billingZip)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billCounty" >
                            <input type="text" name="update_billingCounty" value="${fn:escapeXml(update_billingCounty)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".substationName" >
                            <input type="text" name="update_substation" value="${fn:escapeXml(update_substation)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".feeder" >
                            <input type="text" name="update_feeder" value="${fn:escapeXml(update_feeder)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".pole" >
                            <input type="text" name="update_pole" value="${fn:escapeXml(update_pole)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".transformerSize" >
                            <input type="text" name="update_transformerSize" value="${fn:escapeXml(update_transformerSize)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serviceVoltage" >
                            <input type="text" name="update_serviceVoltage" value="${fn:escapeXml(update_serviceVoltage)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".username" >
                            <input type="text" name="update_username" value="${fn:escapeXml(update_username)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".password" >
                            <input type="text" name="update_password" value="${fn:escapeXml(update_password)}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".userGroup" >
                            <input type="text" name="update_loginGroup" value="${fn:escapeXml(update_loginGroup)}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".run">
                            <cti:button nameKey="testBtn" type="submit" classes="js-blocker"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form>
            </td>
        </tr>
    </table>
    
</cti:standardPage>