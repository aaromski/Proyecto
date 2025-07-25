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

/**
 * Clase encargada de cargar y renderizar un mapa de juego Tiled,
 * así como de crear los cuerpos físicos correspondientes en el mundo Box2D.
 * <p>
 * También gestiona la visualización de información en pantalla como vidas, puntuación y FPS.
 *
 * @author Miguel Rivas
 * @version 1.0
 * @see Constantes
 * @see World
 * @see TiledMap
 */
public class CargarMapa {
    /**
     * El objeto TiledMap que representa el mapa del juego.
     */
    private final TiledMap map;
    /**
     * El renderizador para dibujar el mapa Tiled.
     */
    private final OrthogonalTiledMapRenderer mapRenderer;
    /**
     * El SpriteBatch utilizado para dibujar elementos de la interfaz de usuario.
     */
    private final SpriteBatch batch;
    /**
     * La fuente BitmapFont utilizada para dibujar texto en la interfaz de usuario.
     */
    private final BitmapFont font;

    /**
     * Constructor de la clase CargarMapa.
     * Carga el mapa Tiled desde la ruta especificada y crea los objetos físicos en el mundo Box2D.
     *
     * @param rutaMapa La ruta al archivo .tmx del mapa.
     * @param world El mundo Box2D donde se crearán los objetos físicos del mapa.
     */
    public CargarMapa(String rutaMapa, World world) {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load(rutaMapa);
        mapRenderer = new OrthogonalTiledMapRenderer(map, Constantes.WORLD_ESCALA);
        objetosMapa(world); // Crea los cuerpos físicos a partir de los objetos del mapa
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("Mapa1/letra.fnt"));
        font.getData().setScale(1f); // Escala la fuente
    }

    /**
     * Renderiza el mapa del juego y la información de la interfaz de usuario (vidas, puntuación, FPS).
     *
     * @param camara La cámara ortográfica utilizada para la vista del juego.
     */
    public void renderarMapa(OrthographicCamera camara) {
        AnimatedTiledMapTile.updateAnimationBaseTime(); // Actualiza el tiempo base para animaciones de tiles
        mapRenderer.setView(camara); // Establece la vista del renderizador
        mapRenderer.render(); // Renderiza el mapa

        long tiempo = TimeUtils.millis();
        boolean mostrarTexto = (tiempo / 400) % 2 == 0; // Alterna la visibilidad del texto cada 400 ms

        batch.begin(); // Inicia el dibujo de la interfaz de usuario
        if (mostrarTexto) {
            int aux = Constantes.SCORE[0] + Constantes.SCORE[1] + Constantes.SCORE[2];
            font.setColor(Color.WHITE); // Color de la fuente
            font.draw(batch, "X" + Constantes.VIDAS[0], 80, 970); // Vidas de Sonic
            font.draw(batch, "X" + Constantes.VIDAS[1], 180, 970); // Vidas de Tails
            font.draw(batch, "X" + Constantes.VIDAS[2], 280, 970); // Vidas de Knuckles
            font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 900, 960); // Mostrar FPS
            font.draw(batch, String.valueOf(aux) , 1080, 960); // Puntuación total
        }
        batch.end(); // Finaliza el dibujo de la interfaz de usuario
    }

    /**
     * Procesa los objetos definidos en las capas del mapa Tiled y crea
     * los cuerpos físicos correspondientes en el mundo Box2D.
     * Soporta objetos de tipo Rectángulo, Polígono y Elipse.
     *
     * @param world El mundo Box2D donde se crearán los cuerpos físicos.
     */
    public void objetosMapa(World world) {
        for (MapLayer capa : map.getLayers()) {
            String nombreCapa = capa.getName(); // nombre de la capa actual
            MapObjects objetos = capa.getObjects();
            for (MapObject objeto : objetos) {

                FixtureDef def = new FixtureDef(); // Definición del fixture para el cuerpo físico

                if (objeto instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) objeto).getRectangle();

                    BodyDef bdef = new BodyDef();
                    bdef.type = BodyDef.BodyType.StaticBody; // Los objetos del mapa son estáticos
                    bdef.position.set(
                        (rect.x + rect.width / 2) * Constantes.WORLD_ESCALA,
                        (rect.y + rect.height / 2) * Constantes.WORLD_ESCALA
                    );

                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(rect.width / 2 * Constantes.WORLD_ESCALA, rect.height / 2 * Constantes.WORLD_ESCALA);

                    def.shape = shape;
                    configurarFiltroMapa(def); // Configura los filtros de colisión
                    Body cuerpo = world.createBody(bdef);

                    // UserData según el nombre de la capa
                    if ("Casa".equals(nombreCapa)) {
                        cuerpo.createFixture(def).setUserData("Casa");
                    } else {
                        cuerpo.createFixture(def).setUserData("Objeto");
                    }

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
                    shape.set(verticesEscalados); // Establece los vértices escalados

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
                    float radio = (elipse.width / 2 + elipse.height / 2) / 2 * Constantes.WORLD_ESCALA; // Calcula un radio promedio

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

    /**
     * Configura los filtros de colisión para los objetos del mapa.
     * Estos objetos pertenecen a la categoría de objetos y colisionan con todo.
     *
     * @param def La definición del fixture a configurar.
     */
    public void configurarFiltroMapa(FixtureDef def) {
        def.filter.categoryBits = Constantes.CATEGORY_OBJETOS; // Categoría de objetos del mapa
        def.filter.maskBits = -1; // Colisiona con todas las categorías
    }

    /**
     * Libera los recursos utilizados por el mapa y su renderizador.
     * Es importante llamar a este método cuando el mapa ya no es necesario para evitar fugas de memoria.
     */
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
        if (batch != null) batch.dispose(); // Asegúrate de disponer del batch
        if (font != null) font.dispose(); // Asegúrate de disponer de la fuente
    }
}
