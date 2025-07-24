package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemigas extends Personaje {
    protected Animation<TextureRegion> KO;
    protected Body objetivo;
    protected boolean destruido = false;
    private Vector2 ultimaPosicion;
    private float tiempoAtascado = 0f;
    private Vector2 direccionRodeo = null;
    private float tiempoRodeando = 0f;
    private final float DURACION_RODEO = 1f;

    public Enemigas(Vector2 p, World w) {
        super(p,w);
    }

    public void destruir() {
        ko = true;
        stateTime = 0f; // reiniciar animación KO
        body.setLinearVelocity(0, 0); // detener movimiento si aplica
    }

    public void setObjetivo(Body pos) {
        if(!ko) {
            this.objetivo = pos;
        }
    }

    public boolean sinObjetivo() {
        return objetivo == null;
    }

    @Override
    public void actualizar(float delta) {
        posicion = body.getPosition();

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

            if (tiempoAtascado > 0.6f) {
                Vector2 direccionObjetivo = objetivo.getPosition().cpy().sub(posicion).nor();
                direccionRodeo = new Vector2(-direccionObjetivo.y, direccionObjetivo.x).nor();
                if (Math.random() < 0.5f) {
                    direccionRodeo.scl(-1); // izquierda o derecha al azar
                }

                tiempoRodeando = 0f;
                tiempoAtascado = 0f;
            }
        }

        ultimaPosicion = posicion.cpy();

        // Si está en modo de rodeo
        if (direccionRodeo != null) {
            tiempoRodeando += delta;
            body.setLinearVelocity(direccionRodeo.cpy().scl(velocidad));

            if (tiempoRodeando >= DURACION_RODEO) {
                direccionRodeo = null;
            }

            frameActual = correr.getKeyFrame(stateTime, true);
            return;
        }

        // Movimiento normal hacia el objetivo
        Vector2 direccion = objetivo.getPosition().cpy().sub(posicion).nor().scl(velocidad);
        body.setLinearVelocity(direccion);

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

    public void activarRodeo(Vector2 posicionObstaculo) {
        if (ko || direccionRodeo != null || objetivo == null) return;

        Vector2 direccionObjetivo = objetivo.getPosition().cpy().sub(body.getPosition()).nor();

        // Opción A: rodear según el obstáculo detectado
        Vector2 direccionDesdeObstaculo = body.getPosition().cpy().sub(posicionObstaculo).nor();

        // Generar dirección perpendicular para rodear
        direccionRodeo = new Vector2(-direccionObjetivo.y, direccionObjetivo.x).nor();

        if (Math.random() < 0.5f) {
            direccionRodeo.scl(-1); // izquierda o derecha al azar
        }

        tiempoRodeando = 0f;
        tiempoAtascado = 0f;
    }


}
