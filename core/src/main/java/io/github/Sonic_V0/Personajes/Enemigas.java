package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Clase abstracta que representa un enemigo genérico en el juego.
 * <p>
 * Proporciona la funcionalidad base para enemigos, incluyendo:
 * - Movimiento hacia un objetivo
 * - Detección y evasión de obstáculos
 * - Manejo de estados (KO, destruido)
 *
 * @author Yoryelis Ocando
 * @version 1.1
 * @see Personaje
 */
public abstract class Enemigas extends Personaje {
    /** Animación cuando el enemigo está derrotado (KO) */
    protected Animation<TextureRegion> KO;
    /** Cuerpo físico del objetivo al que persigue */
    protected Body objetivo;
    /** Indica si el enemigo ha sido destruido */
    protected boolean destruido = false;
    /** Última posición registrada para detectar atascos */
    private Vector2 ultimaPosicion;
    /** Tiempo que lleva atascado sin moverse */
    private float tiempoAtascado = 0f;
    /** Dirección actual de rodeo para evitar obstáculos */
    private Vector2 direccionRodeo = null;
    /** Tiempo que lleva en modo de rodeo */
    private float tiempoRodeando = 0f;
    /** Duración máxima del modo de rodeo */
    private final float DURACION_RODEO = 1f;

    /**
     * Constructor de la clase Enemigas.
     *
     * @param p Posición inicial del enemigo
     * @param w Mundo Box2D donde existirá el enemigo
     */
    public Enemigas(Vector2 p, World w) {
        super(p,w);
    }

    /**
     * Marca al enemigo como destruido, deteniendo su movimiento.
     */
    public void destruir() {
        ko = true;
        stateTime = 0f; // Reiniciar animación KO
        body.setLinearVelocity(0, 0); // Detener movimiento
    }

    /**
     * Establece un nuevo objetivo para el enemigo.
     *
     * @param pos Cuerpo físico del nuevo objetivo
     */
    public void setObjetivo(Body pos) {
        if(!ko) {
            this.objetivo = pos;
        }
    }

    /**
     * Verifica si el enemigo no tiene objetivo asignado.
     *
     * @return true si no tiene objetivo, false en caso contrario
     */
    public boolean sinObjetivo() {
        return objetivo == null;
    }

    /**
     * Actualiza el estado del enemigo cada frame.
     *
     * @param delta Tiempo transcurrido desde el último frame en segundos
     */
    @Override
    public void actualizar(float delta) {
        posicion = body.getPosition();

        // Si no hay objetivo, detener movimiento
        if (objetivo == null) {
            body.setLinearVelocity(0, 0);
            return;
        }

        stateTime += delta;

        // Detectar si está atascado (solo si no está en rodeo)
        if (direccionRodeo == null) {
            if (ultimaPosicion != null && posicion.dst(ultimaPosicion) < 0.03f) {
                tiempoAtascado += delta;
            } else {
                tiempoAtascado = 0f;
            }

            // Si lleva demasiado tiempo atascado, activar modo rodeo
            if (tiempoAtascado > 0.6f) {
                Vector2 direccionObjetivo = objetivo.getPosition().cpy().sub(posicion).nor();
                direccionRodeo = new Vector2(-direccionObjetivo.y, direccionObjetivo.x).nor();
                if (Math.random() < 0.5f) {
                    direccionRodeo.scl(-1); // Elegir dirección al azar
                }

                tiempoRodeando = 0f;
                tiempoAtascado = 0f;
            }
        }

        ultimaPosicion = posicion.cpy();

        // Comportamiento en modo rodeo
        if (direccionRodeo != null) {
            tiempoRodeando += delta;
            body.setLinearVelocity(direccionRodeo.cpy().scl(velocidad));

            // Finalizar modo rodeo después de la duración establecida
            if (tiempoRodeando >= DURACION_RODEO) {
                direccionRodeo = null;
            }

            frameActual = correr.getKeyFrame(stateTime, true);
            return;
        }

        // Movimiento normal hacia el objetivo
        Vector2 direccion = objetivo.getPosition().cpy().sub(posicion).nor().scl(velocidad);
        body.setLinearVelocity(direccion);

        // Ajustar orientación del sprite según dirección
        boolean haciaIzq = direccion.x < 0;
        if (frameActual != null) {
            if (haciaIzq && !frameActual.isFlipX()) {
                frameActual.flip(true, false);
            } else if (!haciaIzq && frameActual.isFlipX()) {
                frameActual.flip(true, false);
            }
        }

        frameActual = correr.getKeyFrame(stateTime, true);
    }

    /**
     * Activa el modo de rodeo para evitar un obstáculo.
     *
     * @param posicionObstaculo Posición del obstáculo a evitar
     */
    public void activarRodeo(Vector2 posicionObstaculo) {
        if (ko || direccionRodeo != null || objetivo == null) return;

        Vector2 direccionObjetivo = objetivo.getPosition().cpy().sub(body.getPosition()).nor();

        // Calcular dirección perpendicular para rodear
        direccionRodeo = new Vector2(-direccionObjetivo.y, direccionObjetivo.x).nor();

        // Elegir dirección al azar (izquierda o derecha)
        if (Math.random() < 0.5f) {
            direccionRodeo.scl(-1);
        }

        tiempoRodeando = 0f;
        tiempoAtascado = 0f;
    }
}
