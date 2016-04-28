package org.mewx.projectprpr.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.mewx.projectprpr.MyApp;
import org.mewx.projectprpr.R;
import org.mewx.projectprpr.activity.adapter.NetNovelListAdapter;
import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.plugin.component.NetRequest;
import org.mewx.projectprpr.plugin.component.NovelInfo;
import org.mewx.projectprpr.plugin.component.VolumeInfo;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Todo: need to make a debug kit built in app!
 */

public class DataSourceItemDetailActivity extends AppCompatActivity {
    private static final String TAG = DataSourceItemDetailActivity.class.getSimpleName();
    public static final String VOLUME_TAG = "volume";

    // views
    private LinearLayout linearLayout;
    private LinearLayout novelCard;
    private SimpleDraweeView novelCover;
    private TextView novelTitle;
    private TableLayout infoTable;
    private TextView novelDataSource;
    private TextView novelAuthor;
    private TextView novelIntro;

    // info
    private boolean isLoading = true;
    private String responseBody;
    private String novelTag;
    private NovelDataSourceBasic dataSourceBasic;
    private NovelInfo novelInfo;
    private List<VolumeInfo> volumeInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_source_item_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        novelTag = getIntent().getStringExtra(DataSourceItemInitialActivity.NOVEL_TAG);
        String title = getIntent().getStringExtra(DataSourceItemInitialActivity.NOVEL_TITLE);
        String dataSource = getIntent().getStringExtra(DataSourceItemInitialActivity.NOVEL_DATA_SOURCE);
        String author = getIntent().getStringExtra(DataSourceItemInitialActivity.NOVEL_AUTHOR);
        String coverUrl = getIntent().getStringExtra(DataSourceItemInitialActivity.NOVEL_COVER_URL);
        dataSourceBasic = DataSourceItemInitialActivity.dataSourceBasic; // get a copy of reference
        if(novelInfo != null) {
            getSupportActionBar().setTitle(novelInfo.getTitle());
        }

        // get views
        linearLayout = (LinearLayout) findViewById(R.id.novel_info_scroll);
        novelCover = (SimpleDraweeView) findViewById(R.id.novel_cover); // need initial
        novelCard = (LinearLayout) findViewById(R.id.item_card);
        novelTitle = (TextView)  findViewById(R.id.novel_title); // need initial
        novelDataSource = (TextView) findViewById(R.id.novel_data_source); // need initial
        novelAuthor = (TextView) findViewById(R.id.novel_author); // need initial
        infoTable = (TableLayout) findViewById(R.id.info_table);
        novelIntro = (TextView) findViewById(R.id.novel_intro_full);


        // FloatingActionButton
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // init view values
        novelCard.setClickable(false); // disable ripple effects
        novelCover.setImageURI(Uri.parse(coverUrl));
        novelTitle.setText(title);
        novelDataSource.setText(dataSource);
        novelAuthor.setText(author);
        getSupportActionBar().setTitle(title);

