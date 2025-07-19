package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Enemigas extends Personaje {
    protected Animation<TextureRegion> KO;
    protected boolean destruido = false;
    protected Vector2 objetivo;

    public Enemigas(Body b) {
        super(b);
    }

    public void destruir() {
        destruido = true;
        stateTime = 0f; // reiniciar animación KO
        body.setLinearVelocity(0, 0); // detener movimiento si aplica
    }

    public void setObjetivo(Vector2 pos) {
        this.objetivo = pos;
    }

    @Override
    public void actualizar(float delta) {
        posicion = body.getPosition();
        stateTime += delta;

        if (destruido) {
            frameActual = KO.getKeyFrame(stateTime, false);
            body.setLinearVelocity(0, 0); // asegurar que se queda quieto
            return;
        }

        if (objetivo != null) {
            // Vector hacia el objetivo
            Vector2 direccion = objetivo.cpy().sub(posicion).nor().scl(velocidad);
            body.setLinearVelocity(direccion);

            // Voltear sprite si el objetivo está a la izquierda o derecha
            boolean haciaIzq = direccion.x < 0;
            if (frameActual != null) {
                if (haciaIzq && !frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                } else if (!haciaIzq && frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                }
            }
        } else {
            // Si no hay objetivo, quedarse quieto
            body.setLinearVelocity(0, 0);
        }

        // Animación de movimiento
        frameActual = correr.getKeyFrame(stateTime, true);
    }

}
