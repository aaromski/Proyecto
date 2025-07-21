package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Basura extends Contaminacion{

    public Basura(World world) {
        super(world);
        textura = new Sprite(new Texture("Mapa1/trash.png"));
        textura.setSize(0.8f, 0.8f);
    }
    @Override
    public void crearCuerpo(Vector2 posicion) {
        super.crearCuerpo(posicion);
    }

    @Override
    protected void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_TRASH;
        fdef.filter.maskBits = ~(Constantes.CATEGORY_ROBOT);
    }

}
