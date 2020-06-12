package com.example.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteFragment extends Fragment {

    //This Fragment show list of favorite songs
    View v;
    RecyclerView recyclerView2;
    private ArrayList<Song> favoriteList ;
    private boolean shouldRefreshOnResume = false;

    public FavoriteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.favorite_fragment, container, false);

        //set up adapter and display recyclerview
        recyclerView2 = (RecyclerView) v.findViewById(R.id.favorite_recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(favoriteList, getActivity());
        recyclerView2.setAdapter(adapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get favorite song list from storage
       Storage storage = new Storage(getActivity());
       favoriteList = storage.loadFavoriteSong();
       if(favoriteList == null) favoriteList = new ArrayList<>();


    }

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(shouldRefreshOnResume){
            shouldRefreshOnResume = false;
            Storage storage = new Storage(getActivity());
            favoriteList = storage.loadFavoriteSong();
            if(favoriteList == null) favoriteList = new ArrayList<>();
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldRefreshOnResume = true;
    }
}
