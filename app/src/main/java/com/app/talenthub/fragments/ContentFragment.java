package com.app.talenthub.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.talenthub.R;
import com.app.talenthub.activities.LoginActivity;
import com.app.talenthub.databinding.FragmentContentBinding;
import com.app.talenthub.model.Content;
import com.app.talenthub.model.User;
import com.app.talenthub.others.Const;
import com.app.talenthub.others.PermissionUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContentFragment newInstance(String param1, String param2) {
        ContentFragment fragment = new ContentFragment();
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

    String[] languages, categories;
    FragmentContentBinding binding;
    String userId;
    ArrayAdapter catAdapter, langAdapter;
    DatabaseReference mRef;
    boolean isVideo = true;
    private final int REQUEST_READ_STORAGE = 100;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private int isSelected = -1;

    private Uri videoCover, videoUri, imageCover;
    private String videoCoverUrl, imageCoverUrl, videoUrl;
    String category, language, type = "video";
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait..");
        dialog.setCancelable(false);

        languages = getResources().getStringArray(R.array.array_language);
        categories = getResources().getStringArray(R.array.array_categories);

        updateArtist();
        updateUi();

        initView();


    }

    private void initView() {

        binding.tvSubmit.setOnClickListener(v -> {

            String title = binding.etTitle.getText().toString().trim();
            String artist = binding.etArtist.getText().toString().trim();
            String caption = binding.etCaption.getText().toString().trim();

                /*if (isVideo && coverImagePath == null) {
                    showCustomError("select cover image");
                } else*/

            if (isVideo && videoUri == null) {
                showCustomError("select video");
            } else if (isVideo && videoCover == null) {
                showCustomError("select video cover image");
            } else if (!isVideo && imageCover == null) {
                showCustomError("select image");
            } else if (TextUtils.isEmpty(title)) {
                showCustomError("enter title");
            } else if (TextUtils.isEmpty(artist)) {
                showCustomError("enter artist");
            } else if (category == null) {
                showCustomError("Select category");
            } else if (language == null) {
                showCustomError("Select language");
            } else {

                addPost(title, artist, caption, videoUri, videoCover, imageCover, category, language);

            }

        });


        binding.btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isVideo = true;
                type = "video";
                binding.layoutVideo.setVisibility(View.VISIBLE);
                binding.layoutImage.setVisibility(View.GONE);
                binding.btnVideo.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.btnImage.setTextColor(getResources().getColor(R.color.black));
            }
        });

        binding.btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type = "image";
                isVideo = false;
                binding.layoutVideo.setVisibility(View.GONE);
                binding.layoutImage.setVisibility(View.VISIBLE);
                binding.btnImage.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.btnVideo.setTextColor(getResources().getColor(R.color.black));
            }
        });

        binding.layoutAddVideoCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isSelected = PICK_IMAGE;

                if (!PermissionUtil.checkStoragePermissionGranted(getActivity())) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_READ_STORAGE);

                    return;
                }

                pickImageFromGallery();

            }
        });

        binding.layoutAddAudioCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isSelected = PICK_IMAGE;

                if (!PermissionUtil.checkStoragePermissionGranted(getActivity())) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_READ_STORAGE);

                    return;
                }

                pickImageFromGallery();

            }
        });

        binding.tvVideoAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isSelected = PICK_VIDEO;

                if (!PermissionUtil.checkStoragePermissionGranted(getActivity())) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_READ_STORAGE);

                    return;
                }

                pickVideoFromGallery();

            }
        });

    }

    String fileName = "";
    String folderName = "";

    private void uploadFileToStorage(Uri imageUri, String type) {

        dialog.show();

        if (type.equals("video")) {
            fileName = System.currentTimeMillis() + ".mp4";     //5654322345.png

            folderName = "videos";
        } else {
            fileName = System.currentTimeMillis() + ".png";     //5654322345.png

            folderName = "images";
        }

        StorageReference reference = FirebaseStorage.getInstance().getReference();

        reference.child(folderName).child(fileName)
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.child(folderName).child(fileName).getDownloadUrl().
                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        dialog.dismiss();

                                        if (type.equals("video")) {

                                            videoUrl = uri.toString();
                                        } else {

                                            if (isVideo == true) {
                                                videoCoverUrl = uri.toString();
                                            } else {
                                                imageCoverUrl = uri.toString();
                                            }

                                        }
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });

    }

    private void addPost(String title, String artist, String caption, Uri videoUri, Uri videoCover, Uri imageCover, String category, String language) {

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId != null) {

            mRef = FirebaseDatabase.getInstance().getReference();

            mRef.child(Const.NODE_USER).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);

                    if (user != null) {

                        Content content = null;
                        String id = mRef.push().getKey();

                        if(isVideo)
                            content = new Content(id, title, artist, type,category, language, caption, videoUrl, videoCoverUrl, user.getUserName(), user.getId(),System.currentTimeMillis(), user.getProfileUrl());
                        else
                            content = new Content(id, title, artist, type,category, language, caption, videoUrl, imageCoverUrl, user.getUserName(), user.getId(),System.currentTimeMillis(), user.getProfileUrl());

                        mRef.child(Const.NODE_ALL_POST).child(content.getId()).setValue(content);

                        mRef.child(Const.NODE_POST).child(user.getId()).child(content.getId()).setValue(content).addOnCompleteListener(task -> {

                            if(task.isSuccessful()){

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("post", user.getPost()+1);

                                mRef.child(Const.NODE_USER).child(userId).updateChildren(map);

                                Toast.makeText(getActivity(), "Post added successfully", Toast.LENGTH_SHORT).show();

                                resetField();
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

    private void resetField() {

        binding.etTitle.setText("");
        binding.etCaption.setText("");
        binding.layoutAddAudioCover.setBackgroundResource(R.drawable.app_bg_btn_white);
        binding.layoutAddVideoCover.setBackgroundResource(R.drawable.app_bg_btn_white);

        binding.tvVideoAlbum.setBackgroundResource(R.drawable.app_bg_btn_white);

        imageCoverUrl = "";
        videoCoverUrl = "";
        videoUrl = "";
        videoCover = null;
        imageCover = null;
        videoUri = null;

        binding.spinnerLanguage.setSelection(0);
        binding.spinnerCategory.setSelection(0);


    }

    private void showCustomError(String message) {

        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && data != null && resultCode == RESULT_OK) {

            if (isVideo) {
                videoCover = data.getData();
                binding.layoutAddVideoCover.setBackgroundResource(R.drawable.app_bg_btn_grey);
                uploadFileToStorage(videoCover, "image");
            } else {
                imageCover = data.getData();
                binding.layoutAddAudioCover.setBackgroundResource(R.drawable.app_bg_btn_grey);
                uploadFileToStorage(imageCover, "image");
            }


        } else if (requestCode == PICK_VIDEO && data != null && resultCode == RESULT_OK) {

            videoUri = data.getData();
            binding.tvVideoAlbum.setBackgroundResource(R.drawable.app_bg_btn_grey);
            uploadFileToStorage(videoUri, "video");
        }

    }

    private void pickImageFromGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void pickVideoFromGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_VIDEO);
    }

    private void updateArtist() {

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId != null) {

            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child(Const.NODE_USER).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);
                    if (user != null) {

                        binding.etArtist.setText(user.getName());

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }

    private void updateUi() {

        catAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner_item, categories) {

            @Override
            public boolean isEnabled(int position) {
                                /*if (position == 0) {
                                    return false;
                                } else {
                                    return true;
                                }*/
                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(getResources().getColor(R.color.black));
                } else {
                    textview.setTextColor(getResources().getColor(R.color.black));
                }
                return view;
            }
        };
        catAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        binding.spinnerCategory.setAdapter(catAdapter);

        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = categories[0];
            }
        });

        langAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner_item, languages) {

            @Override
            public boolean isEnabled(int position) {

                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(getResources().getColor(R.color.black));
                } else {
                    textview.setTextColor(getResources().getColor(R.color.black));
                }
                return view;
            }
        };
        langAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        binding.spinnerLanguage.setAdapter(langAdapter);

        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = languages[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                language = languages[0];
            }
        });

    }
}