package com.app.talenthub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.talenthub.R;
import com.app.talenthub.model.Content;
import com.app.talenthub.others.VideoPlayerViewHolder;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;


public class VideoPlayerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Content> mediaObjects;
    private RequestManager requestManager;

    OnItemClickListener onItemClickListener;
    private Context context;
    private String userId;



    public interface OnItemClickListener {

        void onShowCommentClicked(View view, Content post);
        void onLikeButtonClicked(View view, Content post);
        void onShareButtonClicked(Content post);
    }


    public void setOnItemClickListener(Context context, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }


    public VideoPlayerRecyclerAdapter(ArrayList<Content> mediaObjects, RequestManager requestManager, String userId) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
        this.userId = userId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoPlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_post, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((VideoPlayerViewHolder)viewHolder).onBind(mediaObjects.get(i), requestManager, onItemClickListener, context, userId);
    }

    public void deletePost(Content post){

        for (int i = 0; i < getItemCount(); i++) {
            if (mediaObjects.get(i).getId().equals(post.getId())) {
                mediaObjects.remove(i);
                notifyItemRemoved(i);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

    public void setItems(ArrayList<Content> videoList) {
        this.mediaObjects = videoList;
        notifyDataSetChanged();
    }
}














