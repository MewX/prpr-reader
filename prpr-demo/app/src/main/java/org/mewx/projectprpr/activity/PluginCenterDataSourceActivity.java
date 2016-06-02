package org.mewx.projectprpr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.activity.adapter.DataSourceAdapter;
import org.mewx.projectprpr.activity.adapter.DataSourceItem;
import org.mewx.projectprpr.global.DataSourcePluginManager;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.template.AppCompatTemplateActivity;

import java.util.ArrayList;
import java.util.List;

public class PluginCenterDataSourceActivity extends AppCompatTemplateActivity {
    private static final String TAG = PluginCenterDataSourceActivity.class.getSimpleName();

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
        for(int i = 0; i < DataSourcePluginManager.getLocalDataSourcePluginCount(); i ++) {
            NovelDataSourceBasic obj = DataSourcePluginManager.loadDataSourcePluginClassById(i);
            if (obj != null) {
                itemList.add(new DataSourceItem(obj.getName(), obj.getUrl(), obj.getVersionCode(), obj.getLogoUrl(), obj.getAuthor()));
            } else {
                itemList.add(new DataSourceItem(DataSourcePluginManager.getLocalDataSourcePluginInfoById(i).getClassName(),
                        DataSourcePluginManager.getLocalDataSourcePluginInfoById(i).getPath(), 0, "", "unknown"));
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
                    if (DataSourcePluginManager.getLocalDataSourcePluginInfoById(position).getPath().contains("http")) {
                        // TODO: when click, judge whether downloaded, if not, download, else jump to Activity

                        // TODO: change pluginList, and run the following code
                    } else {
                        gotoPluginInitialActivity(position);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void gotoPluginInitialActivity(int index) {
        if (0 <= index && index < DataSourcePluginManager.getLocalDataSourcePluginCount()) {
            Intent intent = new Intent(PluginCenterDataSourceActivity.this, DataSourceItemInitialActivity.class);
            intent.putExtra(DataSourceItemInitialActivity.DATA_SOURCE_TAG, DataSourcePluginManager.getLocalDataSourcePluginInfoById(index));
            startActivity(intent);
        }
    }
}
