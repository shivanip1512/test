<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="generalInfo.${mode}">

    <tags:setFormEditMode mode="${mode}"/>

    <cti:includeCss link="/WebConfig/yukon/styles/operator/energyCompany.css"/>
    <style>
        a.fakePicker {
            font-size: 11px;
            color: black;
            text-decoration: none;
            white-space: nowrap;
        }
        a.fakePicker:hover span{
            font-size: 11px;
            color: black;
            text-decoration: underline;
            cursor: pointer;
            white-space: nowrap;
        }
    </style>
    
    <form action="/spring/stars/operator/energyCompany/editGeneralInfo">
        <input type="hidden" value="0" name="ecId">

        <cti:dataGrid cols="2" tableClasses="energyCompanyOperationsLayout">
        
            <%-- LEFT SIDE COLUMN --%>
            <cti:dataGridCell>
                <div id="viewDiv">
                
                    <tags:formElementContainer nameKey="infoContainer">
                        
                        <tags:nameValueContainer>
                            <tags:nameValue name="Name">Norris Public Power District</tags:nameValue>
                            <tags:nameValue name="Address">606 Irving Street, Beatrice, SE 68310</tags:nameValue>
                            <tags:nameValue name="Main Phone #">(402) 223-4037</tags:nameValue>
                            <tags:nameValue name="Main Fax #">(402) 223-4038</tags:nameValue>
                            <tags:nameValue name="Email">dbrauning@norrispower.com</tags:nameValue>
                            <tags:nameValue name="Time Zone">CST</tags:nameValue>
                            <tags:nameValue name="Default Route">* Cart CCU711</tags:nameValue>
                            <tags:nameValue name="Operator Groups">Web Client Operators Grp
                            </tags:nameValue>
                            <tags:nameValue name="Residentail Groups">STARS Residential Customers Grp
                            </tags:nameValue>
                            <tags:nameValue name="Admin Email Sender">
                                info@cannontech.com
                            </tags:nameValue>
                            <tags:nameValue name="Opt Out Notif Recipients"></tags:nameValue>
                        </tags:nameValueContainer>
                        
                    </tags:formElementContainer>
                </div>
            </cti:dataGridCell>
            
            <cti:displayForPageEditModes modes="VIEW">
                <%-- RIGHT SIDE COLUMN --%>
                <cti:dataGridCell>
                    
                    <tags:boxContainer title="Member Energy Companies">
                        
                        <tags:simplePopup title="Member Company" id="memberCompanyPopup" styleClass="mediumSimplePopup">
                            <tags:nameValueContainer style="width:auto;">
                                <tags:nameValue name="Company Name">South West Coop</tags:nameValue>
                                <tags:nameValue name="Member Login">
                                    <select>
                                        <option>swAdmin</option>
                                        <option>swCSR</option>
                                        <option>(none)</option>
                                    </select>
                                </tags:nameValue>
                            </tags:nameValueContainer>
                            
                            <div class="actionArea">
                                <cti:button key="cancel" onclick="$('memberCompanyPopup').hide();"/>
                                <cti:button key="save" onclick="$('memberCompanyPopup').hide();"/>
                            </div>
                        </tags:simplePopup>
                        
                        <table class="compactResultsTable">
                            <tr>
                                <th>Company Name</th>
                                <th>Member Login</th>
                                <th class="removeColumn">Remove</th>
                            </tr>
                            <tr>
                                <td><a href="javascript:void();" onclick="$('memberCompanyPopup').show()">South West Coop</a></td>
                                <td>swAdmin</td>
                                <td class="removeColumn"><img src="/WebConfig/yukon/Icons/delete.gif"></td>
                            </tr>
                            <tr>
                                <td><a href="javascript:void();">Lakeville Coop</a></td>
                                <td>lvAdmin</td>
                                <td class="removeColumn"><img src="/WebConfig/yukon/Icons/delete.gif"></td>
                            </tr>
                            <tr>
                                <td><a href="">Talladega Coop</a></td>
                                <td>tAdmin</td>
                                <td class="removeColumn"><img src="/WebConfig/yukon/Icons/delete.gif"></td>
                            </tr>
                        </table>
                        
                         <div class="actionArea">
                            <cti:button key="add"/>
                        </div>
                        
                    </tags:boxContainer>
                    
                </cti:dataGridCell>
            </cti:displayForPageEditModes>
        
        </cti:dataGrid>
        
        <div class="pageActionArea">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:button key="edit" type="submit" name="edit"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button key="save" type="submit" name="save"/>
                <cti:button key="cancel" type="submit" name="cancel"/>
            </cti:displayForPageEditModes>
        </div>
    
    </form>
</cti:standardPage>