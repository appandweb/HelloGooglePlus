# HelloGooglePlus
Sample project to work with Google+ API

Contributors
------------

Jose Armando Albarado [@josearmandoam](https://github.com/josearmandoam)

### Introduccion de Google Sign-In a nuestra Aplicación Android

Para introducir el Google Sign-In en nuestra aplicación Android, lo primero que debemos de realizar es obtener el archivo de configuración que nos proporciona Google: `google-services.json`, por tanto accedemos a [Get Configuration File](https://developers.google.com/mobile/add?platform=android&cntapi=signin&cnturl=https:%2F%2Fdevelopers.google.com%2Fidentity%2Fsign-in%2Fandroid%2Fsign-in%3Fconfigured%3Dtrue&cntlbl=Continue%20Adding%20Sign-In), una vez dentro nos pedirá información de nuestra App:

* Nombre de nuestra aplicación

* Nombre del paquete de nuestra aplicación

En el primer campo introducimos el nombre de nuestra aplicación.

Para rellenar el siguiente campo, debemos dirigirnos al AndroidManifest.xml de nuestra aplicación, una vez dentro, en la etiqueta <manifest> hay un atributo “package” que nos indicara el nombre del paquete de nuestra aplicación, pues bien copiamos el nombre y lo pegamos en el campo solicitado mencionado anteriormente:

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.appandweb.examplegooglesignin">

```

Una vez realizado esto pulsamos en continuar, esperamos un momento y nos aparecerá una pantalla con los servicios que ofrece Google para los desarrolladores, por un lado tenemos Google Sign-In que es con el que vamos a trabajar, pero también disponemos de otras herramientas como Cloud Messaging y Analytics, estas dos últimas herramientas las detallaremos con exactitud en un futuro, la gran utilidad que tienen y la forma de implementarlas en nuestras Aplicaciones.

Pues bien, como se ha citado anteriormente la herramienta con la que trabajaremos sera Google Sign-In,   por tanto seleccionamos dicha opción, una vez seleccionada se desplegará un campo el cual nos solicitará la clave SHA-1 de nuestra aplicación, para obtenerlo debemos seguir los siguiente pasos:

1. Abrimos nuestro proyecto Android

2. Seleccionamos Gradle, el cual se encuentra en el panel derecho del proyecto.

3. Una vez abierto el panel de Gradle, hacemos click en “Refresh all gradle projects”

3. Seleccionamos nuestro proyecto

4. Seleccionamos Tasks

5. Seleccionamos Android

6. Hacemos doble Click en “signinReport”

7. Nos aparecerá debajo información acerca de nuestras claves, buscamos la clave SHA-1 *

> *Si nos aparece la descripción de lo que hemos realizado con el tiempo consumido para su ejecución, es decir, como “Run Build ………. 1s 203ms ” etc. En este caso debemos selecciona al botón (/ab) que se encuentra justo a su izquiera (“Toogle Task executions/text mode”).

Una vez obtenida la clave SHA-1, la copiamos y la introducimos en el campo requerido.
Presionamos sobre el botón “Enable Google Sign-In”, esperamos un momento y la opción de “Generate configuration files” se activará, pulsamos sobre dicha opción una vez que esté disponible.

Si hemos realizado todos los pasos anteriores correctamente, se nos permitirá descargar el archivo `google-services.json`, lo descargamos y lo introducimos dentro de la carpeta “app” de nuestro proyecto es decir:  <PROJECT_FOLDER>/app/

Procedemos ahora a crear una **web client ID**, para ello debemos abrir la [Credentials page](https://console.developers.google.com/apis/credentials) , dentro en la parte de arriba a la izquierda aparecerá el nombre del proyecto en el que estamos, hacemos click encima, aparecerá un listado de todos nuestros proyectos creados, vamos a crear un nuevo proyecto con el nombre que deseemos, es recomendable ponerle el nombre de nuestro proyecto Android para no perdernos después a la hora de buscarlo.

Una vez creado el proyecto, vamos a crear un web client ID, para ello  hacemos click en “Crear credenciales” y seleccionamos la opcion de “ID de cliente de Oauth”, a continuación seleccionamos la opción “Web” o “Web application”, la que tengamos disponible en mi caso es “Web”, para finalizar hacemos click en “Crear”.

Si hemos realizado los pasos anteriores correctamente, habremos creado una credencia Web con su correspondiente “ID de cliente”.

Procedemos ahora a trabajar en nuestro proyecto Android, lo primero que debemos hacer dirigirnos al archivo Strings.xml y crear un recurso string almacenando dentro el valor de “ID de cliente” de la credencial web que hemos creado anteriormente.

A continuación importaremos:

1. En el archivo build.gradle a nivel de proyecto introducimos:

```javascript

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
       //...
        classpath 'com.google.gms:google-services:3.0.0'
        // ...
    }
}

