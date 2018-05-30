package univ.m2acdi.apprentissageborel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import univ.m2acdi.apprentissageborel.R;
import univ.m2acdi.apprentissageborel.util.BMObject;
import univ.m2acdi.apprentissageborel.util.SpeechRecognizeManager;
import univ.m2acdi.apprentissageborel.util.Util;


public class GestureToSpeechActivity extends Activity {

    //private TextView textView ;
    private ImageView imageView ;
    private ImageButton imageButtonNext;
    private ImageButton imageButtonPrec;
    private GifImageView gifImageView;
    private TextView texteViewLettres;
    private ImageView wordImgIndice;
    private ImageView imageViewMicro;
    private TextView textUser;

    private static int index = 0;
    private static Boolean flag = false;

    private BMObject bmObject;
    private static JSONArray jsonArray;

    private SpeechRecognizeManager speechRecognizeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_to_speech);

        jsonArray = Util.getJsonArrayDataFromIntent(getIntent(), "jsonArray");

        bmObject = Util.readNextWord(jsonArray, index);
        texteViewLettres = findViewById(R.id.texteViewLettres);
        //gifImageView = findViewById(R.id.gifImageView);
        imageView = findViewById(R.id.word_img_view);
        textUser = findViewById(R.id.text_user);
        wordImgIndice = findViewById(R.id.word_img_view);
        imageView.setImageDrawable(Util.getImageViewByName(getApplicationContext(), bmObject.getGeste()));
        imageButtonNext = findViewById(R.id.btn_next);
        imageButtonPrec = findViewById(R.id.btn_prec);
        imageViewMicro = findViewById(R.id.micro);
        wordImgIndice = findViewById(R.id.word_img_indice);

        speechRecognizeManager = new SpeechRecognizeManager(this);

        speechRecognizeManager.initVoiceRecognizer(recognitionListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //imageButtonNext.setEnabled(flag);
        imageButtonNext.setOnClickListener(onClickListener);
        imageButtonPrec.setOnClickListener(onClickListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btn_next:
                    if(index==jsonArray.length() - 1){
                        index = 0;
                    }else{
                        ++index;
                    }
                    clearComponent();
                    break;
                case R.id.btn_prec:
                    if(index == 0){
                        index = jsonArray.length() - 1;
                    }else{
                        --index;
                    }
                    clearComponent();
                    break;
            }
            bmObject = Util.readNextWord(jsonArray, index);
            imageView.setImageDrawable(Util.getImageViewByName(getApplicationContext(), bmObject.getGeste()));
        }
    };

    /**
     * Méthode pour renitialiser les différents composants
     */

    public void clearComponent(){

        texteViewLettres.setText("");
        textUser.setText("");
        wordImgIndice.setImageDrawable(Util.getImageViewByName(getApplicationContext(), "imageview_border"));
    }

    /**
     * Méthode pour l'écoute du clic sur l'imageButton permettant d'afficher l'image à découvrir
     */
    public void showIndice(View v) {
        wordImgIndice.setImageDrawable(Util.getImageViewByName(getApplicationContext(), bmObject.getImgMot()));
    }

    /**
     * Méthode pour l'écoute du clic sur le button activant le microphone
     * @param v
     */
    public void startListening(View v) {
        imageViewMicro.setImageDrawable(Util.getImageViewByName(getApplicationContext(), "icon_micro_on"));
        speechRecognizeManager.startListeningSpeech();
    }


    protected RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            imageViewMicro.setImageDrawable(Util.getImageViewByName(getApplicationContext(), "icon_micro_off"));
        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {

            imageViewMicro.setImageDrawable(Util.getImageViewByName(getApplicationContext(), "icon_micro_off"));
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++){
                System.out.println(data.get(i));
            }
            for (int i = 0; i < data.size(); i++){
                if(data.get(i).toLowerCase().contains(bmObject.getMotRef().toLowerCase())){
                    wordImgIndice.setImageDrawable(Util.getImageViewByName(getApplicationContext(), bmObject.getImgMot()));
                    texteViewLettres.setText(bmObject.getSon());
                    textUser.setText(data.get(i));
                    Util.showCongratDialog(GestureToSpeechActivity.this);

                    break;
                }
            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

}
