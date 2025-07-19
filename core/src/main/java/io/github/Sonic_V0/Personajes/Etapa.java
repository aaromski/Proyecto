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
    private float timer2 = 0f;
    private final Random random = new Random();
    private final Tails tails;

    public Etapa(Mundo mundo, Sonic sonic, Tails tails, Knuckles knuckles) {
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
        float intervalo = 8f;
        timer2 += delta;
        float intervalo2 = 10f;
        if (timer >= intervalo) {
            timer = 0f;
            generarRobot(mundo.crearCuerpo(getEntrada(), "Robot"));
        }

        if (timer2 >= intervalo2) {
            timer2 = 0f;
            Robot primerRobot = robots.get(0);
            primerRobot.setKO();

        }

        // Actualiza cada robot y destruye si están cerca de Knuckles
        Iterator<Robot> it = robots.iterator();
        while (it.hasNext()) {
            Robot r = it.next();
            r.actualizar(delta);
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
        Amigas objetivo;

        if (Math.random() < 0.5) {
            objetivo = sonic;
        } else {
            objetivo = knuckles;
        }

        Robot r = new Robot(body, sonic.getCuerpo(), mundo); // primer objetivo

        if (!objetivo.getKO()) {
            r.setObjetivo(objetivo.getCuerpo());
        } else {
            r.setObjetivo(r.getCuerpo());
        }
        robots.add(r);
    }

    public void dispose() {
        for (Robot r : robots) {
            r.dispose();
        }
    }
}
