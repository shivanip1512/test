<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="i18nDemo">
    
    <cti:includeScript link="/resources/js/lib/google-code-prettify/prettify.js"/>
    <cti:includeCss link="/resources/js/lib/google-code-prettify/prettify.css"/>

    <div class="i18nDemo">

        <p>This page shows some examples of how to do internationalization in Yukon.</p>

        <h1>Escaping</h1>

        <p>The cti:msg2 tag offers several options for escaping. The i:inline uses the defaults of cti:msg2 so if
            you need greater control over escaping, use cti:msg2 instead of i:inline.</p>

        <p>There are three kinds of escaping as explained here:</p>

        <h2>htmlEscape</h2>
        Escape HTML elements in the entire message, after arguments have been embedded. This is false by default.

        <h2>htmlEscapeArguments</h2>
        Escape HTML elements in the arguments before they are added to the message. This is true by default. If
        htmlEscape is turned on, this will be ignored so arguments do not get escaped twice.

        <h2>javaScriptEscape</h2>
        Escape for use as a JavaScript string. This is false by default. (Like htmlEscape, this attribute escapes the
        entire message after arguments have been added.)

        <h2>HTML Escaping</h2>

        <p>By default, HTML in arguments is escaped but not in the message itself. Consider the following code:</p>

        <pre class="code prettyprint"><cti:msg2 key=".defaultEscapingExampleCode" htmlEscape="true"/></pre>

        <p>with the following key:</p>

        <pre class="code prettyprint"><cti:msg2 key=".defaultEscapingExampleMsg" htmlEscape="true"/></pre>

        <p>and an argument of "${fn:escapeXml(thisNotThat)}", render as:</p>

        <pre class="code prettyprint"><cti:msg2 key=".defaultEscapingExample" argument="${thisNotThat}"/></pre>

        <p>If we turn on HTML escaping for the whole message:</p>

        <pre class="code prettyprint"><cti:msg2 key=".fullHTMLEscapingExampleCode" htmlEscape="true"/></pre>

        <p>we will see everything in the message escaped:</p>

        <pre class="code prettyprint"><cti:msg2 key=".defaultEscapingExample" argument="${thisNotThat}" htmlEscape="true"/></pre>

        <p>If we turn off even HTML argument escaping:</p>

        <pre class="code prettyprint"><cti:msg2 key=".noHTMLEscapingExampleCode" htmlEscape="true"/></pre>

        <p>nothing in the message will be escaped (and thus any HTML in it gets used on the page):</p>

        <pre class="code"><cti:msg2 key=".defaultEscapingExample" argument="${thisNotThat}" htmlEscapeArguments="false"/></pre>

        <h2>JavaScript (ECMA) Escaping</h2>

        <p>The "javaScriptEscape" attribute can be used to escape a string for use as a JavaScript string. I can't
            imagine why we would ever want this.</p>

        <h1>Message Format Options</h1>

        <p>
            The key value pairs we put in .xml file for localization are parsed using standard Java classes. You can
            read all about this in the <a
                href="http://download.oracle.com/javase/6/docs/api/java/text/MessageFormat.html">official Java
                Documentation</a> but here you will find a summary of the most useful bits.
        </p>

        <h2>Choice Format</h2>

        <p>This is the most common format you'll need. The most useful format is (replacing "argumentNumber",
            "noItemsMessage", etc. as appropriate):</p>

        <pre class="code prettyprint">{argumentNumber,choice,0#noItemsMessage|1#oneItemMessage|&lt;multipleItemsMessage}</pre>

        <p>Note that {argumentNumber} will normally also be nested inside the multiple items message.</p>

        <p>Using the following i18n value:</p>

        <pre class="code prettyprint">There {0,choice,0#are no items|1#is one item|1&lt;are {0} items}.</pre>

        <p>you will get the following output using the arguments 0, 1, 2 and 5 respectively:</p>

        <ul>
            <li><i:inline key=".choiceExample" arguments="${numberZero}"/></li>
            <li><i:inline key=".choiceExample" arguments="${numberOne}"/></li>
            <li><i:inline key=".choiceExample" arguments="${numberTwo}"/></li>
            <li><i:inline key=".choiceExample" arguments="${numberFive}"/></li>
        </ul>

        <h2>Number Format</h2>

        <p>The number format is useful when rounding is needed. Using the following format:</p>

        <pre class="code prettyprint">{0,number,0.00}</pre>
        <ul>
            <li>1.0/3.0 formats as <i:inline key=".decimalExample.2places" arguments="${numberOneThird}"/></li>
            <li>1.0/2.0 formats as <i:inline key=".decimalExample.2places" arguments="${numberOneHalf}"/></li>
            <li>2.0/3.0 formats as <i:inline key=".decimalExample.2places" arguments="${numberTwoThirds}"/></li>
            <li>5.0/9.0 formats as <i:inline key=".decimalExample.2places" arguments="${numberFiveNinths}"/></li>
        </ul>

        <p>The number format can also be useful for adding commas to make large numbers more readable. Using the
            following format:</p>

        <pre class="code prettyprint">{0,number,#,##0.0}</pre>

        <p>${largeDecimal} formats as
            <i:inline key=".decimalExample.withCommas" arguments="${largeDecimal}"/>
        </p>
        <p>${largeInteger} formats as
            <i:inline key=".decimalExample.withCommas" arguments="${largeInteger}"/>
        </p>

        <h2>Date and Time Formats</h2>

        <p>Don't use these. Use our own date formatting services (cti:formatDate). If the date is part of a larger
            message, format it as a string and pass it as an argument to the other format.</p>

        <h2>Sorted Enum Values</h2>

        <p>This is an DisplayableEnum sorted with a specific value forced to the top and another foced to the
            bottom.</p>

        <select>
            <c:forEach var="color" items="${colors}">
                <option><cti:msg2 key="${color}"/></option>
            </c:forEach>
        </select>

        <p>This is an enum which implements Displayable sorted.</p>

        <select>
            <c:forEach var="builtInAttribute" items="${builtInAttributes}">
                <option><cti:msg2 key="${builtInAttribute}"/></option>
            </c:forEach>
        </select>

    </div>
    
    <script>$(function() { prettyPrint(); })</script>
</cti:standardPage>