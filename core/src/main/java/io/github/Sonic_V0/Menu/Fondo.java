package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Clase que representa el fondo desplazable del menú principal.
 *
 * Se encarga de cargar una textura de fondo y desplazarla horizontalmente
 * para generar un efecto de scroll continuo.
 *
 * También proporciona métodos para dibujar el fondo y liberar recursos cuando ya no se necesita.
 *
 * @author Aarom Luces
 */
public class Fondo {

    /** Textura del fondo utilizada en el menú */
    static Texture fondo;

    /** Valor de desplazamiento horizontal */
    private float scrollX;

    /**
     * Constructor que carga la textura del fondo desde el recurso.
     */
    public Fondo() {
        fondo = new Texture("Menu/Sunset_Hill.png");
    }

    /**
     * Actualiza la posición de desplazamiento del fondo en función del tiempo.
     *
     * @param delta Tiempo transcurrido desde el último frame (en segundos).
     */
    public void actualizar(float delta) {
        // Desplazamiento del fondo
        scrollX -= delta * 50f; // velocidad de desplazamiento
        if (scrollX <= -fondo.getWidth()) scrollX += fondo.getWidth();
    }

    /**
     * Dibuja el fondo en pantalla de manera continua para cubrir todo el ancho visible.
     *
     * @param batch   Lienzo de dibujo de LibGDX.
     * @param screenW Ancho de la pantalla.
     * @param screenH Alto de la pantalla.
     */
    public void dibujar(SpriteBatch batch, int screenW, int screenH) {
        // Dibujar copias encadenadas del fondo
        for (int x = (int) scrollX; x < screenW; x += fondo.getWidth()) {
            batch.draw(fondo, x, 0, fondo.getWidth(), screenH);
        }
    }

    /**
     * Retorna el ancho de la textura del fondo.
     *
     * @return Ancho en píxeles de la textura del fondo.
     */
    public int getWidth() {
        return fondo.getWidth();
    }

    /**
     * Libera los recursos gráficos asociados a la textura del fondo.
     */
    public void dispose() {
        fondo.dispose();
    }
}
