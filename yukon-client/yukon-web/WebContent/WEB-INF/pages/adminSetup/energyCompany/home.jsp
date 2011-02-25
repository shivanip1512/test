<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="home">

    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
    
        <%-- LEFT SIDE COLUMN --%>
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="companiesContainer">
                
                <div class="membersContainer">
                    <ul>
                        <c:forEach items="${companies}" var="company" varStatus="status">
                            <li>
                                <a href="/spring/adminSetup/energyCompany/general/view?ecId=${company.energyCompanyId}">
                                    <spring:escapeBody htmlEscape="true">${company.name}</spring:escapeBody>
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                
                <cti:checkRolesAndProperties value="ADMIN_SUPER_USER">
                    <div class="actionArea">
                        <form action="/spring/adminSetup/energyCompany/new">
                            <cti:button key="create" type="submit" name="create"/>
                        </form>
                    </div>
                </cti:checkRolesAndProperties>
            
            </tags:boxContainer2>
        
        </cti:dataGridCell>
        
        <%-- RIGHT SIDE COLUMN --%>
        <cti:dataGridCell>
            
        </cti:dataGridCell>
    
    </cti:dataGrid>

</cti:standardPage>