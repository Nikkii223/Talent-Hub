package com.app.talenthub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.talenthub.R;
import com.app.talenthub.model.Content;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<Content> videoList;


    OnItemClickListener onItemClickListener;
    OnLoadMoreListener onLoadMoreListener;

    public interface OnItemClickListener {

    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public VideoAdapter(Context context, List<Content> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;


        View view = LayoutInflater.from(context).inflate(R.layout.item_card_video, null);
        //view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        viewHolder = new MyViewHolder(view);


        return viewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof MyViewHolder) {

            MyViewHolder holder = (MyViewHolder) viewHolder;

            final Content post = videoList.get(position);

            if(post.getImageUrl()!=null){
                Glide.with(context)
                        .load(post.getImageUrl())
                        .placeholder(R.color.white)
                        .into(holder.ivThumbnail);
            }else{
                holder.ivThumbnail.setImageResource(R.drawable.et_bg_white);
            }


        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvLike, tvView, tvComment;
        ImageView ivThumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);

        }
    }


    public void setLoaded() {

        for (int i = 0; i < getItemCount(); i++) {
            if (videoList.get(i) == null) {
                videoList.remove(i);
                notifyItemRemoved(i);
            }
        }


    }

    public void resetAndSetItems(ArrayList<Content> feedList) {

        this.videoList = feedList;
        //notifyItemRangeChanged(0, getItemCount() - 1);
        notifyDataSetChanged();

    }

    public void setItems(ArrayList<Content> videoList) {
        //setLoaded();
       /* int positionStart = getItemCount();
        int itemCount = videoList.size();
        this.videoList.addAll(videoList);
        notifyItemRangeInserted(positionStart, itemCount);*/
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public Content getCurrentItem(int position) {
        return videoList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

   /* public void setLoading() {
        if (getItemCount() != 0) {
            this.feedList.add(null);
            notifyItemInserted(getItemCount() - 1);
        }
    }*/

    public void setLoadingStatus() {
        if (onLoadMoreListener != null) {
            onLoadMoreListener.onLoadMore();
        }
    }

}

