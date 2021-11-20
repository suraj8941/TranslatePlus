package com.sssappdeveloper.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView micTV;
    private MaterialButton translateBtn;
    private TextView translatedTV;
    String[] fromLanguages = {"From", "English", "Afrikaans", "Arabic", "Belarusian", "Bengali", "Catalan", "Czech", "Welsh", "Hindi", "Urdu"};
    String[] toLanguages = {"to", "English", "Afrikaans", "Arabic", "Belarusian", "Bengali", "Catalan", "Czech", "Welsh", "Hindi", "Urdu"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    String languageCode, fromLanguageCode, tolanguageCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);
        sourceEdt = findViewById(R.id.idEdtSource);
        micTV = findViewById(R.id.idIVMic);
        translateBtn = findViewById(R.id.idBtnTranslate);
        translatedTV = findViewById(R.id.idTVTranslatedTV);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tolanguageCode = getLanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedTV.setText("");
                if (sourceEdt.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Your text to translate", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode == "") {
                    Toast.makeText(MainActivity.this, "Please select source language", Toast.LENGTH_SHORT).show();
                } else if (tolanguageCode == "") {
                    Toast.makeText(MainActivity.this, "Please select the lanaguge to make translation", Toast.LENGTH_SHORT).show();
                } else {
                    translateText(fromLanguageCode, tolanguageCode, sourceEdt.getText().toString());
                }
            }
        });

        micTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to covert into text");
                try {
                    startActivityForResult(i, REQUEST_PERMISSION_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    protected void onActivitiyResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_PERMISSION_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));
            }
        }
    }

    private void translateText(String fromLanguageCode, String tolanguageCode, String source) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(tolanguageCode)
                        .build();
        final Translator MTranslator =
                Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder().build();
        MTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                translatedTV.setText("Translating..");
                                MTranslator.translate(source).addOnSuccessListener(new OnSuccessListener<String>(){
                                    @Override
                                    public void onSuccess(String s){
                                        translatedTV.setText(s);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Failed to translate"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Fail to download language modal"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

        MTranslator.translate(source)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                translatedTV.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to Translate", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    public String getLanguageCode(String language) {
        String languageCode = "";
        switch (language) {
            case "English":
                languageCode = TranslateLanguage.ENGLISH;
            break;
            case "Afrikaans":
                languageCode = TranslateLanguage.AFRIKAANS;
                break;
            case "Arabic":
                languageCode = TranslateLanguage.ARABIC;
                break;
            case "Belarusian":
                languageCode = TranslateLanguage.BELARUSIAN;
                break;
            case "Bengali":
                languageCode = TranslateLanguage.BENGALI;
                break;
            case "Catalan":
                languageCode = TranslateLanguage.CATALAN;
                break;
            case "Czech":
                languageCode = TranslateLanguage.CZECH;
                break;
            case "Welsh":
                languageCode = TranslateLanguage.WELSH;
                break;
            case "Hindi":
                languageCode = TranslateLanguage.HINDI;
                break;
            case "Urdu":
                languageCode = TranslateLanguage.URDU;
                break;
            default:
                languageCode="";

        }
      return languageCode;
    }
}