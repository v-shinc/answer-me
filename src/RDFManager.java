import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;

/**
 * 
 */

public class RDFManager extends Object {
	// some definitions
	private Model rdfModel = null;
	private Model sameAsModel = null;
	static public final String NL = System.getProperty("line.separator");
	private ArrayList<String> subjects = null;
	private ArrayList<String> predicates = null;
	private ArrayList<String> objects = null;

	// Subject String with URL prefix
	private HashMap<String, String> oriSub = null;
	// private HashMap<String, String> oriPre = null;
	private HashMap<String, String> oriObj = null;
	private HashMap<String, String> invIdx = null;
	private HashMap<String, ArrayList<String>> sameAs = null;

	public RDFManager(String knowledgeBase) {
		rdfModel = RDFManager.readRDF(knowledgeBase);

		inital();
	}

	// public Model createModel() throws FileNotFoundException {
	// String personURI = "http://somewhere/JohnSmith";
	// String personURI2 = "http://somewhere/ChenShini";
	// String givenName = "John";
	// String familyName = "Smith";
	// String friendURI = "http://csn.com/BigFlower";
	// String documentURI = "http://csn.com/document";
	// String fullName = givenName + " " + familyName;
	// // create an empty model
	// Model model = ModelFactory.createDefaultModel();
	//
	// // create the resource
	// // and add the properties cascading style
	// Resource johnSmith = model
	// .createResource(personURI)
	// .addProperty(VCARD.FN, fullName)
	// .addProperty(
	// VCARD.N,
	// model.createResource()
	// .addProperty(VCARD.Given, givenName)
	// .addProperty(VCARD.Family, familyName));
	// Resource friend = model.createResource(friendURI);
	// Resource friend2 = model.createResource("http://csn.com/BigFlower2");
	//
	// // Add proprties for the resources in a cascading style
	// Resource person = model.createResource(personURI2)
	//
	// .addProperty(RDF.type, FOAF.Person).addProperty(FOAF.knows, friend)
	// .addLiteral(FOAF.name, "Carol Nobody")
	// .addProperty(FOAF.knows, friend2);
	//
	// Resource foafDoc = model.createResource(documentURI)
	// .addProperty(RDF.type, FOAF.PersonalProfileDocument)
	// .addProperty(FOAF.maker, person)
	// .addProperty(FOAF.primaryTopic, person);
	// PrintWriter out = new PrintWriter(
	// new FileOutputStream("data/test.json"));
	// // now write the model in XML form to a filer
	// model.write(out, "TURTLE");
	// return model;
	// }

	private void inital() {
		this.subjects = new ArrayList<String>();
		this.predicates = new ArrayList<String>();
		this.objects = new ArrayList<String>();
		this.oriSub = new HashMap<String, String>();
		// this.oriPre = new HashMap<String, String>();
		this.oriObj = new HashMap<String, String>();
		this.invIdx = new HashMap<String, String>();
		Set<String> s = new HashSet<String>();
		Set<String> p = new HashSet<String>();
		Set<String> o = new HashSet<String>();
		this.sameAs = new HashMap<String, ArrayList<String>>();
		this.getAll(s, p, o, sameAs);

		String prefix = "http:/(/[a-z_.0-9]+)+[/#]";

		s.forEach(ss -> {
			String s1 = ss.toLowerCase().replaceAll(prefix, "")
					.replaceAll("_", " ");
			;
			this.subjects.add(s1);
			oriSub.put(s1, ss);
		});
		// p.forEach(pp -> {
		// String p1 = pp.toLowerCase().replaceAll(prefix, "");
		//
		// this.predicates.add(p1);
		// oriPre.put(p1, pp);
		// });
		o.forEach(oo -> {
			String o1 = oo.toLowerCase().replaceAll(prefix, "")
					.replaceAll("_", " ");

			this.objects.add(o1);
			oriObj.put(o1, oo);
		});

		sameAs.forEach((k, v) -> {
			v.forEach(equ -> {

				this.invIdx.put(equ.replaceAll(prefix, ""), k);

			});
		});

	}

