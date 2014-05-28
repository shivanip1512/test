<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
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
    <p class="description"><span class="label label-info">.results-table</span> is used to display data when a bordered around the table is desired.
    We usually do not want border tables so <span class="label label-info">.compact-results-table</span> are preferred.
    </p>
    <h4 class="subtle">Example:</h4>
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
    <h4 class="subtle">Code:</h4>
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
    <p class="description"><span class="label label-info">.compact-results-table</span> is the preffered table to use when
    tabling data.  It has a .9em font size meaning the font-size will be 90% of the parent's font size. Be careful not to 
    nest these tables since the inner table will be have the 90% applied once for every level of nesting.</p>
    <h4 class="subtle">Example:</h4>
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
    <h4 class="subtle">Code:</h4>
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
        Adding <span class="label label-info">.dashed</span> to a 
        <span class="label label-info">.compact-results-table</span> change the header border to a dashed line.  Use this
        when the table is inside a section container.
    </p>
    <h4 class="subtle">Example:</h4>
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
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:sectionContainer2 nameKey=&quot;areaFormulas&quot;&gt;
    &lt;table class=&quot;compact-results-table dashed&quot;&gt;
        ...
    &lt;/table&gt;
&lt;/tags:sectionContainer2&gt;
</pre>
    
    <hr>
    
    <p class="description">
        Adding <span class="label label-info">.separated</span> to a 
        <span class="label label-info">.compact-results-table</span> will add dashed borders between rows.
    </p>
    <h4 class="subtle">Example:</h4>
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
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table separated&quot;&gt;
...
&lt;/table&gt;
</pre>
</div>

<h2>Name Value Table</h2>
<div class="table-example clearfix stacked">
    <p class="description"><span class="label label-info">.name-value-table</span> is a two column table used to display 
    data that fits in pairs.  This table is often used in a form to label form fields.</p>
    <h4 class="subtle">Example:</h4>
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
    <h4 class="subtle">Code:</h4>
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
    
    <hr>

    <p class="description">Instead of hardcoding this table there are tags that will build it for you. 
        <span class="label label-info">&lt;tags:nameValueContainer2/&gt;</span> will create the table element and 
        <span class="label label-info">&lt;tags:nameValue2&gt;</span> will create the tr and both td elements. These tags 
        provide the localizing of the name text. Use these tags instead of the raw html
    </p>
    <h4 class="subtle">Example:</h4>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".circle">A = &pi; r<sup>2</sup></tags:nameValue2>
        <tags:nameValue2 nameKey=".square">A = a<sup>2</sup></tags:nameValue2>
        <tags:nameValue2 nameKey=".triangle">A = (h<sub>b</sub>b) / 2</tags:nameValue2>
    </tags:nameValueContainer2>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:nameValueContainer2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.circle&quot;&gt;A = &amp;pi; r&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.square&quot;&gt;A = a&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.triangle&quot;&gt;A = (h&lt;sub&gt;b&lt;/sub&gt;b) / 2&lt;/tags:nameValue2&gt;
&lt;/tags:nameValueContainer2&gt;
</pre>
    
    <hr>
    
    <p class="description">
        <span class="label label-info">.name-value-table</span> tables have a min width for the name column
        of 140px by default.  This can be overriden to allow the name column to shrink all the way by adding 
        <span class="label label-info">.name-collapse</span>
    </p>
    <h4 class="subtle">Example:</h4>
    <tags:nameValueContainer2 tableClass="name-collapse">
        <tags:nameValue2 nameKey=".circle">A = &pi; r<sup>2</sup></tags:nameValue2>
        <tags:nameValue2 nameKey=".square">A = a<sup>2</sup></tags:nameValue2>
        <tags:nameValue2 nameKey=".triangle">A = (h<sub>b</sub>b) / 2</tags:nameValue2>
    </tags:nameValueContainer2>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:nameValueContainer2 tableClass=&quot;name-collapse&quot;&gt;
    &lt;tags:nameValue2 nameKey=&quot;.circle&quot;&gt;A = &amp;pi; r&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.square&quot;&gt;A = a&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.triangle&quot;&gt;A = (h&lt;sub&gt;b&lt;/sub&gt;b) / 2&lt;/tags:nameValue2&gt;
