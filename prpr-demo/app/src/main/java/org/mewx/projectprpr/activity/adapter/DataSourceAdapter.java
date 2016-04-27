package org.mewx.projectprpr.activity.adapter;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.mewx.projectprpr.R;

import java.util.List;

/**
 * Created by MewX on 04/17/2016.
 * This class adapt the data source list in plug-in list.
 */
@SuppressWarnings("unused")
public class DataSourceAdapter extends RecyclerView.Adapter {
    private static final String TAG = DataSourceAdapter.class.getSimpleName();

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<DataSourceItem> list;

    public DataSourceAdapter(List<DataSourceItem> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_data_source, null);
        CardView.LayoutParams lp = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new DataSourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final DataSourceViewHolder holder = (DataSourceViewHolder) viewHolder;
        holder.position = i;
        DataSourceItem item = list.get(i);
        holder.textMain.setText(item.getDisplayName() + " [ v" + item.getVersionCode() + " ], by: " + item.getPluginAuthor());
        holder.textSub.setText(item.getWebsiteDomain());
        holder.image.setImageURI(Uri.parse(item.getLogoUrl()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DataSourceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public View rootView;
        public TextView textMain;
        public TextView textSub;
        public SimpleDraweeView image;
        public int position;

        public DataSourceViewHolder(View itemView) {
            super(itemView);
            textMain = (TextView) itemView.findViewById(R.id.site_info);
            textSub = (TextView) itemView.findViewById(R.id.site_meta);
            image = (SimpleDraweeView) itemView.findViewById(R.id.domain_logo);
            rootView = itemView.findViewById(R.id.cardview);
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