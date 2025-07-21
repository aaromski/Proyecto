package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Personajes.*;

public class ManejarContactos implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Object ua = contact.getFixtureA().getUserData();
        Object ub = contact.getFixtureB().getUserData();

        if ((ua instanceof Sonic && ub instanceof Contaminacion) ||
            (ub instanceof Sonic && ua instanceof Contaminacion)) {

            Sonic sonic = (Sonic) (ua instanceof Sonic ? ua : ub);
            Contaminacion contaminante = (Contaminacion) (ua instanceof Contaminacion ? ua : ub);

            if (contaminante instanceof Nube) {
                Constantes.VIDAS[0] -= 1;
                if (Constantes.VIDAS[0] > 0) {
                    sonic.setTLT();
                } else {
                    sonic.setKO();
                }
                contaminante.setActiva(4);
            } else {
                contaminante.setActiva(1);
            }
        }

        if ((ua instanceof Amigas && ub instanceof Enemigas) ||
            (ua instanceof Enemigas && ub instanceof Amigas)) {

            Amigas personaje = (Amigas) (ua instanceof Amigas ? ua : ub);

            int indexVida = -1;
            if (personaje instanceof Sonic) {
                indexVida = 0;
            } else if (personaje instanceof Knuckles) {
                indexVida = 1;
            } else if (personaje instanceof Tails) {
                indexVida = 2;
            }

            if (indexVida != -1) {
                Constantes.VIDAS[indexVida] -= 1;
                if (Constantes.VIDAS[indexVida] > 0) {
                    personaje.setTLT();
                } else {
                    personaje.setKO();
                }
            }
        }
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}


