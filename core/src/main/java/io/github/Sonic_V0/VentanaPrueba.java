package io.github.Sonic_V0;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;

public class VentanaPrueba implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camara;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Sonic sonic;
    private CrearObjetos obj;
    Box2DDebugRenderer debugRenderer;
    BodyDef bodySonic;

    public VentanaPrueba() {
        debugRenderer = new Box2DDebugRenderer();
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 16, 10); // Ajusta según tu escala y resolución
        camara.update();
        bodySonic = new BodyDef();
        bodySonic.position.set( 20 , 10 );
        bodySonic.type = BodyDef.BodyType.DynamicBody;

        batch = new SpriteBatch();
        obj = new CrearObjetos();
        CircleShape box = new CircleShape();
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
    }

        @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        Vector2 sonicPos = sonic.body.getPosition(); // O usa sonic.body si es público
        sonic.actualizar(delta);
        obj.actualizar(delta);
        camara.position.set(sonicPos.x, sonicPos.y, 0);
        camara.update();

        // Dibujar en nueva posición
        ScreenUtils.clear(0, 0, 0, 1);

        // 📌 Renderizar el mapa antes del personaje

        debugRenderer.render(obj.world, camara.combined);

        batch.setProjectionMatrix(camara.combined);
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
    }
}
