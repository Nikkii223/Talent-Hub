package com.app.talenthub.others;


import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.talenthub.R;
import com.app.talenthub.adapter.VideoPlayerRecyclerAdapter;
import com.app.talenthub.model.Content;
import com.bumptech.glide.RequestManager;


import de.hdodenhof.circleimageview.CircleImageView;


public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {

    FrameLayout media_container;
    CircleImageView ivProfile;
    ImageView ivMore;
    ImageView thumbnail, volumeControl;
    TextView tvName, tvUserId, tvLike, tvComment, tvView, tvAllComments;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;
    ImageView playerControl;

    public VideoPlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.iv_thumbnail);
        //title = itemView.findViewById(R.id.title);
        //progressBar = itemView.findViewById(R.id.progressBar);
        //volumeControl = itemView.findViewById(R.id.volume_control);
        //progressBar = itemView.findViewById(R.id.progress_bar);
        playerControl = itemView.findViewById(R.id.player_control);
        tvName = itemView.findViewById(R.id.tv_name);
        tvUserId = itemView.findViewById(R.id.tv_user_id);
       // tvView = itemView.findViewById(R.id.txt_view);
        tvLike = itemView.findViewById(R.id.txt_like);
        tvComment = itemView.findViewById(R.id.txt_comment);
        tvAllComments = itemView.findViewById(R.id.tv_show_comments);
        ivProfile = itemView.findViewById(R.id.circleImageView);
        ivMore = itemView.findViewById(R.id.btn_req);
    }

    public void onBind(Content mediaObject, RequestManager requestManager, VideoPlayerRecyclerAdapter.OnItemClickListener onItemClickListener, Context context, String userId) {
        this.requestManager = requestManager;
        parent.setTag(this);
        tvName.setText(mediaObject.getUserName());
       // tvUserId.setText(View.GONE);
       // tvUserId.setText(mediaObject.getUserName());
        tvLike.setText(String.valueOf(mediaObject.getLike()));
        tvComment.setText(String.valueOf(mediaObject.getComment()));
        //tvView.setText(mediaObject.getViews());
        tvAllComments.setText(String.format("Show all comments (%s)", mediaObject.getComment()));
        /*Glide.with(context)
                .load(comment.getProfile())
                .placeholder(R.color.white)
                .into(holder.ivProfile);*/
        this.requestManager
                .load(mediaObject.getProfileUrl())
                .into(ivProfile);

        if(mediaObject.getType().equals("image")){
            this.requestManager
                    .load(mediaObject.getImageUrl())
                    .into(thumbnail);
            thumbnail.setVisibility(View.VISIBLE);

        }else{
            thumbnail.setVisibility(View.GONE);
        }

        tvAllComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemClickListener.onShowCommentClicked(v, mediaObject);

            }
        });

        tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemClickListener.onLikeButtonClicked(v, mediaObject);

            }
        });

        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   PopupMenu popup = new PopupMenu(context, ivMore);
                MenuInflater inflater = popup.getMenuInflater();
                if(userId.equals(new PrefManager((Activity) context).getUserDetail().getId()))
                    inflater.inflate(R.menu.menu_post, popup.getMenu());
                else
                    inflater.inflate(R.menu.menu_post_other, popup.getMenu());

                popup.setOnMenuItemClickListener(new MyMenuItemClickListener(mediaObject,onItemClickListener, userId));
                popup.show();*/
            }
        });



    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        VideoPlayerRecyclerAdapter.OnItemClickListener onItemClickListener;
        Content mediaObject;
        String userId;
        public MyMenuItemClickListener(Content mediaObject, VideoPlayerRecyclerAdapter.OnItemClickListener onItemClickListener, String userId) {
            this.onItemClickListener = onItemClickListener;
            this.mediaObject = mediaObject;
            this.userId =userId;
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {

          /*  switch (item.getItemId()) {
                case R.id.action_delete:
                        onItemClickListener.onMoreOptionClicked(mediaObject);
                    return true;
                case R.id.action_report:
                        onItemClickListener.reportPostClicked(userId, mediaObject);
                    break;
                default:
            }*/

            return false;
        }
    }

}














