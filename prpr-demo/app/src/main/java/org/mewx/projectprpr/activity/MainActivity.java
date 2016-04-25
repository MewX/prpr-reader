package org.mewx.projectprpr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.activity.adapter.PluginCenterAdapter;
import org.mewx.projectprpr.activity.adapter.PluginCenterItem;
import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.plugin.JavaCallLuaJava;
import org.mewx.projectprpr.template.AppCompatTemplateActivity;
import org.mewx.projectprpr.template.NavigationFitSystemView;
import org.mewx.projectprpr.toolkit.VolleyController;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatTemplateActivity
        implements NavigationFitSystemView.OnNavigationItemSelectedListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private static Boolean isExit = false; // used for exit by exitBy2Click()
    private int currentFragmentId = R.id.nav_library_local;
    private NavigationFitSystemView navigationView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set menu toggle
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if(drawer != null) drawer.addDrawerListener(toggle);
        toggle.syncState();

        // set navigation view, and default fragments
        navigationView = (NavigationFitSystemView) findViewById(R.id.nav_view);
        if(navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true); // set default selected item
        }
        switchToPluginCenter(); // TODO: remove when release

        // set recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        switchToPluginCenter();

        Toast.makeText(this, new JavaCallLuaJava().helloLuaJavaCallFromLuaWithReturn(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitBy2Click();
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
        if(currentFragmentId != id) {
            if (id == R.id.nav_library_local) {
                currentFragmentId = R.id.nav_library_local;
                switchToLocalBookshelf();
            } else if (id == R.id.nav_plug_in_center) {
                currentFragmentId = R.id.nav_plug_in_center;
                switchToPluginCenter();
            } else if (id == R.id.nav_dictionary) {
                // currentFragmentId = R.id.nav_dictionary;
                if(navigationView != null) {
                    // reset click status
                    navigationView.getMenu().findItem(currentFragmentId).setChecked(true);
                }
            } else if (id == R.id.nav_cloud) {

            } else if (id == R.id.nav_settings) {

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer != null) drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cleanRecyclerView() {
        if(recyclerView != null) {
            // TODO
        }
    }

    public void switchToLocalBookshelf() {
        if(recyclerView != null) {
        }
    }

    public void switchToPluginCenter() {
        if(recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            String[] listString = getResources().getStringArray(R.array.plugin_center_items);

            List<PluginCenterItem> list = new ArrayList<>();
            list.add(new PluginCenterItem(listString[0], R.drawable.recycler_item_bg1)); // data source
            list.add(new PluginCenterItem(listString[1], R.drawable.recycler_item_bg2)); // reading format

            PluginCenterAdapter adapter = new PluginCenterAdapter(list);
            adapter.setOnRecyclerViewListener(new PluginCenterAdapter.OnRecyclerViewListener() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0:
                            // data source
                             /* 新建一个Intent对象 */
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, PluginCenterDataSourceActivity.class);
                            MainActivity.this.startActivity(intent);
                            break;
                        case 1:
                            // reading format
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.app_developing), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public boolean onItemLongClick(int position) {
                    return false;
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    public void switchToDictionary() {
        if(recyclerView != null) {

        }
    }

    class DrawerVisualEffects implements DrawerLayout.DrawerListener {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {

        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    }

    private void exitBy2Click() {
        // press twice to exit
        Timer tExit;
        if (!isExit) {
            isExit = true; // ready to exit
            Toast.makeText(
                    this,
                    this.getResources().getString(R.string.app_press_again_to_exit),
                    Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // cancel exit
                }
            }, 2000); // 2 seconds cancel exit task

        } else {
            finish();
            // call fragments and end streams and services
            System.exit(0);
        }
    }
}
