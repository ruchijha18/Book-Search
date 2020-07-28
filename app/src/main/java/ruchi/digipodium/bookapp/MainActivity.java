package ruchi.digipodium.bookapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private Button btnSearch;
    private TextView textBookName;
    private TextView textAuthor;
    private EditText editBookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSearch=findViewById(R.id.btnSearch);
        textBookName = findViewById(R.id.textBookName);
        textAuthor = findViewById(R.id.textAuthorName);
        editBookName = findViewById(R.id.editBookName);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBooks();
            }
        });
    }
    public void searchBooks(){
        String queryString = editBookName.getText().toString();
        new FetchBook(textAuthor,textBookName).execute(queryString);


    }
    public class FetchBook extends AsyncTask<String, Void, String>{
        private WeakReference<TextView>textTitle;
        private WeakReference<TextView>textAuthor;

        public FetchBook(TextView textTitle, TextView textAuthor) {
            this.textTitle=new WeakReference<>(textTitle);
            this.textAuthor=new WeakReference<>(textAuthor);

        }

        @Override
        protected String doInBackground(String... query) {
            return NetworkUtils.getBookInfo(query[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                JSONObject jsonObject= new JSONObject(result);
            JSONArray itemsArray=jsonObject.getJSONArray("items");
            int i=0;
            String title=null;
            String authors=null;

            while (i<itemsArray.length() && (authors==null && title == null)){
                JSONObject book=itemsArray.getJSONObject(i);
                JSONObject volume= book.getJSONObject("volumeInfo");
                try {
                     title=volume.getString("title");
                     authors=volume.getString("authors");
                }catch (Exception e){
                          e.printStackTrace();
                }
                i++;
            }
            if(title!=null && authors!=null)
            {
                textTitle.get().setText(title);
                textAuthor.get().setText(authors);
            }
            else {
                textTitle.get().setText("Unknown");
                textAuthor.get().setText("No result found");
            }
            }catch (Exception e)
            {
                e.printStackTrace();
                textTitle.get().setText("API ERROR");
                textAuthor.get().setText("Please check Logcat");
            }
        }
    }

}