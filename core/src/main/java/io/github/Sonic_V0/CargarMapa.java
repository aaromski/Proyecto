package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class CargarMapa {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public CargarMapa(String rutaMapa, float escala) {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load(rutaMapa);
        mapRenderer = new OrthogonalTiledMapRenderer(map, escala);
    }

    public TiledMap getMap() {
        return map;
    }

    public void renderarMapa(OrthographicCamera camara) {
        mapRenderer.setView(camara);
        mapRenderer.render();
    }

    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
    }

}
