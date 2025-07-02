package io.github.Sonic_Test;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class crearObjetos {
    public World world;
    BodyDef bd;
    BodyDef s;

    CircleShape circleShape;     // Para objetos circulares, como Sonic
    PolygonShape suelo;          // Para suelo/plataformas grandes

    public crearObjetos() {
        world = new World(new Vector2(0, -30f), true);

        bd = new BodyDef();
        bd.position.set(-14, 2);
        bd.type = BodyDef.BodyType.DynamicBody;

        s = new BodyDef();
        s.position.set(0, 0);
        s.type = BodyDef.BodyType.StaticBody;

        objetos();
    }

    public void objetosMapa(TiledMap map) {
        MapLayer capaWalls = map.getLayers().get("walls");
        for (MapObject objeto : capaWalls.getObjects()) {
            if (objeto instanceof RectangleMapObject) {
                RectangleMapObject rectObj = (RectangleMapObject) objeto;
                float halfWidth = rectObj.getRectangle().width / 2 * 0.05f;
                float halfHeight = rectObj.getRectangle().height / 2 * 0.05f;

                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set(
                    (rectObj.getRectangle().x + rectObj.getRectangle().width / 2) * 0.05f,
                    (rectObj.getRectangle().y + rectObj.getRectangle().height / 2) * 0.05f
                );

                PolygonShape polyShape = new PolygonShape();
                polyShape.setAsBox(halfWidth, halfHeight);

                Body body = world.createBody(bdef);
                body.createFixture(polyShape, 0.0f);
                polyShape.dispose();

            } else if (objeto instanceof PolygonMapObject) {
                PolygonMapObject polyObject = (PolygonMapObject) objeto;
                float[] vertices = polyObject.getPolygon().getTransformedVertices();
                float[] worldVertices = new float[vertices.length];

                for (int i = 0; i < vertices.length; i++) {
                    worldVertices[i] = vertices[i] * 0.05f; // Escala igual que el mapa
                }

                PolygonShape polyShape = new PolygonShape();
                polyShape.set(worldVertices);

                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;

                Body body = world.createBody(bdef);
                body.createFixture(polyShape, 0.0f);
                polyShape.dispose();
            }
        }
    }

    public void actualizar(float delta) {
        world.step(delta, 8, 6);
    }

    public void objetos() {
        circleShape = new CircleShape();
        circleShape.setRadius(0.1f);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = circleShape;

        Body oBody = world.createBody(bd);
        oBody.createFixture(fixDef);

        // Ahora asignamos a suelo el PolygonShape para el suelo
        suelo = new PolygonShape();
        suelo.setAsBox(100f, 0.5f); // ancho = 200 (2*100), alto = 1 (2*0.5)

        FixtureDef fixDef2 = new FixtureDef();
        fixDef2.shape = suelo;
        fixDef2.friction = 0.8f;

        Body sue = world.createBody(s);
        sue.createFixture(fixDef2);
    }

    public void crearPlataforma(float x, float y) {
        BodyDef body = new BodyDef();
        body.position.set(x, y);
        body.type = BodyDef.BodyType.StaticBody;

        PolygonShape plataforma = new PolygonShape();
        plataforma.setAsBox(2f, 0.4f); // Mitades

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = plataforma;
        fixDef.friction = 0f;

        Body oBody = world.createBody(body);
        oBody.createFixture(fixDef);

        plataforma.dispose();
    }

    public void dispose() {
        if (circleShape != null) circleShape.dispose();
        if (suelo != null) suelo.dispose();
        if (world != null) world.dispose();
    }
}
