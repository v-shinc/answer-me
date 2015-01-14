import java.io.FileNotFoundException;

public class ToyExample {
	public static String knowledgeBase = "";
	public RDFManager rm = null;

	public ToyExample(String kb) {
		String knowledgeBase = kb;
		rm = new RDFManager(knowledgeBase);
	}

	public static void main(String args[]) throws FileNotFoundException {
		// some definitions

		// rc.createModel();

		// rm.SearchByEditDistance("Gods and Kings", Role.SUBJECT).forEach(
		// s -> System.out.println(s));

		// rc.editDistance("gambol", "gumbo");
		// rc.mainModel = rc.readRDF("data/imdb.ttl");
		// rc.query(rc.mainModel);
		ToyExample te = new ToyExample("data/movie.ttl");
		te.NLQuery("who directed The Hunger Games Mockingjay");

		// rc.NLQuery("Who is the director of the Sin City");
		te.NLQuery("What film did Brie Larson played in");
		te.NLQuery("who are the actors of Wild");
		te.NLQuery("Who acts in the Wild");
		te.NLQuery("Who starred in the Night at the Museum");
		te.NLQuery("Who played in the Taken3");
		te.NLQuery("What kind of film did Brie Larson played in");
		// te.NLQuery("who lived in fallingwater");
		// te.NLQuery("Who was the newscaster in 1948 on cbs evening news");
		// rc.NLQuery("who is the music by in bruce almighty");
		// rc.NLQuery("who was the casting director for meet the parents");
	}

	public String NLQuery(String qestion) {
		StanfordProcessor sp = new StanfordProcessor();
		String posq = sp.POSString(qestion);
		Question2Triples q2t = new Question2Triples();
		Triples ts = q2t.NLPos2Triples(posq);
		if (ts.isEmpty()) {
			System.out.println("can not parse");
			return "can not parse";
		}
		Triples2Sparql t2s = new Triples2Sparql();

		ts = t2s.alignment(ts, rm);
		System.out.println(ts.toString());
		int sz = ts.Size();
		for (int i = 0; i < Math.pow(2, sz); i++) {
			String query = t2s.toSparQL(ts, i);

			rm.executeQuery(query);

		}

		return "";

	}
}
