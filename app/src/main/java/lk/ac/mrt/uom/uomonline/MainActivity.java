package lk.ac.mrt.uom.uomonline;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lk.ac.mrt.uom.uomonline.adapters.MainRVAdapter;
import lk.ac.mrt.uom.uomonline.firebase.FirebaseAuthenticationService;
import lk.ac.mrt.uom.uomonline.model.Article;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    private List<Article> articles = new ArrayList<>();
    private LinkedHashMap<String, Article> linkedHashMap = new LinkedHashMap<>(10, 0.5f);
    MainRVAdapter mainRVAdapter;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (database == null) {
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
                if (!articles.contains(article)) {
                    articles.add(article);
                }
                mainRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Article article = dataSnapshot.getValue(Article.class);
                linkedHashMap.put(article.getId(), article);
                mainRVAdapter.notifyDataSetChanged();
//                Toast.makeText(MainActivity.this, "Updated the " + article.getImageURL(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                linkedHashMap.remove(article.getId());
                mainRVAdapter.notifyDataSetChanged();
//                Toast.makeText(MainActivity.this, " Child Removed " + dataSnapshot.getValue(Article.class).getTitle(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        final RecyclerView rv = findViewById(R.id.mainRV);
        final FloatingActionButton fab = findViewById(R.id.fab);
        final SwipeRefreshLayout swipeRefreshLayout= findViewById(R.id.swipeRefreshLayout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.smoothScrollToPosition(0);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Snackbar.with(MainActivity.this,null)
                        .type(Type.CUSTOM, 0xFF0288D1)
                        .message("Refreshing, Please Wait! ")
                        .duration(Duration.LONG)
                        .show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mainRVAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.with(MainActivity.this,null)
                                .type(Type.SUCCESS)
                                .message(getIntent().getStringExtra("Name").split(" ")[0] + ", Everything seems to be up to date.")
                                .duration(Duration.LONG)
                                .show();
                    }
                }, 2000);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.red,R.color.blue,R.color.teal,R.color.amber);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        linearLayout.setReverseLayout(true);
//        linearLayout.setStackFromEnd(true);

        rv.setLayoutManager(linearLayout);
        mainRVAdapter = new MainRVAdapter(linkedHashMap, MainActivity.this);
        rv.setAdapter(mainRVAdapter);
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstCompletelyVisibleItemPosition = linearLayout.findFirstVisibleItemPosition();
                if (firstCompletelyVisibleItemPosition >= 1) {
                    fab.show();
                } else {
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

        String name = getIntent().getStringExtra("Name");
        String email = getIntent().getStringExtra("Email");
        String image = getIntent().getStringExtra("Image");

        View header = navigationView.getHeaderView(0);
        TextView nav_username = (TextView) header.findViewById(R.id.nav_username);
        TextView nav_email = (TextView) header.findViewById(R.id.nav_email);
        ImageView nav_image = header.findViewById(R.id.nav_imageView);

        nav_username.setText(name);
        nav_email.setText(email);
        if (image == null) {
            nav_image.setVisibility(View.GONE);
        } else {
            Picasso.with(this).load(image).into(nav_image);
        }
        mGoogleApiClient = FirebaseAuthenticationService.getApiClient();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            Snackbar.with(MainActivity.this,null)
                    .type(Type.CUSTOM, 0xFF0288D1)
                    .message("Please click BACK again to exit")
                    .duration(Duration.LONG)
                    .show();

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

        if (id == R.id.nav_share) {
//            FirebaseAuth mAuth = FirebaseAuth.getInstance();
//            mAuth.signOut();
//            GoogleApiClient mGoogleApiClient = FirebaseAuthenticationService.getApiClient();
            new MaterialDialog.Builder(this)
                    .title("Sign Out")
                    .content("Are you sure to sign out")
                    .positiveText("YES")
                    .negativeText("No")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            final MaterialDialog materialDialog = new MaterialDialog.Builder(MainActivity.this)
                                    .progress(true, 0)
                                    .title("Please Wait")
                                    .content("Signing Out")
                                    .show();

                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    materialDialog.dismiss();
                                    MainActivity.this.finish();
                                }
                            }, 2000);

                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
