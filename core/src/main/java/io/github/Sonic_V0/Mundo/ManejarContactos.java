package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Personajes.*;

/**
 * Clase que implementa la lógica de interacción física entre entidades del juego mediante el sistema Box2D.
 * Se encarga de manejar contactos como golpes, colisiones con enemigos, contaminación ambiental
 * y sensores para obstáculos.
 *
 *@author
 */
public class ManejarContactos implements ContactListener {

    /**
     * Se ejecuta cuando inicia el contacto entre dos fixtures.
     * Analiza la colisión y ejecuta lógica específica según el tipo de entidad involucrada.
     *
     * @param contact el objeto de contacto generado por Box2D
     */
    @Override
    public void beginContact(Contact contact) {
        Object ua = contact.getFixtureA().getUserData();
        Object ub = contact.getFixtureB().getUserData();
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();


        // Interacción entre amigas y elementos contaminantes
        if ((ua instanceof Amigas && ub instanceof Contaminacion) ||
            (ub instanceof Amigas && ua instanceof Contaminacion)) {

            Amigas personaje = (Amigas) (ua instanceof Amigas ? ua : ub);
            Contaminacion contaminante = (Contaminacion) (ua instanceof Contaminacion ? ua : ub);

            if (personaje.esInvulnerable()) {
                if (contaminante instanceof Nube) {
                    contaminante.setActiva(4);
                    return;
                }
            }

            if (contaminante instanceof Nube) {
                aplicarDaño(personaje);
                contaminante.setActiva(4);
            } else if (contaminante instanceof Basura || contaminante instanceof CharcoAceite) {
                if (personaje instanceof Sonic) {
                    contaminante.setActiva(1);
                }
            }
        }

        // Interacción entre amigas y enemigas (robots)
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

        // Colisión entre nube y objeto
        if ("Objeto".equals(fa.getUserData()) && ub instanceof Nube
            || "Objeto".equals(fb.getUserData()) && ua instanceof Nube) {
            Nube nube = (ub instanceof Nube) ? (Nube) ub : (Nube) ua;
            nube.setActiva(4);
        }

        // Sensor detecta obstáculo y activa rodeo
        if (esSensorYObstaculo(fa, fb)) {
            Fixture sensorFixture = "sensor".equals(fa.getUserData()) ? fa : fb;
            Fixture obstaculoFixture = sensorFixture == fa ? fb : fa;

            Enemigas enemigo = (Enemigas) sensorFixture.getBody().getUserData();
            Vector2 posicionObstaculo = obstaculoFixture.getBody().getPosition();

            enemigo.activarRodeo(posicionObstaculo);
        }

        // Golpe de Knuckles sobre robot
        if ((("golpeKnuckles".equals(ua) && ub instanceof Robot) ||
            ("golpeKnuckles".equals(ub) && ua instanceof Robot))) {

            Robot robotAfectado = (Robot) (ua instanceof Robot ? ua : ub);
            if (!robotAfectado.getKO()) {
                robotAfectado.setKO();
                Constantes.SCORE[1] += 10;
            }
        }

        // Detección de contacto entre Robot y Casa
        if (("Casa".equals(ua) && ub instanceof Robot) || ("Casa".equals(ub) && ua instanceof Robot)) {
            Robot robot = ua instanceof Robot ? (Robot) ua : (Robot) ub;
            if(robot.getKO()) {
                robot.setDestruido();
                Constantes.SCORE[2] += 10;
            }
        }
    }

    /**
     * Aplica daño al personaje según su tipo y actualiza su estado de vida.
     *
     * @param personaje la instancia de Amigas afectada por el contacto
     */
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

    /**
     * Determina si el contacto incluye un sensor y un obstáculo.
     *
     * @param fa primer fixture del contacto
     * @param fb segundo fixture del contacto
     * @return true si hay contacto entre sensor y obstáculo
     */
    public boolean esSensorYObstaculo(Fixture fa, Fixture fb) {
        return ("sensor".equals(fa.getUserData()) && esObstaculo(fb.getUserData())) ||
            ("sensor".equals(fb.getUserData()) && esObstaculo(fa.getUserData()));
    }

    /**
     * Verifica si el objeto especificado representa un obstáculo.
     *
     * @param dato el userData del fixture
     * @return true si es un obstáculo
     */
    public boolean esObstaculo(Object dato) {
        return "Objeto".equals(dato);
    }

    /**
     * Se ejecuta cuando finaliza el contacto entre dos cuerpos. (No utilizada)
     */
    @Override public void endContact(Contact contact) {}

    /**
     * Se llama antes de resolver la física del contacto. (No utilizada)
     */
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}

    /**
     * Se llama después de resolver la física del contacto. (No utilizada)
     */
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}