	public ArrayList<String> SearchByEditDistance(String key, Role col) {

		ArrayList<String> ans = new ArrayList<String>();

		int minDis = 99999;
		if (col == Role.SUBJECT || col == Role.RESOURCE) {

			for (String s : this.subjects) {
				int dis = editDistance(key, s);
				if (dis <= minDis) {
					if (dis < minDis) {
						minDis = dis;
						ans.clear();
					}
					ans.add(oriSub.get(s));
				}
			}

		}
		if (col == Role.OBJECT || col == Role.RESOURCE) {
			for (String s : this.objects) {
				int dis = editDistance(key, s);
				if (dis <= minDis) {
					if (dis < minDis) {
						minDis = dis;
						ans.clear();
					}
					ans.add(oriObj.get(s));
				}
			}
		}
		if (col == Role.PREDICATE) {
			for (String k : invIdx.keySet()) {
				int dis = editDistance(key, k);
				if (dis <= minDis) {
					if (dis < minDis) {
						minDis = dis;
						ans.clear();
					}
					ans.add(invIdx.get(k));
				}
			}
		}
		return ans;
	}

	public int editDistance(String a, String b) {

		String aa = "_" + a, bb = "_" + b;
		int n = aa.length(), m = bb.length();

		int dis[][] = new int[n][m];
		for (int i = 0; i < n; i++) {
			dis[i][0] = i;
		}
		for (int j = 0; j < m; j++) {
			dis[0][j] = j;
		}
		for (int i = 1; i < n; i++) {
			for (int j = 1; j < m; j++) {
				dis[i][j] = Math.min(dis[i - 1][j], dis[i][j - 1]) + 1;

				dis[i][j] = Math.min(
						dis[i - 1][j - 1]
								+ (aa.charAt(i) == bb.charAt(j) ? 0 : 1),
						dis[i][j]);
			}
		}
		return dis[n - 1][m - 1];

	}

	static Model readRDF(String inputFileName) {
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

	public void getAll(Set<String> subjects, Set<String> predicates,
			Set<String> objects, HashMap<String, ArrayList<String>> sameAs) {
		String queryString = "SELECT ?s ?p ?o WHERE{?s ?p ?o}";
		Query query = QueryFactory.create(queryString);
		// query.serialize(new IndentedWriter(System))
		// Model model = this.readRDF(fileName);
		QueryExecution qexec = QueryExecutionFactory.create(query, rdfModel);

		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode s = rb.get("s");
				RDFNode p = rb.get("p");
				RDFNode o = rb.get("o");
				if (p.toString().contains("sameAs")) {
					if (!sameAs.containsKey(s.toString())) {
						ArrayList<String> arr = new ArrayList<String>();
						arr.add(o.toString());
						arr.add(s.toString());
						sameAs.put(s.toString(), arr);
					} else {
						sameAs.get(s.toString()).add(o.toString());
					}
				} else {
					subjects.add(s.toString());
					predicates.add(p.toString());
					objects.add(o.toString());
				}

			}
		} finally {
			qexec.close();
		}

	}

	public void executeQuery(String arq) {

		Query query = QueryFactory.create(arq);
		QueryExecution qexec = QueryExecutionFactory.create(query, rdfModel);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				// QuerySolution rb = rs.nextSolution();
				System.out.println(arq);
				ResultSetFormatter.out(System.out, rs, query);
			}
		} finally {
			qexec.close();
		}
	}
	// public void query(Model model) {
	//
	// // First part or the query string
	// String prolog =
	// "PREFIX my: <http://idi.fundacionctic.org/tabels/project/imdb/resource/> ";
	// prolog += "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>";
	// // Query string.
	// String queryString = prolog
	// + NL
	// + "SELECT ?x WHERE {?x my:votes ?title. ?x rdfs:label \"Sin City\"}";
	//
	// Query query = QueryFactory.create(queryString);
	// // Print with line numbers
	// query.serialize(new IndentedWriter(System.out, true));
	// System.out.println();
	//
	// // Create a single execution of this query, apply to a model
	// // which is wrapped up as a Dataset
	//
	// QueryExecution qexec = QueryExecutionFactory.create(query, model);
	// // Or QueryExecutionFactory.create(queryString, model) ;
	//
	// System.out.println("Titles: ");
	//
	// try {
	// // Assumption: it's a SELECT query.
	// ResultSet rs = qexec.execSelect();
	//
	// // The order of results is undefined.
	// for (; rs.hasNext();) {
	// QuerySolution rb = rs.nextSolution();
	//
	// // Get title - variable names do not include the '?' (or '$')
	// RDFNode x = rb.get("x");
	//
	// // Check the type of the result value
	// if (x.isLiteral()) {
	// Literal titleStr = (Literal) x;
	// System.out.println("    " + titleStr);
	// } else
	// System.out.println("Strange - not a literal: " + x);
	//
	// }
	// } finally {
	// // QueryExecution objects should be closed to free any system
	// // resources
	// qexec.close();
	// }
	// }

}

enum Role {
	SUBJECT, PREDICATE, OBJECT, RESOURCE
}
