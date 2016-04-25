package org.mewx.projectprpr.activity;

import android.support.annotation.NonNull;

import org.mewx.projectprpr.plugin.NovelDataSourceBasic;
import org.mewx.projectprpr.template.AppCompatTemplateActivity;

/**
 * Created by MewX on 4/14/2016.
 * Call by NovelDataSourceBasic to fetch data.
 */
public class DataSourceItemInitialActivity extends AppCompatTemplateActivity {
    private final static String TAG = DataSourceItemInitialActivity.class.getSimpleName();

    private @NonNull NovelDataSourceBasic dataSource;

    public DataSourceItemInitialActivity(@NonNull NovelDataSourceBasic ds) {
        this.dataSource = ds;
    }
}
