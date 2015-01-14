import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Triples {
	public static enum OPT {
		SELECT
	}

	public ArrayList<Triple> store;
	private int count; // numble of varibles
	public OPT mode;
	public Map<String, String> prefixes;
	static public final String NL = System.getProperty("line.separator");

	public Triples() {
		store = new ArrayList<Triple>();
		count = 0;
		prefixes = new HashMap<String, String>();
	}

	public boolean isEmpty() {
		return store.isEmpty();
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

	public void pop(int idx) {
		store.remove(idx);
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

	public boolean isResource(String s) {
		if (s.contains("http://"))
			return true;
		return false;
	}

	public String wrap(String s) {
		if (s.startsWith("?"))
			return s;
		if (isResource(s)) {
			if (!s.startsWith("<"))
				return "<" + s + ">";
			return s;
		} else {
			if (!s.startsWith("\"")) {
				s = "\"" + s + "\"";
			}
			return s;
		}

	}

	public String toFlat() {
		Subject = wrap(Subject);
		Object = wrap(Object);
		Predicate = wrap(Predicate);

		return String.format("%s %s %s .", Subject, Predicate, Object);
	}

}