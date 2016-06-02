package org.mewx.projectprpr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.activity.adapter.NetNovelListAdapter;
import org.mewx.projectprpr.global.DataSourcePluginManager;
import org.mewx.projectprpr.global.G;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.PageNumBetween;
import org.mewx.projectprpr.plugin.component.PluginInfo;
import org.mewx.projectprpr.template.AppCompatTemplateActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * This is a singleton activity.
 * dataSourceBasic is for after-access, whose activities' access are after this activity.
 * In one word, this activity is the start page of one data source plug-in.
 */

public class DataSourceItemInitialActivity extends AppCompatTemplateActivity {
    private static final String TAG = DataSourceItemInitialActivity.class.getSimpleName();
    public static final String DATA_SOURCE_TAG = "datasource";

    // for global access
    public static NovelDataSourceBasic dataSourceBasic;

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private NetNovelListAdapter adapter;

    private boolean isLoading = true; // escape scroll initial actions
    private PageNumBetween pageRange;
    private List<NovelInfo> novelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_source_item_initial);

        // fetch data
        PluginInfo pluginInfo = (PluginInfo) getIntent().getSerializableExtra(DATA_SOURCE_TAG);
        dataSourceBasic = DataSourcePluginManager.loadDataSourcePluginClassByInfo(pluginInfo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(dataSourceBasic.getName());

        // set recycler view
        initRecyclerView();
        showNovelList();

        // todo: auto login?
        // todo: to keep session on period, when call from local bookshelf, do login operation.


        // first time to request novel list
        pageRange = dataSourceBasic.getMainListPageNum(); // this value should be updated after every request
        requestLatestNovelList();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void requestLatestNovelList() {
        try {
            isLoading = true;
            G.globalOkHttpClient3.newCall(dataSourceBasic.getMainListRequest(pageRange.getNext()).getOkHttpRequest(G.STANDARD_CHARSET))
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DataSourceItemInitialActivity.this, "Loading failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                            e.printStackTrace();
                            isLoading = false;
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                novelList.addAll(dataSourceBasic.parseMainListRequestResult(response.body().string()));
                            } catch (final Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DataSourceItemInitialActivity.this,TAG + e.toString(),Toast.LENGTH_LONG).show();
                                    }
                                });
                                isLoading = false;
                                return;
                            }
                            for (NovelInfo ni : novelList) {
                                ni.setDataSource(dataSourceBasic.getName());
                            }

                            // update this value for page num update
                            int save = pageRange.getCurrent();
                            pageRange = dataSourceBasic.getMainListPageNum();
                            pageRange.setCurrent(save);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshNovelList();
                                }
                            });
                            isLoading = false;
                        }
                    });
        } catch (Exception expected) {
            isLoading = false;
            expected.printStackTrace();
        }
    }

    private void initRecyclerView() {
        // set recycler view
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_net_book_list);
        if(recyclerView != null && layoutManager != null) {
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new MyOnScrollListener()); // add listener
        }
    }

    private void showNovelList() {
        if(recyclerView != null) {
            adapter = new NetNovelListAdapter(novelList);
            adapter.setOnRecyclerViewListener(new NetNovelListAdapter.OnRecyclerViewListener() {
                @Override
                public void onItemClick(int position) {
                    // jump to detail activity
                    Intent intent = new Intent(DataSourceItemInitialActivity.this, DataSourceItemDetailActivity.class);
                    intent.putExtra(DataSourceItemDetailActivity.NOVEL_TAG, novelList.get(position).getBookTag());
                    intent.putExtra(DataSourceItemDetailActivity.NOVEL_TITLE, novelList.get(position).getTitle());
                    intent.putExtra(DataSourceItemDetailActivity.NOVEL_DATA_SOURCE, novelList.get(position).getDataSource());
                    intent.putExtra(DataSourceItemDetailActivity.NOVEL_AUTHOR, novelList.get(position).getAuthor());
                    intent.putExtra(DataSourceItemDetailActivity.NOVEL_COVER_URL, novelList.get(position).getCoverUrl());
                    startActivity(intent);
                }

                @Override
                public boolean onItemLongClick(int position) {
                    return false;
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private void refreshNovelList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // todo: choose contains account functions or not
        getMenuInflater().inflate(R.menu.activity_data_source_initial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_bar_account:
                // todo: log in and set sessions
                break;

            case R.id.action_bar_category:
                // todo: make an dialog to category selection
                break;

            case R.id.action_bar_search:
                // todo: goto search activity
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!isLoading) {
                int pastVisibleItems, visibleItemCount, totalItemCount;
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                // loading when there are 2 items left
                if (visibleItemCount + pastVisibleItems + 2 >= totalItemCount && pageRange.hasNext()) {
                    // load more toast
                    Snackbar.make(recyclerView, getResources().getString(R.string.novel_info_loading)
                                    + "(" + (pageRange.getCurrent() + 1) + "/" + pageRange.getEnd() + ")",
                            Snackbar.LENGTH_SHORT).show();

                    // load more thread
                    requestLatestNovelList();
                }
            }
        }
    }
}
