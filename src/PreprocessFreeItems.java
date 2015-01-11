import java.util.List;
import java.util.regex.Pattern;

public class PreprocessFreeItems {
	public final static String FILE_PATH = "D:\\Study\\kb\\xaa";
	public final static String OUT_PATH = "D:\\Study\\kb\\xaa_1";
	public final static String REGEX = "http://([a-zA-Z_.0-9]+/)+";
	public final static Pattern URL_PATTERN = Pattern.compile(REGEX);
	public static MyReader mr = new MyReader();
	public static MyWriter mw = new MyWriter();

	public String removeURL(String text) {
		if (text != null)
			return text.replaceAll(REGEX, "");
		return "";
	}

	public void run() {
		mr = new MyReader();
		List<String> lines = mr.ReadByLine(FILE_PATH);
		int len = lines.size();
		// System.out.println(lines.get(0));
		// System.out.println(lines.get(0).replaceAll(REGEX, ""));
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append(removeURL(lines.get(i)) + "\n");
		}

		mw.WriteString2File(sb.toString(), OUT_PATH);
	}

	public static void main(String args[]) {

		PreprocessFreeItems pfi = new PreprocessFreeItems();
		pfi.run();
	}
}
