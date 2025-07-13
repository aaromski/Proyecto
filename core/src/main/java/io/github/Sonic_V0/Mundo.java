package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import java.util.ArrayList;

public class Mundo {
    private final World world;
    private final CargarMapa map;
    private final ArrayList<Basura> listaBasura;
    float escala = 0.039f;

    public Mundo() {
        world = new World(new Vector2(0, 0), true);
        listaBasura = new ArrayList<>();
        map = new CargarMapa("Mapa1/mapa.tmx", 0.039f);
        objetosMapa(map.getMap());

        // Configurar el ContactListener aquí mismo
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                Object ua = contact.getFixtureA().getUserData();
                Object ub = contact.getFixtureB().getUserData();

                if ("Sonic".equals(ua) && ub instanceof Basura) {
                    ((Basura) ub).setActiva();
                }

                if ("Sonic".equals(ub) && ua instanceof Basura) {
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

        // Limpiar basuras inactivas después del step
        listaBasura.removeIf(b -> {
            if (!b.estaActiva()) {
                b.destruir(world);
                b.dispose();
                return true;
            }
            return false;
        });
    }
    public void objetosMapa(TiledMap map) {
        for (MapLayer capa : map.getLayers()) {
            MapObjects objetos = capa.getObjects();
                for (MapObject objeto : objetos) {
                    if (objeto instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) objeto).getRectangle();

                        BodyDef bdef = new BodyDef();
                        bdef.type = BodyDef.BodyType.StaticBody;
                        bdef.position.set(
                            (rect.x + rect.width / 2) * escala,
                            (rect.y + rect.height / 2) * escala
                        );

                        PolygonShape shape = new PolygonShape();
                        shape.setAsBox(rect.width / 2 * escala, rect.height / 2 * escala);

                        Body cuerpo = world.createBody(bdef);
                        cuerpo.createFixture(shape, 0.0f);
                        shape.dispose();
                    }

                    // Si tienes objetos poligonales:
                    if (objeto instanceof PolygonMapObject) {
                        PolygonMapObject poly = (PolygonMapObject) objeto;
                        float[] vertices = poly.getPolygon().getTransformedVertices();
                        float[] verticesEscalados = new float[vertices.length];

                        for (int i = 0; i < vertices.length; i++) {
                            verticesEscalados[i] = vertices[i] * escala;
                        }

                        PolygonShape shape = new PolygonShape();
                        shape.set(verticesEscalados);

                        BodyDef bdef = new BodyDef();
                        bdef.type = BodyDef.BodyType.StaticBody;
                        Body cuerpo = world.createBody(bdef);
                        cuerpo.createFixture(shape, 0.0f);
                        shape.dispose();
                    }

                    if (objeto instanceof EllipseMapObject) {
                        Ellipse elipse = ((EllipseMapObject) objeto).getEllipse();
                        float radio = (elipse.width / 2 + elipse.height / 2) / 2 * escala;

                        CircleShape shape = new CircleShape();
                        shape.setRadius(radio);
                        shape.setPosition(new Vector2(
                            (elipse.x + elipse.width / 2) * escala,
                            (elipse.y + elipse.height / 2) * escala
                        ));

                        BodyDef bdef = new BodyDef();
                        bdef.type = BodyDef.BodyType.StaticBody;
                        Body cuerpo = world.createBody(bdef);
                        cuerpo.createFixture(shape, 0.0f);
                        shape.dispose();
                    }
                }
            }
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
        fixDef.friction = 1f;
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
    }

}
