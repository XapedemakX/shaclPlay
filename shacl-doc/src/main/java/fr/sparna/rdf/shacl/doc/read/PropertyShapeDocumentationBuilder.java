package fr.sparna.rdf.shacl.doc.read;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import fr.sparna.rdf.shacl.doc.ConstraintValueReader;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;

import fr.sparna.rdf.shacl.doc.model.Link;


public class PropertyShapeDocumentationBuilder {

	public static PropertyShapeDocumentation build(
			PropertyShape propertyShape,
			List<NodeShape> allNodeShapes,
			Model shaclGraph,
			Model owlGraph,
			String lang) {
		// Start building final structure
		PropertyShapeDocumentation proprieteDoc = new PropertyShapeDocumentation();
		proprieteDoc.setLabel(selectLabel(propertyShape, shaclGraph.union(owlGraph), lang));
		
		if(propertyShape.getShNode() != null) {
			for(NodeShape aBox : allNodeShapes) {
				if(aBox.getNodeShape().getURI().equals(propertyShape.getShNode().getURI())) {
					proprieteDoc.setLinkNodeShapeUri(aBox.getShortForm());
					if(aBox.getRdfsLabel() == null) {
						proprieteDoc.setLinkNodeShape(aBox.getShortForm());
					}else {
						proprieteDoc.setLinkNodeShape(aBox.getRdfsLabel());
					}							
				}
			}
		}
		
		//proprieteDoc.setShortForm(propertyShape.getShPathAsString());
		//proprieteDoc.setPropertyUri(propertyShape.getShPath().isURIResource()?propertyShape.getShPath().getURI():null);				
		
		// URI in the raport
		proprieteDoc.setPropertyUri(buildPathLink(propertyShape));
		
		proprieteDoc.setExpectedValueLabel(selectExpectedValueLabel(
				propertyShape.getShClass(),
				propertyShape.getShNode(),
				propertyShape.getShDatatype(),
				propertyShape.getShNodeKind(),
				propertyShape.getShValue()
		));
		
		if(propertyShape.getShClass() != null) {
			for(NodeShape aNodeShape : allNodeShapes) {
				if(aNodeShape.getShTargetClass() != null && aNodeShape.getShTargetClass().getURI().equals(propertyShape.getShClass().getURI())) {
					proprieteDoc.setLinkNodeShapeUri(aNodeShape.getShortForm()); //aName.getShortForm().getLocalName()
					if(aNodeShape.getRdfsLabel() == null) {
						proprieteDoc.setLinkNodeShape(aNodeShape.getShortForm());
					}else {
						proprieteDoc.setLinkNodeShape(aNodeShape.getRdfsLabel());
					}
					break;
				// checks that the URI of the NodeShape is itself equal to the sh:class
				} else if (aNodeShape.getNodeShape().getURI().equals(propertyShape.getShClass().getURI())) {
					proprieteDoc.setLinkNodeShapeUri(aNodeShape.getShortForm()); //aName.getShortForm().getLocalName()
					if(aNodeShape.getRdfsLabel() == null) {
						proprieteDoc.setLinkNodeShape(aNodeShape.getShortForm());
					}else {
						proprieteDoc.setLinkNodeShape(aNodeShape.getRdfsLabel());
					}
					break;
				}
			}					
		}

		proprieteDoc.setExpectedValueAdditionnalInfoIn(render(propertyShape.getShIn(), false));
		proprieteDoc.setCardinalite(renderCardinalities(propertyShape.getShMinCount(), propertyShape.getShMaxCount()));
		proprieteDoc.setDescription(selectDescription(propertyShape, shaclGraph.union(owlGraph), lang));
		
		// create a String of comma-separated short forms
		proprieteDoc.setOr(render(propertyShape.getShOr(), false));
		
		return proprieteDoc;
	}
	
	public static String selectLabel(PropertyShape prop, Model owlModel, String lang) {
		// if we have a sh:name, take it
		if(prop.getShName() != null) {
			return render(prop.getShName(), true);
		} else if(prop.getShPath().isURIResource()) {
			// otherwise if we have rdfs:label on the property, take it
			return render(ConstraintValueReader.readLiteralInLang(owlModel.getResource(prop.getShPath().getURI()), RDFS.label, lang), true);
		} else {
			return null;
		}
	}
	
