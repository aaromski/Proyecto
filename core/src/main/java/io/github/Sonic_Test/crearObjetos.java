package io.github.Sonic_Test;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class crearObjetos {
    World world;
    BodyDef bd;
    BodyDef s;
    CircleShape shape;
    PolygonShape suelo;

    public crearObjetos() {
        world = new World(new Vector2(0,-30f), true);
        bd = new BodyDef();
        bd.position.set( -14 , 2 );
        bd.type = BodyDef.BodyType.DynamicBody;
        s = new BodyDef();
        s.position.set(0,0);
        s.type = BodyDef.BodyType.StaticBody;
        objetos();

    }
public void objetosMapa (TiledMap map) {
    MapLayer capaWalls = map.getLayers().get("walls");
    for (MapObject objeto : capaWalls.getObjects()) {
        if (objeto instanceof RectangleMapObject) {
            Rectangle rect = ((RectangleMapObject) objeto).getRectangle();

            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(
                (rect.x + rect.width / 2) * 0.05f,
                (rect.y + rect.height / 2) * 0.05f
            );

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(rect.width / 2 * 0.05f, rect.height / 2 * 0.05f);

            Body body = world.createBody(bdef);
            body.createFixture(shape, 0.0f);
            shape.dispose();
        } else if (objeto instanceof PolygonMapObject) {
            PolygonMapObject polyObject = (PolygonMapObject) objeto;
            float[] vertices = polyObject.getPolygon().getTransformedVertices();
            float[] worldVertices = new float[vertices.length];

            for (int i = 0; i < vertices.length; i++) {
                worldVertices[i] = vertices[i] * 0.05f; // Escala igual que el mapa
            }

            PolygonShape shape = new PolygonShape();
            shape.set(worldVertices);

            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;

            Body body = world.createBody(bdef);
            body.createFixture(shape, 0.0f);
            shape.dispose();
        }

    }
}

    public void actualizar(float delta) {
        world.step(delta, 8,6);
    }

    public void objetos() {
        shape = new CircleShape();
        shape.setRadius(.1f);
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        Body oBody = world.createBody(bd);
        oBody.createFixture(fixDef);

        PolygonShape sueloShape = new PolygonShape();
        sueloShape.setAsBox(100f, 0.5f); // ancho = 10, alto = 0.5 (mitades)

// Fixture
        FixtureDef fixDef2 = new FixtureDef();
        fixDef2.shape = sueloShape;
        fixDef2.friction = 0.8f; // puedes ajustar la fricción si el personaje resbala

        Body sue = world.createBody(s);
        sue.createFixture(fixDef2);

    }

    public void crearPlataforma(float x, float y) {
        BodyDef body = new BodyDef();
        body.position.set( x , y );
        body.type = BodyDef.BodyType.StaticBody;

        PolygonShape plataforma = new PolygonShape();
        plataforma.setAsBox(2f, 0.4f); // Mitades, porque setAsBox mide desde el centro
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = plataforma;
        fixDef.friction = 0f; // En la plataforma
        Body oBody = world.createBody(body);
        oBody.createFixture(fixDef);
    }

    public void dispose() {
        shape.dispose();
        suelo.dispose();
    }

}
