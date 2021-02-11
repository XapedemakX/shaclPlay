package fr.sparna.rdf.shacl.doc.model;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import fr.sparna.rdf.shacl.doc.ShaclProperty;

public class PropertyShapeDocumentation {
    
	private String output_propriete;
	private String output_uri;
	private String output_valeur_attendus;
	private String output_patterns;
	private String output_Cardinalite;
	private String output_description;
	private String output_language;
	private String output_shin;
	
	
	public String getOutput_shin() {
		return output_shin;
	}

	public void setOutput_shin(String output_shin) {
		String value = null;
		if (output_shin != null) {
			value = output_shin.substring(0, output_shin.length()-2);
		}		
		this.output_shin = value;
	}

	public String getOutput_language() {
		return output_language;
	}

	public void setOutput_language(String output_language) {
		this.output_language = output_language;
	}

	public String getOutput_patterns() {
		return output_patterns;
	}

	public void setOutput_patterns(String Value_pattern_propriete,String PatternNodeShape, String PatternoNodeShape,
			String Valeu_class,  String Value_node,
			String Value_Target, String Value_datatype, String Value_nodeKind, String URI) {
		String value = null;
		
		// Classe

		if (Valeu_class != null) { //
			value = Value_pattern_propriete;
		} else if (Value_Target != null) { // La valeur d'un Node vers une NodeShape qui a sh:targetClass
			value = PatternoNodeShape;
		} else if (Value_node != null) {
			value = PatternoNodeShape;
		} // Datatype : sh:datatype
		else if (Value_datatype != null) {
			value = Value_pattern_propriete;
		} else if (Value_datatype != null && Value_node != null) {
			value = PatternoNodeShape;
		} // Type de noeud seulement : sh:nodeKind
		else if (Value_nodeKind != null && Value_node == null) {
			value = Value_pattern_propriete;
		} else if (Value_nodeKind != null && Value_node != null) {
			value = PatternoNodeShape;
		}	    
		this.output_patterns = value;
	}

	public String getOutput_Cardinalite() {
		return output_Cardinalite;
	}

	public void setOutput_Cardinalite(String output_valeur_Cardinalite) {
		String value = null;
		if(output_valeur_Cardinalite == null || output_valeur_Cardinalite == "") {
			value = "0..*";
		}else {
			value = output_valeur_Cardinalite;
		}
		
		this.output_Cardinalite = value;
	}

	public String getOutput_propriete() {
		return output_propriete;
	}

	public void setOutput_propriete(String output_propriete,String shLabel) {
		String Value = null;
		if (output_propriete != null) {
			String[] sName = output_propriete.split("@");
			Value = sName[0];
		} else {
			Value = shLabel;
		}
		this.output_propriete = Value;
	}

	public String getOutput_uri() {
		return output_uri;
	}

	public void setOutput_uri(String output_uri) {
		this.output_uri = output_uri;
	}

	public String getOutput_valeur_attendus() {
		return output_valeur_attendus;
	}

	public void setOutput_valeur_attendus(String Valeu_class,  String Value_node,
			String Value_Target, String Value_datatype, String Value_nodeKind, String URI) {

		String value = null;
	
		// Classe

		if (Valeu_class != null) { //
			value = Valeu_class;
		} else if (Value_Target != null) { // La valeur d'un Node vers une NodeShape qui a sh:targetClass
			value = Value_Target;
		} else if (Value_node != null) {
			value = Value_node ;
		} // Datatype : sh:datatype
		else if (Value_datatype != null) {
			value = Value_datatype;
		} else if (Value_datatype != null && Value_node != null) {
			value = Value_node;
		} // Type de noeud seulement : sh:nodeKind
		else if (Value_nodeKind != null && Value_node == null) {
			if (Value_nodeKind.equals("sh:IRI")) {
				String[] ssplit = Value_nodeKind.split(":");
				value = ssplit[ssplit.length-1];			
			}else {
				value = Value_nodeKind;
			}
			//value = Value_nodeKind;
		} else if (Value_nodeKind != null && Value_node != null) {
			value = Value_node;
		}

		this.output_valeur_attendus = value;
	}

	public String getOutput_description() {
		return output_description;
	}

	public void setOutput_description(String output_description, String shComment) {
		String Value = null;
		if (output_description != null) {
			Value = output_description;
		} else {
			Value = null;
		}
		this.output_description = Value;
	} 
	
}