	public static String selectDescription(PropertyShape prop, Model owlModel, String lang) {
		// if we have a sh:description, take it
		if(prop.getShDescription() != null) {
			return render(prop.getShDescription(), true);
		} else if(prop.getShPath().isURIResource()) {
			// otherwise if we have rdfs:comment on the property, take it
			return render(ConstraintValueReader.readLiteralInLang(owlModel.getResource(prop.getShPath().getURI()), RDFS.comment, lang), true);
		} else {
			return null;
		}
	}
	
	public static String render(List<? extends RDFNode> list, boolean plainString) {
		if(list == null) {
			return null;
		}
		
		return list.stream().map(item -> {
			return render(item, plainString);
		}).collect(Collectors.joining(", "));
	}
	
	public static Link buildPathLink(PropertyShape prop) {			
		if(prop.getShPath().isURIResource()) {
			return new Link(
					prop.getShPath().getURI(),
					prop.getShPathAsString()
			);			
		} else {
			return new Link(
					null,
					prop.getShPathAsString()
			);
		}
	}
	
	public static String render(RDFNode node, boolean plainString) {
		if(node == null) {
			return null;
		}
		
		if(node.isURIResource()) {
			return node.getModel().shortForm(node.asResource().getURI());
		} else if(node.isAnon()) {
			return node.toString();
		} else if(node.isLiteral()) {
			// if we asked for a plain string, just return the literal string
			if(plainString) {
				
				if(node.asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#dateTime")) {
					try {
						return fmtDate(node.asLiteral().getLexicalForm(), node.asLiteral().getDatatypeURI());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					
				}
				return node.asLiteral().getLexicalForm();
			}
			
			if (node.asLiteral().getDatatype() != null && !node.asLiteral().getDatatypeURI().equals(RDF.langString.getURI())) {
				// nicely prints datatypes with their short form
				return "\"" + node.asLiteral().getLexicalForm() + "\"<sup>^^"
						+ node.getModel().shortForm(node.asLiteral().getDatatype().getURI())+"</sup>";
			} else if (node.asLiteral().getLanguage() != null) {
				return "\"" + node.asLiteral().getLexicalForm() + "\"<sup>@"
						+ node.asLiteral().getLanguage()+"</sup>";
			} else {
				return node.toString();
			}
		} else {
			// default, should never get there
			return node.toString();
		}
	}
	
	public static String fmtDate(String inputDate, String fmt) throws ParseException {
		String dateFmt = null;
		if (inputDate != null) {
			if(fmt.equals("http://www.w3.org/2001/XMLSchema#date")) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd");
				
				if(!formatter.equals(inputDate)) {
					Date date = formatter.parse(inputDate);
					dateFmt = formatter.format(date);
				}else {
					dateFmt = inputDate;
				}
								
			}
			
			if(fmt.equals("http://www.w3.org/2001/XMLSchema#dateTime")) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				SimpleDateFormat outputformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = formatter.parse(inputDate);
				dateFmt = outputformatter.format(date);
			}
		}
		return dateFmt;
	}
	
	
	public static String renderCardinalities(Integer min, Integer max) {
		String minCount = "0";
		String maxCount = "*";
		
		if (min != null) {
			minCount = min.toString();
		}
		if (max != null) {
			maxCount = max.toString();
		}
		
		return minCount + ".." + maxCount;
	}
	
	public static String selectExpectedValueLabel(
			Resource shClass,
			Resource shNode,
			Resource value_datatype,
			Resource value_nodeKind,
			RDFNode value_shValue
	) {
		String value = null;

		if (value_shValue != null) {
			value = render(value_shValue, false);
		} else if (shClass != null) {
			value = render(shClass, false);
		} else if (shNode != null) {
			value = render(shNode, false);
		} else if (value_datatype != null) {
			value = render(value_datatype, false);
		} else if (value_nodeKind != null) {
			value = renderNodeKind(value_nodeKind);
		}
		
		return value;
	}
	
	public static String renderNodeKind(Resource nodeKind) {
		if(nodeKind == null) {
			return null;
		}
		
		String rendered = render(nodeKind, false);
		if (rendered.startsWith("sh:")) {
			return rendered.split(":")[1];	
		} else {
			return rendered;
		}
	}
	
}
