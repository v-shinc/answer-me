import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class MyReader {
	@SuppressWarnings("finally")
	public ArrayList<String> readFile(String pathname) {
		ArrayList<String> data = new ArrayList<String>();
		File filename = new File(pathname);
		try {
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename));
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			line = br.readLine();
			while (line != null) {
				line = br.readLine();
				data.add(line);
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			return data;
		}
	}

	public String File2String(String filepath) {
		File filename = new File(filepath);
		StringBuffer bs = new StringBuffer();
		try {
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename));

			BufferedReader br = new BufferedReader(reader);
			String line = "";
			line = br.readLine();
			while (line != null) {
				line = br.readLine();
				bs.append(line);
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			return bs.toString();
		}
	}

	@SuppressWarnings("finally")
	public JSONArray File2Json(String filepath) {
		JSONArray array = null;

		try {
			array = new JSONArray(new JSONTokener(new FileReader(new File(
					filepath))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			return array;
		}
	}

	public List<String> ReadByLine(String filepath) {
		File filename = new File(filepath);
		List<String> list = new ArrayList<String>();
		try {
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename));

			BufferedReader br = new BufferedReader(reader);
			String line = "";
			line = br.readLine();
			while (line != null) {
				line = br.readLine();
				list.add(line);
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			return list;
		}
	}
}
