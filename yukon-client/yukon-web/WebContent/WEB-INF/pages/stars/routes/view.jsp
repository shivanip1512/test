<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>



<cti:msgScope paths="yukon.web.modules.operator.routes">
    <cti:standardPage module="operator" page="routes.${mode}">
        <tags:nameValueContainer2>
        
            <form:form modelAttribute="communicationRoute" action="${action}" method="post">
            <tags:sectionContainer2 nameKey="general">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".name">
                        <tags:input path="deviceName" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
                    </tags:nameValue2>
                
                
                    <tags:nameValue2 nameKey=".defaultRoute">
                        <tags:switchButton path="defaultRoute" onNameKey=".yes.label" offNameKey=".no.label"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            </form:form>
            
        </tags:nameValueContainer2>
    </cti:standardPage>
</cti:msgScope>
    

    