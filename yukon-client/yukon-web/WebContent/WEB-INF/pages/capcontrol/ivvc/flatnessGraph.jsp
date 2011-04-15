<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<settings>
	<font>Arial</font>
	<reload_data_interval>0</reload_data_interval>
	<decimals_separator>.</decimals_separator>
    <redraw>true</redraw>
	
	<plot_area>
		<border_color>#e6e6e6</border_color>
		<border_alpha>100</border_alpha>
		<margins>
			<left>50</left>
			<top>40</top>
			<right>15</right>
			<bottom>30</bottom>
		</margins>
	</plot_area>
	
	<grid>
		<x>
			<alpha>40</alpha>
			<approx_count>10</approx_count>
		</x>
		<y_left>
			<alpha>40</alpha>
			<approx_count>10</approx_count>
		</y_left>
	</grid>
	
	<values>
		<x>
			<frequency>1</frequency>
		</x>
		<y>
			<min>${graphSettings.YLowerBound-5}</min>
			<max>${graphSettings.YUpperBound+5}</max>
		</y>
	</values>
	
	<axes>
		<x>
			<color>#e6e6e6</color>
			<width>1</width>
		</x>
		<y>
			<color>#e6e6e6</color>
			<width>1</width>
		</y>
	</axes>
	
	<labels>
		<label>
      		<x>0</x>
      		<y>2%</y>
			<align>center</align>  
      		<text>
        		<![CDATA[<b>${graphSettings.graphTitle}</b>]]>
      		</text>
    	</label>
    	<label>
      		<x>0</x>
      		<y>50%</y>
      		<rotate>90</rotate>
			<align>left</align>  
      		<text>
        		<![CDATA[<b>${graphSettings.YAxisLabel}</b>]]>
      		</text>        
    	</label>
    	<label>
      		<x>0</x>
      		<y>89%</y>
			<align>center</align>  
      		<text>
        		<![CDATA[<b>${graphSettings.XAxisLabel}</b>]]>
      		</text>
    	</label>
  	</labels>
  	
  	<bullets>
  		<hover_brightness>-25</hover_brightness>
  		<grow_time>0</grow_time>
  		<sequenced_grow>true</sequenced_grow>
  	</bullets>
  	
  	<balloon>
  		<color>#FFCC00</color>
  		<alpha>45</alpha>
  		<text_color>#000000</text_color>
  	</balloon>
  	
  	<legend>
  		<enabled>false</enabled>
  	</legend>

	<data>
		<chart>
			<graphs>
				<c:forEach var="line" items="${graph.lines}">
					<graph gid="${line.id}" title="${line.zoneName}"
						bullet="${line.settings.bullet}"
						fill_alpha="${line.settings.fillAlpha}"
						min_max="${line.settings.minMax}"
						color="${line.settings.color}"
						bullet_size="15"
						bullet_max_size="25"
						balloon_text="{description}"
						visible_in_legend="${line.settings.visibleInLegend}">
						
						<c:forEach var="point" items="${line.points}">
							<point x="${point.x}" y="${point.y}" value="${point.y}"><![CDATA[${point.description}]]></point>
						</c:forEach>
					</graph>
				</c:forEach>
			</graphs>
		</chart>
	</data>
</settings>