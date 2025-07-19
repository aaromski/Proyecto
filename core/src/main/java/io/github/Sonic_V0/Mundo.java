package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Personajes.Etapa;
import io.github.Sonic_V0.Personajes.Robot;
import io.github.Sonic_V0.Personajes.Sonic;
import io.github.Sonic_V0.Personajes.Knuckles;
import io.github.Sonic_V0.Personajes.Tails;

import java.util.ArrayList;


public class Mundo {
    private final World world;
    private final CargarMapa map;
    private final ArrayList<Basura> listaBasura;
    private final Sonic sonic;
    private final Knuckles knuckles;
    private final Tails tails;
    private final Etapa etapa;

    public Mundo() {
        world = new World(new Vector2(0, 0), true);
        sonic = new Sonic(crearCuerpo(new Vector2(25f, 22f), "Sonic")); //270-150
        knuckles = new Knuckles(crearCuerpo(new Vector2(20f, 10f), "knuckles"));
        tails = new Tails(crearCuerpo(new Vector2(20f, 22f), "Tails")); //270-150
        etapa = new Etapa(this, sonic, knuckles, tails);
        listaBasura = new ArrayList<>();
        map = new CargarMapa("Mapa1/mapa.tmx", world);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object ua = contact.getFixtureA().getUserData();
                Object ub = contact.getFixtureB().getUserData();

                if ("Sonic".equals(ua) && ub instanceof Basura) {
                    ((Basura) ub).setActiva(1);
                }

                if ("Sonic".equals(ub) && ua instanceof Basura) {
                    ((Basura) ua).setActiva(1);
                }

                if("Sonic".equals(ua) && "Robot".equals(ub)) {
                    Constantes.VIDAS[0] -= 1;
                    if ( Constantes.VIDAS[0] > 0) {
                        sonic.setTLT();
                    } else {
                        sonic.setKO();
                    }
                }

                if("Sonic".equals(ub) && "Robot".equals(ua)) {
                    Constantes.VIDAS[0] -= 1;
                    if ( Constantes.VIDAS[0] >= 0) {
                        sonic.setTLT();
                    } else {
                        sonic.setKO();
                    }
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
        if(sonic.getKO()) {
            sonic.destruir(world);
            sonic.dispose();
        }
        if(tails.getKO()) {
            tails.destruir(world);
            tails.dispose();
        }

        sonic.teletransportar();
        sonic.actualizar(delta);
        tails.teletransportar();
        tails.actualizar(delta);
        knuckles.teletransportar();
        knuckles.actualizar(delta);
        etapa.actualizar(delta); // <-- Actualiza todos los robots generados
    }


    public Body crearCuerpo(Vector2 posicion, String userData) {
        BodyDef bd = new BodyDef();
        bd.position.set(posicion);
        bd.type = BodyDef.BodyType.DynamicBody;

        CircleShape circle = new CircleShape();
        circle.setRadius(0.5f);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = circle;

        Body oBody = world.createBody(bd);
        oBody.setLinearDamping(5f); // Esto reduce el deslizamiento horizontal

        if (userData.equals("Robot")) {
            fixDef.filter.categoryBits = Constantes.CATEGORY_ROBOT;
            fixDef.filter.maskBits = ~(Constantes.CATEGORY_TRASH); // o una lista explícita sin incluir `TRASH`
        } else if (userData.equals("Sonic")) {
            fixDef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
            fixDef.filter.maskBits = -1; // o una lista explícita sin incluir `TRASH`
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
        if(!sonic.getKO()) {
            sonic.render(batch);
        }
        if(!tails.getKO()) {
            tails.render(batch);
        }
        if(!knuckles.getKO()) {
            knuckles.render(batch);
        }

        etapa.renderizar(batch);
    }

    public void renderizarMapa(OrthographicCamera camara) {
        map.renderarMapa(camara);
    }

    public void dispose() {
        for (Basura b : listaBasura) {
            b.dispose();
        }
        map.dispose();      // ← Libera el mapa
        world.dispose();    // ← Libera el mundo Box2D
        sonic.dispose();
        knuckles.dispose();
        tails.dispose();
        etapa.dispose();
    }
}
