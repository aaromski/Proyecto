package io.github.Sonic_Test;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camara;
    public TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Sonic sonic;
    private crearObjetos obj;
    Box2DDebugRenderer debugRenderer;
    BodyDef bodySonic;


    @Override
    public void create() {
        // Cargar el mapa TMX
        TmxMapLoader cargarMapa = new TmxMapLoader();
        map = cargarMapa.load("Escenario2.tmx");

        // Crear el renderizador del mapa
        mapRenderer = new OrthogonalTiledMapRenderer(map, 0.05f);
        debugRenderer = new Box2DDebugRenderer();
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 32, 18); // Ajusta según tu escala y resolución
        camara.update();
        bodySonic = new BodyDef();
        bodySonic.position.set( 20 , 10 );
        bodySonic.type = BodyDef.BodyType.DynamicBody;

        batch = new SpriteBatch();
        obj = new crearObjetos();
        CircleShape box = new CircleShape();
        //PolygonShape box = new PolygonShape();
        //box.setAsBox(1.19f / 2f, 0.91f / 2f); // Mitades, porque setAsBox mide desde el centro
        box.setRadius(0.5f);
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = box;
        Body oBody = obj.world.createBody(bodySonic);
        oBody.setLinearDamping(5f); // Esto reduce el deslizamiento horizontal
        fixDef.friction = 1f;
        oBody.createFixture(fixDef);
        sonic = new Sonic(oBody); //270-150
        obj.crearPlataforma(2f, 1f);
        obj.crearPlataforma( 8f, 2f);
        obj.crearPlataforma(14f, 3f);
        obj.crearPlataforma(20f, 4f);
        obj.objetosMapa(map);
    }

    @Override
    public void render() {
        Vector2 sonicPos = sonic.body.getPosition(); // O usa sonic.body si es público
        float delta = Gdx.graphics.getDeltaTime(); // Tiempo entre frames
        sonic.actualizar(delta);
        obj.actualizar(delta);
        camara.position.set(sonicPos.x, sonicPos.y, 0);
        camara.update();

        // Dibujar en nueva posición
        ScreenUtils.clear(0, 0, 0, 1);

        // 📌 Renderizar el mapa antes del personaje
        mapRenderer.setView(camara);
        // Renderiza solo las capas visibles que sí quieres mostrar
        mapRenderer.render(new int[] {
            map.getLayers().getIndex("capa")
        });


        //debugRenderer.render(obj.world, camara.combined);

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        sonic.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        sonic.dispose();
        batch.dispose();
    }

}
