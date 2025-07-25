package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Sonic_V0.*;
import io.github.Sonic_V0.Mundo.Camara;

/**
 * Clase abstracta base para todas las pantallas de menú del juego.
 * <p>
 * Proporciona funcionalidad común para:
 * - Manejo básico de elementos de UI
 * - Renderizado de botones
 * - Gestión de recursos compartidos
 *
 * @author Jesus
 * @see Screen
 */
public abstract class BaseMenu implements Screen {
    /** Cámara para manejar la vista del menú */
    protected Camara camara;
    /** Referencia a la clase principal del juego */
    protected final Main game;
    /** SpriteBatch para renderizado */
    protected SpriteBatch batch;
    /** Fuente para texto en el menú */
    protected BitmapFont font;
    /** Textura base para los botones */
    protected Texture boton;
    /** Layout para cálculo de dimensiones de texto */
    protected GlyphLayout layout;
    /** Ancho de la pantalla */
    protected int screenW;
    /** Alto de la pantalla */
    protected int screenH;
    /** Ancho estándar para los botones */
    protected float botonW = 250f;
    /** Alto estándar para los botones */
    protected float botonH = 60f;

    /**
     * Constructor de la clase BaseMenu.
     *
     * @param Game Referencia a la clase principal del juego
     */
    public BaseMenu(Main Game) {
        this.game = Game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
        boton = new Texture("Menu/button.png"); // Textura base para botones
        camara = new Camara();
    }

    /**
     * Método llamado cuando la pantalla cambia de tamaño.
     *
     * @param width Nuevo ancho de la pantalla
     * @param height Nuevo alto de la pantalla
     */
    @Override
    public void resize(int width, int height) {
        screenW = width;
        screenH = height;
    }

    /**
     * Libera los recursos utilizados por el menú.
     * <p>
     * Las clases hijas deben llamar a super.dispose() si sobrescriben este método.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        boton.dispose();
    }

    // Métodos de Screen no implementados (deben ser implementados por las clases hijas)
    @Override public abstract void show();
    @Override public abstract void render(float delta);
    @Override public abstract void pause();
    @Override public abstract void resume();

}
