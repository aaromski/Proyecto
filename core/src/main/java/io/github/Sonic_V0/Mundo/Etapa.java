package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.Sonic_V0.Personajes.*;

import java.util.ArrayList;
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
        float intervalo = 15f;
        if (timer >= intervalo) {
            timer = 0f;
            generarRobot();
        }

        for (Robot r : robots) {
            if(r.sinObjetivo()) {
                r.seleccionarObjetivoMasCercano(obtenerObjetivosVivos());
            } else if (!r.getKO()) {
                r.seleccionarObjetivoMasCercano(obtenerObjetivosVivos());
            }
            r.actualizar(delta);
        }
    }

    public void renderizar(SpriteBatch batch) {
        for (Robot r : robots) {
            if (r.getCuerpo() != null) {
                r.render(batch);
            }
        }
    }

    public Vector2 getEntrada() {
        int index = random.nextInt(puntosEntrada.size());
        return puntosEntrada.get(index);
    }


    public void generarRobot() {
        Robot r = new Robot(getEntrada(), objetivoDisponible(), mundo); // primer objetivo
        robots.add(r);
    }

    private Body objetivoDisponible() {
        List<Amigas> posibles = new ArrayList<>();
        if (!sonic.getKO()) posibles.add(sonic);
        if (!tails.getKO()) posibles.add(tails);
        if (!knuckles.getKO()) posibles.add(knuckles);

        if (posibles.isEmpty()) return null;

        Amigas elegido = posibles.get(random.nextInt(posibles.size()));
        return elegido.getCuerpo();
    }

    public List<Amigas> obtenerObjetivosVivos() {
        List<Amigas> activos = new ArrayList<>();
        if (!sonic.getKO()) activos.add(sonic);
        if (!tails.getKO()) activos.add(tails);
        if (!knuckles.getKO()) activos.add(knuckles);
        return activos;
    }

    public List<Robot> destruirRobots() {
        List<Robot> listaDestruidos = new ArrayList<>();

        for (Robot robot : robots) {
            if (robot.getDestruido()) { // Método que verifica si está destruido
                listaDestruidos.add(robot);
            }
        }

        return listaDestruidos;
    }

    public void dispose() {
        for (Robot r : robots) {
            r.dispose();
        }
    }
}
