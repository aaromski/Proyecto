package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.Sonic_V0.Personajes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase que representa una etapa del juego, gestionando la generación y comportamiento
 * de los enemigos robots y su interacción con los personajes jugables.
 *
 * @author Yoryelis Ocando
 * @version 1.0
 * @see Robot
 * @see Mundo
 */
public class Etapa {
    /** Referencia al mundo del juego */
    private final Mundo mundo;
    /** Referencia al personaje Sonic */
    private final Sonic sonic;
    /** Referencia al personaje Knuckles */
    private final Knuckles knuckles;
    /** Lista de robots enemigos activos en la etapa */
    private final List<Robot> robots = new ArrayList<>();
    /** Puntos de entrada posibles para los robots */
    private final List<Vector2> puntosEntrada = new ArrayList<>();
    /** Temporizador para generación de robots */
    private float timer = 0f;
    /** Generador de números aleatorios */
    private final Random random = new Random();
    /** Referencia al personaje Tails */
    private final Tails tails;

    /**
     * Constructor de la etapa del juego.
     *
     * @param mundo Referencia al mundo del juego
     * @param sonic Referencia al personaje Sonic
     * @param tails Referencia al personaje Tails
     * @param knuckles Referencia al personaje Knuckles
     */
    public Etapa(Mundo mundo, Sonic sonic, Tails tails, Knuckles knuckles) {
        this.mundo = mundo;
        this.sonic = sonic;
        this.tails = tails;
        this.knuckles = knuckles;

        // Configuración de puntos de entrada para los robots
        puntosEntrada.add(new Vector2(3f, 21f)); // Izquierda-central
        puntosEntrada.add(new Vector2(25f, 3f));  // Abajo-central
        puntosEntrada.add(new Vector2(25f, 36f)); // Arriba-central
        puntosEntrada.add(new Vector2(47f, 22f)); // Derecha-central
    }

    /**
     * Actualiza el estado de la etapa cada frame.
     *
     * @param delta Tiempo transcurrido desde el último frame en segundos
     */
    public void actualizar(float delta) {
        // Generar robots periódicamente
        timer += delta;
        float intervalo = 8f; // Intervalo entre generación de robots (8 segundos)
        if (timer >= intervalo) {
            timer = 0f;
            generarRobot();
        }

        // Actualizar comportamiento de los robots
        for (Robot r : robots) {
            if(r.sinObjetivo()) {
                r.seleccionarObjetivoMasCercano(obtenerObjetivosVivos());
            } else if (!r.getKO()) {
                r.seleccionarObjetivoMasCercano(obtenerObjetivosVivos());
            }
            r.actualizar(delta);
        }
    }

    /**
     * Dibuja todos los robots de la etapa.
     *
     * @param batch SpriteBatch donde se dibujarán los robots
     */
    public void renderizar(SpriteBatch batch) {
        for (Robot r : robots) {
            if (r.getCuerpo() != null) {
                r.render(batch);
            }
        }
    }

    /**
     * Obtiene un punto de entrada aleatorio para los robots.
     *
     * @return Vector2 con las coordenadas del punto de entrada seleccionado
     */
    public Vector2 getEntrada() {
        int index = random.nextInt(puntosEntrada.size());
        return puntosEntrada.get(index);
    }

    /**
     * Genera un nuevo robot en un punto de entrada aleatorio.
     */
    public void generarRobot() {
        Robot r = new Robot(getEntrada(), objetivoDisponible(), mundo);
        robots.add(r);
    }

    /**
     * Selecciona un objetivo disponible aleatorio entre los personajes vivos.
     *
     * @return Cuerpo físico del personaje seleccionado como objetivo, o null si no hay objetivos disponibles
     */
    private Body objetivoDisponible() {
        List<Amigas> posibles = new ArrayList<>();
        if (!sonic.getKO()) posibles.add(sonic);
        if (!tails.getKO()) posibles.add(tails);
        if (!knuckles.getKO()) posibles.add(knuckles);

        if (posibles.isEmpty()) return null;

        Amigas elegido = posibles.get(random.nextInt(posibles.size()));
        return elegido.getCuerpo();
    }

    /**
     * Obtiene la lista de personajes jugables que están vivos.
     *
     * @return Lista de personajes disponibles como objetivos
     */
    public List<Amigas> obtenerObjetivosVivos() {
        List<Amigas> activos = new ArrayList<>();
        if (!sonic.getKO()) activos.add(sonic);
        if (!tails.getKO()) activos.add(tails);
        if (!knuckles.getKO()) activos.add(knuckles);
        return activos;
    }

    /**
     * @return Lista de robots en estado destruido.
     */
    public List<Robot> destruirRobots() {
        List<Robot> listaDestruidos = new ArrayList<>();

        for (Robot robot : robots) {
            if (robot.getDestruido()) {
                listaDestruidos.add(robot);
            }
        }
        return listaDestruidos;
    }

    /**
     * Libera los recursos utilizados por todos los robots activos en la etapa.
     */
    public void dispose() {
        for (Robot r : robots) {
            r.dispose();
        }
    }
    }
