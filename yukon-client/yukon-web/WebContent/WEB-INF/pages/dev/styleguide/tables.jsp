<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="tables">
<tags:styleguide page="tables">

<style>
.description { line-height: 20px; }
</style>

<h2>Results Table</h2>
<div class="table-example clearfix stacked">
    <p class="description"><span class="label label-default">.results-table</span> is used to display data when a bordered around the table is desired.
    We usually do not want border tables so <span class="label label-default">.compact-results-table</span> are preferred.
    </p>
    <table class="results-table">
        <thead>
            <tr>
                <th>Header 1</th>
                <th>Header 2</th>
                <th>Header 3</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
        </tbody>
    </table>
<pre class="code prettyprint">
&lt;table class=&quot;results-table&quot;&gt;
    &lt;thead&gt;
        &lt;tr&gt;
            &lt;th&gt;Header 1&lt;/th&gt;
            &lt;th&gt;Header 2&lt;/th&gt;
            &lt;th&gt;Header 3&lt;/th&gt;
        &lt;/tr&gt;
    &lt;/thead&gt;
    &lt;tfoot&gt;&lt;/tfoot&gt;
    &lt;tbody&gt;
        &lt;tr&gt;
            &lt;td&gt;Cell 1&lt;/td&gt;
            &lt;td&gt;Cell 2&lt;/td&gt;
            &lt;td&gt;Cell 3&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;Cell 1&lt;/td&gt;
            &lt;td&gt;Cell 2&lt;/td&gt;
            &lt;td&gt;Cell 3&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;Cell 1&lt;/td&gt;
            &lt;td&gt;Cell 2&lt;/td&gt;
            &lt;td&gt;Cell 3&lt;/td&gt;
        &lt;/tr&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
</pre>
</div>

<h2>Compact Results Table</h2>
<div class="table-example clearfix stacked">
    <p class="description"><span class="label label-default">.compact-results-table</span> is the preffered table to use when
    tabling data.  It has a .9em font size meaning the font-size will be 90% of the parent's font size. Becareful not to 
    nest these tables since the inner table will be have the 90% applied once for every level of nesting.</p>
    <table class="compact-results-table">
        <thead>
            <tr>
                <th>Header 1</th>
                <th>Header 2</th>
                <th>Header 3</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
        </tbody>
    </table>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table&quot;&gt;
    &lt;thead&gt;
        &lt;tr&gt;
            &lt;th&gt;Header 1&lt;/th&gt;
            &lt;th&gt;Header 2&lt;/th&gt;
            &lt;th&gt;Header 3&lt;/th&gt;
        &lt;/tr&gt;
    &lt;/thead&gt;
    &lt;tfoot&gt;&lt;/tfoot&gt;
    &lt;tbody&gt;
        &lt;tr&gt;
            &lt;td&gt;Cell 1&lt;/td&gt;
            &lt;td&gt;Cell 2&lt;/td&gt;
            &lt;td&gt;Cell 3&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;Cell 1&lt;/td&gt;
            &lt;td&gt;Cell 2&lt;/td&gt;
            &lt;td&gt;Cell 3&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;Cell 1&lt;/td&gt;
            &lt;td&gt;Cell 2&lt;/td&gt;
            &lt;td&gt;Cell 3&lt;/td&gt;
        &lt;/tr&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
</pre>
</div>
<h3 class="subtle">Dashed Headers and Rows</h3>
<div class="table-example clearfix stacked">
    <p class="description">
        Adding <span class="label label-default">.dashed</span> to a 
        <span class="label label-default">.compact-results-table</span> change the header border to a dashed line.  Use this
        when the table is inside a section container.
    </p>
    <tags:sectionContainer2 nameKey="areaFormulas">
        <table class="compact-results-table dashed">
            <thead><tr><th>Shape</th><th>Forumla</th></tr></thead>
            <tfoot></tfoot>
            <tbody>
                <tr>
                    <td>Circle</td>
                    <td>A = &pi; r<sup>2</sup></td>
                </tr>
                <tr>
                    <td>Square</td>
                    <td>A = a<sup>2</sup></td>
                </tr>
                <tr>
                    <td>Triangle</td>
                    <td>A = (h<sub>b</sub>b) / 2</td>
                </tr>
            </tbody>
        </table>
    </tags:sectionContainer2>
<pre class="code prettyprint">
&lt;tags:sectionContainer2 nameKey=&quot;areaFormulas&quot;&gt;
    &lt;table class=&quot;compact-results-table dashed&quot;&gt;
        ...
    &lt;/table&gt;
