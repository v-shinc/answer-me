/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.VCARD;

/**
 * 
 */

public class RDFManager extends Object {
	// some definitions
	private Model mainModel;
	static public final String NL = System.getProperty("line.separator");

	public RDFManager() {

	}

	public Model createModel() throws FileNotFoundException {
		String personURI = "http://somewhere/JohnSmith";
		String personURI2 = "http://somewhere/ChenShini";
		String givenName = "John";
		String familyName = "Smith";
		String friendURI = "http://csn.com/BigFlower";
		String documentURI = "http://csn.com/document";
		String fullName = givenName + " " + familyName;
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// create the resource
		// and add the properties cascading style
		Resource johnSmith = model
				.createResource(personURI)
				.addProperty(VCARD.FN, fullName)
				.addProperty(
						VCARD.N,
						model.createResource()
								.addProperty(VCARD.Given, givenName)
								.addProperty(VCARD.Family, familyName));
		Resource friend = model.createResource(friendURI);
		Resource friend2 = model.createResource("http://csn.com/BigFlower2");

		// Add proprties for the resources in a cascading style
		Resource person = model.createResource(personURI2)

		.addProperty(RDF.type, FOAF.Person).addProperty(FOAF.knows, friend)
				.addLiteral(FOAF.name, "Carol Nobody")
				.addProperty(FOAF.knows, friend2);

		Resource foafDoc = model.createResource(documentURI)
				.addProperty(RDF.type, FOAF.PersonalProfileDocument)
				.addProperty(FOAF.maker, person)
				.addProperty(FOAF.primaryTopic, person);
		PrintWriter out = new PrintWriter(
				new FileOutputStream("data/test.json"));
		// now write the model in XML form to a filer
		model.write(out, "TURTLE");
		return model;
	}

	public static void main(String args[]) throws FileNotFoundException {
		// some definitions

		RDFManager rc = new RDFManager();
		rc.createModel();

		// rc.mainModel = rc.readRDF("data/imdb.ttl");
		// rc.query(rc.mainModel);
		rc.NLQuery("who directed the Sin City");
		rc.NLQuery("Who is the director of the Sin City");
		rc.NLQuery("What film did Brie Larson played in");
		rc.NLQuery("who are the actors of the Sin City");
		rc.NLQuery("Who acts in the Sin City");
		rc.NLQuery("Who starred in the Sin City");
		rc.NLQuery("Who played in the Sin City");
		rc.NLQuery("What kind of film did Brie Larson played in");
		// rc.NLQuery("who lived in fallingwater");
		// rc.NLQuery("Who was the newscaster in 1948 on cbs evening news");
		// rc.NLQuery("who is the music by in bruce almighty");
		// rc.NLQuery("who was the casting director for meet the parents");
	}

	public Model readRDF(String inputFileName) {
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + inputFileName
					+ " not found");
		}

		RDFDataMgr.read(model, in, RDFLanguages.TURTLE);
		// write it to standard out
		// model.write(System.out, "TURTLE");
		return model;
	}

	public String NLQuery(String qestion) {
		StanfordProcessor sp = new StanfordProcessor();
		String posq = sp.POSString(qestion);
		Question2Triples q2t = new Question2Triples();
		String query = q2t.NLPos2Triples(posq).toSparQL();
		System.out.println(query);
		return "";

	}

	public void query(Model model) {

		// First part or the query string
		String prolog = "PREFIX my: <http://idi.fundacionctic.org/tabels/project/imdb/resource/> ";
		prolog += "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>";
		// Query string.
		String queryString = prolog
				+ NL
				+ "SELECT ?x WHERE {?x my:votes ?title. ?x rdfs:label \"Sin City\"}";

		Query query = QueryFactory.create(queryString);
		// Print with line numbers
		query.serialize(new IndentedWriter(System.out, true));
		System.out.println();

		// Create a single execution of this query, apply to a model
		// which is wrapped up as a Dataset

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		// Or QueryExecutionFactory.create(queryString, model) ;

		System.out.println("Titles: ");

		try {
			// Assumption: it's a SELECT query.
			ResultSet rs = qexec.execSelect();

			// The order of results is undefined.
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();

				// Get title - variable names do not include the '?' (or '$')
				RDFNode x = rb.get("x");

				// Check the type of the result value
				if (x.isLiteral()) {
					Literal titleStr = (Literal) x;
					System.out.println("    " + titleStr);
				} else
					System.out.println("Strange - not a literal: " + x);

			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
	}

}