&lt;/tags:nameValueContainer2&gt;
</pre>
    
    <hr>
    
    <p class="description">There are additional tags that will handle the name and value td elements where the value will be
        a form input.  They have support to handle <a href="http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/view.html">spring binding</a>.
    </p>
    <h4 class="subtle">Example:</h4>
    <form:form commandName="signup">
        <tags:nameValueContainer2>
            <tags:inputNameValue nameKey=".name" path="name"/>
            <tags:selectNameValue nameKey=".type" items="${signupTypes}" path="type"/>
            <tags:nameValueGap2 gapHeight="20px"/>
            <tags:checkboxNameValue nameKey="yukon.web.defaults.blank" checkBoxDescriptionNameKey=".enabled" path="enabled" excludeColon="true"/>
            <tags:textareaNameValue nameKey=".notes" rows="3" cols="20" path="notes"/>
        </tags:nameValueContainer2>
    </form:form>
    <h4 class="subtle">Code:</h4>
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
    <p class="description"><span class="label label-info">.link-table</span> is used to hold of list a links and thier
        descriptions.
    </p>
    <h4 class="subtle">Example:</h4>
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
    <h4 class="subtle">Code:</h4>
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
        <span class="label label-info">.compact-results-table</span> and
        <span class="label label-info">.results-table</span> are striped by default.  striping means every even row
        has a slightly grey background.  You can add striping to other tables by adding the class 
        <span class="label label-info">.striped</span>.
    </p>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:nameValueContainer2 tableClass=&quot;striped&quot;&gt;
    &lt;tags:nameValue2 nameKey=&quot;.circle&quot;&gt;A = &amp;pi; r&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.square&quot;&gt;A = a&lt;sup&gt;2&lt;/sup&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.triangle&quot;&gt;A = (h&lt;sub&gt;b&lt;/sub&gt;b) / 2&lt;/tags:nameValue2&gt;
&lt;/tags:nameValueContainer2&gt;
</pre>
    
    <hr>
    
    <p class="description">
        You can turn striping off for <span class="label label-info">.compact-results-table</span> and
        <span class="label label-info">.results-table</span> tables by using 
        <span class="label label-info">.manual-striping</span> or <span class="label label-info">.no-stipes</span>.  
        You can then add the striping back by using <span class="label label-info">.alt-row</span> on rows you want 
        striped.  This is useful when there are hidden rows that throw off the even/odd counting.
    </p>
    <h4 class="subtle">Example:</h4>
    <table class="compact-results-table manual-striping">
        <thead><tr><th>header 1</th><th>header 2</th></tr></thead>
        <tfoot></tfoot>
        <tbody>
            <tr><td>visible</td><td>visible</td></tr>
            <tr class="dn"><td>hidden</td><td>hidden</td></tr>
            <tr class="alt-row"><td>visible</td><td>visible</td></tr>
        </tbody>
    </table>
    <h4 class="subtle">Code:</h4>
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

<h3 class="subtle">Sizing</h3>
<div class="table-example clearfix stacked">
    <p class="description">
        <span class="label label-info">.natural-width</span> will change a table's width from the
        default <em>100%</em> to <em>auto</em>.
    </p>
    <h4 class="subtle">Example:</h4>
    <table class="compact-results-table natural-width">
        <thead><tr><th>header</th><th>header</th></tr></thead>
        <tfoot></tfoot>
        <tbody>
            <tr><td>cell</td><td>cell</td></tr>
            <tr><td>cell</td><td>cell</td></tr>
            <tr><td>cell</td><td>cell</td></tr>
        </tbody>
    </table>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table natural-width&quot;&gt;
    ...