&lt;/tags:sectionContainer2&gt;
</pre>
    <p class="description">
        Adding <span class="label label-default">.separated</span> to a 
        <span class="label label-default">.compact-results-table</span> will add dashed borders between rows.
    </p>
    <table class="compact-results-table separated">
        <thead>
            <tr>
                <th>Header 1</th>
                <th>Header 2</th>
                <th>Header 3</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
            <tr>
                <td>Cell 1</td>
                <td>Cell 2</td>
                <td>Cell 3</td>
            </tr>
        </tbody>
    </table>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table separated&quot;&gt;
...
&lt;/table&gt;
</pre>
</div>

<h2>Name Value Table</h2>
<div class="table-example clearfix stacked">
    <p class="description"><span class="label label-default">.name-value-table</span> is a two column table used to display 
    data that fits in pairs.  This table is often used in a form to label form fields.</p>
    <table class="name-value-table natural-width">
        <tr>
            <td class="name">Circle</td>
            <td class="value">A = &pi; r<sup>2</sup></td>
        </tr>
        <tr>
            <td class="name">Square</td>
            <td class="value">A = a<sup>2</sup></td>
        </tr>
        <tr>
            <td class="name">Triangle</td>
            <td class="value">A = (h<sub>b</sub>b) / 2</td>
        </tr>
    </table>
<pre class="code prettyprint">
&lt;table class=&quot;name-value-table natural-width&quot;&gt;
    &lt;tr&gt;
        &lt;td class=&quot;name&quot;&gt;Circle&lt;/td&gt;
        &lt;td class=&quot;value&quot;&gt;A = &amp;pi; r&lt;sup&gt;2&lt;/sup&gt;&lt;/td&gt;
    &lt;/tr&gt;
    &lt;tr&gt;
        &lt;td class=&quot;name&quot;&gt;Square&lt;/td&gt;
        &lt;td class=&quot;value&quot;&gt;A = a&lt;sup&gt;2&lt;/sup&gt;&lt;/td&gt;
    &lt;/tr&gt;
    &lt;tr&gt;
        &lt;td class=&quot;name&quot;&gt;Triangle&lt;/td&gt;
        &lt;td class=&quot;value&quot;&gt;A = (h&lt;sub&gt;b&lt;/sub&gt;b) / 2&lt;/td&gt;
    &lt;/tr&gt;
&lt;/table&gt;
</pre>
    <p class="description">Instead of hardcoding this table there are tags that will build it for you. 
        <span class="label label-default">&lt;tags:nameValueContainer2/&gt;</span> will create the table element and 
        <span class="label label-default">&lt;tags:nameValue2&gt;</span> will create the tr and both td elements. These tags 
        provide the localizing of the name text. Use these tags instead of the raw html
    </p>
<pre class="code prettyprint">
&lt;tags:nameValueContainer2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.circle&quot;&gt;A = &amp;pi; r&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.square&quot;&gt;A = a&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.triangle&quot;&gt;A = (h&lt;sub&gt;b&lt;/sub&gt;b) / 2&lt;/tags:nameValue2&gt;
&lt;/tags:nameValueContainer2&gt;
</pre>
    <p class="description">There are additional tags that will handle the name and value td elements where the value will be
        a form input.  They have support to handle <a href="http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/view.html">spring binding</a>.
    </p>
    <form:form commandName="signup">
        <tags:nameValueContainer2>
            <tags:inputNameValue nameKey=".name" path="name"/>
            <tags:selectNameValue nameKey=".type" items="${signupTypes}" path="type"/>
            <tags:nameValueGap2 gapHeight="20px"/>
            <tags:checkboxNameValue nameKey="yukon.web.defaults.blank" checkBoxDescriptionNameKey=".enabled" path="enabled" excludeColon="true"/>
            <tags:textareaNameValue nameKey=".notes" rows="3" cols="20" path="notes"/>
        </tags:nameValueContainer2>
    </form:form>
<pre class="code prettyprint">
&lt;form:form commandName=&quot;signup&quot;&gt;
    &lt;tags:nameValueContainer2&gt;
        &lt;tags:inputNameValue nameKey=&quot;.name&quot; path=&quot;name&quot;/&gt;
        &lt;tags:selectNameValue nameKey=&quot;.type&quot; items=&quot;&#36;{signupTypes}&quot; path=&quot;type&quot;/&gt;
        &lt;tags:nameValueGap2 gapHeight=&quot;20px&quot;/&gt;
        &lt;tags:checkboxNameValue nameKey=&quot;yukon.web.defaults.blank&quot; checkBoxDescriptionNameKey=&quot;.enabled&quot; path=&quot;enabled&quot; excludeColon=&quot;true&quot;/&gt;
        &lt;tags:textareaNameValue nameKey=&quot;.notes&quot; rows=&quot;3&quot; cols=&quot;20&quot; path=&quot;notes&quot;/&gt;
    &lt;/tags:nameValueContainer2&gt;
