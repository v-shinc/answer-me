import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordProcessor {
	String line = System.getProperty("line.separator");

	public static enum Operation {
		TOKENIZE, SSPLIT, POS, LEMMA, NER, PARSE, DCOREF;
	}

	public static enum Tags {
		POS, LEMMA, NER, PARSE, DCOREF;
	}

	/*
	 * label one single text (multiple sentences in text)
	 * 
	 * @return text with POS tag, e.g Obama/NN
	 */
	public String POSString(String text) {
		Annotation document;
		Properties props = new Properties();

		props.put("annotators", OperationFlow.get(Operation.POS));
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		StringBuffer sbpos = new StringBuffer();
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				sbpos.append(word + "/" + pos + " ");
			}
			sbpos.append(line);

		}
		return sbpos.toString().trim();
	}

	ArrayList<Tags> tags = new ArrayList<Tags>();
	public static Map<Operation, String> OperationFlow = new HashMap() {
		{
			put(Operation.TOKENIZE, "tokenize");
			put(Operation.SSPLIT, "tokenize,ssplit");
			put(Operation.POS, "tokenize, ssplit,pos");
			put(Operation.LEMMA, "tokenize,ssplit,pos,lemma");
			put(Operation.NER, "tokenize,ssplit,pos,lemma,ner");
			put(Operation.PARSE, "tokenize,ssplit,pos,lemma,ner,parse");
			put(Operation.DCOREF, "tokenize,ssplit,pos,lemma,ner,parse,dcoref");
		}
	};

	public JSONArray AnnotateJSONArray(JSONArray jsonarray, String key,
			Operation op, final String tagNameTable) throws JSONException {

		Properties props = new Properties();
		Map<String, String> oflag = new HashMap<String, String>();

		Annotation document;
		String flow = OperationFlow.get(op);
		Arrays.asList(tagNameTable.split(",")).forEach((tag) -> {
			String strs[] = tag.split(":");
			if (flow.indexOf(strs[0]) != -1)
				oflag.put(strs[0], strs[1]);
		});
		props.put("annotators", flow);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		String str;
		// for each json object
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = (JSONObject) jsonarray.get(i);
			String text = (obj.getString(key));
			document = new Annotation(text);
			pipeline.annotate(document);
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			StringBuffer sbpos = new StringBuffer(), sbner = new StringBuffer();
			StringBuffer sbparse = new StringBuffer();
			StringBuffer sbdcoref = new StringBuffer();
			for (CoreMap sentence : sentences) {

				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					String word = token.get(TextAnnotation.class);
					if (oflag.containsKey("pos")) {
						String pos = token.get(PartOfSpeechAnnotation.class);
						sbpos.append(word + "/" + pos + " ");
					}

					if (oflag.containsKey("ner")) {
						String ne = token.get(NamedEntityTagAnnotation.class);
						sbner.append(ne + " ");
					}
				}

				if (oflag.containsKey("parse")) {
					Tree tree = sentence.get(TreeAnnotation.class);
					// tree.pennPrint();
					sbparse.append(tree.toString() + line);

				}
				if (oflag.containsKey("dcoref")) {
					SemanticGraph dependencies = sentence
							.get(CollapsedCCProcessedDependenciesAnnotation.class);
					dependencies.prettyPrint();
					sbdcoref.append(dependencies.toString() + line);
				}

			}
			if (oflag.containsKey("pos")) {
				obj.put(oflag.get("pos"), sbpos.toString());
			}
			if (oflag.containsKey("ner")) {
				obj.put(oflag.get("ner"), sbner.toString());
			}
			if (oflag.containsKey("parse")) {
				obj.put(oflag.get("parse"), sbparse.toString());
			}
			if (oflag.containsKey("dcoref")) {
				//
				obj.put(oflag.get("dcoref"), sbdcoref.toString());
			}
			// This is the coreference link graph
			// Each chain stores a set of mentions that link to each other,
			// along with a method for getting the most representative mention
			// Both sentence and token offsets start at 1!
			Map<Integer, CorefChain> graph = document
					.get(CorefChainAnnotation.class);

		}
		return jsonarray;
	}
}
