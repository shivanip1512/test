<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="operator" page="accountImport">

    <cti:verifyRolePropertyValue property="OPERATOR_IMPORT_CUSTOMER_ACCOUNT" expectedValue="true"/>
    
    <cti:url var="importImg" value="/WebConfig/yukon/Icons/import_by_DEVICE_TYPE.gif"/>
    <cti:msg var="instructionsText" key="yukon.web.modules.operator.accountImport.instructionsText" />
    
    <tags:boxContainer2 nameKey="fileUploadContainer">
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:50%;">
    
            <cti:dataGridCell>
    
                <form:form id="importForm" commandName="accountImportData" action="/spring/stars/operator/account/uploadImportFiles" enctype="multipart/form-data">
                
                    <%-- note --%>
                    <table>
                        <tr valign="top">
                            <td class="smallBoldLabel"><i:inline key=".noteLabel"/></td>
                            <td style="font-size:11px;"><i:inline key=".noteText"/></td>
                        </tr>
                    </table>
                    
                    <br>
                    
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".accountImportFile" ><input type="file" name="accountImportFile" size="35"></tags:nameValue2>
                        <tags:nameValue2 nameKey=".hardwareImportFile"><input type="file" name="hardwareImportFile" size="35"></tags:nameValue2>
                        <tags:inputNameValue nameKey=".email" path="email" size="35"/>
                    </tags:nameValueContainer2>
                    
                    <br>

                    <cti:button nameKey="prescan" type="submit" styleClass="f_blocker"/>

                    <br><br>
                    
                </form:form>
                
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                
                <%-- instructions --%>
                <table>
                    <tr valign="top">
                        <td class="smallBoldLabel"><i:inline key=".instructionsLabel"/></td>
                        <td style="font-size:11px;">${instructionsText}</td>
                    </tr>
                </table>
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".sampleFiles">
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Account_Import_File.csv"/>"><i:inline key=".sampleAccountFile"/></a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Hardware_Import_File.csv"/>"><i:inline key=".sampleHardwareFile"/></a>,
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Account_And_Hardware_Import_File.csv"/>"><i:inline key=".sampleAccountAndHardwareFile"/></a> 
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                
            </cti:dataGridCell>
            
            <cti:dataGridCell>
            
                <table class="miniResultsTable bulkImport" style="font-size:11px;width:100%;">
                    
                    <tr>
                        <td colspan="2" style="background-color:#CDCDCD;">
                        
                            <table class="noStyle">
                                <tr valign="top">
                                    <td rowspan="2"><img src="${importImg}"></td>
                                    <td><div class="normalBoldLabel"><i:inline key=".importAccountTableHeader"/></div></td>
                                </tr>
                                <tr>
                                    <td><i:inline key=".importAccountTableDescription"/></td>
                                </tr>
                            </table>
                        
                        </td>
                    </tr>
            
                    <tr valign="bottom">
                        <th style="width:150px;" align="left"><i:inline key=".accountColumnHeader"/></th>
                        <th><i:inline key=".accountColumnDescription"/></th>
                    </tr>
                    
                    <c:forEach var="field" items="${accountFields}">
                    
                        <tr valign="top">
                        
                            <td class="smallBoldLabel">${field.name} <sup>${field.legendKey}</sup></td>
                            
                            <td>
                                <i:inline key=".accountField.description.${field}"/>
                            </td>
                            
                        </tr>
                    
                    </c:forEach>
                    
                    <tr>
                        <td colspan="2" style="background-color:#CDCDCD;">
                            <table>
                                <tr>
                                    <td><i:inline key=".accountFieldLegend.a.label"/></td>
                                    <td><i:inline key=".accountFieldLegend.a.text"/></td>
                                </tr>
                                <tr>
                                    <td><i:inline key=".accountFieldLegend.b.label"/></td>
                                    <td><i:inline key=".accountFieldLegend.b.text"/></td>
                                </tr>
                                <tr>
                                    <td><i:inline key=".accountFieldLegend.c.label"/></td>
                                    <td><i:inline key=".accountFieldLegend.c.text"/></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                </table>
                
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                
                <table class="miniResultsTable bulkImport" style="font-size:11px;width:100%;">
                    
                    <tr>
                        <td colspan="2" style="background-color:#CDCDCD;">
                        
                            <table class="noStyle">
                                <tr valign="top">
                                    <td rowspan="2"><img src="${importImg}"></td>
                                    <td><div class="normalBoldLabel"><i:inline key=".importHardwareTableHeader"/></div></td>
                                </tr>
                                <tr>
                                    <td><i:inline key=".importHardwareTableDescription"/></td>
                                </tr>
                            </table>
                        
                        </td>
                    </tr>
            
                    <tr valign="bottom">
                        <th style="width:150px;" align="left"><i:inline key=".hardwareColumnHeader"/></th>
                        <th><i:inline key=".hardwareColumnDescription"/></th>
                    </tr>
                    
                    <c:forEach var="field" items="${hardwareFields}">
                    
                        <tr valign="top">
                        
                            <td class="smallBoldLabel">${field.name} <sup>${field.legendKey}</sup></td>
                            
                            <td>
                                <i:inline key=".hardwareField.description.${field}"/>
                            </td>
                            
                        </tr>
                    
                    </c:forEach>
                    
                    <tr>
                        <td colspan="2" style="background-color:#CDCDCD;">
                            <table>
                                <tr>
                                    <td><i:inline key=".hardwareFieldLegend.a.label"/></td>
                                    <td><i:inline key=".hardwareFieldLegend.a.text"/></td>
                                </tr>
                                <tr>
                                    <td><i:inline key=".hardwareFieldLegend.b.label"/></td>
                                    <td><i:inline key=".hardwareFieldLegend.b.text"/></td>
                                </tr>
                                <tr>
                                    <td><i:inline key=".hardwareFieldLegend.c.label"/></td>
                                    <td><i:inline key=".hardwareFieldLegend.c.text"/></td>
                                </tr>
                                <tr>
                                    <td><i:inline key=".hardwareFieldLegend.d.label"/></td>
                                    <td><i:inline key=".hardwareFieldLegend.d.text"/></td>
                                </tr>
                                <tr>
                                    <td><i:inline key=".hardwareFieldLegend.e.label"/></td>
                                    <td><i:inline key=".hardwareFieldLegend.e.text"/></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                </table>
            </cti:dataGridCell>
            
        </cti:dataGrid>
    
    </tags:boxContainer2>

</cti:standardPage>