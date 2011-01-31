<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="home">

    <cti:includeCss link="/WebConfig/yukon/styles/admin/energyCompany.css"/>

    <cti:dataGrid cols="2" tableClasses="energyCompanyHomeLayout">
    
        <%-- LEFT SIDE COLUMN --%>
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="companiesContainer">
                
                <div class="membersContainer">
                    <table class="compactResultsTable">
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th><i:inline key=".type"/></th>
                        </tr>
                        
                        <c:forEach items="${companies}" var="company" varStatus="status">
                            <tr>
                                <td><a href="/spring/adminSetup/energyCompany/general/view?ecId=${company.energyCompanyId}">${company.name}</a></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${status.index == 0}"><i:inline key=".parent"/></c:when>
                                        <c:otherwise><i:inline key=".member"/></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                
                <div class="actionArea">
                    <form action="/spring/adminSetup/energyCompany/create">
                        <cti:button key="create" type="submit" name="create"/>
                    </form>
                </div>
            
            </tags:boxContainer2>
        
        </cti:dataGridCell>
        
        <%-- RIGHT SIDE COLUMN --%>
        <cti:dataGridCell>
            
        </cti:dataGridCell>
    
    </cti:dataGrid>

</cti:standardPage>