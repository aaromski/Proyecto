package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Clase abstracta base para los personajes "amigables" del juego (Sonic, Tails, Knuckles).
 * <p>
 * Define propiedades y comportamientos comunes a estos personajes, como el manejo
 * de animaciones de movimiento, habilidades y el estado de teletransporte.
 *
 * @author Miguel Rivas
 * @version 1.0
 * @see Personaje
 * @see Sonic
 * @see Tails
 * @see Knuckles
 */
public abstract class Amigas extends Personaje {
    /** Indica si el personaje se mueve a la izquierda. */
    protected boolean izq = false;
    /** Indica si el personaje se mueve a la derecha. */
    protected boolean der = false;
    /** Indica si el personaje se mueve hacia arriba. */
    protected boolean arr = false;
    /** Indica si el personaje se mueve hacia abajo. */
    protected boolean abj = false;
    /** Indica si el personaje está usando una habilidad. */
    protected boolean hab = false;
    /** Indica si alguna tecla de movimiento o habilidad está siendo presionada. */
    protected boolean presionando = false;

    /** Animación del personaje moviéndose hacia abajo. */
    protected Animation<TextureRegion> abajo;
    /** Animación del personaje moviéndose hacia arriba. */
    protected Animation<TextureRegion> arriba;
    /** Animación del personaje moviéndose en diagonal hacia arriba. */
    protected Animation<TextureRegion> diagonalarr;
    /** Animación del personaje moviéndose en diagonal hacia abajo. */
    protected Animation<TextureRegion> diagonalabj;
    /** Animación de la habilidad especial del personaje. */
    protected Animation<TextureRegion> habilidad;

    /** Indica si el personaje está en modo de teletransporte (TLT). */
    protected boolean TLT = false;
    /** Tiempo restante para alguna acción o cooldown (ej. habilidad). */
    protected float tiempoRestante = 5f;
    /** Indica si el personaje está actualmente usando su habilidad. */
    protected boolean usandoHabilidad = false;
    /** Indica si el personaje puede usar su habilidad en este momento. */
    protected boolean puedeUsarHabilidad = true;
    /** Duración máxima de alguna característica o cooldown (ej. habilidad). */
    protected final float MAX_TIEMPO = 30f;

    //** Indica si el personaje esta en modo invulnerable (invulnerable).*/
    protected boolean invulnerable = false;
    //* Contador del tiempo de invulnerabilidad. */
    protected float tiempoInvulnerable = 0f;

    /**
     * Constructor de la clase Amigas.
     * Llama al constructor de la clase padre {@link Personaje} para inicializar el cuerpo físico.
     *
     * @param p La posición inicial del personaje.
     * @param w El mundo Box2D donde reside el personaje.
     */
    public Amigas(Vector2 p, World w) {
        super(p,w); // Llama al constructor de Personaje que inicializa 'this.world'
    }

    /**
     * Actualiza el estado y las animaciones del personaje en cada frame.
     * Maneja la lógica de movimiento, selección de animaciones y estado de habilidades.
     *
     * @param delta El tiempo transcurrido desde el último frame en segundos.
     */
    @Override
    protected void actualizar(float delta) {
        this.posicion = body.getPosition(); // Actualiza la posición del sprite a la del cuerpo físico

        if (invulnerable) {  //Verificador de invulnerabilidad
            tiempoInvulnerable -= delta;
            if (tiempoInvulnerable <= 0f) {
                invulnerable = false;
            }
        }

        boolean diagonalArriba = arr && (izq || der);
        boolean diagonalAbajo = abj && (izq || der);

        // Actualiza el tiempo de estado solo si hay movimiento o habilidad activa
        if (diagonalArriba || diagonalAbajo || izq || der || abj || arr || hab) {
            stateTime += delta;
        } else {
            stateTime = 0; // Reinicia el tiempo de estado si no hay movimiento
        }

        // Lógica de selección de animaciones basada en el estado de movimiento y habilidad
        if (hab && presionando) {
            frameActual = habilidad.getKeyFrame(stateTime, true);

            // Voltear sprite según la dirección de la habilidad
            if (der && frameActual.isFlipX()) {
                frameActual.flip(true, false);
            } else if (izq && !frameActual.isFlipX()) {
                frameActual.flip(true, false);
            }

        } else if (diagonalArriba) {
            frameActual = diagonalarr.getKeyFrame(stateTime, true);
            // Lógica de volteo específica para personajes en diagonal superior
            if (this instanceof Sonic) {
                if (der && !frameActual.isFlipX()) frameActual.flip(true, false);
                if (izq && frameActual.isFlipX()) frameActual.flip(true, false);
            } else {
                if (der && frameActual.isFlipX()) frameActual.flip(true, false);
                if (izq && !frameActual.isFlipX()) frameActual.flip(true, false);
            }

        } else if (diagonalAbajo) {
            frameActual = diagonalabj.getKeyFrame(stateTime, true);
            // Lógica de volteo específica para personajes en diagonal inferior
            if (this instanceof Sonic || this instanceof Tails) {
                if (izq && !frameActual.isFlipX()) frameActual.flip(true, false);
                if (der && frameActual.isFlipX()) frameActual.flip(true, false);
            } else {
                if (izq && frameActual.isFlipX()) frameActual.flip(true, false);
                if (der && !frameActual.isFlipX()) frameActual.flip(true, false);
            }

        } else if (abj) {
            frameActual = abajo.getKeyFrame(stateTime, true);
        } else if (arr) {
            frameActual = arriba.getKeyFrame(stateTime, true);
        } else if (izq || der) {
            frameActual = correr.getKeyFrame(stateTime, true);
        } else {
            frameActual = sprite; // Animación por defecto (parado)
        }

        // Voltear sprite para movimiento horizontal si no es diagonal
        if (!diagonalArriba && !diagonalAbajo) {
            if (izq && frameActual != null && !frameActual.isFlipX()) {
                frameActual.flip(true, false);
            } else if (der && frameActual != null && frameActual.isFlipX()) {
                frameActual.flip(true, false);
            }
        }
    }

    /**
     * Activa el modo invulnerable del personaje durante un período definido.
     *
     * @param duracion Tiempo en segundos que el personaje será invulnerable.
     */
    public void activarInvulnerabilidad(float duracion) {
        invulnerable = true;
        tiempoInvulnerable = duracion;
    }

    /**
     * Verifica si el personaje está actualmente en estado invulnerable.
     *
     * @return true si el personaje es invulnerable, false en caso contrario.
     */
    public boolean esInvulnerable() {
        return invulnerable;
    }

    /**
     * Teletransporta al personaje a una posición fija en el mapa si el modo TLT está activo.
     * También activa la invulnerabilidad por un breve periodo y desactiva el modo TLT al finalizar.
     */
    public void teletransportar() {
        if(TLT && body != null) {
            body.setTransform(25, 22, body.getAngle()); // Mueve el cuerpo a la posición (25, 22)
            activarInvulnerabilidad(3f);                // Activa invulnerabilidad temporal
            setTLT();                                   // Desactiva modo de teletransporte
        }
    }

    /**
     * Alterna el estado del modo de teletransporte (TLT).
     * Si está activo, lo desactiva; si está inactivo, lo activa.
     */
    public void setTLT() {
        TLT = !TLT;
    }
}
