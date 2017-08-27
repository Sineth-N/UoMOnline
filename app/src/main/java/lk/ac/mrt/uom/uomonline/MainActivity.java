package lk.ac.mrt.uom.uomonline;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import lk.ac.mrt.uom.uomonline.adapters.MainRVAdapter;
import lk.ac.mrt.uom.uomonline.model.ArticleMinified;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce= false;
    private List<ArticleMinified> articleMinifieds = new ArrayList<>();
    private LinkedHashMap<String,ArticleMinified> linkedHashMap = new LinkedHashMap<>(10,0.5f);
    MainRVAdapter mainRVAdapter;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (database == null){
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("articles");
        }
        myRef.keepSynced(true);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("articles");
        ref.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Toast.makeText(MainActivity.this,dataSnapshot.getValue(ArticleMinified.class).getImageURL(),Toast.LENGTH_SHORT).show();
                ArticleMinified articleMinified = new ArticleMinified(
                        dataSnapshot.getValue(ArticleMinified.class).getTitle(),
                        dataSnapshot.getValue(ArticleMinified.class).getImageURL(),
                        dataSnapshot.getValue(ArticleMinified.class).getId()
                );

                linkedHashMap.put(articleMinified.getId(),articleMinified);
                if (!articleMinifieds.contains(articleMinified)){
                    articleMinifieds.add(articleMinified);
                }
                mainRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ArticleMinified article = dataSnapshot.getValue(ArticleMinified.class);
                linkedHashMap.put(article.getId(),article);
                mainRVAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"Updated the "+ article.getImageURL(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ArticleMinified article = dataSnapshot.getValue(ArticleMinified.class);
                linkedHashMap.remove(article.getId());
                mainRVAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this," Child Removed " + dataSnapshot.getValue(ArticleMinified.class).getTitle(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = myRef.push().getKey();
                ArticleMinified articleMinified = new ArticleMinified("Sineth","https://firebasestorage.googleapis.com/v0/b/uomonline-1bc39.appspot.com/o/articleImages%2Fsparks-1.jpg?alt=media&token=2c4489e4-a08f-42f3-92ae-4c98b651bf7b",key);
                Task<Void> task = myRef.child(key).setValue(articleMinified);
                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this,"Successfully SET",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
//        linearLayout.setReverseLayout(true);
//        linearLayout.setStackFromEnd(true);
        RecyclerView rv = (RecyclerView) findViewById(R.id.mainRV);
        rv.setLayoutManager(linearLayout);
        mainRVAdapter = new MainRVAdapter(linkedHashMap,MainActivity.this);
        rv.setAdapter(mainRVAdapter);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
