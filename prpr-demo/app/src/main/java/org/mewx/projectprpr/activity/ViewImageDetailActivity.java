package org.mewx.projectprpr.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.mewx.projectprpr.R;
import org.mewx.projectprpr.global.YBL;
import org.mewx.projectprpr.toolkit.FileTool;
import org.mewx.projectprpr.toolkit.thirdparty.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by MewX on 2015/7/28.
 * View large image activity.
 */
public class ViewImageDetailActivity extends AppCompatActivity {
    private static final String TAG = ViewImageDetailActivity.class.getSimpleName();
    public static final String PATH_TAG = "PATH";

    private SystemBarTintManager tintManager;
    private String path;
    private SubsamplingScaleImageView imageView;

    private View btnRotate, btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_view_image_detail);

        // fetch value
        path = getIntent().getStringExtra(PATH_TAG);
        Log.e(TAG, path);

        // set indicator enable
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        if(getSupportActionBar() != null && upArrow != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        getSupportActionBar().setTitle(path.split("/")[path.split("/").length-1]);

        btnRotate = findViewById(R.id.btn_rotate);
        btnDownload = findViewById(R.id.btn_download);

        // set tint color
        if (Build.VERSION.SDK_INT >= 16 ) {
            // Android API 22 has more effects on status bar, so ignore
            // create our manager instance after the content view is set
            tintManager = new SystemBarTintManager(this);
            // enable all tint
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setStatusBarAlpha(0.9f);
            tintManager.setNavigationBarAlpha(0.8f);
            // set all color
            tintManager.setTintColor(getResources().getColor(android.R.color.black));
        }

        // set image
        imageView = (SubsamplingScaleImageView) findViewById(R.id.image_scalable);
        imageView.setImage(ImageSource.uri(path));
        imageView.setMaxScale(2.0f);
        imageView.setOnClickListener(new View.OnClickListener() {
            private boolean shown = true;

            @Override
            public void onClick(View v) {
                if (shown) {
                    // hide
                    shown = false;
                    hideNavigationBar();
                    findViewById(R.id.toolbar_actionbar).setVisibility(View.INVISIBLE);
                    findViewById(R.id.image_detail_bot).setVisibility(View.INVISIBLE);
                    if (Build.VERSION.SDK_INT >= 16 ) {
                        tintManager.setStatusBarAlpha(0.0f);
                        tintManager.setNavigationBarAlpha(0.0f);
                    }
                } else {
                    shown = true;
                    showNavigationBar();
                    findViewById(R.id.toolbar_actionbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.image_detail_bot).setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= 16 ) {
                        tintManager.setStatusBarAlpha(0.9f);
                        tintManager.setNavigationBarAlpha(0.8f);
                    }
                }
            }
        });

        // set on click listeners
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (imageView.getOrientation()) {
                    case SubsamplingScaleImageView.ORIENTATION_0:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_90);
                        break;
                    case SubsamplingScaleImageView.ORIENTATION_90:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_180);
                        break;
                    case SubsamplingScaleImageView.ORIENTATION_180:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_270);
                        break;
                    case SubsamplingScaleImageView.ORIENTATION_270:
                        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_0);
                        break;
                }
            }
        });
        btnRotate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ViewImageDetailActivity.this, getResources().getString(R.string.reader_rotate), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewImageDetailActivity.this, FilePickerActivity.class);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH,
                        TextUtils.isEmpty(YBL.pathPickedSave) ?
                                Environment.getExternalStorageDirectory().getPath() : YBL.pathPickedSave);
                startActivityForResult(i, 0);
            }
        });
        btnDownload.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ViewImageDetailActivity.this, getResources().getString(R.string.reader_download), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();
                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            runSaveProcedure(uri.toString());
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra(FilePickerActivity.EXTRA_PATHS);
                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                            runSaveProcedure(uri.toString());
                        }
                    }
                }
            } else {
                Uri uri = data.getData();
                // Do something with the URI
                runSaveProcedure(uri.toString());
            }
        }
    }

    private void runSaveProcedure(String uri) {
        final String newuri = uri.replaceAll("file://", "");
        YBL.pathPickedSave = newuri;
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title(R.string.dialog_title_save_file_name)
                .content(getResources().getString(R.string.dialog_content_saved_path) + newuri)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                .input( "", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, final CharSequence input) {
                        if(FileTool.existFile(newuri + File.separator + input + ".jpg")) {
                            // judge file exists? choose to force write
                            new MaterialDialog.Builder(ViewImageDetailActivity.this)
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            // copy file from 'path' to 'uri + File.separator + input + ".jpg"'
                                            FileTool.copyFile(path, newuri + File.separator + input + ".jpg", true);
                                            Toast.makeText(ViewImageDetailActivity.this, "已保存：" + newuri + File.separator + input + ".jpg", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onNegative(MaterialDialog dialog) {
                                            super.onNegative(dialog);
                                            Toast.makeText(ViewImageDetailActivity.this, "目标文件名已存在，未保存。", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .theme(Theme.LIGHT)
                                    .title(R.string.dialog_title_found_file)
                                    .content(R.string.dialog_content_force_write_file)
                                    .contentGravity(GravityEnum.CENTER)
                                    .positiveText(R.string.dialog_positive_yes)
                                    .negativeText(R.string.dialog_negative_no)
                                    .show();
                        }
                        else {
                            // copy file from 'path' to 'uri + File.separator + input + ".jpg"'
                            FileTool.copyFile(path, newuri + File.separator + input + ".jpg", true);
                            Toast.makeText(ViewImageDetailActivity.this, "已保存：" + newuri + File.separator + input + ".jpg", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNavigationBar();
    }

    private void hideNavigationBar() {
        // set navigation bar status, remember to disable "setNavigationBarTintEnabled"
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // This work only for android 4.4+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }

    private void showNavigationBar() {
        // set navigation bar status, remember to disable "setNavigationBarTintEnabled"
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // This work only for android 4.4+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.fade_out);
    }
}
