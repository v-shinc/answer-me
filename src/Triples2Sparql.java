import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Triples2Sparql {
	static public final String NL = System.getProperty("line.separator");

	public Triples alignment(Triples ts, RDFManager rm) {
		int idx = -1;
		for (int i = 0; i < ts.Size(); i++)
			if (ts.store.get(i).Predicate.equals("is-a")) {
				idx = i;
				break;
			}
		if (idx > -1)
			ts.pop(idx);
		for (Triple t : ts.store) {
			if (!t.Subject.startsWith("?")) {
				t.Subject = rm.SearchByEditDistance(t.Subject, Role.RESOURCE)
						.get(0);
			}

			if (!t.Object.startsWith("?")) {
				ArrayList<String> candidates = rm.SearchByEditDistance(
						t.Object, Role.RESOURCE);
				t.Object = candidates.get(0);
				// candidates.forEach(s -> System.out.println(s));
			}
			if (!(t.Predicate.startsWith("?"))) {
				t.Predicate = rm.SearchByEditDistance(t.Predicate,
						Role.PREDICATE).get(0);
			}

		}
		return ts;
	}

	public String toSparQL(Triples ts, int order) {

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
			for (int i = 0; i < ts.Size(); i++) {
				Triple t = ts.store.get(i);

				if (order % 2 == 1) {

					String tmp = t.Subject;
					t.Subject = t.Object;
					t.Object = tmp;
					// ts.store.set(i, t);
					sb.append(t.toFlat() + NL);
				} else {
					sb.append(t.toFlat() + NL);
				}

				order /= 2;
			}

			sb.append("}");
		}
		return sb.toString();
	}
}
