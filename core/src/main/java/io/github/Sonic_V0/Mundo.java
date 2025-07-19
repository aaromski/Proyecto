package io.github.Sonic_V0; // Asegúrate de que este sea el paquete correcto

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Personajes.Etapa;
import io.github.Sonic_V0.Personajes.Knuckles;

import java.util.ArrayList;

public class Mundo {
    private final World world;
    private final CargarMapa map;
    private final ArrayList<Basura> listaBasura;
    private final Knuckles knuckles;
    private final Etapa etapa;

    public Mundo() {
        world = new World(new Vector2(0, 0), true);
        knuckles = new Knuckles(crearCuerpo(new Vector2(20f, 10f), "knuckles"));
        etapa = new Etapa(this, knuckles);
        listaBasura = new ArrayList<>();
        map = new CargarMapa("Mapa1/mapa.tmx", world);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object ua = contact.getFixtureA().getUserData();
                Object ub = contact.getFixtureB().getUserData();

                if ("knuckles".equals(ua) && ub instanceof Basura) {
                    ((Basura) ub).setActiva();
                }
                if ("knuckles".equals(ub) && ua instanceof Basura) {
                    ((Basura) ua).setActiva();
                }
            }
            @Override public void endContact(Contact contact) {}
            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }

    public void actualizar(float delta) {
        world.step(delta, 8, 6);
        listaBasura.removeIf(b -> {
            if (!b.estaActiva()) {
                b.destruir(world);
                b.dispose();
                return true;
            }
            return false;
        });

        knuckles.actualizar(delta);
        etapa.actualizar(delta);
    }

    public Body crearCuerpo(Vector2 posicion, String userData) {
        BodyDef bd = new BodyDef();
        bd.position.set(posicion);
        bd.type = BodyDef.BodyType.DynamicBody;

        CircleShape circle = new CircleShape();
        circle.setRadius(0.5f);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = circle;
        fixDef.density = 1.0f; // Mantengo la densidad para un comportamiento físico correcto

        Body oBody = world.createBody(bd);
        oBody.setLinearDamping(5f);

        if ("Robot".equals(userData)) {
            fixDef.filter.categoryBits = Constantes.CATEGORY_ROBOT;
            // Vuelve a como estaba: todo excepto TRASH
            fixDef.filter.maskBits = ~(Constantes.CATEGORY_TRASH);
        } else if ("knuckles".equals(userData)) {
            fixDef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
            fixDef.filter.maskBits = -1; // -1 significa que colisiona con todo
        }

        Fixture f = oBody.createFixture(fixDef);
        f.setUserData(userData);

        return oBody;
    }

    public void generarBasura(Vector2 posicion) {
        Basura basura = new Basura(world);
        basura.crearCuerpo(posicion);
        listaBasura.add(basura);
    }

    public void render(SpriteBatch batch) {
        for (Basura b : listaBasura) {
            if (b.estaActiva() && b.getCuerpo() != null) {
                b.render(batch);
            }
        }
        knuckles.render(batch);
        etapa.renderizar(batch);
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
        knuckles.dispose();
        etapa.dispose();
    }
}
