package io.github.Sonic_V0;

import com.badlogic.gdx.Game;
import io.github.Sonic_V0.Menu.MenuPrincipal;

/**
 * Clase principal del juego que extiende {@link Game}, utilizada para iniciar y administrar pantallas.
 *
 * En el método {@code create()}, se inicializa la pantalla principal del menú.
 *
 * @author Aarom
 */
public class Main extends Game {

    /** Instancia del menú principal que actúa como la primera pantalla del juego. */
    public MenuPrincipal menuPrincipal;

    /**
     * Método llamado al iniciar la aplicación.
     * Se configura la pantalla inicial con una instancia de {@link MenuPrincipal}.
     */
    @Override
    public void create() {
        menuPrincipal = new MenuPrincipal(this);  // Instancia única del menú
        setScreen(menuPrincipal);
    }
}
