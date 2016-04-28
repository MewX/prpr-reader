package org.mewx.projectprpr.activity.adapter;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.mewx.projectprpr.MyApp;
import org.mewx.projectprpr.R;
import org.mewx.projectprpr.plugin.component.NovelInfo;

import java.util.List;

@SuppressWarnings("unused")
public class NetNovelListAdapter  extends RecyclerView.Adapter {
    private static final String TAG = NetNovelListAdapter.class.getSimpleName();
    public static final int ORIGIN_TABLE_ITEM_COUNT = 2;

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<NovelInfo> list;

    public NetNovelListAdapter(List<NovelInfo> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_net_novel_item, null);
        CardView.LayoutParams lp = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new DataSourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final DataSourceViewHolder holder = (DataSourceViewHolder) viewHolder;
        holder.position = i;

        NovelInfo item = list.get(i);
        holder.cover.setImageURI(Uri.parse(item.getCoverUrl()));

        holder.tableLayout.removeViewsInLayout(ORIGIN_TABLE_ITEM_COUNT, holder.tableLayout.getChildCount() - ORIGIN_TABLE_ITEM_COUNT);
        RelativeLayout.LayoutParams tableLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams textLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (String key : item.getInfoPairs().keySet()) {
            /*
            <TableRow
                android:id="@+id/novel_data_source_row"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" >

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:paddingRight="16dp"
                    android:textSize="12sp"
                    android:text="@string/novel_item_data_source_with_colon"
                    android:textColor="@color/novel_item_text"/>

                <TextView
                    android:id="@+id/novel_data_source"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:singleLine="true"
                    android:textSize="12sp"
                    android:text="@string/novel_item_loading"
                    android:textColor="@color/novel_item_text"/>
            </TableRow>
             */
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
            tv2.setText(item.getInfoPairs().get(key).toString());
            row.addView(tv2);

            holder.tableLayout.addView(row);
        }

        holder.title.setText(item.getTitle());
        holder.dataSource.setText(item.getDataSource());
        holder.author.setText(item.getAuthor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DataSourceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public View rootView;
        public SimpleDraweeView cover;
        public TextView title;
        public TableLayout tableLayout;
        public TextView dataSource;
        public TextView author;
        public int position;

        public DataSourceViewHolder(View itemView) {
            super(itemView);
            cover = (SimpleDraweeView) itemView.findViewById(R.id.novel_cover);
            tableLayout = (TableLayout) itemView.findViewById(R.id.info_table);
            dataSource = (TextView) itemView.findViewById(R.id.novel_data_source);
            author = (TextView) itemView.findViewById(R.id.novel_author);
            title = (TextView) itemView.findViewById(R.id.novel_title);

            rootView = itemView.findViewById(R.id.item_card);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return null != onRecyclerViewListener && onRecyclerViewListener.onItemLongClick(position);
        }
    }

}