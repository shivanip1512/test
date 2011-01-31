<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="generalInfo.${mode}">

<script type="text/javascript">
YEvent.observeSelectorClick('img[id^=removeMemberIcon]', function(event) {
    var theCell = Event.findElement(event, 'td');
    var ecId = $F(theCell.down('input[name=memberId]'));
    var title = $F(theCell.down('input[name=title]'));
    var message = $F(theCell.down('input[name=message]'));
    var form = $('memberCompanyForm');
    Element.writeAttribute(form, 'action', '/spring/adminSetup/energyCompany/general/removeMember');
    
    var confirmPopup = $('confirmPopup');
    var titleBar = confirmPopup.down('div[id=confirmPopup_title]');
    titleBar.innerHTML = title;
    var messageContainer = confirmPopup.down('div[id=confirmMessage]');
    messageContainer.innerHTML = message;
    var memberId = confirmPopup.down('input[name=memberId]');
    Element.writeAttribute(memberId, 'value', ecId);
    confirmPopup.show();
});

YEvent.observeSelectorClick('#addMember', function(event) {
    var form = $('memberCompanyForm');
    Element.writeAttribute(form, 'action', '/spring/adminSetup/energyCompany/general/addMember');
    var selectedMemberIndex = $('newMemberId').selectedIndex;
    var selectedMember =  $('newMemberId').options[selectedMemberIndex].text; 
    var title = $F('addTitle') + selectedMember;
    var message = $F('addMessage') + selectedMember + '?';
    
    var confirmPopup = $('confirmPopup');
    var titleBar = confirmPopup.down('div[id=confirmPopup_title]');
    titleBar.innerHTML = title;
    var messageContainer = confirmPopup.down('div[id=confirmMessage]');
    messageContainer.innerHTML = message;
    var memberId = confirmPopup.down('input[name=memberId]');
    var newMemberId = $F('newMemberId');
    Element.writeAttribute(memberId, 'value', newMemberId);
    confirmPopup.show();
});

YEvent.observeSelectorClick('#confirmCancel', function(event) {
    $('confirmPopup').hide();
});

