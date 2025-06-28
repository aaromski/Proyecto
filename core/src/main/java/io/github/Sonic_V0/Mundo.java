package io.github.Sonic_V0;

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

public class Mundo {
    World world;
    float escala = 0.039f;

    public Mundo() {
        world = new World(new Vector2(0,0), true);
    }

    public void actualizar(float delta) {
        world.step(delta, 8,6);
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

    public Body crearCuerpo(Vector2 posicion) {
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
        oBody.createFixture(fixDef);

        return oBody;
    }
}
