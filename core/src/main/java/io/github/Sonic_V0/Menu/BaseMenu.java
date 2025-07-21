package io.github.Sonic_V0.Menu;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Sonic_V0.*;
import io.github.Sonic_V0.Mundo.Camara;

public abstract class BaseMenu implements Screen {
    protected Camara camara;
    protected final Main game;
    protected SpriteBatch batch;
    protected BitmapFont font;
    protected Texture  boton;
    protected GlyphLayout layout;
    protected int screenW;
    protected int screenH;
    protected float botonW = 250f;
    protected float botonH = 60f;

    public BaseMenu(Main Game) {
        this.game = Game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
        boton = new Texture("Menu/button.png"); // Usa la misma textura que en el menú
        camara = new Camara();
    }

    @Override
    public void resize(int width, int height) {
        screenW = width;
        screenH = height;
    }
}
