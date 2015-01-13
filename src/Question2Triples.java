import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question2Triples {
	static String w = "[0-9a-zA-Z',._]+";
	static String N = String.format("%s/NN(S|PS?)?", w);
	static String V = String.format("%s/VB(D|G|N|P|Z)?", w);
	static String VBN = "[a-zA-Z.]+/VBN";
	static String CD = "[a-zA-Z0-9]+/CD";
	static String DT = String.format("%s/DT", w);
	static String IN = str("%s/IN", w);
	static String POS = "'s/POS";
	static String TO = "to/TO";
	static String JJ = str("%s/JJ", w);
	static String PRP = str("%s/PRP", w);
	static String PRE = String.format("(%s|%s)", IN, TO);

	static String DO = "(did/VBD|does/VBZ|do/VBP)";
	static String NN = String.format(
			"((%s|%s) )?(%s )*((%s|%s|%s) )*(%s|%s)( %s)*", DT, PRP, CD, JJ, N,
			CD, JJ, N, CD);
	static String AND = str("%s/CC", w);
	static String NAN = str("%s( %s %s)*", NN, AND, NN);
	static String DONEBY = str("%s %s", VBN, PRE);
	static String NNN = String.format("(%s (%s|%s|%s) )*%s", NAN, IN, POS,
			DONEBY, NAN);

	static String WDT = "what/WDT";
	static String WP = "what/WP";
	static String WHO = "who/WP";

	static String BE = "(is/VBZ|are/VBP|was/VBD|were/VBD)";
	static String PREPHRASE = String.format("(%s )?%s %s", DT, N, PRE);
	// static String BeDoneTo = String.format("%s %s %s", BE, V, PRE);
	static String VPRE = pattern(VBN, PRE);
	static String JJPRE = pattern(JJ, PRE);
	static String RELATION = String.format("((%s )?%s %s|%s( %s)?)", DT, N,
			PRE, V, PRE);
	static String PRENNN = String.format("((on/IN|in/IN|%s/TO) %s)", w, NNN);
	private Map<String, Pattern> templates = new HashMap<String, Pattern>();

	public Question2Triples() {
		templates.put("WhatXIsX",
				Pattern.compile(whatXIsX, Pattern.CASE_INSENSITIVE));
		templates.put("WhatXIsInX",
				Pattern.compile(whatXIsInX, Pattern.CASE_INSENSITIVE));
		templates.put("WhatXBeJJToX",
				Pattern.compile(whatXBeJJToX, Pattern.CASE_INSENSITIVE));
		templates.put("WhatIsNDoneInN",
				Pattern.compile(whatIsNDoneInN, Pattern.CASE_INSENSITIVE));
		templates.put("WhatIsNOfN",
				Pattern.compile(whatIsNOfN, Pattern.CASE_INSENSITIVE));
		templates.put("WhatNDoNVPre",
				Pattern.compile(whatNDoNVPre, Pattern.CASE_INSENSITIVE));
		templates.put("WhoVerbXX",
				Pattern.compile(whoVerbXX, Pattern.CASE_INSENSITIVE));
	}

	/** Insert a space among word **/
	public static String pattern(String... args) {
		StringBuffer sb = new StringBuffer();
		for (String arg : args)
			sb.append(arg + " ");
		return sb.toString().trim();
	}

	/** Proxy of String.format */
	public static String str(String format, Object... args) {
		return String.format(format, args);
	}

	public static String named(String name, String format, Object... args) {
		return str("(?<%s>%s)", name, str(format, args));

	}

	/**
	 * toy demo for movie data.
	 * 
	 * who is the director of the godfather?
	 * 
	 * who directed the godfather?
	 * 
	 * who starred in the godfather?
	 * 
	 * */

	static String whoVerbXX = String.format("^%s (?<V>%s( %s)*) (?<N>%s)$",
			WHO, V, PRE, NNN);

	public Triples WhoVerbXX(Matcher m) {
		Triples ts = new Triples();
		String V = pretty(m.group("V"));
		String N = pretty(m.group("N"));
		ts.push(ts.random(), V, N);
		System.out.println(ts.toString());
		return ts;
	}

	static String whatNDoNVPre = String
			.format("^(%s|%s) (?<N1>%s) %s (?<N2>%s) (?<DO>%s( %s)?)( (?<INX>%s %s))?$",
					WDT, WHO, NNN, DO, NNN, V, PRE, PRE, NNN);

	/**
	 * @e.g what A does B do in
	 * 
	 * @e.g what A does B do
	 * 
	 * @e.g what A does B do in C
	 */
	public Triples WhatNDoNVPre(Matcher m) {
		String N1 = pretty(m.group("N1"));
		String DO = pretty(m.group("DO"));
		String N2 = pretty(m.group("N2"));
		String INX = pretty(m.group("INX"));
		if (INX != null)
			N1 += " " + INX;
		Triples ts = new Triples();
		ts.push(N2, DO, ts.random());
		ts.push(ts.last(), "is-a", N1);
		print(ts.toString());
		return ts;
	}

	// static String whatIsNDoneInN = pattern("^" + WP, BE, str("(?<N>%s)",
	// NNN),
	// str("(?<P>%s %s)", V, PRE) + str("( (?<S>%s))?$", PRENNN));

	static String whatIsNDoneInN = String.format(
			"^(%s|%s) %s (?<N>%s) (?<P>%s %s)( (?<S>%s))?$", WP, WHO, BE, NNN,
			V, PRE, PRENNN);

	/*
	 * @e.g what is A Done by B (in C)?
	 */
	public Triples WhatIsNDoneInN(Matcher m) {
		String N1 = pretty(m.group("N"));
		String P = "be " + pretty(m.group("P"));
		String S = pretty(m.group("S"));
		if (S != null)
			N1 += " " + S;
		Triples ts = new Triples();
		ts.push(N1, P, ts.random());
		System.out.println(ts.toString());
		return ts;
	}

	static void print(String arg) {
		System.out.println(arg);
	}

	// static String whatIsNOfN = pattern("^" + WP, BE, str("(?<N1>%s)", NAN),
	// str("(?<P>%s|%s|%s)", IN, POS, DONEBY), str("(?<N2>%s)$", NNN));
	static String whatIsNOfN = String.format(
			"^(%s|%s) %s (?<N1>%s)( (?<P>%s|%s|%s) (?<N2>%s))?$", WP, WHO, BE,
			NAN, IN, POS, DONEBY, NNN);

	/*
	 * @e.g what is A
	 * 
	 * @e.g what is A of B's C for D ...
	 * 
	 * @e.g what is A done by C of D ...
	 * 
	 * @TODO what is A in B ...
	 */
	public Triples WhatIsNOfN(Matcher m) {
		String N1 = pretty(m.group("N1"));
		String N2 = pretty(m.group("N2"));
		String P = m.group("P");
		Triples ts = new Triples();
		if (P == null) {
			ts.push(N1, "is", ts.random());

		} else if (Pattern.matches(IN, P))
			ts.push(N2, N1, ts.random());
		else if (Pattern.matches(POS, P))
			ts.push(N1, N2, ts.random());
		else
			ts.push(pattern(N1, P, N2), "is-a", ts.random());
		print(ts.toString());
		return ts;

	}

	// static String whatIsX = String.format("^%s %s %s$", WDT, BE, NAN);
	static String whatXIsX = String.format(
			"^(%s|%s) (?<N1>%s) %s (?<N2>%s)( (?<N3>%s))?$", WDT, WHO, NNN, BE,
			NNN, RELATION);

	/*
	 * @e.g what A(type) is B
	 * 
	 * @e.g what A is B in C
	 */
	private Triples WhatXIsX(Matcher m) {
		// System.out.println("What X Is X\n");
		String N1 = m.group("N1");
		String N2 = m.group("N2");
		String N3 = m.group("N3");

		Triples ts = new Triples();
		if (N3 != null) {
			String r = ts.random();
			ts.push(pretty(N2), "be " + pretty(N3), r);
			ts.push(r, "sub-class-of", pretty(N1));

		} else {
			String r = ts.random();
			ts.push(pretty(N2), "is-a", r);
			ts.push(r, "sub-class-of", pretty(N1));

		}
		System.out.println(ts);
		return ts;

	}

	private String whatXIsInX = String.format(
			"^(%s|%s) (?<N1>%s) %s (?<IN>%s) (?<N2>%s)$", WDT, WHO, NNN, BE,
			IN, NNN);

	// private String whatXIsInX = pattern("^" + WDT, named("N1", NNN), BE,
	// named("IN", IN), named("N2", NNN) + "$");

	/*
	 * @e.g what A is in B
	 */
	public Triples WhatXIsInX(Matcher m) {
		String N1 = pretty(m.group("N1"));
		String IN = pretty(m.group("IN"));
		String N2 = pretty(m.group("N2"));
		Triples ts = new Triples();

		ts.push(N2, N1, ts.random());
		System.out.println(ts);
		return ts;
	}

	public String whatXBeJJToX = String.format(
			"^(%s|%s) (?<N1>%s) %s (?<P>%s|%s) (?<N2>%s)$", WDT, WHO, NNN, BE,
			DONEBY, JJPRE, NNN);

	/*
	 * @e.g what A be done by B
	 * 
	 * @e.g what A be JJ to B
	 */
	public Triples WhatXBeJJToX(Matcher m) {

		// System.out.println("What X Passive X\n");
		String N1 = pretty(m.group("N1"));
		String P = pretty(m.group("P"));
		String N2 = pretty(m.group("N2"));
		Triples ts = new Triples();
		ts.push(ts.random(), "be " + P, N2);
		ts.push(ts.last(), "sub-class-of", N1);

		System.out.println(ts);
		return ts;
	}

	public String pretty(String text) {
		if (text == null)
			return null;
		String[] words = text.split(" ");
		StringBuffer sb = new StringBuffer();
		Arrays.asList(words).forEach((word) -> {
			sb.append(word.split("/")[0] + " ");
		});

		return sb.toString().trim();
	}

	public Triples decomposition(Triples ts) {
		ArrayList<Triple> tmp = new ArrayList<Triple>();
		for (int i = 0; i < ts.store.size(); i++) {
			tmp.add(ts.store.get(i));
		}
		for (Triple t : tmp) {
			int idx = t.Object.toLowerCase().indexOf("of");
			if (idx >= 0) {

				String A = t.Object.substring(0, idx - 1);
				String B = t.Object.substring(idx + 2);
				ts.push(t.Subject, t.Predicate, B);
				ts.push(t.Subject, A, ts.random());
				ts.pop(t.Subject, t.Predicate, t.Object);
			}
		}
		return ts;
	}

	private Triples dispatch(String text) {
		System.out.println(text.trim());
		Triples ts = new Triples();
		// this.templates.forEach((funcName, p) -> {
		Iterator iter = templates.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String funcName = (String) entry.getKey();
			Pattern p = (Pattern) entry.getValue();
			Matcher m = p.matcher(text.trim());
			if (m.find()) {
				try {
					Method method = Question2Triples.class.getDeclaredMethod(
							funcName, new Class[] { Matcher.class });
					method.setAccessible(true);
					ts = (Triples) method.invoke(this, new Object[] { m });
					/* reflect public function */
					// ts = (Triples) (this.getClass().getMethod(funcName,
					// new Class[] { Matcher.class }).invoke(this,
					// new Object[] { m }));

					return ts;
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}
		return ts;
		// });

	}

	public Triples NLPos2Triples(String question) {
		return decomposition(dispatch(question));
	}

	public static void main(String args[]) {

		StanfordProcessor sp = new StanfordProcessor();
		Question2Triples q2t = new Question2Triples();
		String[] ss = new String[] {
				"Who/WP directed/VBD the/DT Sin/NNP City/NNP",
				"Who/WP is/VBZ the/DT director/NN of/IN the/DT Sin/NNP City/NNP",
				"Who/WP was/VBD the/DT newscaster/NN in/IN 1948/CD on/IN cbs/NNS evening/NN news/NN",
				"what/WDT type/NN of/IN tea/NN is/VBZ gunpowder/NN tea/NN",
				"what/WDT issue/NN of/IN sandman/NN is/VBZ a/DT dream/NN of/IN a/DT thousand/CD cats/NNS",
				"what/WDT league/NN is/VBZ real/JJ madrid/NN a/DT member/NN of/IN",
				"what/WDT genre/NN is/VBZ doonesbury/NN",
				"what/WDT type/NN of/IN beer/NN is/VBZ miller/JJ lite/NN",
				"what/WDT year/NN was/VBD danny/JJ devito/JJ born/VBN",
				"what/WDT ingredients/NNS are/VBP in/IN italian/JJ cuisine/NN",
				"what/WDT languages/NNS are/VBP spoken/VBN in/IN firefly/NN",
				"what/WDT artists/NNS are/VBP influenced/VBN by/IN lead/NN belly/NN",
				"what/WDT type/NN of/IN infection/NN is/VBZ syphilis/NN",
				"what/WDT type/NN of/IN clothing/NN are/VBP knickerbockers/NNS",
				"what/WDT blogs/NNS are/VBP in/IN german/NN ",
				"what/WDT topics/NNS are/VBP equivalent/JJ to/TO us/PRP county/NN",
				"what/WDT airlines/NNS is/VBZ the/DT london/NN heathrow/NN airport/NN a/DT hub/NN for/IN",

				"what/WP is/VBZ the/DT hull/NN made/VBN of/IN on/IN the/DT james/NNS craig/NN",
				"what/WP are/VBP the/DT file/NN formats/NNS supported/VBN by/IN the/DT iphone/NN",
				"what/WP are/VBP the/DT deities/NNS of/IN hinduism/NN",
				"what/WP are/VBP the/DT latitude/NN and/CC longitude/NN of/IN the/DT eiffel/NN tower/NN",
				"what/WP is/VBZ angelina/JJ jolie/NN 's/POS net/JJ worth/NN",
				"what/WP is/VBZ the/DT average/JJ temperature/NN in/IN sydney/NN in/IN august/NNP",
				"what/WP is/VBZ the/DT transmission/NN of/IN a/DT 2011/CD honda/NN fit/NN",
				"what/WP is/VBZ the/DT orbital/JJ period/NN of/IN the/DT moon/NN ",
				"what/WP is/VBZ the/DT address/NN of/IN the/DT mitchell/JJ public/JJ library/NN ",
				"what/WP is/VBZ the/DT motto/NN of/IN the/DT order/NN of/IN the/DT golden/JJ fleece/NN",
				"what/WP is/VBZ the/DT retail/JJ floor/NN space/NN of/IN the/DT dubai/NN marina/NN mall/NN",
				"what/WP is/VBZ the/DT symbol/NN for/IN yen/NN",
				"what/WP is/VBZ the/DT origin/NN of/IN the/DT mississippi/NN river/NN",
				"what/WP is/VBZ the/DT release/NN date/NN for/IN 2/CD fast/JJ 2/CD furious/JJ",
				"what/WP is/VBZ serge/NN made/VBN out/IN of/IN ",
				"what/WDT buildings/NNS were/VBD destroyed/VBN in/IN september/NNP 11th/JJ",
				"what/WP are/VBP some/DT gato/NN class/NN submarines/NNS",
				"what/WP is/VBZ the/DT capacity/NN of/IN a/DT rolls/NNS royce/VBP merlin/NN",

				"what/WP is/VBZ ron/NN glass/NN 's/POS religion/NN",
				"what/WP is/VBZ the/DT cape/NN may/MD lighthouse/NNP made/VBD of/IN",
				"what/WP was/VBD roe/NN v./CC wade/VB about/IN",
				"what/WDT position/NN does/VBZ craig/NNP adams/NNS play/VBP",
				"what/WDT sizes/NNS does/VBZ pilsner/NN urquell/NN come/VBN in/IN",
				"what/WDT languages/NNS has/VBZ microsoft/VBN designed/VBN",
				"what/WDT languages/NNS has/VBZ microsoft/NN designed/VBN",
				"what/WDT works/NNS did/VBD anthony/NNP payne/NN finish/NN ",
				"what/WDT works/NNS did/VBD anthony/NNP payne/NN finish/VBP ",
				"what/WDT conferences/NNS does/VBZ google/NN sponsor/NN",
				"what/WDT conferences/NNS does/VBZ google/NN sponsor/VBP",
				"what/WDT lines/NNS does/VBZ the/DT london/JJ overground/JJ operate/VBP",
				"what/WDT qualification/NN does/VBZ woody/VB strode/VBN have/VBP in/IN seishindo/NN kenpo/NN",
				"what/WDT powers/NNS does/VBZ sonic/VB the/DT hedgehog/NN have/VBP",
				"what/WDT area/NN does/VBZ kifm/VB serve/VB",
				"what/WDT genres/NNS does/VBZ meet/VB the/DT parents/NNS consist/VBP of/IN" };
		// Arrays.asList(ss).forEach((sss -> {
		// q2t.NL2Triple(question)(sss);
		// }));
		Triples2Sparql t2s = new Triples2Sparql();
		for (String sss : ss) {
			Triples ts = q2t.NLPos2Triples(sss);
			String arq = t2s.toSparQL(ts);
			// System.out.println(arq);

		}
		// q2t.testName();
		// q2t.testPre();
		// q2t.testBE();
	}
}
