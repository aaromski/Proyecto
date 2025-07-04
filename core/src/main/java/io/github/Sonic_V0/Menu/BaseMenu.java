package io.github.Sonic_V0.Menu;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Sonic_V0.*;

public abstract class BaseMenu implements Screen {
    protected final Main game;
    protected SpriteBatch batch;
    protected BitmapFont font;
    protected Texture  boton;
    protected GlyphLayout layout;
    protected int screenW;
    protected int screenH;

    public BaseMenu(Main Game) {
        this.game = Game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
        boton = new Texture("Menu/button.png"); // Usa la misma textura que en el menú

    }

    @Override
    public void resize(int width, int height) {
        screenW = width;
        screenH = height;
    }
}
