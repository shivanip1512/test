<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<form:checkbox path="${param.path}.parameters['${param.parameterName}']" value="true"/><cti:msg2 key="${param.parameterKey}"/>