&lt;/table&gt;
</pre>
    
    <hr>
    
    <p class="description">
        <span class="label label-info">.two-column-table</span> will produce a table that has a width of 100% and 
        td's are 50%.  Do <strong>NOT</strong> use this for page layout, only data. See <a href="grids">grids</a> for page layout.
        <span class="label label-info">.three-column-table</span> is similar with three 33% columns.
    </p>
    <h4 class="subtle">Example:</h4>
    <table class="compact-results-table two-column-table">
        <thead><tr><th>header</th><th>header</th></tr></thead>
        <tfoot></tfoot>
        <tbody>
            <tr><td>cell</td><td>cell</td></tr>
            <tr><td>cell</td><td>cell</td></tr>
            <tr><td>cell</td><td>cell</td></tr>
        </tbody>
    </table>
    <table class="compact-results-table three-column-table">
        <thead><tr><th>header</th><th>header</th><th>header</th></tr></thead>
        <tfoot></tfoot>
        <tbody>
            <tr><td>cell</td><td>cell</td><td>cell</td></tr>
            <tr><td>cell</td><td>cell</td><td>cell</td></tr>
            <tr><td>cell</td><td>cell</td><td>cell</td></tr>
        </tbody>
    </table>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table two-column-table&quot;&gt;
    ...
&lt;/table&gt;
&lt;table class=&quot;compact-results-table three-column-table&quot;&gt;
    ...
&lt;/table&gt;
</pre>
</div>

<h3 class="subtle">Sorting</h3>
<div class="table-example clearfix stacked">
    <p class="description">
        Sorting can be done using the <span class="label label-info">SortableData</span> and 
        <span class="label label-info">SortableColumn</span> Java classes, the 
        <span class="label label-info">tags:sort</span> tag and the 
        <span class="label label-info">data-url</span> attribute. Click the headers in the table below.
    </p>
    <h4 class="subtle">Example:</h4>
    <div data-url="tables/sort-example">
        <%@ include file="sort-example.jsp" %>
    </div>
    <h4 class="subtle">Sort Request Mapping:</h4>
<pre class="code prettyprint">
@RequestMapping(&quot;/styleguide/tables/sort-example&quot;)
public String tables(ModelMap model, int sort, Direction dir) {
    
    List&lt;Population&gt; data = new ArrayList&lt;&gt;();
    data.add(new Population(&quot;Daluth&quot;, 86211));
    data.add(new Population(&quot;Minneapolis&quot;, 392880));
    data.add(new Population(&quot;St. Paul&quot;, 290770));
    
    Comparator&lt;Population&gt; comparator = compares.get(sort);
    
    if (dir == Direction.desc) {
        comparator = Collections.reverseOrder(comparator);
    }
    
    Collections.sort(data, comparator);
    
    SortableColumn c1 = new SortableColumn(dir, sort == 0 ? true : false, true, &quot;City&quot;);
    SortableColumn c2 = new SortableColumn(dir, sort == 1 ? true : false, true, &quot;Population&quot;);
    List&lt;SortableColumn&gt; columns = ImmutableList.of(c1, c2);
    
    SortableData pops = new SortableData(data, columns);
    model.addAttribute(&quot;pops&quot;, pops);
    
    return &quot;styleguide/sort-example.jsp&quot;;
}
</pre>
<h4 class="subtle">Page:</h4>
<pre class="code prettyprint">
&lt;div data-url=&quot;tables/sort-example&quot;&gt;
    &lt;%@ include file=&quot;sort-example.jsp&quot; %&gt;
&lt;/div&gt;
</pre>
<h4 class="subtle">sort-example.jsp:</h4>
<pre class="code prettyprint">
&lt;%@ page trimDirectiveWhitespaces=&quot;true&quot; %&gt;

&lt;%@ taglib prefix=&quot;c&quot; uri=&quot;http://java.sun.com/jsp/jstl/core&quot; %&gt;
&lt;%@ taglib prefix=&quot;tags&quot; tagdir=&quot;/WEB-INF/tags&quot; %&gt;

