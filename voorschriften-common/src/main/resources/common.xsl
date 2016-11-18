<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:tw="http://www.denhaag.nl/tw/extensions"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:template match="xsd:attribute | xsd:attributeGroup | xsd:group" mode="notallowedglobal">
		<li>&lt;<xsl:value-of select="local-name(.)" />&gt;<xsl:value-of select="./@name" /> (<xsl:value-of select="./@type" />)
			<table class="unknown"><xsl:apply-templates select="child::node()" mode="local"/>
			</table>
		</li>
	</xsl:template>
	<xsl:template match="*" mode="notallowedlocal">
		<li>&lt;<xsl:value-of select="local-name(.)" />&gt;<xsl:value-of select="./@name" /> (<xsl:value-of select="./@type" />)
			<ul><xsl:apply-templates select="child::node()" mode="notallowedlocal"/></ul>
		</li>
	</xsl:template>
	<xsl:template match="xsd:import" mode="global">
		<li><xsl:value-of select="./@namespace" /><xsl:text>: </xsl:text><span class="location"><xsl:value-of select="./@schemaLocation" /></span>		
		</li>
	</xsl:template>
	<xsl:template match="xsd:include" mode="global">
		<li><span class="location"><xsl:value-of select="./@schemaLocation" /></span></li>
	</xsl:template>
	<xsl:template match="xsd:redefine" mode="global">
		<li><span class="location"><xsl:value-of select="./@schemaLocation" /></span></li>
	</xsl:template>
	<xsl:template match="xsd:simpleType" mode="global">
			<tr><th colspan="4" class="left"><xsl:call-template name="upperCaseCheck"><xsl:with-param name="name"><xsl:value-of select="./@name" /></xsl:with-param></xsl:call-template><xsl:text> </xsl:text><xsl:value-of select="./@name" /></th>
			<xsl:apply-templates select="child::node()" mode="insideSimpleType"/>
			</tr>		
	</xsl:template>
	<xsl:template match="xsd:simpleType" mode="local">
		<table class="simpleType">
			<tr><th colspan="4"><xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>Bij voorkeur geen gebruik van local types.</xsl:text></xsl:with-param></xsl:call-template>Local Simple Type</th>
			<xsl:apply-templates select="child::node()" mode="insideSimpleType"/>
			</tr>
		</table>	
	</xsl:template>
	<xsl:template match="xsd:restriction" mode="insideSimpleType">
		<td><xsl:call-template name="passed"/><xsl:value-of select="local-name(.)" /><xsl:text>: </xsl:text> <xsl:value-of select="./@base" /></td>
	</xsl:template>
	<xsl:template match="*" mode="insideSimpleType">
		<td><xsl:call-template name="lowpriority"/><xsl:value-of select="local-name(.)" /></td>
	</xsl:template>
	<xsl:template match="xsd:complexType" mode="global">
		<li>
		<table class="complexType">
			<tr><th colspan="4"><xsl:call-template name="upperCaseCheck"><xsl:with-param name="name"><xsl:value-of select="./@name" /></xsl:with-param></xsl:call-template>
			<xsl:if test="./@mixed='true'"><xsl:call-template name="highpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Het gebruik van mixed content is niet toegestaan.</xsl:text></xsl:with-param></xsl:call-template></xsl:if>
			<xsl:text> </xsl:text><xsl:value-of select="./@name" /></th>
			</tr>
			<xsl:apply-templates select="child::node()" mode="insideComplexType"/>
		</table>
		</li>			
	</xsl:template>

	<xsl:template match="xsd:complexType" mode="local">
		<table class="complexType">
			<tr><th colspan="4"><xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Maak geen gebruik van lokale complexe types.</xsl:text></xsl:with-param></xsl:call-template>
			<xsl:if test="./@mixed='true'"><xsl:call-template name="highpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Het gebruik van mixed content is niet toegestaan.</xsl:text></xsl:with-param></xsl:call-template></xsl:if>
			Local Complex Type</th>
			</tr>
			<xsl:apply-templates select="child::node()" mode="insideComplexType"/>
			
		</table>
	</xsl:template>
	<xsl:template match="xsd:sequence" mode="insideComplexType">
		<tr><th colspan="4" class="subHeader"><xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>In een complexType mag alleen gebruik gemaakt worden van een sequence van elementen.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="local-name(.)" /></th></tr>
		<xsl:apply-templates select="child::node()" mode="local"/>
	</xsl:template>
	<xsl:template match="xsd:complexContent" mode="insideComplexType">
		<xsl:apply-templates select="child::node()" mode="insideComplexType"/>
	</xsl:template>
	<xsl:template match="xsd:simpleContent" mode="insideComplexType">
		<xsl:apply-templates select="child::node()" mode="insideComplexType"/>
	</xsl:template>
	<xsl:template match="xsd:extension" mode="insideComplexType">
		<tr><th colspan="4" class="subHeader"><xsl:text>extends: </xsl:text><xsl:value-of select="./@base" /></th></tr>
		<xsl:apply-templates select="child::node()" mode="insideComplexType"/>
	</xsl:template>
	<xsl:template match="xsd:restriction" mode="insideComplexType">
		<tr><th colspan="4" class="subHeader"><xsl:call-template name="badsupport"/><xsl:text>restrict: </xsl:text><xsl:value-of select="./@base" /></th></tr>
		<xsl:apply-templates select="child::node()" mode="insideComplexType"/>
	</xsl:template>
	<xsl:template match="*" mode="insideComplexType">
		<tr><th colspan="4" class="subHeader"><xsl:call-template name="badsupport"/><xsl:value-of select="local-name(.)" /></th></tr>
		<xsl:apply-templates select="child::node()" mode="insideComplexType"/>
	</xsl:template>
	<xsl:template match="*"  mode="local">
		<tr><td colspan="4"><xsl:call-template name="badsupport"/><xsl:value-of select="local-name(.)" /></td></tr>
		<xsl:apply-templates select="child::node()" mode="local"/>
	</xsl:template>
	<xsl:template match="xsd:attribute | xsd:anyAttribute" mode="insideComplexType">
		<tr><th colspan="4" class="subHeader"><xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Het gebruik van attributen is niet toegestaan.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="local-name(.)" /></th></tr>
	</xsl:template>
	<xsl:template match="xsd:attribute | xsd:anyAttribute"  mode="local">
		<tr><td colspan="4"><xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Het gebruik van attributen is niet toegestaan.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="local-name(.)" /></td></tr>
	</xsl:template>
	<xsl:template match="xsd:any" mode="insideComplexType">
		<tr><th colspan="4" class="subHeader"><xsl:call-template name="highpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Alle mogelijkheden van XML Schema om gegevens te beschrijven dienen benut te worden.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="local-name(.)" /></th></tr>
	</xsl:template>
	<xsl:template match="xsd:any"  mode="local">
		<tr><td colspan="4"><xsl:call-template name="highpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>Alle mogelijkheden van XML Schema om gegevens te beschrijven dienen benut te worden.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="local-name(.)" /></td></tr>
	</xsl:template>
	<xsl:template match="xsd:element"  mode="local">
		<tr>
			<td><xsl:value-of select="./@name" /></td>
			<td >
				<xsl:choose>
					<xsl:when test="./@minOccurs or ./@maxOccurs">
						<xsl:text>[</xsl:text>
						<xsl:choose>
							<xsl:when test="./@minOccurs"><xsl:value-of select="./@minOccurs" /></xsl:when>
							<xsl:otherwise><xsl:text>1</xsl:text></xsl:otherwise>
						</xsl:choose>
						<xsl:text>..</xsl:text>
						<xsl:choose>
							<xsl:when test="fn:matches(./@maxOccurs, 'unbounded')"><xsl:text>*</xsl:text></xsl:when>
							<xsl:when test="./@maxOccurs"><xsl:value-of select="./@maxOccurs" /></xsl:when>
							<xsl:otherwise><xsl:text>1</xsl:text></xsl:otherwise>
						</xsl:choose>
						<xsl:text>]</xsl:text>
					</xsl:when>
					<xsl:otherwise><xsl:text>1</xsl:text></xsl:otherwise>
				</xsl:choose>
				<xsl:if test="./@nillable='true'">
					<xsl:call-template name="nillable"/>					
				</xsl:if>				
			</td>
			<td><xsl:variable name="lowerCamelCase" select="fn:concat(fn:lower-case(fn:substring(./@name,1,1)), fn:substring(./@name,2))" />
				<xsl:choose>
				<xsl:when test="$lowerCamelCase = ./@name">
					<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam een element moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!--   xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam een element moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template-->
					<xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam een element moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:otherwise>
			</xsl:choose></td>
			<td>
				<xsl:choose>
				<xsl:when test="./@type">
					<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>Bij voorkeur geen gebruik van anonieme types.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="./@type" />
				</xsl:when>
				<xsl:when test="./@ref">
					<xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>Bij voorkeur geen gebruik van anonieme types.</xsl:text></xsl:with-param></xsl:call-template><xsl:text>ref: </xsl:text><xsl:value-of select="./@ref" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="child::node()" mode="local"/>			
				</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>

	</xsl:template>
	<xsl:template match="xsd:element"  mode="global">
		<tr><td>
			<xsl:variable name="lowerCamelCase" select="fn:concat(fn:lower-case(fn:substring(./@name,1,1)), fn:substring(./@name,2))" />
				<xsl:choose>
				<xsl:when test="$lowerCamelCase = ./@name">
					<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam een element moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam een element moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template-->
					<xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam een element moet beginnen met een kleine letter en is lowerCamelCase.</xsl:text></xsl:with-param></xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="./@nillable='true'">
					<xsl:call-template name="nillable"/>					
				</xsl:when>
			</xsl:choose></td>
				<xsl:choose>
				<xsl:when test="./@type">
					<td><xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>Bij voorkeur geen gebruik van anonieme types.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="./@name" /></td><td><xsl:value-of select="./@type" /></td>
				</xsl:when>
				<xsl:when test="./@ref">
					<td><xsl:call-template name="warningWithParams"><xsl:with-param name="specificMessage"><xsl:text>Bij voorkeur geen gebruik van anonieme types.</xsl:text></xsl:with-param></xsl:call-template><xsl:value-of select="./@name" /></td><td><xsl:text> ref: </xsl:text><xsl:value-of select="./@ref" /></td>
				</xsl:when>
				<xsl:otherwise>
					<td><xsl:value-of select="./@name" /></td><td><xsl:apply-templates select="child::node()" mode="local"/></td>			
				</xsl:otherwise>
				</xsl:choose>
				
	</tr>

	</xsl:template>
	<xsl:template name="upperCaseCheck">
		<xsl:param name="name"></xsl:param>
		<xsl:variable name="UpperCamelCase" select="fn:concat(fn:upper-case(fn:substring($name,1,1)), fn:substring($name,2))" />
		<xsl:choose>
			<xsl:when test="$UpperCamelCase = $name">
					<xsl:call-template name="passedWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam van een type definitie (bijv. SimpleType en ComplexType) moeten beginnen met een hoofdletter en is UpperCamelCase.</xsl:text></xsl:with-param></xsl:call-template>		
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="lowpriorityWithParams"><xsl:with-param name="specificMessage"><xsl:text>De naam van een type definitie (bijv. SimpleType en ComplexType) moeten beginnen met een hoofdletter en is UpperCamelCase.</xsl:text></xsl:with-param></xsl:call-template>
			</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	<xsl:template match="xsd:annotation" mode="#all">

	</xsl:template>	
	<xsl:template name="nillable">
		<span title="nillable=true"><xsl:text>(n)</xsl:text></span>
	</xsl:template>	
	<xsl:template name="highpriority">
		<xsl:choose>
			<xsl:when test="tw:excludedNamespace(/node()/@targetNamespace)">
				<xsl:call-template name="highpriority-ignored"/>
			</xsl:when>
			<xsl:otherwise>
				<img src="{$basedir}images/highpriority.png"  alt="Gefaald (hogere prioriteit)" title="Voldoet NIET aan de voorschriften, het heeft hoge prioriteit om dit op te lossen."/>
				<xsl:value-of select="tw:message('highpriority')"/>			
			</xsl:otherwise>	
		</xsl:choose>
		
	</xsl:template>
	<xsl:template name="highpriorityWithParams">
		<xsl:param name="specificMessage"></xsl:param>
		<xsl:choose>
			<xsl:when test="tw:excludedNamespace(/node()/@targetNamespace)">
				<xsl:call-template name="highpriority-ignored"/>
			</xsl:when>
			<xsl:otherwise>
				<img src="{$basedir}images/highpriority.png"  alt="Gefaald (hogere prioriteit)" title="Voldoet NIET aan de voorschriften, het heeft hoge prioriteit om dit op te lossen.: {$specificMessage}"/>
				<xsl:value-of select="tw:message('highpriority')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="lowpriority">
		<xsl:choose>
			<xsl:when test="tw:excludedNamespace(/node()/@targetNamespace)">
				<xsl:call-template name="lowpriority-ignored"/>
			</xsl:when>
			<xsl:otherwise>
				<img src="{$basedir}images/lowpriority.png"  alt="Gefaald" title="Voldoet NIET aan de voorschriften."/>
				<xsl:value-of select="tw:message('lowpriority')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="lowpriorityWithParams">
		<xsl:param name="specificMessage"></xsl:param>
		<xsl:choose>
			<xsl:when test="tw:excludedNamespace(/node()/@targetNamespace)">
				<xsl:call-template name="lowpriority-ignored"/>
			</xsl:when>
			<xsl:otherwise>
				<img src="{$basedir}images/lowpriority.png"  alt="Gefaald" title="Voldoet NIET aan de voorschriften: {$specificMessage}"/>
				<xsl:value-of select="tw:message('lowpriority')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="warning">
		<xsl:choose>
			<xsl:when test="tw:excludedNamespace(/node()/@targetNamespace)">
				<xsl:call-template name="warning-ignored"/>
			</xsl:when>
			<xsl:otherwise>
				<img src="{$basedir}images/warning.png"  alt="Waarschuwing" title="Voldoet aan de voorschriften, maar de voorschriften ontraden dit."/>
				<xsl:value-of select="tw:message('warning')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="warningWithParams">
		<xsl:param name="specificMessage"></xsl:param>
		<xsl:choose>
			<xsl:when test="tw:excludedNamespace(/node()/@targetNamespace)">
				<xsl:call-template name="warning-ignored"/>
			</xsl:when>
			<xsl:otherwise>
				<img src="{$basedir}images/warning.png"  alt="Waarschuwing" title="Voldoet aan de voorschriften, maar de voorschriften ontraden dit: {$specificMessage}"/>
				<xsl:value-of select="tw:message('warning')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="passed">
		<img src="{$basedir}images/ok.png" alt="OK" title="Voldoet aan de voorschriften"/>
		<xsl:value-of select="tw:message('passed')"/>
	</xsl:template>
	<xsl:template name="passedWithParams">
		<xsl:param name="specificMessage"></xsl:param>
		<img src="{$basedir}images/ok.png" alt="OK" title="Voldoet aan de voorschriften: {$specificMessage}"/>
		<xsl:value-of select="tw:message('passed')"/>
	</xsl:template>
	<xsl:template name="badsupport">	
		<xsl:choose>
			<xsl:when test="tw:excludedNamespace(/node()/@targetNamespace)">
				<xsl:call-template name="lowpriority-ignored"/>
			</xsl:when>
			<xsl:otherwise>
				<img src="{$basedir}images/lowpriority.png" alt="Slechte ondersteuning tools" title="Slechte ondersteuning tools. Gebruik meer gangbare elementen."/>			
				<xsl:value-of select="tw:message('lowpriority')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="highpriority-ignored">
		<img src="{$basedir}images/highpriority-ignored.png" alt="IGNORED" title="Genegeerd"/>
		<xsl:value-of select="tw:message('highpriority-ignored')"/>
	</xsl:template>		
	<xsl:template name="lowpriority-ignored">
		<img src="{$basedir}images/lowpriority-ignored.png" alt="IGNORED" title="Genegeerd"/>
		<xsl:value-of select="tw:message('lowpriority-ignored')"/>
	</xsl:template>	
	<xsl:template name="warning-ignored">
		<img src="{$basedir}images/warning-ignored.png" alt="IGNORED" title="Genegeerd"/>
		<xsl:value-of select="tw:message('warning-ignored')"/>
	</xsl:template>			
	<xsl:template name="ignored">
		<img src="{$basedir}images/ignored.png" alt="IGNORED" title="Genegeerd"/>
		<xsl:value-of select="tw:message('passed')"/>
	</xsl:template>	
</xsl:stylesheet>