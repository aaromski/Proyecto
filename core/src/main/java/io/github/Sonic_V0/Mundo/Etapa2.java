package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.Sonic_V0.Personajes.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Clase que representa la segunda etapa del juego, gestionando enemigos y lógica específica.
 * <p>
 * Esta clase maneja la generación y actualización de enemigos (Robotnik y Robots)
 * durante la segunda etapa del juego, incluyendo su spawn, comportamiento y eliminación.
 *
 * @author Jesus
 * @version 1.0
 * @see Mundo
 * @see Robotnik
 * @see Robot
 * @see Etapa
 */
public class Etapa2 {
    /** Referencia al mundo del juego */
    private final Mundo mundo;
    /** Referencia al personaje Sonic */
    private final Sonic sonic;
    /** Referencia al personaje Tails */
    private final Tails tails;
    /** Referencia al personaje Knuckles */
    private final Knuckles knuckles;
    /** Referencia a la primera etapa del juego */
    private final Etapa etapa1;
    /** Lista de enemigos Robotnik activos */
    private final List<Robotnik> robotniks = new ArrayList<>();
    /** Lista de enemigos Robots activos */
    private final List<Robot> robots = new ArrayList<>();
    /** Puntos de entrada posibles para los enemigos */
    private final List<Vector2> puntosEntrada = new ArrayList<>();
    /** Temporizador para generación de enemigos */
    private float timer = 0f;
    /** Generador de números aleatorios */
    private final Random random = new Random();

    /**
     * Constructor de la segunda etapa.
     *
     * @param mundo Referencia al mundo del juego
     * @param sonic Referencia al personaje Sonic
     * @param tails Referencia al personaje Tails
     * @param knuckles Referencia al personaje Knuckles
     * @param etapa1 Referencia a la primera etapa del juego
     */
    public Etapa2(Mundo mundo, Sonic sonic, Tails tails, Knuckles knuckles, Etapa etapa1) {
        this.mundo = mundo;
        this.sonic = sonic;
        this.tails = tails;
        this.knuckles = knuckles;
        this.etapa1 = etapa1;

        // Configuración de puntos de entrada para enemigos
        puntosEntrada.add(new Vector2(3f, 21f)); // Izquierda-central
        puntosEntrada.add(new Vector2(25f, 3f));  // Abajo-central
        puntosEntrada.add(new Vector2(25f, 36f)); // Arriba-central
        puntosEntrada.add(new Vector2(47f, 22f)); // Derecha-central
    }

    /**
     * Actualiza el estado de la etapa cada frame.
     * <p>
     * Maneja:
     * - Generación periódica de Robotnik
     * - Actualización de enemigos existentes
     * - Eliminación de enemigos que cumplen condiciones
     *
     * @param delta Tiempo transcurrido desde el último frame en segundos
     */
    public void actualizar(float delta) {
        // Generar Robotnik si no hay ninguno activo
        if (robotniks.isEmpty()) {
            timer += delta;
            float intervalo = 60f; // Intervalo entre apariciones (60 segundos)
            if (timer >= intervalo) {
                timer = 0f;
                generarRobotnik();
            }
        }

        // Actualizar y limpiar Robotniks
        Iterator<Robotnik> robotnikIt = robotniks.iterator();
        while (robotnikIt.hasNext()) {
            Robotnik r = robotnikIt.next();
            r.actualizar(delta);
            if (r.isListoDespawn()) {
                if (r.getCuerpo() != null) {
                    mundo.getWorld().destroyBody(r.getCuerpo());
                }
                r.dispose();
                robotnikIt.remove();
            }
        }

        // Actualizar y limpiar Robots
        Iterator<Robot> robotIt = robots.iterator();
        while (robotIt.hasNext()) {
            Robot r = robotIt.next();
            r.actualizar(delta);
            if (r.estaListoParaEliminar()) {
                if (r.getCuerpo() != null) {
                    mundo.getWorld().destroyBody(r.getCuerpo());
                }
                robotIt.remove();
            }
        }
    }

    /**
     * Dibuja todos los enemigos de la etapa.
     *
     * @param batch SpriteBatch donde se dibujarán los enemigos
     */
    public void renderizar(SpriteBatch batch) {
        for (Robotnik r : robotniks) {
            r.render(batch);
        }
        for (Robot r : robots) {
            r.render(batch);
        }
    }

    /**
     * Obtiene un punto de entrada aleatorio para los enemigos.
     *
     * @return Vector2 con las coordenadas del punto de entrada seleccionado
     */
    private Vector2 getEntrada() {
        int index = random.nextInt(puntosEntrada.size());
        return puntosEntrada.get(index);
    }

    /**
     * Genera un nuevo enemigo Robotnik.
     * <p>
     * El objetivo se selecciona según los personajes disponibles (prioridad: Sonic > Tails > Knuckles)
     */
    private void generarRobotnik() {
        if(sonic.getCuerpo() != null) {
            Robotnik robotnik = new Robotnik(sonic.getCuerpo(), mundo, this, etapa1, getEntrada());
            robotniks.add(robotnik);
        } else if(tails.getCuerpo() != null) {
            Robotnik robotnik = new Robotnik(tails.getCuerpo(), mundo, this, etapa1, getEntrada());
            robotniks.add(robotnik);
        } else if(knuckles.getCuerpo() != null) {
            Robotnik robotnik = new Robotnik(knuckles.getCuerpo(), mundo, this, etapa1, getEntrada());
            robotniks.add(robotnik);
        }
    }

    /**
     * Libera los recursos utilizados por los enemigos de esta etapa.
     */
    public void dispose() {
        for (Robotnik r : robotniks) {
            r.dispose();
        }
    }
}