        // request novel info
        requestNovelInfoDetail(true);
    }

    private void requestNovelInfoDetail(boolean forceLoad) {
        if (forceLoad || dataSourceBasic != null && !isLoading) {
            try {
                isLoading = true;
                YBL.globalOkHttpClient3.newCall(dataSourceBasic.getNovelInfoRequest(novelTag).getOkHttpRequest(YBL.STANDARD_CHARSET))
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Toast.makeText(DataSourceItemDetailActivity.this, "Loading failed", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                isLoading = false;
                                requestNovelInfoDetail(false); // retry
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                responseBody = response.body().string();
                                try {
                                    novelInfo = dataSourceBasic.parseNovelInfo(responseBody);
                                    novelInfo.setDataSource(dataSourceBasic.getName()); // set data source
                                } catch (final Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            novelIntro.setText(responseBody);
                                            Toast.makeText(DataSourceItemDetailActivity.this,TAG + e.toString(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    isLoading = false;
                                    return;
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateNovelInfo();
                                        isLoading = false;
                                        requestNovelVolumeDetail(); // load volumes
                                    }
                                });
                            }
                        });
            } catch (Exception expected) {
                expected.printStackTrace();
                isLoading = false;
            }
        }
    }

    private void requestNovelVolumeDetail() {
        if (dataSourceBasic != null && !isLoading) {
            isLoading = true;

            NetRequest netRequest = dataSourceBasic.getNovelVolumeRequest(novelTag);
            if (netRequest == null) {
                // use novel info response
                try {
                    volumeInfoList = dataSourceBasic.parseNovelVolume(responseBody);
                    updateChapterInfo();
                } catch (final Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DataSourceItemDetailActivity.this, TAG + e.toString(), Toast.LENGTH_LONG).show();
                }
                isLoading = false;
            } else {
                // new request
                try {
                    YBL.globalOkHttpClient3.newCall(netRequest.getOkHttpRequest(YBL.STANDARD_CHARSET))
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Toast.makeText(DataSourceItemDetailActivity.this, "Loading failed", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                    isLoading = false;
                                    requestNovelVolumeDetail(); // retry
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        volumeInfoList = dataSourceBasic.parseNovelVolume(response.body().string());
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(DataSourceItemDetailActivity.this, TAG + e.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        isLoading = false;
                                        return;
                                    }

                                    // for extensible
                                    requestNovelChapterDetail();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateChapterInfo();
                                            isLoading = false;
                                        }
                                    });
                                }
                            });
                } catch (Exception expected) {
                    expected.printStackTrace();
                    isLoading = false;
                }
            }
        }
    }

    private void requestNovelChapterDetail() {
        for (VolumeInfo vi : volumeInfoList) {
            NetRequest request = dataSourceBasic.getNovelChapterRequest(vi.getVolumeTag());
            if(request == null) break;
            // todo: get each chapters in this thread

        }
    }

    private void updateNovelInfo() {
        String introShort = "", introFull = "";

        // update texts
        novelCover.setImageURI(Uri.parse(novelInfo.getCoverUrl()));
        novelTitle.setText(novelInfo.getTitle());
        novelDataSource.setText(novelInfo.getDataSource());
        novelAuthor.setText(novelInfo.getAuthor());

        // update table layout
        infoTable.removeViewsInLayout(NetNovelListAdapter.ORIGIN_TABLE_ITEM_COUNT,
                infoTable.getChildCount() - NetNovelListAdapter.ORIGIN_TABLE_ITEM_COUNT);
        RelativeLayout.LayoutParams tableLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams textLayoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (String key : novelInfo.getInfoPairs().keySet()) {
            // specialised for full intro, all the intro are skipped escaping duplications
            if (key.equals(getResources().getString(R.string.novel_info_short_intro))) {
                introShort = novelInfo.getInfoPairs().get(key).toString();
                continue;
            } else if (key.equals(getResources().getString(R.string.novel_info_full_intro))) {
                introFull = novelInfo.getInfoPairs().get(key).toString();
                continue;
            }

            // copied from NetNovelListAdapter
            TableRow row = new TableRow(MyApp.getContext());
            row.setLayoutParams(tableLayoutParams);

            TextView tv = new TextView(MyApp.getContext());
            tv.setLayoutParams(textLayoutParams);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setTextColor(MyApp.getContext().getResources().getColor(R.color.novel_item_text));
            tv.setText(key);
            tv.setPadding(0, 0, 16, 0);
            row.addView(tv);

            TextView tv2 = new TextView(MyApp.getContext());
            tv2.setLayoutParams(textLayoutParams);
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv2.setSingleLine(true);
            tv2.setTextColor(MyApp.getContext().getResources().getColor(R.color.novel_item_text));
            tv2.setText(novelInfo.getInfoPairs().get(key).toString());
            row.addView(tv2);

            infoTable.addView(row);
        }

        // set intro
        if (!TextUtils.isEmpty(introFull)) {
            novelIntro.setText(introFull);
        } else if (!TextUtils.isEmpty(introShort)) {
            novelIntro.setText(introShort);
        } else {
            novelIntro.setText(R.string.novel_info_no_intro);
        }
    }
    
    private void updateChapterInfo() {
        // remove all TextView(in CardView, in RelativeView)
        if(linearLayout.getChildCount() > 2)
            linearLayout.removeViews(2, linearLayout.getChildCount() - 2);

        for(final VolumeInfo vl : volumeInfoList) {
            // get view
            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(DataSourceItemDetailActivity.this).inflate(R.layout.recycler_item_chapter, null);

            // set text and listeners
            TextView tv = (TextView) rl.findViewById(R.id.chapter_title);
            tv.setText(vl.getTitle());
            // todo: update novel status
//            if(vl.inLocal)
//                ((TextView) rl.findViewById(R.id.chapter_status)).setText(getResources().getString(R.string.bookshelf_inlocal));
            rl.findViewById(R.id.chapter_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo: jump to chapter select activity
                    Intent intent = new Intent(DataSourceItemDetailActivity.this, DataSourceItemChapterActivity.class);
                    intent.putExtra(VOLUME_TAG, vl);
                    startActivity(intent);
                }
            });

            // add to scroll view
            linearLayout.addView(rl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_net_book_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_bar_add:
                // todo: add to bookshelf
                break;

            case R.id.action_bar_download:
                // todo: make an dialog to select download options
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
