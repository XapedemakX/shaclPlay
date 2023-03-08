package fr.sparna.rdf.shacl.generate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;

import fr.sparna.rdf.jena.JenaResultSetHandlers;
import fr.sparna.rdf.jena.QueryExecutionService;

public class SamplingShaclGeneratorDataProvider implements ShaclGeneratorDataProviderIfc {

	// root where SPARQL queries are located
	private static final String CLASSPATH_ROOT = "shacl/generate/";

	// executes SPARQL queries either on local Model or remote endpoint
	private final QueryExecutionService queryExecutionService;

	// paginates through SPARQL queries
	private final PaginatedQuery paginatedQuery;


	public SamplingShaclGeneratorDataProvider(PaginatedQuery paginatedQuery, String endpointUrl) {
		super();
		this.queryExecutionService = new QueryExecutionService(endpointUrl);
		this.paginatedQuery = paginatedQuery;
	}

	public SamplingShaclGeneratorDataProvider(PaginatedQuery paginatedQuery, Model inputModel) {
		super();
		this.queryExecutionService = new QueryExecutionService(inputModel);
		this.paginatedQuery = paginatedQuery;
	}

	@Override
	public List<String> getTypes() {
		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(this.queryExecutionService, readQuery("select-types.rq"));
		List<String> types = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);

		// types.sort(getIriComparator(configuration));

		return types;
	}

	@Override
	public List<String> getProperties(String classUri) {
		/*
		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(
				this.queryExecutionService,
				readQuery("select-properties-sample.rq"),
				QueryExecutionService.buildQuerySolution("type", ResourceFactory.createResource(classUri))
		);
		
		List<String> properties = paginatedQuery.convertSingleColumnUriToStringList(rows);
		*/
		
		/*
		List<Map<String, RDFNode>> instanceRows = this.paginatedQuery.select(
				this.queryExecutionService,
				readQuery("select-instances-sample.rq"),
				QueryExecutionService.buildQuerySolution("type", ResourceFactory.createResource(classUri))
		);
		List<RDFNode> instances = JenaResultSetHandlers.convertSingleColumnUriToRDFNodeList(instanceRows);
		
		Set<String> properties = new HashSet<>();
		for (RDFNode rdfNode : instances) {
			List<Map<String, RDFNode>> rows = this.paginatedQuery.select(
					this.queryExecutionService,
					readQuery("select-instances-properties.rq"),
					QueryExecutionService.buildQuerySolution("uri", rdfNode)
			);
			properties.addAll(JenaResultSetHandlers.convertSingleColumnUriToStringList(rows));
		}
		
		return new ArrayList<String>(properties);
		*/
		
		SamplingQuery samplingQuery = new SamplingQuery(2, 50000, 50);
		List<Map<String, RDFNode>> instanceRows = samplingQuery.select(
				this.queryExecutionService,
				readQuery("select-instances-sample.rq"),
				QueryExecutionService.buildQuerySolution("type", ResourceFactory.createResource(classUri))
		);
		List<RDFNode> instances = JenaResultSetHandlers.convertSingleColumnUriToRDFNodeList(instanceRows);
		
		Set<String> properties = new HashSet<>();
		for (RDFNode rdfNode : instances) {
			List<Map<String, RDFNode>> rows = this.paginatedQuery.select(
					this.queryExecutionService,
					readQuery("select-instances-properties.rq"),
					QueryExecutionService.buildQuerySolution("uri", rdfNode)
			);
			properties.addAll(JenaResultSetHandlers.convertSingleColumnUriToStringList(rows));
		}
		
		return new ArrayList<String>(properties);
	}

	@Override
	public boolean hasInstanceWithoutProperty(String classUri, String propertyUri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasInstanceWithTwoProperties(String classUri, String propertyUri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasIriObject(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		return this.queryExecutionService.executeAskQuery(readQuery("nodekind-is-iri.rq"), qs);
	}

	@Override
	public boolean hasLiteralObject(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		return this.queryExecutionService.executeAskQuery(readQuery("nodekind-is-literal.rq"), qs);
	}

	@Override
	public boolean hasBlankNodeObject(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		return this.queryExecutionService.executeAskQuery(readQuery("nodekind-is-blank.rq"), qs);
	}

	@Override
	public List<String> getDatatypes(String classUri, String propertyUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNotUniqueLang(String classUri, String propertyUri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getLanguages(String classUri, String propertyUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getObjectTypes(String classUri, String propertyUri) {
		QuerySolutionMap qs = new QuerySolutionMap();
		qs.add("type", ResourceFactory.createResource(classUri));
		qs.add("property", ResourceFactory.createResource(propertyUri));
		List<Map<String, RDFNode>> rows = this.paginatedQuery.select(this.queryExecutionService, readQuery("select-object-types.rq"), qs);
		List<String> types = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);
		return types;
	}

	private String readQuery(String resourceName) {
		try {
			return getResourceFileAsString(CLASSPATH_ROOT+resourceName);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Reads given resource file as a string.
	 *
	 * @param fileName path to the resource file
	 * @return the file's contents
	 * @throws IOException if read fails for any reason
	 */
	static String getResourceFileAsString(String fileName) throws IOException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(fileName)) {
			if (is == null) return null;
			try (InputStreamReader isr = new InputStreamReader(is);
					BufferedReader reader = new BufferedReader(isr)) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
	}

}
