package com.nguyenhoanglam.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;

/**
 * Created by hoanglam on 8/28/17.
 */

public class MainFragment extends Fragment {

    public static final String EXTRA_CONFIG = "Config";


    private RecyclerView recyclerView;
    private Button pickImageButton;

    private Config config;
    ;
    private ImageAdapter adapter;
    private ArrayList<Image> images = new ArrayList<>();


    public static MainFragment newInstance(Config config) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CONFIG, config);

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = getArguments().getParcelable(EXTRA_CONFIG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        pickImageButton = view.findViewById(R.id.button_pick_image);

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });

        adapter = new ImageAdapter(getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void start() {

        ImagePicker.with(this)
                .setFolderMode(config.isFolderMode())
                .setCameraOnly(config.isCameraOnly())
                .setFolderTitle(config.getFolderTitle())
                .setMultipleMode(config.isMultipleMode())
                .setSelectedImages(config.getSelectedImages())
                .setMaxSize(config.getMaxSize())
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            adapter.setData(images);
        }
    }
}
