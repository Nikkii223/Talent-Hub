package com.app.talenthub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.talenthub.R;
import com.app.talenthub.model.Comment;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder>{

    private Context context;
    private List<Comment> commentList;

    public CommentListAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_comment,parent, false));
       /* else
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_follow,parent, false));*/
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(holder instanceof MyViewHolder){

            final Comment comment = commentList.get(position);
            holder.tvName.setText(comment.getName());
         //   holder.tvTime.setText(comment.getTime());
            holder.tvMessage.setText(comment.getComment());


            Glide.with(context)
                    .load(comment.getProfileUrl())
                    .placeholder(R.color.white)
                    .into(holder.ivProfile);



        }
    }

    public void updateItem(Comment comment) {
        for (int i = 0; i < getItemCount(); i++) {
            if (comment.getId().equals(commentList.get(i).getId())) {
                //feedList.set(i, feed);
                notifyItemChanged(i, comment);
            }
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvTime, tvMessage;
        CircleImageView ivProfile;
        ImageView ivLike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvMessage = itemView.findViewById(R.id.tv_message);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            ivLike = itemView.findViewById(R.id.iv_like);

        }
    }



    public void setItems(ArrayList<Comment> comments){
        this.commentList = comments;
        notifyDataSetChanged();
    }

}

