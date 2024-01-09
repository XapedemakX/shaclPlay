package fr.sparna.rdf.shacl.doc.read;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.generate.Configuration;
import fr.sparna.rdf.shacl.generate.DefaultModelProcessor;
import fr.sparna.rdf.shacl.generate.PaginatedQuery;
import fr.sparna.rdf.shacl.generate.SamplingShaclGeneratorDataProvider;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;
import fr.sparna.rdf.shacl.generate.visitors.ComputeStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ComputeValueStatisticsVisitor;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisit;

public class DatasetDocumentationModelReader implements DatasetDocumentationReaderIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public ShapesDocumentation readDatasetDocumentation(
			Model dataset,
			Model owlModel,
			String lang
	) {
		
		// 1. Generate SHACL profile of the Dataset		
		Configuration config = new Configuration(new DefaultModelProcessor(), "https://shacl-play.sparna.fr/shapes/", "shapes");
		config.setShapesOntology("https://shacl-play.sparna.fr/shapes");
		config.setLang(lang);
		SamplingShaclGeneratorDataProvider dataProvider = new SamplingShaclGeneratorDataProvider(new PaginatedQuery(100),dataset);	
		
		ShaclGenerator generator = new ShaclGenerator();		
		// 2. Generate statistics as post-processing of the SHACL generation
		// arbitrary model name for Dataset name generation
		final String SOURCE_NAME = "data";
		Model statisticsModel = ModelFactory.createDefaultModel();
		generator.getExtraVisitors().add(new ComputeStatisticsVisitor(dataProvider, statisticsModel, SOURCE_NAME));
		generator.getExtraVisitors().add(new ComputeValueStatisticsVisitor(dataProvider,statisticsModel));
		
		// trigger generation
		Model shapes = generator.generateShapes(
				config,
				dataProvider);
		
		return generateDatasetDocumentation(
				owlModel,
				statisticsModel,
				shapes,
				lang
		);
	}
	
	@Override
	public ShapesDocumentation generateDatasetDocumentation(
			Model owlModel,
			Model statisticsModel,
			Model shapesModel,
			String lang
	) {
		// 3. Generate standard SHACL documentation
		ShapesDocumentationReaderIfc reader = new ShapesDocumentationModelReader(false, null);
		ShapesDocumentation shapesDocumentation = reader.readShapesDocumentation(shapesModel, ModelFactory.createDefaultModel(),lang,false);
		
		// 4. Apply post-processing to the generated documentation, to populate number of instances and charts
		ShaclVisit visit = new ShaclVisit(shapesModel);
		visit.visit(new EnrichDocumentationWithStatisticsVisitor(statisticsModel, shapesDocumentation));
		visit.visit(new EnrichDocumentationWithChartsVisitor(statisticsModel, shapesDocumentation, lang));

		return shapesDocumentation;
	}
	
}