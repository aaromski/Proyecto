package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Personajes.*;

public class ManejarContactos implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Object ua = contact.getFixtureA().getUserData();
        Object ub = contact.getFixtureB().getUserData();
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();


        if ((ua instanceof Amigas && ub instanceof Contaminacion) ||
            (ub instanceof Amigas && ua instanceof Contaminacion)) {

            Amigas personaje = (Amigas) (ua instanceof Amigas ? ua : ub);
            Contaminacion contaminante = (Contaminacion) (ua instanceof Contaminacion ? ua : ub);

            if (personaje.esInvulnerable()) return;

            if (contaminante instanceof Nube) {
                aplicarDaño(personaje);
                contaminante.setActiva(4);
            } else if (contaminante instanceof Basura || contaminante instanceof CharcoAceite) {
                if (personaje instanceof Sonic) {
                    contaminante.setActiva(1);
                }
            }
        }


        if ((ua instanceof Amigas && ub instanceof Enemigas) ||
            (ua instanceof Enemigas && ub instanceof Amigas)) {

            Amigas personaje = (ua instanceof Amigas) ? (Amigas) ua : (Amigas) ub;
            Enemigas enemigo = (ua instanceof Enemigas) ? (Enemigas) ua : (Enemigas) ub;

            // Obtener el fixture principal del enemigo
            Fixture fixtureEnemigo = enemigo.getCuerpo().getFixtureList().first();
            Filter filtro = fixtureEnemigo.getFilterData();
            if (personaje.esInvulnerable()) return;
            // Verificar si todavía está activo
            if (filtro.categoryBits == Constantes.CATEGORY_ROBOT) {
                aplicarDaño(personaje);
            }
        }

        if ("Objeto".equals(fa.getUserData()) && ub instanceof Nube
            || "Objeto".equals(fb.getUserData()) && ua instanceof Nube) {
            Nube nube = (ub instanceof Nube) ? (Nube) ub : (Nube) ua;
            nube.setActiva(4);
        }

        if (esSensorYObstaculo(fa, fb)) {
            Fixture sensorFixture = "sensor".equals(fa.getUserData()) ? fa : fb;
            Fixture obstaculoFixture = sensorFixture == fa ? fb : fa;

            Enemigas enemigo = (Enemigas) sensorFixture.getBody().getUserData();
            Vector2 posicionObstaculo = obstaculoFixture.getBody().getPosition();

            enemigo.activarRodeo(posicionObstaculo);
        }

        // NUEVA LÓGICA: Detección del golpe de Knuckles a un Robot
        if ((("golpeKnuckles".equals(ua) && ub instanceof Robot) ||
            ("golpeKnuckles".equals(ub) && ua instanceof Robot))) {

            Robot robotAfectado = (Robot) (ua instanceof Robot ? ua : ub);
            if (!robotAfectado.getKO()) { // Si el robot no está ya KO, lo ponemos en KO
                robotAfectado.setKO(); // Llama al método setKO del robot
                Constantes.SCORE[1] += 10;
            }
        }
    }

    public static void aplicarDaño(Amigas personaje) {
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

    public boolean esSensorYObstaculo(Fixture fa, Fixture fb) {
        return ("sensor".equals(fa.getUserData()) && esObstaculo(fb.getUserData())) ||
            ("sensor".equals(fb.getUserData()) && esObstaculo(fa.getUserData()));
    }

    public boolean esObstaculo(Object dato) {
        return "Objeto".equals(dato);
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
