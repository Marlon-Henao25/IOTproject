package com.example.iotproject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    private ImageButton abrir;
    private ImageButton cerrar;
    private TextView mensaje;
    private String Seguro="false";
    private String O_P="false";
    private String Seguridad="";
    private String URL="https://iot-project-uc.000webhostapp.com/EnviarDatosA.php";
    private String URL2="https://iot-project-uc.000webhostapp.com/RecibirDatosB.php";

    private static final int RECOGNIZE_SPEECH_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_king);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageButton logout = (ImageButton) findViewById(R.id.imageView2);
        ImageButton abrir = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton cerrar= (ImageButton) findViewById(R.id.imageButton3);
        ImageButton refrescar= (ImageButton) findViewById(R.id.imageButton4);
        TextView mensaje= (TextView) findViewById(R.id.textView5);

        refrescar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar2(URL2);
                mensaje.setText(Seguridad);
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar(URL);
                O_P="false";
            }
        });

        abrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar(URL);
                O_P="true";
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(MainActivity2.this, "Cierre de sesión realizado", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RECOGNIZE_SPEECH_ACTIVITY:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> speech = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String strSpeech2Text = speech.get(0);
                    if(strSpeech2Text.equals("close the door") || strSpeech2Text.equals("cerrar puerta") ){
                        Toast.makeText(MainActivity2.this, "Puerta CERRADA", Toast.LENGTH_LONG).show();
                        Seguro="false";
                    }
                    if(strSpeech2Text.equals("open the door")  || strSpeech2Text=="abrir puerta"){
                        Toast.makeText(MainActivity2.this, "Puerta ABIERTA", Toast.LENGTH_LONG).show();
                        Seguro="true";
                    }
                    validar(URL);
                }
                break;
            default:
                break;
        }
    }
    public void onClickImgBtnHablar(View v) {
        Intent intentActionRecognizeSpeech = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Configura el Lenguaje (Español-México)
        intentActionRecognizeSpeech.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        try {
            startActivityForResult(intentActionRecognizeSpeech,
                    RECOGNIZE_SPEECH_ACTIVITY);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Tú dispositivo no soporta el reconocimiento por voz",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void validar(String URL){
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Resultado Registrado")){
                    Toast.makeText(MainActivity2.this, "DATOS ENVIADOS", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity2.this, "DATOS NO ENVIADOS "+ response, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<String, String>();
                parametros.put("Seguro", Seguro);
                parametros.put("O_P", O_P);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void validar2(String URL){
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Seguridad=response;
                Toast.makeText(MainActivity2.this, "Datos extraidos Correctamente "+response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
