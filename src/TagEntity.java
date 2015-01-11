import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TagEntity {
	private static String EntityAPI = "http://access.alchemyapi.com/calls/text/TextGetRankedNamedEntities";
	private String apikey = "0785fe7b9f07e4a19ab22a9a4436c7f3a521151c";

	public JSONArray TagEntity(JSONArray jsonArray, String key) {

		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = (JSONObject) jsonArray.get(i);
				String text = (String) obj.get(key);
				String Res = TagOne(text);
				if (Res == null)
					continue;

				JSONObject jsonRes = new JSONObject(Res);
				jsonRes.remove("usage");
				jsonRes.remove("Use");
				jsonRes.remove("url");
				jsonRes.remove("status");
				jsonRes.remove("totalTransactions");
				obj.put("entities", jsonRes);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	public String TagOne(String query) {
		HttpURLConnection connection = null;
		String APIUrl = "http://tagme.di.unipi.it/tag";

		String relationApi = "http://access.alchemyapi.com/calls/text/TextGetRelations";
		// String query = "Who developed the video game World of Warcraft";

		String urlParameters = "apikey=" + apikey
				+ "&knowledgeGraph=1&outputMode=json&text=" + query;
		StringBuffer response = null;
		try {
			URL url = new URL(EntityAPI);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (response != null)
			return response.toString();
		return null;
	}

	public static void main(String[] arg) {

	}
}