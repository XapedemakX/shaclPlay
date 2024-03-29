package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.jena.ModelReadingUtils;
import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;;

public class PlantUmlBoxReader {
	
	public PlantUmlBox read(Resource nodeShape, List<Resource> allNodeShapes) {
		PlantUmlBox box = new PlantUmlBox(nodeShape);
		return box;
	}
	
	public List<PlantUmlProperty> readProperties(Resource nodeShape) {
		
		List<Statement> propertyStatements = nodeShape.listProperties(SH.property).toList();
		List<PlantUmlProperty> properties = new ArrayList<>();
		
		
		for (Statement aPropertyStatement : propertyStatements) {
			RDFNode object = aPropertyStatement.getObject();
			
			if(object.isResource()) {
				Resource propertyShape = object.asResource();			
				PlantUmlProperty plantvalueproperty = new PlantUmlProperty(propertyShape);
				properties.add(plantvalueproperty);					
			}
		
		}		

		properties.sort((PlantUmlProperty ps1, PlantUmlProperty ps2) -> {
			if(ps1.getShOrder().isPresent()) {
				if(ps2.getShOrder().isPresent()) {
					return ((ps1.getShOrder().map(o -> o.getDouble()).get() - ps2.getShOrder().map(o -> o.getDouble()).get()) > 0)?1:-1;
				} else {
					return -1;
				}
			} else {
				if(ps2.getShOrder().isPresent()) {
					return 1;
				} else {
					// both sh:order are null, try with sh:path
					if(ps1.getPathAsSparql() != null && ps2.getPathAsSparql() != null) {						
						return ps1.getPathAsSparql().compareTo(ps2.getPathAsSparql());
					} else {
						return ps1.getPropertyShape().toString().compareTo(ps2.getPropertyShape().toString());
					}
				}
			}
		});
		 
		return properties;	
	}
	
}