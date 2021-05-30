package com.olaapp;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
   
    SearchView searchView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    RequestQueue queue;
    GridLayoutManager lLayout;
    private ArrayList<Album> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        list = new ArrayList<>();
        albumAdapter = new AlbumAdapter(this, list);
        lLayout = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(lLayout);
        recyclerView.setAdapter(albumAdapter);

        queue = AppController.getInstance().getRequestQueue();

        savatjson();

    }

    public void savatjson() {
        String url = "https://pastebin.com/raw/rXdF4NwK";

        JsonArrayRequest ssa = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Album movie = new Album(obj.getString("song"), obj.getString("url"),
                                        obj.getString("artists"), obj.getString("cover_image"));
										
                                list.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }  //กูงงกับตรงนี้แหละ

                        albumAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());


            }
        });

        queue.add(ssa);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("ค้นหา เช่น เพื่อชีวิต สตริง");
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Album> arrayListp = new ArrayList<>();
        for (Album album : list) {
            String name = album.getSong().toLowerCase();
            if (name.contains(newText))
                arrayListp.add(album);
        }
        albumAdapter.setfilter(arrayListp);
        return true;
    }
}
