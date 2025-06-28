package io.github.Sonic_V0;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

public class VentanaPrueba implements Screen {
    //Box2DDebugRenderer debugRenderer;
    private final SpriteBatch batch;
    private final Sonic sonic;
    private final Mundo mundo;
    private final CargarMapa map;
    private final Camara camara;

    public VentanaPrueba() {
        //debugRenderer = new Box2DDebugRenderer();
        map = new CargarMapa("Mapa1/mapa.tmx", 0.039f);
        camara = new Camara();
        mundo = new Mundo();
        batch = new SpriteBatch();
        sonic = new Sonic(mundo.crearCuerpo(new Vector2(20f, 10f))); //270-150
        mundo.objetosMapa(map.getMap());
    }

        @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        sonic.actualizar(delta);
        mundo.actualizar(delta);

        ScreenUtils.clear(0, 0, 0, 1);
        map.renderarMapa(camara.getCamara());

        // 📌 Dibujar cuerpo de los objetos
       //debugRenderer.render(obj.world, camara.combined);

        batch.setProjectionMatrix(camara.getCamara().combined);
        batch.begin();
        sonic.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        sonic.dispose();
        batch.dispose();
        map.dispose();
    }
}
