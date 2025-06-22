package io.github.Sonic_V0;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
       // setScreen(new FirstScreen()); //Principal
        setScreen(new VentanaPrueba()); //Prueba
    }
}
