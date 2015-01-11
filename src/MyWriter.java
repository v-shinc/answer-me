import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;

public class MyWriter {
	public void WriteJsonArray2File(JSONArray jsonarray, String filepath) {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileOutputStream(filepath));
			out.println(jsonarray.toString(4));
		} catch (FileNotFoundException e) {
			System.out.println("error in writting json array to file");
			e.printStackTrace();
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	public void WriteString2File(String text, String filepath) {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileOutputStream(filepath));
			out.println(text);
		} catch (FileNotFoundException e) {
			System.out.println("error in writting string to file");
			e.printStackTrace();
		}

	}
}
