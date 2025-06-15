package io.github.Sonic_Test;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camara;
    public TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Sonic sonic;
    @Override
    public void create() {
        // Cargar el mapa TMX
        TmxMapLoader cargarMapa = new TmxMapLoader();
        map = cargarMapa.load("Escenario2.tmx");

        // Crear el renderizador del mapa
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1.2f);

        camara = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camara.position.set(400, 250, 0);
        camara.update();
        sonic = new Sonic(270, 150);
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime(); // Tiempo entre frames
        sonic.actualizar(delta);

        camara.position.set(sonic.posX, sonic.posY, 0);
        camara.update();

        // Dibujar en nueva posición
        ScreenUtils.clear(0, 0, 0, 1);

        // 📌 Renderizar el mapa antes del personaje
        mapRenderer.setView(camara);
        mapRenderer.render();

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