&lt;/form:form&gt;
</pre>
</div>

<h2>Link Table</h2>
<div class="table-example clearfix stacked">
    <p class="description"><span class="label label-default">.link-table</span> is used to hold of list a links and thier
        descriptions.
    </p>
    <table class="link-table">
        <tr>
            <td><a href="grids">Style Guide: Grids</a></td>
            <td>A page showing you how to use the grid system.</td>
        </tr>
        <tr>
            <td><a href="buttons">Style Guide: Buttons</a></td>
            <td>A page showing you how to create button elements.</td>
        </tr>
        <tr>
            <td><a href="icons">Style Guide: Icons</a></td>
            <td>A page listing all the icons and their class names.</td>
        </tr>
    </table>
<pre class="code prettyprint">
&lt;table class=&quot;link-table&quot;&gt;
    &lt;tr&gt;
        &lt;td&gt;&lt;a href=&quot;grids&quot;&gt;Style Guide: Grids&lt;/a&gt;&lt;/td&gt;
        &lt;td&gt;A page showing you how to use the grid system.&lt;/td&gt;
    &lt;/tr&gt;
    &lt;tr&gt;
        &lt;td&gt;&lt;a href=&quot;buttons&quot;&gt;Style Guide: Buttons&lt;/a&gt;&lt;/td&gt;
        &lt;td&gt;A page showing you how to create button elements.&lt;/td&gt;
    &lt;/tr&gt;
    &lt;tr&gt;
        &lt;td&gt;&lt;a href=&quot;icons&quot;&gt;Style Guide: Icons&lt;/a&gt;&lt;/td&gt;
        &lt;td&gt;A page listing all the icons and their class names.&lt;/td&gt;
    &lt;/tr&gt;
&lt;/table&gt;
</pre>
</div>

<h2>Table Helpers</h2>
<h3 class="subtle">Striping</h3>
<div class="table-example clearfix stacked">
    <p class="description">
        <span class="label label-default">.compact-results-table</span> and
        <span class="label label-default">.results-table</span> are striped by default.  striping means every even row
        has a slightly grey background.  You can add striping to other tables by adding the class 
        <span class="label label-default">.striped</span>.
    </p>
    
<pre class="code prettyprint">
&lt;tags:nameValueContainer2 tableClass=&quot;striped&quot;&gt;
    &lt;tags:nameValue2 nameKey=&quot;.circle&quot;&gt;A = &amp;pi; r&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.square&quot;&gt;A = a&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.triangle&quot;&gt;A = (h&lt;sub&gt;b&lt;/sub&gt;b) / 2&lt;/tags:nameValue2&gt;
&lt;/tags:nameValueContainer2&gt;
</pre>
    <p class="description">
        You can turn striping off for <span class="label label-default">.compact-results-table</span> and
        <span class="label label-default">.results-table</span> tables by using 
        <span class="label label-default">.manual-striping</span>.  You can then add the striping back by using
         <span class="label label-default">.alt-row</span> on rows you want striped.  This is useful when there are hidden
         rows that throw off the even/odd counting.
    </p>
    <table class="compact-results-table manual-striping">
        <thead><tr><th>header 1</th><th>header 2</th></tr></thead>
        <tfoot></tfoot>
        <tbody>
            <tr><td>visible</td><td>visible</td></tr>
            <tr class="dn"><td>hidden</td><td>hidden</td></tr>
            <tr class="alt-row"><td>visible</td><td>visible</td></tr>
        </tbody>
    </table>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table manual-striping&quot;&gt;
    &lt;thead&gt;&lt;tr&gt;&lt;th&gt;header 1&lt;/th&gt;&lt;th&gt;header 2&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;
    &lt;tfoot&gt;&lt;/tfoot&gt;
    &lt;tbody&gt;
        &lt;tr&gt;&lt;td&gt;visible&lt;/td&gt;&lt;td&gt;visible&lt;/td&gt;&lt;/tr&gt;
        &lt;tr class=&quot;dn&quot;&gt;&lt;td&gt;hidden&lt;/td&gt;&lt;td&gt;hidden&lt;/td&gt;&lt;/tr&gt;
        &lt;tr class=&quot;alt-row&quot;&gt;&lt;td&gt;visible&lt;/td&gt;&lt;td&gt;visible&lt;/td&gt;&lt;/tr&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
</pre>
</div>

</tags:styleguide>
</cti:standardPage>