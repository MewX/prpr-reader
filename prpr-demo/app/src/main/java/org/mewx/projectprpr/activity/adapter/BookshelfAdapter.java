package org.mewx.projectprpr.activity.adapter;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.mewx.projectprpr.MyApp;
import org.mewx.projectprpr.R;
import org.mewx.projectprpr.plugin.component.BookshelfSaver;

import java.util.List;

/**
 * Created by MewX on 05/15/2016.
 * Grid view for local bookshelf.
 */
public class BookshelfAdapter extends RecyclerView.Adapter {
    private static final String TAG = DataSourceAdapter.class.getSimpleName();

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<BookshelfSaver> list;

    public BookshelfAdapter(List<BookshelfSaver> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_bookshelf, null);
        CardView.LayoutParams lp = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new DataSourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final DataSourceViewHolder holder = (DataSourceViewHolder) viewHolder;
        holder.position = i;
        BookshelfSaver item = list.get(i);
        holder.textTitle.setText(item.getNovelInfo().getTitle());
        if (!TextUtils.isEmpty(item.getNovelInfo().getCoverUrl())) {
            holder.image.setImageURI(Uri.parse(item.getNovelInfo().getCoverUrl()));
        } else {
            holder.image.setImageURI(Uri.parse("android.resource://" + MyApp.getContext().getPackageName() + "/" + R.drawable.ic_empty_image));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DataSourceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public View rootView;
        public TextView textTitle;
        public SimpleDraweeView image;
        public int position;

        public DataSourceViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.cardtext);
            image = (SimpleDraweeView) itemView.findViewById(R.id.cardimage);
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
