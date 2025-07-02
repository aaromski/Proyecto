package io.github.Sonic_Test;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Game;
import io.github.Sonic_Test.Menu.MenuPrincipal;

public class Main extends Game {
    private SpriteBatch batch;
    private OrthographicCamera camara;
    public TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Sonic sonic;
    private crearObjetos obj;
    Box2DDebugRenderer debugRenderer;
    BodyDef bodySonic;

    public MenuPrincipal menuPrincipal;

    @Override
    public void create() {
        menuPrincipal = new MenuPrincipal(this);  // Instancia única del menú
        setScreen(menuPrincipal);                 // Pantalla inicial
    }

    @Override
    public void render() {
        super.render(); // Llama automáticamente al render() de la pantalla activa
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
