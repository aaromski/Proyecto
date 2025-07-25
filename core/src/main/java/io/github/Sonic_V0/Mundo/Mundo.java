package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Personajes.*;

import java.util.ArrayList;
import java.util.List;


public class Mundo {
    private final World world;
    private final CargarMapa map;
    private final ArrayList<Basura> listaBasura;
    private final ArrayList<Nube> listaNube;
    private final ArrayList<CharcoAceite> listaCharcos;
    private final Sonic sonic;
    private final Etapa2 etapa2;
    private final Tails tails;
    private final Knuckles knuckles;
    private final Etapa etapa;

    public Mundo() {
        world = new World(new Vector2(0, 0), true);
        knuckles = new Knuckles(new Vector2(22f, 22f), world);
        sonic = new Sonic(new Vector2(25f, 22f), world);
        tails = new Tails(new Vector2(20f, 22f), world);
        etapa = new Etapa(this, sonic, tails, knuckles);
        etapa2 = new Etapa2(this, sonic, tails, knuckles ,etapa);
        listaBasura = new ArrayList<>();
        listaNube = new ArrayList<>();
        listaCharcos = new ArrayList<>();
        map = new CargarMapa("Mapa1/mapa.tmx", world);

        world.setContactListener(new ManejarContactos());
    }

    public void actualizar(float delta) {
        world.step(delta, 8, 6);
        listaBasura.removeIf(b -> {
            if (!b.estaActiva()) {
                b.destruir(); // <-- ¡MODIFICADO! Sin parámetro
                b.dispose();
                return true;
            }
            return false;
        });
        if(sonic.getKO()) {
            sonic.destruir(); // <-- ¡MODIFICADO! Sin parámetro
            sonic.dispose();
        }
        if(knuckles.getKO()) {
            knuckles.destruir(); // <-- ¡MODIFICADO! Sin parámetro
            knuckles.dispose();
        }
        if(tails.getKO()) {
            tails.destruir(); // <-- ¡MODIFICADO! Sin parámetro
            tails.dispose();
        }

        listaNube.removeIf(n -> {
            if (!n.estaActiva()) {
                n.destruir(); // <-- ¡MODIFICADO! Sin parámetro
                n.dispose();
                return true;
            }
            return false;
        });

        listaCharcos.removeIf(c -> {
            if (!c.estaActiva()) {
                c.destruir(); // <-- ¡MODIFICADO! Sin parámetro
                c.dispose();
                return true;
            }
            return false;
        });

        for (Robot robot : etapa.destruirRobots()) {

            robot.destruir();
        }

        sonic.teletransportar();
        knuckles.teletransportar();
        sonic.actualizar(delta);
        knuckles.actualizar(delta);
        tails.teletransportar();
        tails.actualizar(delta);
        etapa.actualizar(delta);
        etapa2.actualizar(delta);
    }

    public void generarBasura(Vector2 posicion) {
        Basura basura = new Basura(world);
        basura.crearCuerpo(posicion);
        listaBasura.add(basura);
    }

    public void generarCharco(Vector2 posicion) {
        CharcoAceite charco = new CharcoAceite(world);
        charco.crearCuerpo(posicion);
        listaCharcos.add(charco);
    }

    public void generarNube(Vector2 posicion, Vector2 direccion) {
        Nube nube = new Nube(world);
        nube.crearCuerpo(posicion, direccion);
        listaNube.add(nube);
    }

    public void robotEtapa2() {
        etapa.generarRobot();
    }


    public void render(SpriteBatch batch) {
        for (CharcoAceite c : listaCharcos) {
            if (c.estaActiva() && c.getCuerpo() != null) {
                c.render(batch);
            }
        }

        for (Basura b : listaBasura) {
            if (b.estaActiva() && b.getCuerpo() != null) {
                b.render(batch);
            }
        }

        if (knuckles.getCuerpo() != null) {
            knuckles.render(batch);
        }
        if (sonic.getCuerpo() != null) {
            sonic.render(batch);
        }
        if(!tails.getKO()) {
            tails.render(batch);
            tails.dibujarIman(batch);
        }
        etapa.renderizar(batch);
        etapa2.renderizar(batch);
        for (Nube n : listaNube) {
            if (n.estaActiva() && n.getCuerpo() != null) {
                n.render(batch);
            }
        }
    }

    public void renderizarMapa(OrthographicCamera camara) {
        map.renderarMapa(camara);
    }

    public void dispose() {
        for (Basura b : listaBasura) {
            b.dispose();
        }
        map.dispose();
        world.dispose();
        sonic.dispose();
        knuckles.dispose();
        tails.dispose();
        etapa.dispose();
    }

    public World getWorld() {
        return world;
    }
}
