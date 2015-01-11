import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Triples {
	public static enum OPT {
		SELECT
	}

	private ArrayList<Triple> store;
	private int count; // numble of varibles
	public OPT mode;
	private Map<String, String> prefixes;
	static public final String NL = System.getProperty("line.separator");

	public Triples() {
		store = new ArrayList<Triple>();
		count = 0;
		prefixes = new HashMap<String, String>();
	}

	public ArrayList<String> getVaribles() {

		ArrayList<String> arr = new ArrayList<String>();
		for (int i = 1; i <= count; i++)
			arr.add("?x" + i);
		return arr;
	}

	public void setPrefix(String key, String value) {
		prefixes.put(key, value);
	}

	public String random() {
		return "?x" + ++count;
	}

	public String toSparQL() {
		this.decomposition();
		ArrayList<String> vars = getVaribles();
		StringBuffer sb = new StringBuffer();
		String key, val;
		if (!prefixes.isEmpty()) {

			Iterator iter = prefixes.entrySet().iterator();
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
			for (Triple t : store) {
				sb.append(t.toFlat() + NL);
			}
			sb.append("}");
		}
		return sb.toString();
	}

	public String last() {
		return "?x" + (count);
	}

	public void push(String subject, String predicate, String object) {
		this.pop(subject, predicate, object);
		store.add(new Triple(subject, predicate, object));
	}

	public Triple get(int i) {
		return store.get(i);
	}

	public void pop(String s, String p, String o) {

		int len = store.size();
		for (int i = 0; i < len; i++) {
			if (store.get(i).equals(s, p, o)) {
				store.remove(i);
				break;
			}
		}
	}

	public void decomposition() {
		ArrayList<Triple> tmp = new ArrayList<Triple>();
		for (int i = 0; i < store.size(); i++) {
			tmp.add(store.get(i));
		}
		for (Triple t : tmp) {
			int idx = t.Object.toLowerCase().indexOf("of");
			if (idx >= 0) {

				String A = t.Object.substring(0, idx - 1);
				String B = t.Object.substring(idx + 2);
				this.push(t.Subject, t.Predicate, B);
				this.push(t.Subject, A, this.random());
				this.pop(t.Subject, t.Predicate, t.Object);
			}
		}
	}

	public int Count() {
		return count;
	}

	public int Size() {
		return store.size();
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		int len = store.size();
		for (int i = 0; i < len; i++) {
			if (i > 0)
				s.append(" ");
			s.append(store.get(i));
		}
		return s.toString();
	}

	// public static void main(String args[]) {
	// Triples triples = new Triples();
	// triples.push("csn", "is", "student");
	// triples.push("buaa", "is", "school");
	// System.out.println(triples);
	// triples.pop("csn", "is", "student");
	// System.out.println(triples);
	// }
}

class Triple {
	public String Subject;
	public String Predicate;
	public String Object;

	public Triple() {
		this.Subject = "";
		this.Predicate = "";
		this.Object = "";
	}

	public Triple(String s, String p, String o) {
		this.Subject = s;
		this.Predicate = p;
		this.Object = o;
	}

	public boolean equals(String s, String p, String o) {
		if (this.Object.equals(o) && this.Predicate.equals(p)
				&& this.Subject.equals(s))
			return true;
		return false;
	}

	public boolean equals(Triple t) {
		if (this.Object.equals(t.Subject) && this.Predicate.equals(t.Predicate)
				&& this.Subject.equals(t.Object))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return String.format("(%s,%s,%s)", this.Subject, this.Predicate,
				this.Object);
	}

	public String toFlat() {
		if (!Subject.startsWith("?")) {
			Subject = "\"" + Subject + "\"";
		}
		if (!Object.startsWith("?")) {
			Object = "\"" + Object + "\"";
		}
		return String.format("%s %s %s.", Subject, Predicate, Object);
	}

}