package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.Sonic_V0.Mundo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Etapa {
    private final Mundo mundo;
    private final Sonic sonic;
    private final Knuckles knuckles;
    private final List<Robot> robots = new ArrayList<>();
    private final List<Vector2> puntosEntrada = new ArrayList<>();
    private float timer = 0f;
    private final Random random = new Random();
    private final Tails tails;

    public Etapa(Mundo mundo, Sonic sonic, Knuckles knuckles, Tails tails) {
        this.mundo = mundo;
        this.sonic = sonic;
        this.tails = tails;
        this.knuckles = knuckles;

        // Ajusta estos puntos para que no estén pegados a las esquinas
        puntosEntrada.add(new Vector2(3f, 21f));
        puntosEntrada.add(new Vector2(25f, 3f));
        puntosEntrada.add(new Vector2(25f, 36f));
        puntosEntrada.add(new Vector2(47f, 22f));
    }

    public void actualizar(float delta) {
        timer += delta;
        float intervalo = 5f;
        if (timer >= intervalo) {
            timer = 0f;
            generarRobot(mundo.crearCuerpo(getEntrada(), "Robot"));
        }

        // Actualiza cada robot y destruye si están cerca de Sonic
        Iterator<Robot> it = robots.iterator();
        while (it.hasNext()) {
            Robot r = it.next();

            // Actualiza el objetivo con la posición de Sonic
            if (!sonic.getKO()) {
                r.setObjetivo(sonic.getPosicion().cpy());
            } else {
                r.setObjetivo(r.getPosicion());
            }

            r.actualizar(delta);

            // Destruir si llegó cerca de Sonic
            if (r.getPosicion().dst(sonic.getPosicion()) < 0.8f) {
                r.destruir();
                it.remove();
            }
        }
    }

    public void renderizar(SpriteBatch batch) {
        for (Robot r : robots) {
            r.render(batch);
        }
    }

    private Vector2 getEntrada() {
        int index = random.nextInt(puntosEntrada.size());
        return puntosEntrada.get(index);
    }

    private void generarRobot(Body body) {
        Robot robot = new Robot(body, sonic.getPosicion().cpy(), mundo); // primer objetivo
        robots.add(robot);
    }

    public void dispose() {
        for (Robot r : robots) {
            r.dispose();
        }
    }
}
