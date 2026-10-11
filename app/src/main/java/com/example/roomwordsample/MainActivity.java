package com.example.roomwordsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private WordViewModel mWordViewModel;

    private final ActivityResultLauncher<Intent> newWordActivityLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            String wordText = result.getData()
                                    .getStringExtra(NewWordActivity.EXTRA_REPLY);

                            if (wordText != null && !wordText.trim().isEmpty()) {
                                Word word = new Word(wordText);
                                mWordViewModel.insert(word);
                            }

                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    R.string.empty_not_saved,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuration du RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        final WordListAdapter adapter =
                new WordListAdapter(new WordListAdapter.WordDiff());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Connexion du ViewModel aux donnees
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        mWordViewModel.getAllWords().observe(this, words -> {
            adapter.submitList(words);
        });

        // Bouton pour ajouter un nouveau mot
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
            newWordActivityLauncher.launch(intent);
        });
    }
}
