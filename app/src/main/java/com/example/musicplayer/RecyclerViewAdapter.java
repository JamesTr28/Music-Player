package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    List<Song> songList ;
    Context context;

    public RecyclerViewAdapter(List<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SongPlaying.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SONG", songList.get(holder.getAdapterPosition()));
                bundle.putInt("INDEX", holder.getAdapterPosition());
                bundle.putSerializable("LIST", (Serializable) songList);
                intent.putExtra("BUNDLE", bundle);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(songList.get(position).getTitle());
        holder.artist.setText(songList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return songList.size();
    }

}

class ViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView artist;
    CardView cardView;

    ViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.songtitle);
        artist = itemView.findViewById(R.id.songartist);
        cardView = itemView.findViewById(R.id.cardview);
    }
}