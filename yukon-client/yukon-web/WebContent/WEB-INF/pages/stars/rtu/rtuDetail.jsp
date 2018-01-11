<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="operator" page="rtuDetail">

    <tags:setFormEditMode mode="${mode}" />

    <form:form commandName="rtu">
    
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="general">
                    <tags:nameValueContainer2 tableClass="natural-width">
                        <tags:nameValue2 nameKey=".name">
                            <tags:input path="name" size="25" maxlength="60" autofocus="autofocus"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".type">
                            <i:inline key="${rtu.paoType}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".status">
                            <tags:switchButton path="disableFlag" inverse="${true}"
                                offNameKey=".disabled.label" onNameKey=".enabled.label" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="configuration">
                </tags:sectionContainer2>
            </div>
        </div>

    </form:form>
    
</cti:standardPage>