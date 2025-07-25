package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Main;

import java.util.Arrays;

/**
 * Clase que representa el menú principal del juego.
 * <p>
 * Maneja la lógica y renderizado de la pantalla de inicio, incluyendo:
 * - Animación de entrada del logo
 * - Botones de navegación principales
 * - Transiciones a otras pantallas
 *
 * @author Jesus
 * @version 1.0
 * @see BaseMenu
 * @see PantallaJuego
 * @see PantallaAyuda
 * @see PantallaRanking
 */
public class MenuPrincipal extends BaseMenu {
    /** Textura del logo del juego */
    private Texture logo;
    /** Fondo animado del menú */
    private final Fondo fondo;
    /** Posición X inicial para los botones */
    private final int startX = Gdx.graphics.getWidth() / 2 - (int)botonW / 2;
    /** Posición Y inicial para los botones */
    private final int startY = 300;
    /** Espaciado vertical entre botones */
    private final int espacio = 70;
    /** Valor alpha para la animación de fade-in del logo */
    private float logoAlpha = 0f;
    /** Posición Y actual del logo durante la animación */
    private float logoYactual;
    /** Posición Y final del logo después de la animación */
    private final float logoYfinal = Gdx.graphics.getHeight() - 120 - 30;

    /**
     * Constructor del menú principal.
     *
     * @param game Referencia a la clase principal del juego
     */
    public MenuPrincipal(Main game) {
        super(game);
        fondo = new Fondo();
    }

    /**
     * Método llamado cuando esta pantalla se convierte en la actual.
     * <p>
     * Inicializa los recursos necesarios para el menú.
     */
    @Override
    public void show() {
        logo = new Texture("Menu/sonicmania.png");
        logoYactual = logoYfinal + 100f; // Comienza más arriba para la animación
    }

    /**
     * Método principal de renderizado llamado cada frame.
     *
     * @param delta Tiempo transcurrido desde el último frame en segundos
     */
    @Override
    public void render(float delta) {
        fondo.actualizar(delta);

        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Dibujar fondo
        fondo.dibujar(batch, screenW, screenH);

        // Animación del logo (fade-in + movimiento)
        if (logoAlpha < 1f) logoAlpha += delta;
        if (logoYactual > logoYfinal) logoYactual -= delta * 80f;
        if (logoYactual < logoYfinal) logoYactual = logoYfinal;

        // Dibujar logo con alpha animado
        batch.setColor(1, 1, 1, logoAlpha);
        int logoW = 300, logoH = 120;
        int logoX = screenW / 2 - logoW / 2;
        batch.draw(logo, logoX, logoYactual, logoW, logoH);
        batch.setColor(Color.WHITE);

        // Dibujar botones del menú
        for (int i = 0; i < 4; i++) drawBoton(getTextoBoton(i), i);

        batch.end();

        // Manejar clics en botones
        if (Gdx.input.justTouched()) {
            int mx = Gdx.input.getX();
            int my = screenH - Gdx.input.getY();

            for (int i = 0; i < 4; i++) {
                int by = startY - i * espacio;
                if (mx >= startX && mx <= startX + botonW && my >= by && my <= by + botonH) {
                    switch (i) {
                        case 0: // PLAY
                            reiniciar();
                            game.setScreen(new PantallaJuego(game));
                            break;
                        case 1: // AYUDA
                            game.setScreen(new PantallaAyuda(game));
                            break;
                        case 2: // RANKING
                            game.setScreen(new PantallaRanking(game));
                            break;
                        case 3: // SALIR
                            Gdx.app.exit();
                            break;
                    }
                }
            }
        }
    }

    /**
     * Obtiene el texto correspondiente a cada botón del menú.
     *
     * @param i Índice del botón (0-3)
     * @return Texto que debe mostrar el botón
     */
    private String getTextoBoton(int i) {
        switch (i) {
            case 0: return "PLAY";
            case 1: return "AYUDA";
            case 2: return "RANKING";
            default: return "SALIR";
        }
    }

    /**
     * Dibuja un botón del menú en pantalla.
     *
     * @param texto Texto a mostrar en el botón
     * @param index Posición del botón (0 = arriba, 3 = abajo)
     */
    private void drawBoton(String texto, int index) {
        int y = startY - index * espacio;
        batch.draw(boton, startX, y, botonW, botonH);
        font.setColor(1, 1, 1, 1);
        layout.setText(font, texto);
        float textoX = startX + botonW / 2f - layout.width / 2f;
        float textoY = y + botonH / 2f + layout.height / 2f;
        font.draw(batch, texto, textoX, textoY);
    }

    /**
     * Reinicia los valores del juego a su estado inicial.
     * <p>
     * Restablece:
     * - Vidas de los jugadores
     * - Puntuaciones
     */
    public void reiniciar() {
        // Reiniciar vidas
        Arrays.fill(Constantes.VIDAS, 3);

        // Reiniciar score
        Arrays.fill(Constantes.SCORE, 0);
    }

    // Métodos de ciclo de vida no implementados
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    /**
     * Libera todos los recursos utilizados por este menú.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondo.dispose();
        boton.dispose();
        logo.dispose();
    }
}
