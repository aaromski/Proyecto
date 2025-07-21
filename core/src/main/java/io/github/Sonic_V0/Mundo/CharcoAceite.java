package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import io.github.Sonic_V0.Constantes;


public class CharcoAceite extends Contaminacion {
    // El constructor recibe el cuerpo, que ya fue creado en la clase Mundo.
    public CharcoAceite(World world) {
        super(world);
        //this.cuerpo = body;
        textura = new Sprite(new Texture("Mapa1/oil.png"));
        textura.setSize(1.5f, 1.5f);
    }

    @Override
    public void crearCuerpo(Vector2 posicion) {
        super.crearCuerpo(posicion);
    }

    @Override
    protected void configurarFiltro(FixtureDef fdef) {
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constantes.CATEGORY_TRASH;
        fdef.filter.maskBits = ~(Constantes.CATEGORY_ROBOT);
    }

}
