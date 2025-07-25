package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Clase que representa la cámara principal del juego.
 * <p>
 * Encapsula una {@link OrthographicCamera} para controlar la vista del jugador sobre el mundo.
 * Proporciona métodos para proyectar coordenadas y obtener el área visible actual como un rectángulo.
 * Ideal para detectar qué objetos están dentro del campo de visión o para posicionar HUD y elementos gráficos.
 *
 * @author Miguel Carreño
 */
public class Camara {

    /** Instancia de la cámara ortográfica utilizada para visualizar el mundo del juego. */
    private final OrthographicCamera camara;

    /**
     * Constructor que inicializa la cámara con una vista definida.
     * <p>
     */
    public Camara() {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 50f, 41.1f); // Ajusta según tu escala y resolución
        camara.update();
    }

    /**
     * Obtiene la instancia de {@link OrthographicCamera} utilizada.
     *
     * @return la cámara ortográfica del juego.
     */
    public OrthographicCamera getCamara() {
        return camara;
    }

    /**
     * Proyecta un conjunto de coordenadas del mundo al espacio de pantalla.
     * <p>
     *
     * @param cx Coordenada X en el mundo.
     * @param cy Coordenada Y en el mundo.
     * @param cz Coordenada Z (profundidad) en el mundo.
     * @return {@link Vector3} proyectado con las coordenadas en pantalla.
     */
    public Vector3 getProject(float cx, float cy, float cz) {
        return camara.project(new Vector3(cx, cy, cz));
    }

    /**
     * Calcula el área rectangular visible de la cámara en el mundo.
     * <p>
     *
     * @return un {@link Rectangle} que representa el área visible en el mundo.
     */
    public Rectangle getVistaRectangular() {
        return new Rectangle(
            camara.position.x - camara.viewportWidth / 2f,
            camara.position.y - camara.viewportHeight / 2f,
            camara.viewportWidth,
            camara.viewportHeight
        );
    }
}
