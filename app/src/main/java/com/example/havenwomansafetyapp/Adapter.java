package com.example.havenwomansafetyapp;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {
    String TAG = "Adapter";
    MediaPlayer mediaPlayer;
    List<String> strings;

    public Adapter(List<String> strings) {
        this.strings = strings;
    }

    @NonNull
    @Override
    public Adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single,parent,false);
        return new viewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull Adapter.viewHolder holder, int position) {
        String filePath = "/storage/emulated/0/Android/data/com.ashutosh.voicerecorder/files/Music/" + strings.get(position);
        holder.textView.setText(strings.get(position));
        holder.textView.setOnClickListener(v -> {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                if (mediaPlayer.isPlaying()) {
                    Log.d(TAG, "onBindViewHolder: Playback started");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error playing audio: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


    @Override
    public int getItemCount() {
        return strings.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        Button textView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTxt);
        }
    }
}