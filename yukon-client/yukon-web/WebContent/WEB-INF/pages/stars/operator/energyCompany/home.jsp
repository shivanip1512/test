<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="energyCompanyOperations">

    <cti:includeCss link="/WebConfig/yukon/styles/operator/energyCompany.css"/>

    <cti:dataGrid cols="2" tableClasses="energyCompanyOperationsLayout">
    
        <%-- LEFT SIDE COLUMN --%>
        <cti:dataGridCell>
            <tags:boxContainer title="Configure Energy Company">
                
                <table class="compactResultsTable">
                    <tr>
                        <th>Name</th>
                        <th>Login</th>
                        <th>Type</th>
                        <th class="removeColumn">Delete</th>
                    </tr>
                    <tr>
                        <td><a href="/spring/stars/operator/energyCompany/generalInfo?ecId=0">Norris Public Power District</a></td>
                        <td>norrisAdmin</td>
                        <td>Parent</td>
                        <td class="removeColumn"><img src="/WebConfig/yukon/Icons/delete.gif"></td>
                    </tr>
                
                    <tr>
                        <td><a href="/spring/stars/operator/energyCompany/energyCompanyInfo?ecId=0&mode=VIEW">South West Coop</a></td>
                        <td>swAdmin</td>
                        <td>Member</td>
                        <td class="removeColumn"><img src="/WebConfig/yukon/Icons/delete.gif"></td>
                    </tr>
                    <tr>
                        <td><a href="">Lakeville Coop</a></td>
                        <td>lvAdmin</td>
                        <td>Member</td>
                        <td class="removeColumn"><img src="/WebConfig/yukon/Icons/delete.gif"></td>
                    </tr>
                    <tr>
                        <td><a href="">Talladega Coop</a></td>
                        <td>tAdmin</td>
                        <td>Member</td>
                        <td class="removeColumn"><img src="/WebConfig/yukon/Icons/delete.gif"></td>
                    </tr>
                </table>
                
                <div class="actionArea">
                    <cti:button key="create"/>
                </div>
            
            </tags:boxContainer>
        
        </cti:dataGridCell>
        
        <%-- RIGHT SIDE COLUMN --%>
        <cti:dataGridCell>
            
        </cti:dataGridCell>
    
    </cti:dataGrid>

</cti:standardPage>