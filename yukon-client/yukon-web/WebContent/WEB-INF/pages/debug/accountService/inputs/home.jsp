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
                    <div class="error">${errorReason}</div>
                </tags:nameValue2>
            </c:forEach>
            
            <c:forEach items="${results}" var="result">
                <tags:nameValue2 nameKey=".result">
                    <div class="success">${result}</div>
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
                            <input type="text" name="delete_accountNumber" value="${delete_accountNumber}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".operator" >
                            <input type="text" name="delete_user" value="${delete_user}">
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
                            <input type="text" name="add_user" value="${add_user}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="add_accountNumber" value="${add_accountNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".lastName" >
                            <input type="text" name="add_lastName" value="${add_lastName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".firstName" >
                            <input type="text" name="add_firstName" value="${add_firstName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".homePhone" >
                            <input type="text" name="add_homePhone" value="${add_homePhone}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".workPhone" >
                            <input type="text" name="add_workPhone" value="${add_workPhone}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".emailAddress" >
                            <input type="text" name="add_emailAddress" value="${add_emailAddress}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altTracking" >
                            <input type="text" name="add_altTrackingNum" value="${add_altTrackingNum}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".map" >
                            <input type="text" name="add_mapNum" value="${add_mapNum}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".commercial" >
                            <input type="text" name="add_isCommercial" value="${add_isCommercial}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".companyName" >
                            <input type="text" name="add_companyName" value="${add_companyName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".address1" >
                            <input type="text" name="add_streetAddress1" value="${add_streetAddress1}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".address2" >
                            <input type="text" name="add_streetAddress2" value="${add_streetAddress2}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".city" >
                            <input type="text" name="add_city" value="${add_city}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".state" >
                            <input type="text" name="add_state" value="${add_state}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".zip" >
                            <input type="text" name="add_zip" value="${add_zip}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".county" >
                            <input type="text" name="add_county" value="${add_county}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".billAddress1" >
                            <input type="text" name="add_billingAddress1" value="${add_billingAddress1}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billAddress2" >
                            <input type="text" name="add_billingAddress2" value="${add_billingAddress2}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billCity" >
                            <input type="text" name="add_billingCity" value="${add_billingCity}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billState" >
                            <input type="text" name="add_billingState" value="${add_billingState}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billZip" >
                            <input type="text" name="add_billingZip" value="${add_billingZip}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billCounty" >
                            <input type="text" name="add_billingCounty" value="${add_billingCounty}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".substationName" >
                            <input type="text" name="add_substation" value="${add_substation}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".feeder" >
                            <input type="text" name="add_feeder" value="${add_feeder}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".pole" >
                            <input type="text" name="add_pole" value="${add_pole}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".transformerSize" >
                            <input type="text" name="add_transformerSize" value="${add_transformerSize}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serviceVoltage" >
                            <input type="text" name="add_serviceVoltage" value="${add_serviceVoltage}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".username" >
                            <input type="text" name="add_username" value="${add_username}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".password" >
                            <input type="text" name="add_password" value="${add_password}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".userGroup" >
                            <input type="text" name="add_loginGroup" value="${add_loginGroup}">
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
                            <input type="text" name="update_user" value="${update_user}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".accountNumber" >
                            <input type="text" name="update_accountNumber" value="${update_accountNumber}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".lastName" >
                            <input type="text" name="update_lastName" value="${update_lastName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".firstName" >
                            <input type="text" name="update_firstName" value="${update_firstName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".homePhone" >
                            <input type="text" name="update_homePhone" value="${update_homePhone}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".workPhone" >
                            <input type="text" name="update_workPhone" value="${update_workPhone}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".emailAddress" >
                            <input type="text" name="update_emailAddress" value="${update_emailAddress}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altTracking" >
                            <input type="text" name="update_altTrackingNum" value="${update_altTrackingNum}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".map" >
                            <input type="text" name="update_mapNum" value="${update_mapNum}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".commercial" >
                            <input type="text" name="update_isCommercial" value="${update_isCommercial}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".companyName" >
                            <input type="text" name="update_companyName" value="${update_companyName}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".address1" >
                            <input type="text" name="update_streetAddress1" value="${update_streetAddress1}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".address2" >
                            <input type="text" name="update_streetAddress2" value="${update_streetAddress2}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".city" >
                            <input type="text" name="update_city" value="${update_city}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".state" >
                            <input type="text" name="update_state" value="${update_state}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".zip" >
                            <input type="text" name="update_zip" value="${update_zip}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".county" >
                            <input type="text" name="update_county" value="${update_county}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".billAddress1" >
                            <input type="text" name="update_billingAddress1" value="${update_billingAddress1}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billAddress2" >
                            <input type="text" name="update_billingAddress2" value="${update_billingAddress2}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billCity" >
                            <input type="text" name="update_billingCity" value="${update_billingCity}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billState" >
                            <input type="text" name="update_billingState" value="${update_billingState}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billZip" >
                            <input type="text" name="update_billingZip" value="${update_billingZip}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".billCounty" >
                            <input type="text" name="update_billingCounty" value="${update_billingCounty}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".substationName" >
                            <input type="text" name="update_substation" value="${update_substation}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".feeder" >
                            <input type="text" name="update_feeder" value="${update_feeder}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".pole" >
                            <input type="text" name="update_pole" value="${update_pole}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".transformerSize" >
                            <input type="text" name="update_transformerSize" value="${update_transformerSize}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serviceVoltage" >
                            <input type="text" name="update_serviceVoltage" value="${update_serviceVoltage}">
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".username" >
                            <input type="text" name="update_username" value="${update_username}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".password" >
                            <input type="text" name="update_password" value="${update_password}">
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".userGroup" >
                            <input type="text" name="update_loginGroup" value="${update_loginGroup}">
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