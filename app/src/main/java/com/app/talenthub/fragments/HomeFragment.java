package com.app.talenthub.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.allattentionhere.autoplayvideos.AAH_CustomViewHolder;
import com.app.talenthub.R;
import com.app.talenthub.activities.OtherProfileActivity;
import com.app.talenthub.adapter.CommentListAdapter;
import com.app.talenthub.adapter.MyVideosAdapter;
import com.app.talenthub.adapter.VideoPlayerRecyclerAdapter;
import com.app.talenthub.databinding.FragmentHomeBinding;
import com.app.talenthub.model.Comment;
import com.app.talenthub.model.Content;
import com.app.talenthub.model.Notification;
import com.app.talenthub.model.User;
import com.app.talenthub.others.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentHomeBinding binding;
    private ArrayList<Content> videoList;
    VideoPlayerRecyclerAdapter adapter;

    DatabaseReference mRef;

    String userId;
    public static boolean IS_RECRUITER = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoList = new ArrayList<>();
        //initView();

        mRef = FirebaseDatabase.getInstance().getReference();
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                binding.swipeRefresh.setRefreshing(true);
                loadVideoList();
            }
        });

        initRecycler();
        loadVideoList();


    }



    private void loadVideoList() {

        mRef.child(Const.NODE_ALL_POST).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (binding.swipeRefresh.isRefreshing())
                    binding.swipeRefresh.setRefreshing(false);

                videoList = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Content content = snap.getValue(Content.class);
                    videoList.add(content);
                }

                mAdapter.setItem(videoList);

              /*  binding.recyclerView.setMediaObjects(videoList);
                adapter.setItems(videoList);
                isDataLoaded = true;*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (binding.swipeRefresh.isRefreshing())
                    binding.swipeRefresh.setRefreshing(false);

            }
        });


      /*  mRef.child(Const.NODE_ALL_POST).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (binding.swipeRefresh.isRefreshing())
                    binding.swipeRefresh.setRefreshing(false);

                videoList = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Content content = snap.getValue(Content.class);
                    videoList.add(content);
                }
                initRecycler();
              *//*  binding.recyclerView.setMediaObjects(videoList);
                adapter.setItems(videoList);
                isDataLoaded = true;*//*


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (binding.swipeRefresh.isRefreshing())
                    binding.swipeRefresh.setRefreshing(false);

            }
        });
*/
    }

    MyVideosAdapter mAdapter;

    private void initRecycler() {

        mAdapter = new MyVideosAdapter(videoList, getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        //todo before setAdapter
        binding.recyclerView.setActivity(getActivity());

        //optional - to play only first visible video
         binding.recyclerView.setPlayOnlyFirstVideo(false); // false by default

        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        binding.recyclerView.setCheckForMp4(false); //true by default

        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        //recyclerView.setDownloadPath(Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

        // binding.recyclerView.setDownloadVideos(true); // false by default

        binding.recyclerView.setVisiblePercent(50); // percentage of View that needs to be visible to start playing

        //extra - start downloading all videos in background before loading RecyclerView
        List<String> urls = new ArrayList<>();
        for (Content object : videoList) {
            if (object.getVideoUrl() != null && object.getVideoUrl().contains("http"))
                urls.add(object.getVideoUrl());
        }
         binding.recyclerView.preDownload(urls);

        binding.recyclerView.setAdapter(mAdapter);
        //call this functions when u want to start autoplay on loading async lists (eg firebase)
        binding.recyclerView.smoothScrollBy(0, 1);
        binding.recyclerView.smoothScrollBy(0, -1);

     /*   mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        adapter = new VideoPlayerRecyclerAdapter(videoList, initGlide(), userId);
        binding.recyclerView.setAdapter(adapter);
*/


        mAdapter.setOnItemClickListener(getActivity(), new MyVideosAdapter.OnItemClickListener() {
            @Override
            public void onShowCommentClicked(AAH_CustomViewHolder holder, View view, Content post) {
                if (holder!=null && post.getType().equals("video")) {
                    if(holder.isPlaying()) {
                        holder.pauseVideo();
                        holder.setPaused(true);
                    }
                }

                showBottomSheetDialog(post);
            }

            @Override
            public void onLikeButtonClicked(AAH_CustomViewHolder holder, View view, Content post) {
                if (holder!=null && post.getType().equals("video")) {
                    if(holder.isPlaying()) {
                        holder.pauseVideo();
                        holder.setPaused(true);
                    }
                }

                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (userId != null) {

                    mRef.child(Const.NODE_LIKE).child(userId).child(post.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Content content = snapshot.getValue(Content.class);
                            if (content != null) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("like", post.getLike() - 1);

                                ((ImageView) view).setColorFilter(ContextCompat.getColor(getContext(), R.color.grey_300));
                                mRef.child(Const.NODE_LIKE).child(userId).child(post.getId()).removeValue();
                                mRef.child(Const.NODE_ALL_POST).child(post.getId()).updateChildren(map);
                            } else {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("like", post.getLike() + 1);
                                ((ImageView) view).setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                mRef.child(Const.NODE_LIKE).child(userId).child(post.getId()).setValue(post);
                                mRef.child(Const.NODE_ALL_POST).child(post.getId()).updateChildren(map);

                                addNotification(post);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onShareButtonClicked(AAH_CustomViewHolder holder, Content post) {
                if (holder!=null && post.getType().equals("video")) {
                    if(holder.isPlaying()) {
                        holder.pauseVideo();
                        holder.setPaused(true);
                    }
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if(post.getType().equals("image"))
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Talent Hub \n"+post.getImageUrl());
                else
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Talent Hub \n"+post.getVideoUrl());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }

            @Override
            public void onProfileButtonClicked(AAH_CustomViewHolder holder, Content post) {
                if (holder!=null && post.getType().equals("video")) {
                    if(holder.isPlaying()) {
                        holder.pauseVideo();
                        holder.setPaused(true);
                    }
                }

                Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
                intent.putExtra(Const.KEY_OBJECT, post);
                intent.putExtra(Const.KEY_STRING, "home");
                startActivity(intent);

            }


        });

    }

    private void addNotification(Content content) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid != null) {

            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child(Const.NODE_USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);

                    if (user != null) {

                        String id = mRef.push().getKey();
                        String message = user.getName()+" Liked your post.";

                        Notification notification = new Notification(id, uid, user.getName(), user.getProfileUrl(), content.getUserId(), message, System.currentTimeMillis(), content.getId());
                        mRef.child(Const.NODE_NOTIFICATION).child(content.getUserId()).child(notification.getId()).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    mRef.child(Const.NODE_USER).child(content.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            User user = snapshot.getValue(User.class);

                                            if (user != null) {

                                               sendNotification(notification.getMessage(), user.getToken());

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void sendNotification(String message, String token){



        JSONObject json = new JSONObject();

        try {

            JSONObject notificationJson = new JSONObject();
            notificationJson.put("body",message);
            notificationJson.put("title","Talent Hub");


            json.put("data", notificationJson);
            json.put("notification", notificationJson);
            json.put("to", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .header("Authorization", "key=" + "AAAAuU89TXM:APA91bEH3i64r83_t6dJEjWKH23PQy8pNJuMr0dS2Yl8rsgMSu3vdtmUfSnFHqAJtACOgHTYjw84lSLRMsF9qrjaqLnOD0AfuxScTcPat0l6h5G6He4KjjV4JGV2R0k6jOGiSvE_KpKC")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("FCM_MESSAGE", "remoteMessage");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("FCM_MESSAGE", "remoteMessage");
            }
        });

    }

    ArrayList<Comment> commentList = new ArrayList<>();
    private void showBottomSheetDialog(Content post) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(
                R.layout.layout_bottom_sheet_comment,
                (LinearLayout) getActivity().findViewById(R.id.bottom_container)
        );
        bottomSheetDialog.setContentView(bottomSheetView);

        CommentListAdapter commentAdapter = new CommentListAdapter(getActivity(), commentList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recycler_comment);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentAdapter);

        mRef.child(Const.NODE_COMMENT).child(post.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                commentList = new ArrayList<>();
                for(DataSnapshot snap : snapshot.getChildren()){
                    commentList.add(snap.getValue(Comment.class));
                }

                commentAdapter.setItems(commentList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageView ivSend = bottomSheetView.findViewById(R.id.iv_send);
        EditText etMessage = bottomSheetView.findViewById(R.id.et_message);
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = etMessage.getText().toString().trim();

                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(userId!=null){

                    mRef = FirebaseDatabase.getInstance().getReference();
                    mRef.child(Const.NODE_USER).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            User user = snapshot.getValue(User.class);

                            if (user != null) {
                                String id = mRef.push().getKey();
                                Comment comment = new Comment(id, user.getName(), user.getProfileUrl(), message, System.currentTimeMillis());

                                if(message.length()>0){
                                    mRef.child(Const.NODE_COMMENT).child(post.getId()).child(comment.getId()).setValue(comment);
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("comment", post.getComment()+1);
                                    mRef.child(Const.NODE_ALL_POST).child(post.getId()).updateChildren(map);

                                    String nId = mRef.push().getKey();
                                    String message = user.getName()+" commented on your post"+"\""+comment.getComment()+"\"";

                                    Notification notification = new Notification(nId, user.getId(), user.getName(), user.getProfileUrl(), post.getUserId(), message, System.currentTimeMillis(), comment.getId());
                                    mRef.child(Const.NODE_NOTIFICATION).child(post.getUserId()).child(notification.getId()).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                mRef.child(Const.NODE_USER).child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        User user = snapshot.getValue(User.class);

                                                        if (user != null) {

                                                            sendNotification(message, user.getToken());

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }

                                        }
                                    });



                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }


            }
        });

        bottomSheetDialog.show();

    }


}