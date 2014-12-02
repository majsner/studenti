package kit.pef.studenti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import org.json.JSONObject;
import org.json.JSONException;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends ActionBarActivity {
    private static final String URL =
            "http://www.csita.cz/sklad/studenti.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getString(R.string.loading), Toast.LENGTH_SHORT).show();
                new DownloadWebpageTask().execute(URL);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }


    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
//            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is, 2000000);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    private void parseJson(String result) {
        JSONObject jsonObject;
        ArrayList<Student> data = new ArrayList<Student>();
        JSONArray jarray = new JSONArray();
        try {
            jsonObject = new JSONObject(result);
//            System.out.println(jsonObject);
            jarray = jsonObject.getJSONArray("studenti");
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "Chyba načítání JSON", Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject object = jarray.getJSONObject(i);
                Student student = new Student();
                student.setJmeno(object.getString("jmeno"));
                student.setPrijmeni(object.getString("prijmeni"));
                student.setId(Integer.valueOf(object.getString("id")));
                data.add(student);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        db db = new db(this);
        // jelikož nám zbyli nejspíš studenti z minula
        db.getWritableDatabase().delete("studenti",null,null);

        db.insertStudenti(data);


        // podle čeho řadíme?
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String by = sharedPref.getString("radit_list", "jmeno");
//        Ukládáme do db?
        Boolean save = sharedPref.getBoolean("db_save", false);

        // setřídíme/vybereme setříděná data
        ArrayList<Student> dataOrdered; //= new ArrayList<Student>(); --> pokud byste chtěli kolekci sami plnit (dataOrdered.add...)
        if (!save) dataOrdered = order(data,by);
        else {
            dataOrdered = db.getStudenti(by);
        }
        db.close();// neplýtváme prostředky
        StudentiAdapeter adapter = new StudentiAdapeter(this, dataOrdered);

        ListView v = (ListView) findViewById(R.id.listView);

        v.setAdapter(adapter);

    }

    private ArrayList<Student> order(ArrayList<Student> data,final String by) {
        //Sorting
        Collections.sort(data, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                if (by.equals("jmeno")) return s1.getJmeno().compareTo(s2.getJmeno());
                else if (by.equals("prijmeni")) return s1.getPrijmeni().compareTo(s2.getPrijmeni());
                else /*if (by.equals("id"))*/ return (s1.getId() - s2.getId());
            }
        });
        return data;
    }


    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            parseJson(result);
//            TextView myTextView = (TextView) findViewById(R.id.textView);
//            myTextView.setText(result);

        }
    }

}
