package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.Sonic_V0.Personajes.*;

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
            generarRobot();
        }

        if (timer2 >= intervalo2) {
            timer2 = 0f;
            Robot primerRobot = robots.get(0);
            primerRobot.setKO();

        }

        for (Robot r : robots) {
            r.actualizar(delta);
        }
    }

    public void renderizar(SpriteBatch batch) {
        for (Robot r : robots) {
            r.render(batch);
        }
    }

    public Vector2 getEntrada() {
        int index = random.nextInt(puntosEntrada.size());
        return puntosEntrada.get(index);
    }


    public void generarRobot() {
        Amigas objetivo;

        int ra = (int) (Math.random() * 3);
        if (ra == 0) {
            objetivo = sonic;
        } else if (ra == 1) {
            objetivo = knuckles;
        } else {
            objetivo = tails;
        }

        Robot r = new Robot(getEntrada(), sonic.getCuerpo(), mundo); // primer objetivo

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
