package com.example.havenwomansafetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class NoteEditorActivity extends AppCompatActivity {
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        EditText editText = (EditText) findViewById(R.id.editText);
        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);

        if (noteId != -1) {
            editText.setText(Memo.notes.get(noteId));
        }
    }

    public void saveNote(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String noteText = editText.getText().toString();

        if (noteId != -1) {
            Memo.notes.set(noteId, noteText);
        } else {
            Memo.notes.add(noteText);
        }

        Memo.arrayAdapter.notifyDataSetChanged();
        finish();
    }
}