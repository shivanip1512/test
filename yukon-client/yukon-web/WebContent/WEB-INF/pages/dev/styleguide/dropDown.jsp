<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="dropDown">
    <tags:styleguide page="drop-down">

<style>     
    .description {
        line-height: 22px;
    } 
</style>

    <p class="description">
     Dropdowns are components built by the 
        <span class="label label-attr">&lt;tags:selectWithItems&gt;</span> tag that allow for easy selection of items from the list of limited options. In Yukon there are two types of Dropdowns:-<br>1.Single Select<br>2.Multi Select
    </p>

    <h2>Dropdown with Sample Data (Single Select)</h2>
    
    <p class="description">
        The following attributes are mandatory to use this tag and example illustrates how a single select dropdown looks like and able to select only one value at a time.
    </p>
    
    <h2>Attributes</h2>
    <tags:nameValueContainer>
        <tags:nameValue name="items">
            The 'item' of the selectWithItems. <span class="label label-attr">selectWithItems</span> tag should be used to pass the values which are shown in the dropdown.
        </tags:nameValue>
        <tags:nameValue name="path">
            The 'path' value passed to the <span class="label label-attr">&lt;form:form&gt;</span> tag which builds an input
            with a special name.
        </tags:nameValue>
    </tags:nameValueContainer>
    
    <h1 class="dib stacked">Examples</h1>
    
    <div class="column-4-20 clearfix style-guide-example">
        <div class="column one"><h4 class="subtle">Example:</h4></div>
        <div class="column two nogutter">
            <tags:selectWithItems items="${operationalStates}" id="dropDownExample" path="operationalStates"/>
        </div>
    </div>
    <h4 class="subtle">Code:</h4>
        <pre class="code prettyprint">
&lt;tags:selectWithItems items=&quot;list&quot; path=&quot;dropDownObj&quot;&gt;
</pre>

    <h2>Dropdown with Search functionality (Search bar within)</h2>
    
    <p class="description">
        To make single select dropdown into searchable dropdown ".chosen()" function is being called upon the ID/Class.
        The chosen() function is part of a jQuery plugin developed by Harvest.
        For more information on the used plugin please visit <a href="https://harvesthq.github.io/chosen/">https://harvesthq.github.io/chosen/</a>
    </p>
    
    <h1 class="dib stacked">Examples</h1>
    
    <div class="column-4-20 clearfix style-guide-example">
        <div class="column one"><h4 class="subtle">Example:</h4></div>
        <div class="column two nogutter">
            <select name="cars" id="cars">
                <option value="saab">Saab</option>
                <option value="volvo">Volvo</option>
                <option value="mercedes">Mercedes</option>
                <option value="audi">Audi</option>
                <option value="bmw">BMW</option>
                <option value="ford">Ford</option>
                <option value="rangeRover">Range Rover</option>
                <option value="kia">KIA</option>
            </select>
        </div>
    </div>
    <h4 class="subtle">HTML Code:</h4>
    <pre class="code prettyprint">
&lt;select name=&quot;cars&quot; id=&quot;cars&quot;&gt;
  &lt;option value=&quot;saab&quot;&gt;Saab&lt;option&gt;
  &lt;option value=&quot;volvo&quot;&gt;Volvo&lt;option&gt;
  &lt;option value=&quot;mercedes&quot;&quot;&gt;Mercedes&lt;option&gt;
  &lt;option value=&quot;audi&quot;&gt;Audi&lt;option&gt;
  &lt;option value=&quot;bmw&quot;&gt;BMW&lt;option&gt;
  &lt;option value=&quot;rangeRover&quot;&gt;Ford&lt;option&gt;
  &lt;option value=&quot;kia&quot; id=&quot;cars&quot;&gt;KIA&lt;option&gt;
&lt;/select&gt;
</pre>

    <h4 class="subtle">JavaScript Code:</h4>
        <pre class="code prettyprint">
$ (&quot;#cars&quot;).chosen();
</pre>

    <h2>Dropdown with Search functionality and multiple select</h2>
    
    <p class="description">
        To make single select dropdown to multi select dropdown "multiple" attribute is used in select tag itself.
        The selected values got disabled during next selection. You can remove the already selected values using cross icons.
    </p>
    
    <h1 class="dib stacked">Examples</h1>
    
    <div class="column-4-20 clearfix style-guide-example">
        <div class="column one"><h4 class="subtle">Example:</h4></div>
        <div class="column two nogutter">
            <select name="city" id="citys" multiple >
                <option value="paris">Paris</option>
                <option value="newYork">New York</option>
                <option value="london">London</option>
                <option value="bangkok">Bangkok</option>
                <option value="hongKong">Hong Kong</option>
                <option value="dubai">Dubai</option>
                <option value="singapore">Singapore</option>
                <option value="rome">Rome</option>
            </select>
        </div>
    </div>
    <h4 class="subtle">HTML Code:</h4>
    <pre class="code prettyprint">
&lt;select name=&quot;citys&quot; id=&quot;citys&quot; multiple&gt;
  &lt;option value=&quot;newYork&quot;&gt;New York&lt;option&gt;
  &lt;option value=&quot;london&quot;&gt;London&lt;option&gt;
  &lt;option value=&quot;bangkok&quot;&gt;Bangkok&lt;option&gt;
  &lt;option value=&quot;hongKong&quot;&gt;Hong Kong&lt;option&gt;
  &lt;option value=&quot;dubai&quot;&gt;Dubai&lt;option&gt;
  &lt;option value=&quot;singapore&quot;&gt;Singapore&lt;option&gt;
  &lt;option value=&quot;rome&quot;&gt;Rome&lt;option&gt;
  &lt;option value=&quot;paris&quot;&gt;Paris&lt;option&gt;
&lt;/select&gt;
</pre>

    <h4 class="subtle">JavaScript Code:</h4>
        <pre class="code prettyprint">
$ (&quot;#cars&quot;).chosen();
</pre>
        </tags:styleguide>
        <script>
        $("#cars").chosen();
        $("#citys").chosen({'width': '450px'});
        </script>
</cti:standardPage>