&lt;table class=&quot;compact-results-table&quot;&gt;
    &lt;thead&gt;
        &lt;tr&gt;
            &lt;c:forEach var=&quot;column&quot; items=&quot;&#36;{pops.columns}&quot;&gt;
                &lt;tags:sort column=&quot;&#36;{column}&quot;/&gt;
            &lt;/c:forEach&gt;
        &lt;/tr&gt;
    &lt;/thead&gt;
    &lt;tfoot&gt;&lt;/tfoot&gt;
    &lt;tbody&gt;
        &lt;c:forEach var=&quot;pop&quot; items=&quot;&#36;{pops.data}&quot;&gt;
            &lt;tr&gt;
                &lt;td&gt;&#36;{pop.city}&lt;/td&gt;
                &lt;td&gt;&#36;{pop.population}&lt;/td&gt;
            &lt;/tr&gt;
        &lt;/c:forEach&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
</pre>
</div>

<h3 class="subtle">Misc</h3>
<div class="table-example clearfix stacked">

    <p class="description">
        <span class="label label-info">.with-form-controls</span> is used on tables that will have buttons or text-fields
        or other similar form controls. Those components have a height of 26px so adding this class will change the 
        line-height to 26px which centers any text vertically to match the form control. Use
        <span class="label label-info">.form-control</span> to get the same behavior for non-table elements. Note that if 
        you are using <span class="label label-info">&lt;label&gt;</span> html elements they will vertically center text on what 
        they are labeling so this class is not necessary.  The easiest way to use 
        <span class="label label-info">&lt;label&gt;</span> is to wrap the text and the form control inside a 
        <span class="label label-info">&lt;label&gt;</span> element. 
    </p>
    <h4 class="subtle">Example:</h4>
    <tags:nameValueContainer2 tableClass="with-form-controls">
        <tags:nameValue2 nameKey=".name"><cti:button nameKey="edit" classes="M0"/></tags:nameValue2>
        <tags:nameValue2 nameKey=".type"><select><option>Option 1</option></select></tags:nameValue2>
    </tags:nameValueContainer2>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:nameValueContainer2 tableClass=&quot;with-form-controls&quot;&gt;
    &lt;tags:nameValue2 nameKey=&quot;.name&quot;&gt;&lt;cti:button nameKey=&quot;edit&quot; classes=&quot;M0&quot;/&gt;&lt;/tags:nameValue2&gt;
    &lt;tags:nameValue2 nameKey=&quot;.type&quot;&gt;&lt;select&gt;&lt;option&gt;Option 1&lt;/option&gt;&lt;/select&gt;&lt;/tags:nameValue2&gt;
&lt;/tags:nameValueContainer2&gt;
</pre>
    
    <hr>
    
    <p class="description">
        <span class="label label-info">.row-highlighting</span> will produce row highlighting when hovering. 
        Do <strong>NOT</strong> use this unless instructed to.  The built in row striping is usually enough to help users
        horizontally scan long rows.
    </p>
    <h4 class="subtle">Example:</h4>
    <table class="compact-results-table row-highlighting">
        <thead><tr><th>header</th><th>header</th><th>header</th></tr></thead>
        <tfoot></tfoot>
        <tbody>
            <tr><td>cell</td><td>cell</td><td>cell</td></tr>
            <tr><td>cell</td><td>cell</td><td>cell</td></tr>
            <tr><td>cell</td><td>cell</td><td>cell</td></tr>
        </tbody>
    </table>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table row-highlighting&quot;&gt;
    ...
