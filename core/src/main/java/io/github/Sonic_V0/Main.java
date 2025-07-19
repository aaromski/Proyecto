package io.github.Sonic_V0;

import com.badlogic.gdx.Game;
import io.github.Sonic_V0.Menu.MenuPrincipal;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public MenuPrincipal menuPrincipal;
    @Override
    public void create() {
        menuPrincipal = new MenuPrincipal(this);  // Instancia única del menú
        setScreen(menuPrincipal);
    }
}
