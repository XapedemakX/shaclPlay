<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- controls output style -->
	<xsl:output indent="yes" method="xml" />

	<!-- Language parameter to the XSLT -->
	<xsl:param name="LANG">
		en
	</xsl:param>

	<!-- french labels -->
	<xsl:variable name="LABELS_FR">
		<labels>
			<entry key="TOC" label="Table des Matières" />
			<entry key="COLUMN_PROPERTY" label="Nom de la propriété" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Valeur attendue" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_DESCRIPTION" label="Description" />
		</labels>
	</xsl:variable>

	<!-- English labels -->
	<xsl:variable name="LABELS_EN">
		<labels>
			<entry key="TOC" label="Table of Content" />
			<entry key="COLUMN_PROPERTY" label="Property name" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Expected value" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_DESCRIPTION" label="Description" />
		</labels>
	</xsl:variable>

	<!-- french labels prefix -->
	<xsl:variable name="LABELS_FR_prefix">
		<labels>
			<entry key="TOC_PREFIX" label="Table des Prefix" />
			<entry key="COLUMN_PREFIX" label="Namespace" />
			<entry key="COLUMN_URI" label="URI" />
		</labels>
	</xsl:variable>
	<!-- English labels prefix -->
	<xsl:variable name="LABELS_EN_prefix">
		<labels>
			<entry key="TOC_PREFIX" label="Table of Prefix" />
			<entry key="COLUMN_PREFIX" label="Namespace" />
			<entry key="COLUMN_URI" label="URI" />
		</labels>
	</xsl:variable>

	<!-- Liste de description -->
	<xsl:variable name="LABELS_EN_Description">
		<labels>
			<entry key="COMMENTS" label="Introduction:" />
			<entry key="DATE" label="Update Date:" />
			<entry key="VERSION" label="Version:" />
		</labels>
	</xsl:variable>

	<xsl:variable name="LABELS_FR_Description">
		<labels>
			<entry key="COMMENTS" label="Présentation:" />
			<entry key="DATE" label="Date de dernière modification " />
			<entry key="VERSION" label="Numèro de Version" />
		</labels>
	</xsl:variable>

	<!-- Diagramme -->
	<xsl:variable name="LABELS_FR_Digramme">
		<labels>
			<entry key="DIAGRAMME" label="Le Diagramme:" />
		</labels>
	</xsl:variable>

	<xsl:variable name="LABELS_EN_Digramme">
		<labels>
			<entry key="DIAGRAMME" label="Shape Diagram" />
		</labels>
	</xsl:variable>

	<!-- Variables sin Target -->
	<xsl:variable name="LABELS_FR_sans_properties">
		<labels>
			<entry key="LABEL_NODEKIND" label="Le node sont: " />
			<entry key="LABEL_PATTERNS" label="L'URI pattern: " />
			<entry key="LABEL_CLOSE" label="Fermé " />
		</labels>
	</xsl:variable>
	<xsl:variable name="LABELS_EN_sans_properties">
		<labels>
			<entry key="LABEL_NODEKIND" label="Nodes are: " />
			<entry key="LABEL_PATTERNS" label="URI pattern: " />
			<entry key="LABEL_CLOSE" label="closed " />
		</labels>
	</xsl:variable>


	<!-- Select labels based on language param -->
	<xsl:variable name="LABELS_prefix"
		select="if($LANG = 'fr') then $LABELS_FR_prefix else $LABELS_EN_prefix" />
	<xsl:variable name="LABELS"
		select="if($LANG = 'fr') then $LABELS_FR else $LABELS_EN" />
	<xsl:variable name="LABELS_description"
		select="if($LANG = 'fr') then $LABELS_FR_Description else $LABELS_EN_Description" />
	<xsl:variable name="LABELS_Diagramme"
		select="if($LANG = 'fr') then $LABELS_FR_Digramme else $LABELS_EN_Digramme" />
	<xsl:variable name="LABELS_Properties"
		select="if($LANG = 'fr') then $LABELS_FR_sans_properties else $LABELS_EN_sans_properties" />


	<!-- Principal -->

	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ShapesDocumentation">
		<html lang="en">
			<head>
				<link rel="stylesheet"
					href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css"
					integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l"
					crossorigin="anonymous" />
				<style type="text/css">
					hr { display: block;
					unicode-bidi: isolate;
					margin-block-start: 0.9em;
					margin-block-end: 0.9em;
					margin-inline-start: auto;
					margin-inline-end: auto;
					border-style:
					inset;
					border-width: 1px;}

				</style>
			</head>
			<body>
				<div class="container-md">
					<br />
					<h1>
						<center>
							<xsl:value-of select="title" />
						</center>
					</h1>
					<br />
					<xsl:if test="VersionOntology != null">
						<xsl:value-of
							select="$LABELS_description/labels/entry[@key='DATE']/@label" />
						<xsl:value-of select="VersionOntology" />
					</xsl:if>
					<xsl:if test="VersionOntology != null">
						<xsl:value-of
							select="$LABELS_description/labels/entry[@key='VERSION']/@label" />
						<xsl:value-of select="VersionOntology" />
						<br />
					</xsl:if>
					<xsl:if test="commentOntology != ''">
						<br />
						<hr />
						<br />
						<h2>
							<xsl:value-of
								select="$LABELS_description/labels/entry[@key='COMMENTS']/@label" />
						</h2>
						<xsl:value-of select="commentOntology" />
						<br />
					</xsl:if>
					<br />
					<ul class="nav justify-content-left">
						<div>
							<!-- Table de matieres -->
							<h2>
								<xsl:value-of
									select="$LABELS/labels/entry[@key='TOC']/@label" />
							</h2>
							<xsl:if test="drawnImagenXML != ''">
								<xsl:variable name="TableDiagramme">
									<xsl:value-of
										select="$LABELS_Diagramme/labels/entry[@key='DIAGRAMME']/@label" />
								</xsl:variable>
								<a href="{concat('#',$TableDiagramme)}">
									<xsl:value-of select="$TableDiagramme" />
								</a>
								<br />
							</xsl:if>
							<xsl:for-each select="sections/section">
								<xsl:if test="count(properties/property)>0">
									<xsl:variable name="TitleNodeSapetab"
										select="dURI" />
									<xsl:variable name="Title" select="title" />
									<a href="{concat('#',$TitleNodeSapetab)}">
										<xsl:value-of select="$Title" />
									</a>
									<br />
								</xsl:if>
							</xsl:for-each>
						</div>
					</ul>
				</div>
				<br />
				<br />
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="drawnImagenXML">
		<xsl:if test=". != ''">
			<ul class="nav justify-content-center">
				<div class="container-md">
					<h2>
						<xsl:value-of
							select="$LABELS_Diagramme/labels/entry[@key='DIAGRAMME']/@label" />
					</h2>
					<br />
					<xsl:value-of select="." disable-output-escaping="yes" />
				</div>
			</ul>
			<br />
		</xsl:if>
	</xsl:template>

	<xsl:template match="shnamespaces">
		<ul class="nav justify-content-left">
			<div class="container-md">
				<h2>
					<xsl:value-of
						select="$LABELS_prefix/labels/entry[@key='TOC_PREFIX']/@label" />
				</h2>
				<table class="table table-striped" style="width:80%">
					<thead>
						<tr>
							<th>
								<xsl:value-of
									select="$LABELS_prefix/labels/entry[@key='COLUMN_PREFIX']/@label" />
							</th>
							<th>
								<xsl:value-of
									select="$LABELS_prefix/labels/entry[@key='COLUMN_URI']/@label" />
							</th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates />
					</tbody>
				</table>
			</div>
		</ul>
		<br />
	</xsl:template>

	<xsl:template match="shnamespace">
		<tr>
			<td>
				<xsl:value-of select="output_prefix" />
			</td>
			<td>
				<xsl:value-of select="output_namespace" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="sections">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="section">
		<xsl:variable name="TitleNodeSape" select="dURI" />
		<div class="container-md" id="{$TitleNodeSape}">
			<lefth>
				<h2>
					<xsl:value-of select="title" />
				</h2>
			</lefth>
			<xsl:choose>
				<xsl:when test="count(properties/property)>0">
					<lefth>
						<xsl:if test="comments != ''">
							<em>
								<xsl:value-of select="comments" />
							</em>
						</xsl:if>
						<xsl:if test="nodeKindNS != ''">
							<li>
								<xsl:value-of
									select="$LABELS_Properties/labels/entry[@key='LABEL_NODEKIND']/@label" />
								<xsl:value-of select="nodeKindNS" />
							</li>
						</xsl:if>
						<xsl:if test="patternNS != ''">
							<li>
								<xsl:value-of
									select="$LABELS_Properties/labels/entry[@key='LABEL_PATTERNS']/@label" />
								<xsl:value-of select="patternNS" />
							</li>
						</xsl:if>
						<xsl:if test="closeNS != '' and closeNS='true'">
							<li>
								<xsl:value-of
									select="$LABELS_Properties/labels/entry[@key='LABEL_CLOSE']/@label" />
							</li>
							<br />
						</xsl:if>
					</lefth>
					<table class="table table-striped" style="width:100%">
						<thead>
							<tr>
								<th>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='COLUMN_PROPERTY']/@label" />
								</th>
								<th>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='COLUMN_URI']/@label" />
								</th>
								<th>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='COLUMN_EXPECTED_VALUE']/@label" />
								</th>
								<th>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='COLUMN_CARD']/@label" />
								</th>
								<th>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='COLUMN_DESCRIPTION']/@label" />
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates />
						</tbody>
					</table>
				</xsl:when>
				<xsl:otherwise>
					<lefth>
						<xsl:if test="comments != ''">
							<em>
								<xsl:value-of select="comments" />
							</em>
						</xsl:if>
						<ul>
							<xsl:if test="nodeKindNS != ''">
								<li>
									<xsl:value-of
										select="$LABELS_Properties/labels/entry[@key='LABEL_NODEKIND']/@label" />
									<xsl:value-of select="nodeKindNS" />
								</li>
							</xsl:if>
							<xsl:if test="patternNS != ''">
								<li>
									<xsl:value-of
										select="$LABELS_Properties/labels/entry[@key='LABEL_PATTERNS']/@label" />
									<xsl:value-of select="patternNS" />
								</li>
							</xsl:if>
							<xsl:if test="closeNS != '' and closeNS = 'true'">
								<li>
									<xsl:value-of
										select="$LABELS_Properties/labels/entry[@key='LABEL_CLOSE']/@label" />
								</li>
								<br />
							</xsl:if>
						</ul>
					</lefth>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<br />
	</xsl:template>

	<xsl:template match="properties">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="property">
		<tr>
			<td>
				<xsl:value-of select="output_propriete" />
			</td>
			<td class="text-break">
				<xsl:if test="output_uri != null or output_uri != ''">
					<code>
						<xsl:value-of select="output_uri" />
					</code>
				</xsl:if>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="ouput_relnodeShape != ''">
						<a href="{concat('#',ouput_relnodeShape)}">
							<!--  <xsl:value-of select="output_valeur_attendus" /> -->
							<xsl:value-of select="ouput_relnodeShape" />
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
						  <xsl:when test="output_lieNodeshape != ''">
						     <a href="{concat('#',output_lieNodeshape)}">
						     <xsl:value-of select="output_lieNodeshape" />
						     </a>						     
						  </xsl:when>
						  <xsl:otherwise>
						     <xsl:value-of select="output_valeur_attendus" />
						  </xsl:otherwise>
						</xsl:choose>
						
					</xsl:otherwise>
				</xsl:choose>

				<br />
				<p class="text-break">
					<small>
						<xsl:value-of select="output_patterns" />
					</small>
				</p>
				<xsl:if test="output_shin != null or output_shin != ''">
					<p class="text-break">
						<small>
							<xsl:value-of select="concat('(',output_shin,')')" />
						</small>
					</p>
				</xsl:if>
				<xsl:if test="output_shvalue != null or output_shvalue != ''">
					<p class="text-break">
						<small>
							<xsl:value-of select="output_shvalue" />
						</small>
					</p>
				</xsl:if>
			</td>
			<td>
				<center />
				<xsl:value-of select="output_Cardinalite" />
			</td>
			<td class="text-break">
				<xsl:value-of select="output_description" />
			</td>
		</tr>
	</xsl:template>

	<!-- fin de format -->
	<xsl:template match="text()"></xsl:template>


</xsl:stylesheet>