</script>
    
    <cti:includeCss link="/WebConfig/yukon/styles/admin/energyCompany.css"/>
    <tags:setFormEditMode mode="${mode}"/>
    
        <cti:dataGrid cols="${cols}" tableClasses="energyCompanyHomeLayout">
        
            <%-- LEFT SIDE COLUMN --%>
            <cti:dataGridCell>
                <form:form commandName="generalInfo" method="post" action="/spring/adminSetup/energyCompany/general/update">
                    <form:hidden path="ecId"/>
                    
                    <tags:formElementContainer nameKey="infoContainer">
                    
                        <tags:nameValueContainer2>
                            <tags:inputNameValue nameKey=".name" path="name" size="35" maxlength="60"/>
                            
                            <cti:displayForPageEditModes modes="EDIT">
                                <tags:inputNameValue nameKey=".streetAddress1" path="address.locationAddress1" size="35" maxlength="40"/>
                                <tags:inputNameValue nameKey=".streetAddress2" path="address.locationAddress2" size="35" maxlength="40"/>
                                <tags:inputNameValue nameKey=".city" path="address.cityName" size="32" maxlength="32"/>
                                <tags:inputNameValue nameKey=".stateCode" path="address.stateCode" size="2" maxlength="2"/>
                                <tags:inputNameValue nameKey=".zipCode" path="address.zipCode" size="12" maxlength="12"/>
                                <tags:inputNameValue nameKey=".county" path="address.county" size="30" maxlength="30"/>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="VIEW">
                                <tags:nameValue2 nameKey=".address">
                                    <tags:address address="${generalInfo.address}" inLine="true" />
                                </tags:nameValue2>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="VIEW">
                                <tags:nameValue2 nameKey=".phone"><cti:formatPhoneNumber value="${generalInfo.phone}"/></tags:nameValue2>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="EDIT">
                                <tags:inputNameValue nameKey=".phone" path="phone"/>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="VIEW">
                                <tags:nameValue2 nameKey=".fax"><cti:formatPhoneNumber value="${generalInfo.fax}"/></tags:nameValue2>
                            </cti:displayForPageEditModes>
                            
                            <cti:displayForPageEditModes modes="EDIT">
                                <tags:inputNameValue nameKey=".fax" path="fax"/>
                            </cti:displayForPageEditModes>

                            <tags:inputNameValue nameKey=".email" path="email" size="35" maxlength="130"/>
                            <tags:selectNameValue items="${routes}" itemLabel="paoName" nameKey=".route" path="defaultRouteId" itemValue="yukonID" defaultItemLabel="${none}" defaultItemValue="-1"/>
                            
                        </tags:nameValueContainer2>
                    
                    </tags:formElementContainer>
                    
                    <div class="pageActionArea">
                        <cti:displayForPageEditModes modes="VIEW">
                            <cti:button key="edit" type="submit" name="edit"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button key="save" type="submit" name="save"/>
                            <cti:button key="delete" type="submit" name="delete"/>
                            <cti:button key="cancel" type="submit" name="cancel"/>
                        </cti:displayForPageEditModes>
                    </div>
                
                </form:form>
            </cti:dataGridCell>
            
            <cti:displayForPageEditModes modes="VIEW">
                
                <%-- RIGHT SIDE COLUMN --%>
                <cti:checkRolesAndProperties value="ADMIN_MANAGE_MEMBERS">
                
                    <cti:dataGridCell>
                
                        <tags:boxContainer2 nameKey="membersContainer">
                            <c:choose>
                                <c:when test="${empty members}">
                                    <div><i:inline key=".noMembers"/></div>
                                </c:when>
                                <c:otherwise>
                                    <div class="membersContainer">
                                        <table class="compactResultsTable">
                                            <tr>
                                                <th><i:inline key=".companyName"/></th>
                                                <th class="removeColumn"><i:inline key=".remove"/></th>
                                            </tr>
                                            
                                            <c:forEach var="company" items="${members}">
                                                <cti:url value="/spring/adminSetup/energyCompany/general/view" var="viewEcUrl">
                                                    <cti:param name="ecId" value="${company.ecId}"/>
                                                </cti:url>
                                                <tr>
                                                    <td><a href="${viewEcUrl}">${company.name}</a></td>
                                                    <td class="removeColumn">
                                                        <cti:img key="remove" id="removeMemberIcon${company.ecId}" styleClass="pointer"/>
                                                        <cti:msg2 key="yukon.web.modules.adminSetup.generalInfo.confirmRemove.title" argument="${company.name}" var="title"/>
                                                        <cti:msg2 key="yukon.web.modules.adminSetup.generalInfo.confirmRemove.message" argument="${company.name}" var="message"/>
                                                        <input type="hidden" name="memberId" value="${company.ecId}">
                                                        <input type="hidden" name="title" value="${title}">
                                                        <input type="hidden" name="message" value="${message}">
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            
                            <div>
                                <span style="float: left;margin-top: 15px;"><cti:button key="create" type="submit" name="create"/></span>
                                <c:if test="${!empty memberCandidates}">
                                    <span class="actionArea" style="float: right;">
                                        <select name="newMemberId" id="newMemberId">
                                            <c:forEach items="${memberCandidates}" var="member">
                                                <option value="${member.energyCompanyId}">${member.name}</option>
                                            </c:forEach>
                                        </select>
                                        <cti:button key="add" id="addMember"/>
                                        <cti:msg2 key="yukon.web.modules.adminSetup.generalInfo.confirmAdd.title" var="addTitle"/>
                                        <cti:msg2 key="yukon.web.modules.adminSetup.generalInfo.confirmAdd.message" var="addMessage"/>
                                        <input type="hidden" id="addTitle" value="${addTitle}">
                                        <input type="hidden" id="addMessage" value="${addMessage}">
                                    </span>
                                </c:if>
                            </div>
                            
                            <tags:simplePopup title="" id="confirmPopup" styleClass="smallSimplePopup">
                                <form action="" method="post" id="memberCompanyForm">
                                    <input type="hidden" name="ecId" value="${ecId}">
                                    <input type="hidden" name="memberId" value="">
                                    <div id="confirmMessage"></div>
                                    <div class="actionArea">
                                        <cti:button key="ok" type="submit"/>
                                        <cti:button key="cancel" id="confirmCancel"/>
                                    </div>
                                </form>
                            </tags:simplePopup>
    
                        </tags:boxContainer2>
                        
                    </cti:dataGridCell>
                    
                </cti:checkRolesAndProperties>
                
            </cti:displayForPageEditModes>
        
        </cti:dataGrid>
        
        
</cti:standardPage>