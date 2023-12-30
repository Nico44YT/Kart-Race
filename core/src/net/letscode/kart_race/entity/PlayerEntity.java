package net.letscode.kart_race.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class PlayerEntity extends Entity {
    public PlayerEntity(Vector2 position, Sprite sprite) {
        super(position, sprite);
    }

    @Override
    public void setSprite(Sprite sprite) {
        sprite.setScale(1.8f);
        this.sprite = sprite;
    }
}
