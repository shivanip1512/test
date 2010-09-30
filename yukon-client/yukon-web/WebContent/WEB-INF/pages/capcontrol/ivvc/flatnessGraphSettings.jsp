<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<settings>
	<font>Tahoma</font>
	<mask>false</mask>
	<plot_area>
		<border_color>e6e6e6</border_color>
		<border_alpha>100</border_alpha>
		<margins>
			<left>40</left>
			<top>10</top>
			<right>10</right>
			<bottom/>
		</margins>
	</plot_area>
	<grid>
		<x>
			<approx_count>20</approx_count>
			<alpha>1</alpha>
		</x>

		<y>
			<approx_count>20</approx_count>
			<fill_alpha>1</fill_alpha>
			<fill_color>000000</fill_color>
			<alpha>1</alpha>
		</y>
	</grid>

	<values>
		<x>
			<rotate>90</rotate>
		</x>
		<y>
		</y>
	</values>
	
	<axes>
		<x>
			<color>e6e6e6</color>
			<width>1</width>
		</x>
		<y>
			<color>e6e6e6</color>
			<width>1</width>
		</y>
	</axes>

	<legend>
	<enabled>false</enabled>
	</legend>
	<graphs>
		<c:forEach var="line" items="${graph.lines}">
			<graph gid="${line.id}">
				<color>${line.color}</color>
				<balloon_text>{y}</balloon_text>
				<bullet>round_outlined</bullet>
				<fill_alpha>${line.fillAlpha}</fill_alpha>
				<min_max>${line.minMax}</min_max>
			</graph>
		</c:forEach>
		<graph gid="-10">
			<min_max>true</min_max>
		</graph>
		<graph gid="-11">
			<min_max>true</min_max>
		</graph>
	</graphs>
</settings>