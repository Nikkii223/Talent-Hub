package com.app.talenthub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.allattentionhere.autoplayvideos.AAH_CustomViewHolder;
import com.allattentionhere.autoplayvideos.AAH_VideosAdapter;
import com.app.talenthub.R;
import com.app.talenthub.model.Content;
import com.app.talenthub.others.Const;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyVideosAdapter extends AAH_VideosAdapter {

    private List<Content> list;
    private Context context;
    private static final int TYPE_VIDEO = 0, TYPE_IMAGE = 1;

    OnItemClickListener onItemClickListener;

    public void setItem(List<Content> videoList) {
        this.list = videoList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        void onShowCommentClicked(AAH_CustomViewHolder holder,View view, Content post);
        void onLikeButtonClicked(AAH_CustomViewHolder holder, View view, Content post);
        void onShareButtonClicked(AAH_CustomViewHolder holder, Content post);
        void onProfileButtonClicked(AAH_CustomViewHolder holder, Content post);
    }


    public void setOnItemClickListener(Context context,OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    public class MyViewHolder extends AAH_CustomViewHolder {

        FrameLayout media_container;
        CircleImageView ivProfile;
        ImageView ivMore;
        ImageView thumbnail, volumeControl;
        TextView tvName, tvUserId, tvLike, tvComment, tvView, tvAllComments;
        ProgressBar progressBar;
        View parent;
        RequestManager requestManager;
        ImageView playerControl;
        ImageView img_vol, img_playback,ivLike, ivShare, ivComment;
        boolean isMuted;

        public MyViewHolder(View x) {
            super(x);
            parent = x;
            media_container = x.findViewById(R.id.media_container);
            thumbnail = x.findViewById(R.id.iv_thumbnail);
            //title = itemView.findViewById(R.id.title);
            //progressBar = itemView.findViewById(R.id.progressBar);
            //volumeControl = itemView.findViewById(R.id.volume_control);
            //progressBar = itemView.findViewById(R.id.progress_bar);
            playerControl = x.findViewById(R.id.player_control);
            tvName = x.findViewById(R.id.tv_name);
            tvUserId = x.findViewById(R.id.tv_user_id);
            // tvView = itemView.findViewById(R.id.txt_view);
            tvLike = x.findViewById(R.id.txt_like);
            tvComment = x.findViewById(R.id.txt_comment);
            tvAllComments = x.findViewById(R.id.tv_show_comments);
            ivProfile = x.findViewById(R.id.circleImageView);
            ivMore = x.findViewById(R.id.btn_req);
            img_vol = x.findViewById(R.id.img_vol);
            ivLike = x.findViewById(R.id.iv_like);
            ivComment = x.findViewById(R.id.iv_comment);
            ivShare = x.findViewById(R.id.iv_share);
            img_playback = x.findViewById(R.id.img_playback);
        }

        //override this method to get callback when video starts to play
        @Override
        public void videoStarted() {
            super.videoStarted();
            img_playback.setImageResource(R.drawable.ic_pause);
            if (isMuted) {
                muteVideo();
                img_vol.setImageResource(R.drawable.ic_mute);
            } else {
                unmuteVideo();
                img_vol.setImageResource(R.drawable.ic_unmute);
            }
        }

        @Override
        public void pauseVideo() {
            super.pauseVideo();
            img_playback.setImageResource(R.drawable.ic_play);
        }
    }


    public class MyImageViewHolder extends AAH_CustomViewHolder {

        FrameLayout media_container;
        CircleImageView ivProfile;
        ImageView ivMore;
        ImageView thumbnail, volumeControl;
        TextView tvName, tvUserId, tvLike, tvComment, tvView, tvAllComments;
        ProgressBar progressBar;
        View parent;
        RequestManager requestManager;
        ImageView playerControl, ivLike, ivShare, ivComment;

        public MyImageViewHolder(View x) {
            super(x);
            parent = x;
            media_container = x.findViewById(R.id.media_container);
            thumbnail = x.findViewById(R.id.iv_thumbnail);
            //title = itemView.findViewById(R.id.title);
            //progressBar = itemView.findViewById(R.id.progressBar);
            //volumeControl = itemView.findViewById(R.id.volume_control);
            //progressBar = itemView.findViewById(R.id.progress_bar);
            playerControl = x.findViewById(R.id.player_control);
            tvName = x.findViewById(R.id.tv_name);
            tvUserId = x.findViewById(R.id.tv_user_id);
            // tvView = itemView.findViewById(R.id.txt_view);
            tvLike = x.findViewById(R.id.txt_like);
            tvComment = x.findViewById(R.id.txt_comment);
            tvAllComments = x.findViewById(R.id.tv_show_comments);
            ivProfile = x.findViewById(R.id.circleImageView);
            ivMore = x.findViewById(R.id.btn_req);
            ivLike = x.findViewById(R.id.iv_like);
            ivComment = x.findViewById(R.id.iv_comment);
            ivShare = x.findViewById(R.id.iv_share);
        }
    }

    public MyVideosAdapter(List<Content> list_urls, Context context) {
        this.list = list_urls;
        this.context = context;

    }

    @Override
    public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType== TYPE_IMAGE) {
            View itemView1 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_post_image, parent, false);
            return new MyImageViewHolder(itemView1);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_post_video, parent, false);
            return new MyViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(final AAH_CustomViewHolder holder, int position) {
        if (list.get(position).getType().startsWith("image")) {
            //((MyImageViewHolder) holder).tv.setText(list.get(position).getName());

            ((MyImageViewHolder) holder).tvName.setText(list.get(position).getUserName());
            // tvUserId.setText(View.GONE);
            // tvUserId.setText(mediaObject.getUserName());
            ((MyImageViewHolder) holder).tvLike.setText(String.valueOf(list.get(position).getLike()));
            ((MyImageViewHolder) holder).tvComment.setText(String.valueOf(list.get(position).getComment()));
            //tvView.setText(mediaObject.getViews());
            ((MyImageViewHolder) holder).tvAllComments.setText(String.format("Show all comments (%s)", list.get(position).getComment()));

            Glide.with(context)
                    .load(list.get(position).getImageUrl())
                    .placeholder(R.color.white)
                    .into(((MyImageViewHolder) holder).thumbnail);

            Glide.with(context)
                    .load(list.get(position).getProfileUrl())
                    .placeholder(R.color.white)
                    .into(((MyImageViewHolder) holder).ivProfile);

            ((MyImageViewHolder) holder).tvAllComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onShowCommentClicked(holder,v, list.get(position));

                }
            });

            ((MyImageViewHolder) holder).ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onLikeButtonClicked(holder,v, list.get(position));

                }
            });

            ((MyImageViewHolder) holder).ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onLikeButtonClicked(holder,v, list.get(position));

                }
            });

            ((MyImageViewHolder) holder).ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onProfileButtonClicked(holder, list.get(position));

                }
            });

            checkLiked(((MyImageViewHolder) holder).ivLike, list.get(position));


        } else {

            ((MyViewHolder) holder).ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onLikeButtonClicked(holder,v, list.get(position));

                }
            });
            ((MyViewHolder) holder).tvAllComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onShowCommentClicked(holder,v, list.get(position));

                }
            });

            ((MyViewHolder) holder).ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onShareButtonClicked(holder, list.get(position));

                }
            });

            ((MyViewHolder) holder).ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onProfileButtonClicked(holder, list.get(position));

                }
            });

            //todo
            holder.setImageUrl(list.get(position).getImageUrl());
            holder.setVideoUrl(list.get(position).getVideoUrl());

            //load image into imageview
           /* if (list.get(position).getImageUrl() != null && !list.get(position).getImageUrl().isEmpty()) {
                Picasso.get().load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
            }*/

            holder.setLooping(true); //optional - true by default

            ((MyViewHolder) holder).tvName.setText(list.get(position).getUserName());
            // tvUserId.setText(View.GONE);
            // tvUserId.setText(mediaObject.getUserName());
            ((MyViewHolder) holder).tvLike.setText(String.valueOf(list.get(position).getLike()));
            ((MyViewHolder) holder).tvComment.setText(String.valueOf(list.get(position).getComment()));
            //tvView.setText(mediaObject.getViews());
            ((MyViewHolder) holder).tvAllComments.setText(String.format("Show all comments (%s)", list.get(position).getComment()));

            Glide.with(context)
                    .load(list.get(position).getProfileUrl())
                    .placeholder(R.color.white)
                    .into(((MyViewHolder) holder).ivProfile);


            //to play pause videos manually (optional)
            ((MyViewHolder) holder).img_playback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.isPlaying()) {
                        holder.pauseVideo();
                        holder.setPaused(true);
                    } else {
                        holder.playVideo();
                        holder.setPaused(false);
                    }
                }
            });

            //to mute/un-mute video (optional)
            ((MyViewHolder) holder).img_vol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MyViewHolder) holder).isMuted) {
                        holder.unmuteVideo();
                        ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_unmute);
                    } else {
                        holder.muteVideo();
                        ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_mute);
                    }
                    ((MyViewHolder) holder).isMuted = !((MyViewHolder) holder).isMuted;
                }
            });

            if (list.get(position).getVideoUrl() == null) {
                ((MyViewHolder) holder).img_vol.setVisibility(View.GONE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.GONE);
            } else {
                ((MyViewHolder) holder).img_vol.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).img_playback.setVisibility(View.VISIBLE);
            }

            checkLiked(((MyViewHolder) holder).ivLike, list.get(position));
        }


    }

    private void checkLiked(ImageView view, Content post) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId != null) {

            Query query = FirebaseDatabase.getInstance().getReference().child(Const.NODE_LIKE).child(userId).child(post.getId());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.getChildrenCount()>0){
                        ((ImageView) view).setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    }else{
                        ((ImageView) view).setColorFilter(ContextCompat.getColor(context, R.color.grey_300));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getType().startsWith("image")) {
            return TYPE_IMAGE;
        } else return TYPE_VIDEO;
    }


}