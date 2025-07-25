package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Main;

/**
 * Pantalla que muestra el ranking de puntuaciones de los jugadores.
 * <p>
 * Muestra las puntuaciones individuales de Sonic, Knuckles y Tails,
 * Incluye un botón para volver al menú principal.
 *
 * @author Yoryelis Ocando
 * @version 1.0
 * @see BaseMenu
 * @see MenuPrincipal
 */
public class PantallaRanking extends BaseMenu {
    /** Fondo animado de la pantalla */
    private final Fondo fondo;

    /**
     * Constructor de la pantalla de ranking.
     *
     * @param game Referencia a la clase principal del juego
     */
    public PantallaRanking(Main game) {
        super(game);
        fondo = new Fondo();
    }

    /**
     * Método llamado cuando esta pantalla se convierte en la actual.
     */
    @Override
    public void show() {
        // No se requiere inicialización adicional
    }

    /**
     * Dibuja un botón con texto y detecta si ha sido presionado.
     *
     * @param batch SpriteBatch para dibujar
     * @param font Fuente a utilizar para el texto
     * @param layout Layout para calcular dimensiones del texto
     * @param botonTex Textura del botón
     * @param texto Texto a mostrar en el botón
     * @param x Posición X del botón
     * @param y Posición Y del botón
     * @param width Ancho del botón
     * @param height Alto del botón
     * @return true si el botón fue presionado, false en caso contrario
     */
    private boolean dibujarBotonConTexto(SpriteBatch batch, BitmapFont font, GlyphLayout layout,
                                         Texture botonTex, String texto,
                                         float x, float y, float width, float height) {
        // Dibujar el botón
        batch.draw(botonTex, x, y, width, height);

        // Configurar y dibujar el texto centrado
        font.setColor(1, 1, 1, 1); // Color blanco
        layout.setText(font, texto);
        float textoX = x + width / 2f - layout.width / 2f;
        float textoY = y + height / 2f + layout.height / 2f;
        font.draw(batch, texto, textoX, textoY);

        // Verificar si se hizo clic en el botón
        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();

        return Gdx.input.justTouched()
            && mx >= x && mx <= x + width
            && my >= y && my <= y + height;
    }

    /**
     * Método principal de renderizado llamado cada frame.
     *
     * @param delta Tiempo transcurrido desde el último frame en segundos
     */
    @Override
    public void render(float delta) {
        // Actualizar y dibujar el fondo
        fondo.actualizar(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Dibujar fondo animado
        fondo.dibujar(batch, screenW, screenH);

        // Título de la pantalla
        font.setColor(1, 1, 0, 1); // Amarillo
        font.draw(batch, "RANKING DE JUGADORES", 100, 450);

        // Puntajes individuales de los personajes
        font.setColor(0, 0, 1, 1); // Azul
        font.draw(batch, "1. Sonic      - " + Constantes.SCORE[0] + " pts", 100, 390);
        font.draw(batch, "2. Knuckles   - " + Constantes.SCORE[1] + " pts", 100, 360);
        font.draw(batch, "3. Tails      - " + Constantes.SCORE[2] + " pts", 100, 330);

        // Calcular y mostrar puntaje total
        int total = Constantes.SCORE[0] + Constantes.SCORE[1] + Constantes.SCORE[2];
        font.setColor(1, 0.5f, 0, 1); // Naranja
        font.draw(batch, "TOTAL: " + total + " pts", 100, 290);

        // Dibujar botón VOLVER centrado en la parte inferior
        float botonVolverX = (Gdx.graphics.getWidth() - botonW) / 2f;
        float botonVolverY = 100;
        boolean clicVolver = dibujarBotonConTexto(batch, font, layout, boton, "VOLVER",
            botonVolverX, botonVolverY, botonW, botonH);

        batch.end();

        // Manejar clic en el botón VOLVER
        if (clicVolver) {
            game.setScreen(new MenuPrincipal(game));
        }
    }

    // Métodos del ciclo de vida no implementados
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    /**
     * Libera los recursos utilizados por esta pantalla.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondo.dispose();
        boton.dispose();
    }
}
