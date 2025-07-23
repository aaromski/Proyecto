package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.Sonic_V0.CharcoAceite;
import io.github.Sonic_V0.Mundo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Etapa2 {
    private final Mundo mundo;
    private final Sonic sonic;
    private final Etapa etapa1;
    private final List<Robotnik> robotniks = new ArrayList<>();
    private final List<Robot> robots = new ArrayList<>();
    private final List<Vector2> puntosEntrada = new ArrayList<>();
    private float timer = 0f;
    private final Random random = new Random();

    public Etapa2(Mundo mundo, Sonic sonic, Etapa etapa1) {
        this.mundo = mundo;
        this.sonic = sonic;
        this.etapa1 = etapa1;
        // Ajusta estos puntos para que no estén pegados a las esquinas
        puntosEntrada.add(new Vector2(3f, 21f));
        puntosEntrada.add(new Vector2(25f, 3f));
        puntosEntrada.add(new Vector2(25f, 36f));
        puntosEntrada.add(new Vector2(47f, 22f));
    }

    public void actualizar(float delta) {
        if (robotniks.isEmpty()) {
            timer += delta;
            float intervalo = 5f;
            if (timer >= intervalo) {
                timer = 0f;
                generarRobotnik(mundo.crearCuerpo(getEntrada(), "Robotnik"));
            }
        }

        Iterator<Robotnik> it = robotniks.iterator();
        while (it.hasNext()) {
            Robotnik r = it.next();
            r.actualizar(delta);
        }

        Iterator<Robot> robotIt = robots.iterator();
        while (robotIt.hasNext()) {
            Robot r = robotIt.next();
            r.actualizar(delta);
            if (r.estaListoParaEliminar()) {
                if (r.getCuerpo() != null) {
                    mundo.getWorld().destroyBody(r.getCuerpo());
                }
                robotIt.remove();
            }
        }
    }

    public void renderizar(SpriteBatch batch) {
        for (Robotnik r : robotniks) {
            r.render(batch);
        }
        for (Robot r : robots) {
            r.render(batch);
        }

    }

    public void generarRobot(Vector2 posicion) {
        Body body = mundo.crearCuerpo(posicion, "Robot");
        Robot robot = new Robot(body, sonic.getCuerpo(), mundo);
        robots.add(robot);
    }

    public List<Robot> getRobots() {
        return robots;
    }


    public List<Robotnik> getEnemigos() {
        return robotniks;
    }

    private Vector2 getEntrada() {
        int index = random.nextInt(puntosEntrada.size());
        return puntosEntrada.get(index);
    }

    private void generarRobotnik(Body body) {
        Robotnik robotnik = new Robotnik(body, sonic.getCuerpo(), mundo, this, etapa1); // primer objetivo
        robotniks.add(robotnik);
    }

    public void dispose() {
        for (Robotnik r : robotniks) {
            r.dispose();
        }
    }
}
