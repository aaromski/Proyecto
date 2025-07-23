package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.utils.TimeUtils;
import io.github.Sonic_V0.Constantes;


public class CargarMapa {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    public CargarMapa(String rutaMapa, World world) {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load(rutaMapa);
        mapRenderer = new OrthogonalTiledMapRenderer(map, Constantes.WORLD_ESCALA);
        objetosMapa(world);
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("Mapa1/letra.fnt"));
        font.getData().setScale(1f);
    }

    public void renderarMapa(OrthographicCamera camara) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        mapRenderer.setView(camara);
        mapRenderer.render();

        long tiempo = TimeUtils.millis();
        boolean mostrarTexto = (tiempo / 400) % 2 == 0; // Alterna cada 500 ms

        batch.begin();
        if (mostrarTexto) {
            int aux = Constantes.SCORE[0] + Constantes.SCORE[1] + Constantes.SCORE[2];
            font.setColor(Color.WHITE); // O el color que prefieras
            font.draw(batch, "X" + Constantes.VIDAS[0], 80, 970);
            font.draw(batch, "X" + Constantes.VIDAS[1], 180, 970);
            font.draw(batch, "X" + Constantes.VIDAS[2], 280, 970);
            font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 900, 960);
            font.draw(batch, String.valueOf(aux) , 1080, 960);
        }
        batch.end();
    }

    public void objetosMapa(World world) {
        for (MapLayer capa : map.getLayers()) {
            MapObjects objetos = capa.getObjects();
            for (MapObject objeto : objetos) {

                FixtureDef def = new FixtureDef();

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

                    def.shape = shape;
                    configurarFiltroMapa(def);

                    Body cuerpo = world.createBody(bdef);
                    cuerpo.createFixture(def).setUserData("Objeto");
                    shape.dispose();
                }

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

                    def.shape = shape;
                    configurarFiltroMapa(def);

                    Body cuerpo = world.createBody(bdef);
                    cuerpo.createFixture(def).setUserData("Objeto");
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

                    def.shape = shape;
                    configurarFiltroMapa(def);

                    Body cuerpo = world.createBody(bdef);
                    cuerpo.createFixture(def).setUserData("Objeto");
                    shape.dispose();
                }
            }
        }
    }

    public void configurarFiltroMapa(FixtureDef def) {
        def.filter.categoryBits = Constantes.CATEGORY_OBJETOS;
        def.filter.maskBits = -1;
    }

    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
    }

}
