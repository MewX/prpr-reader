package org.mewx.projectprpr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.plugin.component.ChapterInfo;
import org.mewx.projectprpr.plugin.component.VolumeInfo;
import org.mewx.projectprpr.reader.activity.ReaderActivityV1;

import java.util.ArrayList;
import java.util.List;

public class DataSourceItemChapterActivity extends AppCompatActivity {
    private static final String TAG = DataSourceItemChapterActivity.class.getSimpleName();

    private LinearLayout linearLayout;

    private String novelTag;
    private VolumeInfo volumeInfo;
    private List<ChapterInfo> chapterInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_source_item_chapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // fetch data
        novelTag = getIntent().getStringExtra(DataSourceItemDetailActivity.NOVEL_TAG);
        volumeInfo = (VolumeInfo) getIntent().getSerializableExtra(DataSourceItemDetailActivity.VOLUME_TAG);
        getSupportActionBar().setTitle(volumeInfo.getTitle());

        chapterInfoList = new ArrayList<>();
        for (int i = 0; i < volumeInfo.getChapterListSize(); i ++) {
            chapterInfoList.add(volumeInfo.getChapterByListIndex(i));
        }

        // find views
        linearLayout = (LinearLayout) findViewById(R.id.novel_chapter_scroll);

        // update layout
        listAllChapters();
    }

    private void listAllChapters() {
        for(final ChapterInfo ci : chapterInfoList) {
            // get view
            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(DataSourceItemChapterActivity.this).inflate(R.layout.recycler_item_chapter, null);

            TextView tv = (TextView) rl.findViewById(R.id.chapter_title);
            tv.setText(ci.getTitle());
            rl.findViewById(R.id.chapter_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo: test does file exist, load from cloud or local
                    // if not downloaded yet, download first

                    // todo: jump to reader activity
                    Intent intent = new Intent(DataSourceItemChapterActivity.this, ReaderActivityV1.class); //VerticalReaderActivity.class);
                    intent.putExtra("novelTag", novelTag);
                    intent.putExtra("volumeInfo", volumeInfo);
                    intent.putExtra("currentChapterTag", ci.getChapterTag());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.hold); // fade in animation
                }
            });

            linearLayout.addView(rl); // add to scroll view
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
