package lk.ac.mrt.uom.uomonline;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lk.ac.mrt.uom.uomonline.adapters.MainRVAdapter;
import lk.ac.mrt.uom.uomonline.firebase.FirebaseAuthenticationService;
import lk.ac.mrt.uom.uomonline.model.Article;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce= false;
    private List<Article> articles = new ArrayList<>();
    private LinkedHashMap<String,Article> linkedHashMap = new LinkedHashMap<>(10,0.5f);
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
//                Toast.makeText(MainActivity.this,dataSnapshot.getValue(Article.class).getImageURL(),Toast.LENGTH_SHORT).show();
                Article article = new Article(
                        dataSnapshot.getValue(Article.class).getTitle(),
                        dataSnapshot.getValue(Article.class).getImageURL(),
                        dataSnapshot.getValue(Article.class).getId(),
                        dataSnapshot.getValue(Article.class).getTagLine(),
                        dataSnapshot.getValue(Article.class).getStory()

                );

                linkedHashMap.put(article.getId(), article);
                if (!articles.contains(article)){
                    articles.add(article);
                }
                mainRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Article article = dataSnapshot.getValue(Article.class);
                linkedHashMap.put(article.getId(),article);
                mainRVAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"Updated the "+ article.getImageURL(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                linkedHashMap.remove(article.getId());
                mainRVAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this," Child Removed " + dataSnapshot.getValue(Article.class).getTitle(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        final RecyclerView rv = (RecyclerView) findViewById(R.id.mainRV);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.smoothScrollToPosition(0);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        linearLayout.setReverseLayout(true);
//        linearLayout.setStackFromEnd(true);

        rv.setLayoutManager(linearLayout);
        mainRVAdapter = new MainRVAdapter(linkedHashMap,MainActivity.this);
        rv.setAdapter(mainRVAdapter);
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstCompletelyVisibleItemPosition = linearLayout.findFirstVisibleItemPosition();
                if (firstCompletelyVisibleItemPosition >= 1 ){
                    fab.show();
                }else {
                    fab.hide();
                }
            }
        });

//        String key = myRef.push().getKey();
//        Article article = new Article.ArticleBuilder().setid(key)
//                .setimageURL("https://firebasestorage.googleapis.com/v0/b/uomonline-1bc39.appspot.com/o/articleImages%2FDAAD%201_0.jpg?alt=media&token=8040f101-7c43-4b5c-b80d-3f9af855ecd9")
//                .setTitle("Delegates from Lahore University of Management Sciences (LUMS), Pakistan visit University of Moratuwa")
//                .setstory("Dr. Jawad Syed, the Dean & Professor of Suleman Dawood School of Business ,Lahore University of Management Sciences (LUMS), Pakistan visited University of Moratuwa (UOM) on 10th March 2017 as a part of their international expansion strategy to network with academic institutions and to enroll Sri Lankan students to MBA programs of LUMS under full scholarships funded by Asian Development Bank. LUMS is keen on building partnerships in the areas of research collaborations, faculty collaborations and other forms of partnerships. The visit was coordinated by Mr. Chaminda Hettiarachchi, Visiting lecturer, Department of Computer Science and Engineering\n" +
//                        "\n" +
//                        "The Vice Chancellor Prof. Ananda Jayawardane presided  the meeting which was attended by the Director, International Affairs, Prof. Gihan Dias and the Dean, Faculty of Business, Prof. Neranjan Gunawardana.")
//                .setTagLine("Pakistan to Sri Lanka ")
//                .build();
//        Task<Void> task = myRef.child(key).setValue(article);
//        task.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(MainActivity.this,"Successfully SET",Toast.LENGTH_SHORT).show();
//            }
//        });

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
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

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
            FirebaseAuth mAuth =FirebaseAuth.getInstance();
            mAuth.signOut();
            GoogleApiClient mGoogleApiClient = FirebaseAuthenticationService.getApiClient();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {

                            Toast.makeText(MainActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
                        }
                    });

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
