package org.mewx.projectprpr.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.activity.adapter.NetNovelListAdapter;
import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.PageNumBetween;
import org.mewx.projectprpr.plugin.component.PluginInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DataSourceItemInitialActivity extends AppCompatActivity {
    private static final String TAG = DataSourceItemInitialActivity.class.getSimpleName();

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private NetNovelListAdapter adapter;

    private boolean isLoading = true; // escape scroll initial actions
    private PluginInfo pluginInfo;
    private NovelDataSourceBasic dataSourceBasic;
    private PageNumBetween pageRange;
    private List<NovelInfo> novelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_source_item_initial);

        // fetch data
        pluginInfo = (PluginInfo) getIntent().getSerializableExtra(PluginCenterDataSourceActivity.DATA_SOURCE_TAG);
        try {
            Class<?> aClass = Class.forName(YBL.PLUGIN_PACKAGE + "." + pluginInfo.getClassName());
            dataSourceBasic = (NovelDataSourceBasic) aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void requestLatestNovelList() {
        try {
            isLoading = true;
            YBL.globalOkHttpClient3.newCall(dataSourceBasic.getMainListRequest(pageRange.getNext()).getOkHttpRequest(YBL.STANDARD_CHARSET))
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(DataSourceItemInitialActivity.this, "Loading failed", Toast.LENGTH_SHORT).show();
                            isLoading = false;
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            novelList.addAll(dataSourceBasic.parseMainListRequestResult(response.body().string()));
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
                    // todo
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
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
