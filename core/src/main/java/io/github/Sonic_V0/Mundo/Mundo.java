package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Personajes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa el entorno principal del juego.
 *
 * Contiene y gestiona los personajes principales (Sonic, Tails, Knuckles), enemigos y elementos del escenario como basura,
 * nubes contaminantes y charcos de aceite. Se encarga de generar, actualizar, renderizar y liberar los recursos asociados.
 *
 * También administra el mapa, físicas del mundo con Box2D y la lógica de contacto entre objetos.
 *
 * @author Aarom Luces
 */
public class Mundo {

    /** Mundo físico de Box2D */
    private final World world;

    /** Mapa cargado desde TMX */
    private final CargarMapa map;

    /** Lista de objetos basura activos */
    private final ArrayList<Basura> listaBasura;

    /** Lista de nubes contaminantes activas */
    private final ArrayList<Nube> listaNube;

    /** Lista de charcos de aceite activos */
    private final ArrayList<CharcoAceite> listaCharcos;

    /** Personaje principal: Sonic */
    private final Sonic sonic;

    /** Personaje: Knuckles */
    private final Knuckles knuckles;

    /** Personaje: Tails */
    private final Tails tails;

    /** Etapa inicial del juego */
    private final Etapa etapa;

    /** Etapa secundaria del juego */
    private final Etapa2 etapa2;

    /**
     * Constructor de la clase Mundo. Inicializa el mundo físico,
     * personajes, etapas y configura el mapa y el listener de contactos.
     */
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

    /**
     * Actualiza el estado del mundo, personajes y enemigos.
     * Elimina elementos inactivos y teletransporta personajes.
     *
     * @param delta Tiempo transcurrido desde el último frame.
     */
    public void actualizar(float delta) {
        world.step(delta, 8, 6);

        listaBasura.removeIf(b -> {
            if (!b.estaActiva()) {
                b.destruir();
                b.dispose();
                return true;
            }
            return false;
        });

        if (sonic.getKO()) {
            sonic.destruir();
            sonic.dispose();
        }

        if (knuckles.getKO()) {
            knuckles.destruir();
            knuckles.dispose();
        }

        if (tails.getKO()) {
            tails.destruir();
            tails.dispose();
        }

        if (tails.getDestruirJoin()) {
            tails.setDestruirJoin();
        }

        listaNube.removeIf(n -> {
            if (!n.estaActiva()) {
                n.destruir();
                n.dispose();
                return true;
            }
            return false;
        });

        listaCharcos.removeIf(c -> {
            if (!c.estaActiva()) {
                c.destruir();
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

    /**
     * Genera una nueva basura en la posición indicada.
     *
     * @param posicion Posición en el mundo físico.
     */
    public void generarBasura(Vector2 posicion) {
        Basura basura = new Basura(world);
        basura.crearCuerpo(posicion);
        listaBasura.add(basura);
    }

    /**
     * Genera un nuevo charco de aceite en la posición indicada.
     *
     * @param posicion Posición en el mundo físico.
     */
    public void generarCharco(Vector2 posicion) {
        CharcoAceite charco = new CharcoAceite(world);
        charco.crearCuerpo(posicion);
        listaCharcos.add(charco);
    }

    /**
     * Genera una nueva nube contaminante en una posición y dirección dada.
     *
     * @param posicion  Posición inicial.
     * @param direccion Dirección del movimiento de la nube.
     */
    public void generarNube(Vector2 posicion, Vector2 direccion) {
        Nube nube = new Nube(world);
        nube.crearCuerpo(posicion, direccion);
        listaNube.add(nube);
    }

    /**
     * Invoca generación de robots desde la etapa principal.
     */
    public void robotEtapa2() {
        etapa.generarRobot();
    }

    /**
     * Renderiza todos los elementos visuales en pantalla.
     *
     * @param batch SpriteBatch utilizado para dibujar.
     */
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

        if (!tails.getKO()) {
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

    /**
     * Renderiza el mapa usando la cámara ortográfica actual.
     *
     * @param camara Cámara utilizada para visualizar el mapa.
     */
    public void renderizarMapa(OrthographicCamera camara) {
        map.renderarMapa(camara);
    }

    /**
     * Libera todos los recursos utilizados en el mundo.
     */
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

    /**
     * Retorna la instancia del mundo físico de Box2D.
     *
     * @return El mundo físico utilizado en el juego.
     */
    public World getWorld() {
        return world;
    }
}
