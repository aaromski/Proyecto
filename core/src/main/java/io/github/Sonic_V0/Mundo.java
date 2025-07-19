package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Personajes.Etapa;
import io.github.Sonic_V0.Personajes.Sonic;
import io.github.Sonic_V0.Personajes.Knuckles;
import io.github.Sonic_V0.Personajes.Tails;

import java.util.ArrayList;


public class Mundo {
    private final World world;
    private final CargarMapa map;
    private final ArrayList<Basura> listaBasura;
    private final ArrayList<Nube> listaNube;
    private final ArrayList<CharcoAceite> listaCharcos;
    private final Sonic sonic;
    private final Etapa2 etapa;
    private final float fuerzaGolpe = 15f;
    private final Tails tails;
    private final Knuckles knuckles;
    private final Etapa etapa;

    public Mundo() {
        world = new World(new Vector2(0, 0), true);
        knuckles = new Knuckles(crearCuerpo(new Vector2(22f, 22f), "Knuckles"));
        sonic = new Sonic(crearCuerpo(new Vector2(25f, 22f), "Sonic")); //270-150
        tails = new Tails(crearCuerpo(new Vector2(20f, 22f), "Tails")); //270-150
        etapa = new Etapa(this, sonic, tails, knuckles);
        listaBasura = new ArrayList<>();
        listaNube = new ArrayList<>();
        listaCharcos = new ArrayList<>();
        map = new CargarMapa("Mapa1/mapa.tmx", world);

        // Configurar el ContactListener aquí mismo
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
                if ("Sonic".equals(ua) && ub instanceof CharcoAceite) {
                    ((CharcoAceite) ub).setActiva();
                }

                if ("Sonic".equals(ub) && ua instanceof CharcoAceite) {
                    ((CharcoAceite) ua).setActiva();
                if("Sonic".equals(ub) && "Robot".equals(ua)) {
                    Constantes.VIDAS[0] -= 1;
                    if ( Constantes.VIDAS[0] >= 0) {
                        sonic.setTLT();
                    } else {
                        sonic.setKO();
                    }
                }

                if ("Sonic".equals(ua) && ub instanceof Nube) {
                    Vector2 direccionKnockback = sonic.getBody().getPosition().cpy().sub(((Nube) ub).getCuerpo().getPosition()).nor();
                    sonic.getBody().applyLinearImpulse(direccionKnockback.scl(fuerzaGolpe), sonic.getBody().getWorldCenter(), true);
                    ((Nube) ub).setActiva();
                if("Knuckles".equals(ua) && "Robot".equals(ub)) {
                    Constantes.VIDAS[1] -= 1;
                    if ( Constantes.VIDAS[1] > 0) {
                        knuckles.setTLT();
                    } else {
                        knuckles.setKO();
                    }
                }

                if ("Sonic".equals(ub) && ua instanceof Nube) {
                    Vector2 direccionKnockback = sonic.getBody().getPosition().cpy().sub(((Nube) ua).getCuerpo().getPosition()).nor();
                    sonic.getBody().applyLinearImpulse(direccionKnockback.scl(fuerzaGolpe), sonic.getBody().getWorldCenter(), true);
                    ((Nube) ua).setActiva();
                }
            }
                if("Knuckles".equals(ub) && "Robot".equals(ua)) {
                    Constantes.VIDAS[1] -= 1;
                    if ( Constantes.VIDAS[1] >= 0) {
                        knuckles.setTLT();
                    } else {
                        knuckles.setKO();
                    }
                }

                if("Tails".equals(ua) && "Robot".equals(ub)) {
                    Constantes.VIDAS[2] -= 1;
                    if ( Constantes.VIDAS[2] > 0) {
                        knuckles.setTLT();
                    } else {
                        knuckles.setKO();
                    }
                }

                if("Tails".equals(ub) && "Robot".equals(ua)) {
                    Constantes.VIDAS[2] -= 1;
                    if ( Constantes.VIDAS[2] >= 0) {
                        knuckles.setTLT();
                    } else {
                        knuckles.setKO();
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
        } else if(knuckles.getKO()) {
            knuckles.destruir(world);
            knuckles.dispose();
        }
        if(tails.getKO()) {
            tails.destruir(world);
            tails.dispose();
        }

        listaNube.removeIf(n -> {
            if (!n.estaActiva()) {
                n.destruir(world);
                n.dispose();
                return true;
            }
            return false;
        });

        listaCharcos.removeIf(c -> {
            if (!c.estaActiva()) {
                c.destruir(world);
                c.dispose();
                return true;
            }
            return false;
        });

        sonic.teletransportar();
        knuckles.teletransportar();
        sonic.actualizar(delta);
        knuckles.actualizar(delta);
        tails.teletransportar();
        tails.actualizar(delta);
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
        if (userData.equals("Aceite")) {
            oBody.setType(BodyDef.BodyType.StaticBody);
            fixDef.isSensor = true;
            oBody.createFixture(fixDef).setUserData(userData);
        } else if (userData.equals("Robot")) {
            fixDef.filter.categoryBits = Constantes.CATEGORY_ROBOT;
            fixDef.filter.maskBits = (short) ~(Constantes.CATEGORY_TRASH | Constantes.CATEGORY_NUBE);
        } else if (userData.equals("Sonic")) {
            fixDef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
            fixDef.filter.maskBits = -1;
        }   else if (userData.equals("Nube")) {
            fixDef.filter.categoryBits = Constantes.CATEGORY_NUBE;
            fixDef.filter.maskBits = (short) ~(Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_TRASH);
        }

        Fixture f = oBody.createFixture(fixDef);
        f.setUserData(userData);
        circle.dispose();

        return oBody;
    }

    //Ataques de Robots

    public void generarBasura(Vector2 posicion) {
        Basura basura = new Basura(world);
        basura.crearCuerpo(posicion);
        listaBasura.add(basura);
    }

    //Ataques de Robotnik :)

    public void generarCharco(Vector2 posicion) {
        Body body = crearCuerpo(posicion, "Aceite");
        CharcoAceite charco = new CharcoAceite(body);
        if (!body.getFixtureList().isEmpty()) {
            body.getFixtureList().first().setUserData(charco);
        }
        listaCharcos.add(charco);
    }

    public void generarNube(Vector2 posicion, Vector2 direccion) {
        Nube nube = new Nube(world);
        nube.crearCuerpo(posicion, direccion);
        listaNube.add(nube);
    }

    public void generarRobot(Vector2 posicion) {
        etapa.generarRobot(posicion);
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
        etapa.renderizar(batch);

        for (Nube n : listaNube) {
            if (n.estaActiva() && n.getCuerpo() != null) {
                n.render(batch);
            }
        }

        sonic.render(batch);
    }

    public void limpiarArea(Vector2 posicionAtaque) {
        float radioAtaque = 1.3f;

        for (Basura basura : listaBasura) {
            if (basura.estaActiva()) {
                float distancia = basura.getCuerpo().getPosition().dst(posicionAtaque);
                if (distancia < radioAtaque) {
                    basura.setActiva();
                }
            }
        }

        for (CharcoAceite charco : listaCharcos) {
            if (charco.estaActiva()) {
                float distancia = charco.getCuerpo().getPosition().dst(posicionAtaque);
                if (distancia < radioAtaque) {
                    charco.setActiva();
                }
            }
        }

        for (Enemigas enemigo : etapa.getEnemigos()) {
            if (enemigo instanceof Robot) {
                float distancia = enemigo.getBody().getPosition().dst(posicionAtaque);
                if (distancia < radioAtaque) {
                    ((Robot) enemigo).destruir();
                }
            }
        }

        for (Robot robot : etapa.getRobots()) {
            float distancia = robot.getBody().getPosition().dst(posicionAtaque);
            if (distancia < radioAtaque) {
                robot.destruir();
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
        map.dispose();      // ← Libera el mapa
        world.dispose();    // ← Libera el mundo Box2D
        sonic.dispose();
        knuckles.dispose();
        tails.dispose();
        etapa.dispose();
    }


    public World getWorld() {
        return world;
    }
}
