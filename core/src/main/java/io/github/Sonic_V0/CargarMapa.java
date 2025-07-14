package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;



public class CargarMapa {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public CargarMapa(String rutaMapa, World world) {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load(rutaMapa);
        mapRenderer = new OrthogonalTiledMapRenderer(map, Constantes.WORLD_ESCALA);
        objetosMapa(world);
    }

    public void renderarMapa(OrthographicCamera camara) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        mapRenderer.setView(camara);
        mapRenderer.render();
    }

    public void objetosMapa(World world) {
        for (MapLayer capa : map.getLayers()) {
            MapObjects objetos = capa.getObjects();
            for (MapObject objeto : objetos) {
                if (objeto instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) objeto).getRectangle();

                    BodyDef bdef = new BodyDef();
                    bdef.type = BodyDef.BodyType.StaticBody;
                    bdef.position.set(
                        (rect.x + rect.width / 2) * Constantes.WORLD_ESCALA,
                        (rect.y + rect.height / 2) * Constantes.WORLD_ESCALA
                    );

                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(rect.width / 2 * Constantes.WORLD_ESCALA, rect.height / 2 * Constantes.WORLD_ESCALA);

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
                        verticesEscalados[i] = vertices[i] * Constantes.WORLD_ESCALA;
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
                    float radio = (elipse.width / 2 + elipse.height / 2) / 2 * Constantes.WORLD_ESCALA;

                    CircleShape shape = new CircleShape();
                    shape.setRadius(radio);
                    shape.setPosition(new Vector2(
                        (elipse.x + elipse.width / 2) * Constantes.WORLD_ESCALA,
                        (elipse.y + elipse.height / 2) * Constantes.WORLD_ESCALA
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

    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
    }

}
