package com.app.talenthub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.app.talenthub.R;
import com.app.talenthub.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>{

    private Context context;
    private List<User> searchList;


    OnItemClickListener onItemClickListener;



    public interface OnItemClickListener {
        void onItemClick(View view, User user);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public SearchAdapter(Context context, List<User> searchList) {
        this.context = context;
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_search,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(holder instanceof MyViewHolder){

            final User user = searchList.get(position);
            holder.tvName.setText(user.getName());
            holder.tvUserName.setText(user.getUserName());

            Glide.with(context)
                    .load(user.getProfileUrl())
                    .placeholder(R.color.white)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivProfile);

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemClickListener.onItemClick(view, user);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvUserName, tvFollow;
        CircleImageView ivProfile;
        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_user_name);
            tvUserName = itemView.findViewById(R.id.tv_user_id);
            layout = itemView.findViewById(R.id.card_parent);
            ivProfile = itemView.findViewById(R.id.iv_profile);

        }
    }


    public void setItems(ArrayList<User> searchList){
        this.searchList = searchList;
        notifyDataSetChanged();
    }

}

