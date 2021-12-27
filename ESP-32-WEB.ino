#include<ESP8266WiFi.h>
#include<Servo.h>
#include <Stepper.h>  

//VARIABLES DE PRE-PROCESADO

Stepper motor1(2048, 14, 26, 27, 25); //CONFIGURACIÓN DE PINES DEL ESP-32 PARA MOTOR PASO A PASO
const char* ssid = "nombre de tu red"; 
const char* password = "contraseña de tu red";
String text;
String line;
int bandera=0;
const char* host= "servidor o host";
int RX0 = 3;
int TX0 = 1; 
int demora = 10;
int sensor = 13;
int estadosensor;
String seguridad="false";
String seguro="false";
String O_P;
Servo servo;
int pinservo = 33;
int pulsomin = 1000;
int pulsomax = 2000;
int contsensor = 0;


//FUNCIONES

//funcion sensor de impacto 
void sensor_I(){
estadosensor = analogRead(sensor);
  if(estadosensor == HIGH){
    contsensor ++;
    if(contsensor == 3){
      seguridad="NO SEGURO";
    }
  }else{
      seguridad="SEGURO";
    }
}
//funcion del servomotor / se recibe un parametro String
void servo_A(){
 if(seguro=="true"){
     servo.write(0);
     delay(500);
  }if(seguro=="false")
  {
    servo.write(180);
 delay(500);
    }
}

//FUNCION DE MOTOR PASO A PASO
void motor() {
   
  if(seguro=="false"&&bandera==0){
       motor1.step(-512);        // signo menos indica giro en sentido opuesto
       bandera=1;
  delay(500);  
    }else{
      if(seguro=="true"&&bandera==1){             
      motor1.step(512);         // cantidad de pasos
      delay(500);  
      }
    }
}


//CONFIGURACIÓN DE PARAMETROS INICIALES
void setup(){
  Serial.begin(115200);
  delay(10);
  
  motor1.setSpeed(2);
  pinMode(sensor,INPUT);
  servo.attach(pinservo,pulsomin,pulsomax);
  
  Serial.println();
  Serial.println();
  Serial.print("Conectando con: ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);
  while(WiFi.status()!=WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("Conectando a WiFi");
  Serial.println("Direccion IP: ");
  Serial.println(WiFi.localIP());
  int value=0;
}


//PROGRAMA PRINCIPAL
void loop(){
  sensor_I();//LLAMADO FUNCION DE LECTURA DE SENSOR
  delay(2000);
  ++value;

  Serial.print("Conectando a: ");
  Serial.println(host);

  //CONEXIÖN AL HOST
  WiFiClient client;
  const int httpPort=80;
  if(!client.connect(host,httpPort)){
    Serial.println("Fallo la conexion");
  }
  
  String url="URL a donde vas a enviar los datos";
  String data="variables a modificar en el algoritmo alojado en el servidor";

  Serial.print("Consultando a: ");
  Serial.println(url);

  //SENTENCIA HTTP POR METODO POST
  client.print(String("POST ") + url + " HTTP/1.0\r\n" +
  "Host: " + host + "\r\n" + 
  "Accept: " + "/" + "\r\n" +
  "Content-Length: " + data.length() + "\r\n" +
  "Content-Type: application/x-www-form-urlencoded\r\n" +
  "\r\n" + data);
  delay(1000);

  Serial.println("Respuesta: ");
  text="";
  while(client.available()){
    String line=client.readStringUntil('/r');
    text = text+line;
  }
  Serial.print(text);
  
  //FRAGMENTO DE ALGORITMO QUE PERMITE EXTRAER LA RESPUESTA DEL HOST O SERVIDOR
  int cantidad_de_caracteres=text.length();
   Serial.print("tamaño: ");
  Serial.print(cantidad_de_caracteres);
  int ini=text.indexOf ("&");
  int fin=text.indexOf ("}");
  seguro = text.substring(ini+1, fin);
    
  motor();

  ini=text.indexOf ("%");
  fin=text.indexOf ("}");
  O_P = text.substring(ini+1, fin);
  Serial.println(O_P);
  
  servo_A();       
    
  Serial.println();

  Serial.println("Cerrando la conexion");

}
