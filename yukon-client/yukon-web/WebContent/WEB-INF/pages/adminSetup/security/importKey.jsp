<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="modules.adminSetup.security">

<cti:flashScopeMessages/>

<form:form method="POST" modelAttribute="fileImportBindingBean" action="importKeyFile" autocomplete="off" enctype="multipart/form-data">
   <tags:nameValueContainer2>
    <cti:csrfToken/>
    <tags:nameValue2 nameKey=".importKeyFile">
        <tags:bind path="file">
            <tags:file name="keyFile"/>
        </tags:bind>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".keyName">
        <tags:input path="name" size="50" />
    </tags:nameValue2>
   </tags:nameValueContainer2>
</form:form>

</cti:msgScope>