&lt;/table&gt;
</pre>
    
    <hr>
    
    <p class="description">
        <span class="label label-info">.has-actions</span> is used in conjunction with 
        <a href="buttons#drop-down-example">drop downs</a>.  It will hide the drop down button until the row is hovered.  It is
        also used to register right-click events on the row to display the drop down menu.  Do <strong>NOT</strong> use 
        <span class="label label-info">.row-highlighting</span> with 
        <span class="label label-info">.has-actions</span> because it makes it very hard to notice that a drop down
        button appeared. 
    </p>
    <h4 class="subtle">Example:</h4>
    <table class="compact-results-table has-actions">
        <thead><tr><th>header</th><th>header</th></tr></thead>
        <tfoot></tfoot>
        <tbody>
            <tr>
                <td>cell</td>
                <td>cell
                    <cm:dropdown triggerClasses="fr">
                        <cm:dropdownOption icon="icon-pencil">Edit</cm:dropdownOption>
                        <cm:dropdownOption icon="icon-cross">Delete</cm:dropdownOption>
                    </cm:dropdown>
                </td>
            </tr>
            <tr>
                <td>cell</td>
                <td>cell
                    <cm:dropdown triggerClasses="fr">
                        <cm:dropdownOption icon="icon-pencil">Edit</cm:dropdownOption>
                        <cm:dropdownOption icon="icon-cross">Delete</cm:dropdownOption>
                    </cm:dropdown>
                </td>
            </tr>
        </tbody>
    </table>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table has-actions&quot;&gt;
    &lt;thead&gt;&lt;tr&gt;&lt;th&gt;header&lt;/th&gt;&lt;th&gt;header&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;
    &lt;tfoot&gt;&lt;/tfoot&gt;
    &lt;tbody&gt;
        &lt;tr&gt;
            &lt;td&gt;cell&lt;/td&gt;
            &lt;td&gt;cell
                &lt;cm:dropdown triggerClasses=&quot;fr&quot;&gt;
                    &lt;cm:dropdownOption icon=&quot;icon-pencil&quot;&gt;Edit&lt;/cm:dropdownOption&gt;
                    &lt;cm:dropdownOption icon=&quot;icon-cross&quot;&gt;Delete&lt;/cm:dropdownOption&gt;
                &lt;/cm:dropdown&gt;
            &lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;cell&lt;/td&gt;
            &lt;td&gt;cell
                &lt;cm:dropdown triggerClasses=&quot;fr&quot;&gt;
                    &lt;cm:dropdownOption icon=&quot;icon-pencil&quot;&gt;Edit&lt;/cm:dropdownOption&gt;
                    &lt;cm:dropdownOption icon=&quot;icon-cross&quot;&gt;Delete&lt;/cm:dropdownOption&gt;
                &lt;/cm:dropdown&gt;
            &lt;/td&gt;
        &lt;/tr&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
</pre>

    <hr>
    
    <p class="description">
        <span class="label label-info">.has-alerts</span> is used when each row in a table is expected to have the first
        column be icon describing the row.  It will ensure the first column only takes up enough space to show the icon. See
        <a href="icons">icons</a> to learn more about using icons.
    </p>
    <h4 class="subtle">Example:</h4>
    <table class="compact-results-table has-alerts">
        <thead><tr><th colspan="2">Issues</th></tr></thead>
        <tfoot></tfoot>
        <tbody>
            <tr>
                <td><cti:icon icon="icon-error"/></td>
                <td>High kVAr measured at substation Winthrop.</td>
            </tr>
            <tr>
                <td><cti:icon icon="icon-exclamation"/></td>
                <td>High Voltage measured at substation Alberta.</td>
            </tr>
        </tbody>
    </table>
    <h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;table class=&quot;compact-results-table has-alerts&quot;&gt;
    &lt;thead&gt;&lt;tr&gt;&lt;th colspan=&quot;2&quot;&gt;Issues&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;
    &lt;tfoot&gt;&lt;/tfoot&gt;
    &lt;tbody&gt;
        &lt;tr&gt;
            &lt;td&gt;&lt;cti:icon icon=&quot;icon-error&quot;/&gt;&lt;/td&gt;
            &lt;td&gt;High kVAr measured at substation Winthrop.&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;&lt;cti:icon icon=&quot;icon-exclamation&quot;/&gt;&lt;/td&gt;
            &lt;td&gt;High Voltage measured at substation Alberta.&lt;/td&gt;
        &lt;/tr&gt;
    &lt;/tbody&gt;
&lt;/table&gt;
</pre>
</div>

</tags:styleguide>
</cti:standardPage>