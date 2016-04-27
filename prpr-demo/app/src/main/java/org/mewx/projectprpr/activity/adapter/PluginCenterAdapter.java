package org.mewx.projectprpr.activity.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mewx.projectprpr.R;

import java.util.List;

@SuppressWarnings("unused")
public class PluginCenterAdapter extends RecyclerView.Adapter {

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<PluginCenterItem> list;

    public PluginCenterAdapter(List<PluginCenterItem> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_plugin_center, null);
        CardView.LayoutParams lp = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new PluginCenterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        PluginCenterViewHolder holder = (PluginCenterViewHolder) viewHolder;
        holder.position = i;
        PluginCenterItem item = list.get(i);
        holder.text.setText(item.getCenterName());
        holder.image.setImageResource(item.getBackgroundId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PluginCenterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public View rootView;
        public TextView text;
        public ImageView image;
        public int position;

        public PluginCenterViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.cardtext);
            image = (ImageView) itemView.findViewById(R.id.cardimage);
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
            return null != onRecyclerViewListener &&onRecyclerViewListener.onItemLongClick(position);
        }
    }

}