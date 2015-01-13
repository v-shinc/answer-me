import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Triples2Sparql {
	static public final String NL = System.getProperty("line.separator");

	public Triples alignment(Triples ts, RDFManager rm) {
		for (Triple t : ts.store) {
			if (!t.Subject.startsWith("?")) {
				t.Subject = rm.SearchByEditDistance(t.Subject, Role.SUBJECT)
						.get(0);
			}

			if (!t.Object.startsWith("?")) {
				ArrayList<String> candidates = rm.SearchByEditDistance(
						t.Object, Role.OBJECT);
				t.Object = candidates.get(0);
			}

		}
		return ts;
	}

	public String toSparQL(Triples ts) {

		ArrayList<String> vars = ts.getVaribles();
		StringBuffer sb = new StringBuffer();
		String key, val;
		if (!ts.prefixes.isEmpty()) {

			Iterator iter = ts.prefixes.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				key = (String) entry.getKey();
				val = (String) entry.getValue();
				sb.append("PREFIX " + key + " : " + val + NL);
			}
		}
		if (vars.size() > 0) {
			sb.append("SELECT");
			for (String v : vars) {
				sb.append(" " + v);
			}
			sb.append(NL + "WHERE{" + NL);
			for (Triple t : ts.store) {
				sb.append(t.toFlat() + NL);
			}
			sb.append("}");
		}
		return sb.toString();
	}
}
