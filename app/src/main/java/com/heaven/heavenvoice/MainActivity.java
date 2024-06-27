package com.heaven.heavenvoice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private CardView spkBtn;
    private ImageView clearBtn;
    private TextToSpeech textToSpeech;
    private TextView speak;
    private LottieAnimationView line, voice;

    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    private String nUtteranceID = "TextToSpeechAudio";
    private boolean isSpeaking = false; // To track if TTS is currently speaking

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        spkBtn = findViewById(R.id.spkBtn);
        speak = findViewById(R.id.speak);
        line = findViewById(R.id.line);
        voice = findViewById(R.id.voice);
        clearBtn= findViewById(R.id.clearBtn);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Set US English as the language for TTS
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        spkBtn.setEnabled(false); // Disable the button if language not supported
                    } else {
                        spkBtn.setEnabled(true); // Enable the button if language is supported
                    }
                } else {
                    spkBtn.setEnabled(false); // Disable the button on failure
                }
            }
        });

        // Set up UtteranceProgressListener to track TTS completion
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Not used in this implementation
            }

            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (utteranceId.equals(nUtteranceID)) {
                            speak.setText("Speak Now");
                            isSpeaking = false;
                            line.setVisibility(View.VISIBLE);
                            voice.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {
                // Not used in this implementation
            }
        });

        spkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString().trim(); // Get text from TextInputEditText
                if (text.isEmpty()) {
                    Toast.makeText(MainActivity.this,"Write something", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isSpeaking) {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, nUtteranceID);
                    speak.setText("Stop");
                    isSpeaking = true;
                    line.setVisibility(View.GONE);
                    voice.setVisibility(View.VISIBLE);
                } else {
                    // Stop speaking
                    textToSpeech.stop();
                    speak.setText("Speak Now");
                    isSpeaking = false;
                    line.setVisibility(View.VISIBLE);
                    voice.setVisibility(View.GONE);
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString().trim(); // Get text from TextInputEditText
                if (!text.isEmpty()) {
                    editText.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Nothing to Clear", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.alert_logo)
                .setTitle("Permission Required!")
                .setMessage("Do you Really want to Exit?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user clicked "No", dismiss the dialog and do nothing
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user clicked "Yes", close the activity and exit the app
                        dialog.dismiss();
                        finishAndRemoveTask();
                    }
                })
                .show();
    }
}
