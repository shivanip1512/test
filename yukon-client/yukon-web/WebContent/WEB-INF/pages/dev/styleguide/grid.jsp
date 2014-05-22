<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="grid">
<tags:styleguide page="grid">

<style type="text/css">
.grid-example .column {padding: 10px;background-color: #efefef;min-height: 89px;}
.grid-example .column .label {margin: 2px 0;display: inline-block;line-height: 14px;}
</style>

<p>
The grid system in Yukon uses a 24 column grid. The column widths and gutters are percent based. That means grids can be nested.
Grids are made with a container element and child elements.  The container element uses one of the column classes 
that defines the number and width of the columns, and a clearfix.
For example, a grid with two columns of equal width would be <span class="label label-default">.column-12-12</span>
The child elements are the columns, they use the <span class="label label-default">.column</span> class and column number class 
<span class="label label-default">.one</span>, <span class="label label-default">.two</span>, <span class="label label-default">three</span> etc.
The last column must always specify the <span class="label label-default">.nogutter</span> class.
</p>
<h5>A grid with one row, two columns, each being 12 columns wide:</h5>
<pre class="code prettyprint">
&lt;div class=&quot;column-12-12 clearfix&quot;&gt;
    &lt;div class=&quot;column one&quot;&gt;&lt;/div&gt;
    &lt;div class=&quot;column two nogutter&quot;&gt;&lt;/div&gt;
&lt;/div&gt;
</pre>
<div class="separated-sections">

    <div class="section">
        <span class="label label-default">.column-4-20</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-4-20 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-6-18</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-6-18 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-7-17</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-7-17 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-8-16</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-8-16 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-10-14</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-10-14 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-12-12</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-12-12 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-14-10</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-14-10 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-16-8</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-16-8 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-18-6</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-18-6 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>

    <div class="section">
        <span class="label label-default">.column-21-3</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-21-3 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-3-18-3</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-3-18-3 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-4-8-12</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-4-8-12 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-4-10-10</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-4-10-10 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-4-12-8</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-4-12-8 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-4-14-6</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-4-14-6 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-6-6-12</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-6-6-12 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-6-8-10</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-6-8-10 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-6-10-8</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-6-10-8 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-6-12-6</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-6-12-6 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-6-15-3</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-6-15-3 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-8-8-8</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-8-8-8 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-8-10-6</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-8-10-6 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-8-13-3</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-8-13-3 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-10-7-7</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-10-7-7 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-10-8-6</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-10-8-6 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-10-11-3</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-10-11-3 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-12-6-6</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-12-6-6 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-12-9-3</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-12-9-3 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
    <div class="section">
        <span class="label label-default">.column-6-6-6-6</span>&nbsp;
        <span class="label label-default">.clearfix</span>
        <div class="column-6-6-6-6 clearfix buffered grid-example">
            <div class="column one">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.one</span>
            </div>
            <div class="column two">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.two</span>
            </div>
            <div class="column three">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.three</span>&nbsp;
            </div>
            <div class="column four nogutter">
                <span class="label label-default">.column</span>&nbsp;
                <span class="label label-default">.four</span>&nbsp;
                <span class="label label-default">.nogutter</span>
            </div>
        </div>
    </div>
    
</div>
</tags:styleguide>
</cti:standardPage>