```

2. En el archivo build.gradle de nivel de app añadimos al final:

```javascript

dependencies {
// ...
}
apply plugin: 'com.google.gms.google-services'

```

3. En el archivo  build.gradle de nivel de app, en las dependencias introducimos:

```javascript

dependencies {
// ...
    compile 'com.google.android.gms:play-services-auth:10.2.0'
 // ...
}

```

4. Compilar el proyecto para comprobar que todo se ha añadido correctamente.

Si compila sin ningún problema, procedemos a integrar el botón de sign-in, en caso contrario revisa los pasos anteriores con atención porque hasta el momento no debería de fallar nada.

Procedemos por tanto a introducir el botón de Sign-in en nuestro layout , para ello debemos introducir lo siguiente:

```xml

<com.google.android.gms.common.SignInButton
android:id="@+id/sign_in_button"
android:layout_width="wrap_content"
android:layout_height="wrap_content" />

```

Una vez realizado esto, nos dirigimos a la vista en la que trabajaremos con el Sign-In, en mi caso mi vista es SignInActivity, procedemos ahora a configurar Google Sign-In y el GoogleApiClient:
1. En nuestro método onCreate() , creamos un objeto GoogleSingInOptions y lo inicializamos de la forma:

```javascript

GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.server_client_id))
        .requestEmail()
        .build();

```

> *El recurso **server_client_id** almacena la informacion de ID de cliente que guardamos antes como un recurso tipo string.

2. Creamos un objeto GoogleApiClient, pero esta vez no será una variable local ya que después la necesitaremos. Su definición también irá dentro de onCreate().

```javascript

mGoogleApiClient = new GoogleApiClient.Builder(this)
        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();

```

Una vez hecho esto, debemos implementar **OnConnectionFailedListener** a nuestra clase e introducir lo que queramos hacer en caso de que la conexión falle en el método sobrecargado:

```javascript

@Override
public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
	// ...
}

```

3. Nuestro método **onCreate()** debe quedar de la forma:

```javascript

@Override
protected void onCreate(Bundle savedInstanceState) {
   // ...
GoogleSignInOptions gso = new 	GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
	.requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build();
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();
}

```

Ahora vamos a trabajar con botón de login, procederemos a identificarlo y darle funcionalidad:
1.  Creamos un objeto SignInButton y lo identicamos de la forma siguiente:

```javascript

SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
signInButton.setSize(SignInButton.SIZE_STANDARD);

```

 2. Le asignamos un OnClickListener al botón:

```javascript

signInButton.setOnClickListener(this);

```

3. Debemos implementar OnClickListener a nuestra clase y sobrecargamos el metodo onClick definiendo dentro lo siguiente:

```javascript

@Override
public void onClick(View view) {
    switch (view.getId()) {
        case R.id.sign_in_button:
            signIn();
            break;
        // ...
    }
}

```

4. Definimos el metodo signIn() :

```javascript

private void signIn() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
}

```

La constante _RC_SIGN_IN_ tiene el valor de **9001**.
Una vez realizado esto, debemos controlar la respuesta que recibimos al lanzar el Intent, es decir, debemos sobrecargar **onActivityResult(...)**:

```javascript

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }
}

```

Definimos el método handleSignInResult(result) de la siguiente forma:

```javascript

private void handleSignInResult(GoogleSignInResult result) {
    if (result.isSuccess()) {
        GoogleSignInAccount acct = result.getSignInAccount();
        Toast.makeText(this, "Login Success " + acct.getEmail() + " - " + acct.getGivenName(), Toast.LENGTH_SHORT).show();
    } else {
        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
    }
}

```

La variable **acct** almacenará toda la información que solicitamos del usuario logueado, con ella podriamos mostrar su email, cargar su imagen de perfil etc.

Ahora podemos probar la aplicación y debe funcionar todo correctamente, un saludo.
