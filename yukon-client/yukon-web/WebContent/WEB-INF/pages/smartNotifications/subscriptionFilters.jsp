<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cti:msgScope paths="modules.smartNotifications">

    <div id="filter-popup" class="dn" data-title="<cti:msg2 key=".filters"/>">

    <cti:url var="action" value="/notifications/subscription/saveDetails"/>
        <form:form action="${baseUrlWithContextPath}" commandName="filter" method="get">
            
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".filter.name">
                    <form:input path="name" size="40"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".filter.state">
                    <form:select path="state">
                        <form:option value="all"><cti:msg2 key=".filter.state.all"/></form:option>
                        <form:option value="active"><cti:msg2 key=".filter.state.active"/></form:option>
                        <form:option value="inactive"><cti:msg2 key=".filter.state.inactive"/></form:option>
                    </form:select>
                </tags:nameValue2>
                <cti:checkRolesAndProperties value="DR_VIEW_PRIORITY">
                    <tags:nameValue2 nameKey=".filter.priority">
                        <tags:input path="priority.min" size="5"/>&nbsp;
                        <i:inline key=".filter.min"/>&nbsp;
                        <tags:input path="priority.max" size="5"/>&nbsp;
                        <i:inline key=".filter.max"/>
                    </tags:nameValue2>
                </cti:checkRolesAndProperties>
            </tags:nameValueContainer2>
            
            <div class="action-area">
                <cti:button nameKey="filter" type="submit" classes="action primary"/>
                <cti:button nameKey="showAll" href="${baseUrlWithContextPath}"/>
            </div>
        </form:form>
     </div>


</cti:msgScope>
