package org.mewx.projectprpr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.activity.adapter.DataSourceAdapter;
import org.mewx.projectprpr.activity.adapter.DataSourceItem;
import org.mewx.projectprpr.activity.adapter.PluginCenterAdapter;
import org.mewx.projectprpr.activity.adapter.PluginCenterItem;
import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.plugin.component.PluginInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PluginCenterDataSourceActivity extends AppCompatActivity {
    private static final String TAG = PluginCenterDataSourceActivity.class.getSimpleName();

    private List<PluginInfo> pluginList = Arrays.asList(YBL.BUILTIN_PLUGIN); // for operation
    private List<DataSourceItem> itemList = new ArrayList<>(); // for displaying
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_center_data_source);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_data_source);

        // add all to itemList
        for (PluginInfo info : pluginList) {
            // make class and get info to itemList, from pluginList
            try {
                Class<?> aClass = Class.forName(YBL.PLUGIN_PACKAGE + "." + info.getClassName());
                NovelDataSourceBasic obj = (NovelDataSourceBasic) aClass.newInstance();
                itemList.add(new DataSourceItem(obj.getName(), obj.getUrl(), obj.getVersionCode(), obj.getLogoUrl()));
            } catch (Exception e) {
                e.printStackTrace();
                itemList.add(new DataSourceItem(info.getClassName(), info.getPath(), 0, ""));
            }

        }

        // get plugin list, pluginList has already filled with built-in plug-ins
        // TODO: fetch online list
        showDataSourceList();

    }

    private void showDataSourceList() {
        if(recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            DataSourceAdapter adapter = new DataSourceAdapter(itemList);
            adapter.setOnRecyclerViewListener(new DataSourceAdapter.OnRecyclerViewListener() {
                @Override
                public void onItemClick(int position) {
                    Toast.makeText(PluginCenterDataSourceActivity.this, "position: " + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public boolean onItemLongClick(int position) {
                    return false;
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
