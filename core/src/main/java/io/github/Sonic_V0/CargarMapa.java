package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;

public class CargarMapa {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    public CargarMapa(String rutaMapa, float escala) {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load(rutaMapa);
        mapRenderer = new OrthogonalTiledMapRenderer(map, escala);
    }

    public TiledMap getMap() {
        return map;
    }

    public void renderarMapa(OrthographicCamera camara) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        mapRenderer.setView(camara);
        mapRenderer.render();
    }

    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
    }

}
