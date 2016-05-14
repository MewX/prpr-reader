package org.mewx.projectprpr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.activity.adapter.PluginCenterAdapter;
import org.mewx.projectprpr.activity.adapter.PluginCenterItem;
import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.plugin.JavaCallLuaJava;
import org.mewx.projectprpr.template.AppCompatTemplateActivity;
import org.mewx.projectprpr.template.NavigationFitSystemView;
import org.mewx.projectprpr.toolkit.thirdparty.OkHttp3NetworkFetcher;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatTemplateActivity
        implements NavigationFitSystemView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

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

        // initial all folders
        new File(YBL.getStoragePath(YBL.PROJECT_FOLDER)).mkdirs();
        new File(YBL.getStoragePath(YBL.PROJECT_FOLDER_CACHE)).mkdirs();
        new File(YBL.getStoragePath(YBL.PROJECT_FOLDER_DOWNLOAD)).mkdirs();
        new File(YBL.getStoragePath(YBL.PROJECT_FOLDER_READER_IMAGES)).mkdirs();

        // initial okHttp & Fresco, share the same chache size! I am so clever!!!
        CookieManager cookieManager = new CookieManager(); // enable cookies
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        YBL.globalOkHttpClient3 = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .cache(new Cache(new File(YBL.getStoragePath(YBL.PROJECT_FOLDER_CACHE)), YBL.IMAGE_CACHE_DISK_SIZE))
                .build();
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                //.setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),getPackageName())).setBaseDirectoryName("image")
                .setBaseDirectoryPath(new File(YBL.getStoragePath(YBL.PROJECT_FOLDER_CACHE)))
                .setBaseDirectoryName(YBL.FOLDER_NAME_IMAGE)
                .setMaxCacheSize(YBL.IMAGE_CACHE_DISK_SIZE)
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setNetworkFetcher(new OkHttp3NetworkFetcher(YBL.globalOkHttpClient3))
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(this, config);

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
                Toast.makeText(this, getResources().getString(R.string.app_developing), Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, "org.mewx.projectprpr.activity.SettingsActivity$GeneralPreferenceFragment");
                startActivity(intent);
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
        }
    }
}
