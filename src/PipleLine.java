import org.json.JSONArray;
import org.json.JSONException;

public class PipleLine {
	static String free917 = "D:\\Source Code\\java\\AnswerMe\\data\\free917\\free917.train.examples.canonicalized.json";

	public static void main(String arg[]) {
		MyReader mr = new MyReader();
		MyWriter mw = new MyWriter();
		TagEntity te = new TagEntity();
		JSONArray array = mr.File2Json(free917);
		StanfordProcessor spro = new StanfordProcessor();
		try {
			spro.AnnotateJSONArray(array, "utterance",
					StanfordProcessor.Operation.POS, "pos:pos,parse:parse");
			// te.TagEntity(array, "utterance");
			mw.WriteJsonArray2File(array, "./data/free917pos.txt");

			// for (int i = 0; i < array.length(); i++) {
			//
			// System.out.println(((JSONObject) array.get(i))
			// .getString("utterance"));
			// System.out
			// .println(((JSONObject) array.get(i)).getString("pos"));
			// System.out.println(((JSONObject) array.get(i))
			// .getString("parse"));
			//
			// }
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
