package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.Sonic_V0.Personajes.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Etapa2 {
    private final Mundo mundo;
    private final Sonic sonic;
    private final Tails tails;
    private final Knuckles knuckles;
    private final Etapa etapa1;
    private final List<Robotnik> robotniks = new ArrayList<>();
    private final List<Robot> robots = new ArrayList<>();
    private final List<Vector2> puntosEntrada = new ArrayList<>();
    private float timer = 0f;
    private final Random random = new Random();


    public Etapa2(Mundo mundo, Sonic sonic, Tails tails, Knuckles knuckles, Etapa etapa1) {
        this.mundo = mundo;
        this.sonic = sonic;
        this.tails = tails;
        this.knuckles = knuckles;
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
            float intervalo = 60f;
            if (timer >= intervalo) {
                timer = 0f;
                generarRobotnik();
            }
        }

        Iterator<Robotnik> robotnikIt = robotniks.iterator();
        while (robotnikIt.hasNext()) {
            Robotnik r = robotnikIt.next();
            r.actualizar(delta);
            if (r.isListoDespawn()) {
                if (r.getCuerpo() != null) {
                    mundo.getWorld().destroyBody(r.getCuerpo());
                }
                r.dispose();
                robotnikIt.remove();
            }
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
            if (r.getCuerpo() != null) {
                r.render(batch);
            }
        }
        for (Robot r : robots) {
            if (r.getCuerpo() != null) {
                r.render(batch);
            }
        }

    }

    private Vector2 getEntrada() {
        int index = random.nextInt(puntosEntrada.size());
        return puntosEntrada.get(index);
    }

    private void generarRobotnik() {
        if(sonic.getCuerpo()!=null){
            Robotnik robotnik = new Robotnik(sonic.getCuerpo(), mundo, this, etapa1, getEntrada());
            robotniks.add(robotnik);
        } else if(tails.getCuerpo()!=null){
            Robotnik robotnik = new Robotnik(tails.getCuerpo(), mundo, this, etapa1, getEntrada());
            robotniks.add(robotnik);
        } else if(knuckles.getCuerpo()!=null){
            Robotnik robotnik = new Robotnik(knuckles.getCuerpo(), mundo, this, etapa1, getEntrada());
            robotniks.add(robotnik);
        }

    }

    public void dispose() {
        for (Robotnik r : robotniks) {
            r.dispose();
        }
    }
}
