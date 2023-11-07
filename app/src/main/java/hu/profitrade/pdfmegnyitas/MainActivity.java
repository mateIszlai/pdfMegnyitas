package hu.profitrade.pdfmegnyitas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import hu.profitrade.pdfmegnyitas.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fileOpenBtn.setOnClickListener( view -> openFile());

    }

    private void openFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));

        startActivityForResult(intent, 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null)
                uri = data.getData();

            binding.pathTv.setText(uri == null ? "" : uri.getPath());

            if(uri != null){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ignored){
                    StringBuilder sb = new StringBuilder();
                    try (InputStream inputStream =
                            getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(Objects.requireNonNull(inputStream)))){
                          String line;
                          while((line = reader.readLine()) != null)
                              sb.append(line);
                        } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}