package com.codenjoy.dojo.loderunner.model;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.List;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 4:58
 */
public class Printer {
    private Loderunner game;
    private Elements[][] field;
    private final int size;

    public Printer(Loderunner game) {
        this.game = game;
        size = game.getSize();
    }

    @Override
    public String toString() {
        fillField();

        String string = "";
        for (Elements[] currentRow : field) {
            for (Elements element : currentRow) {
                string += element.ch;
            }
            string += "\n";
        }
        return string;
    }

    private void fillField() {
        field = new Elements[size][size];

        for (int rowNumber = 0; rowNumber < size; rowNumber++) {
            for (int colNumber = 0; colNumber < size; colNumber++) {
                set(new PointImpl(rowNumber, colNumber), Elements.NONE);
            }
        }

        List<Point> borders = game.getBorders();
        for (Point border : borders) {
            set(border, Elements.UNDESTROYABLE_WALL);
        }

        List<Brick> bricks = game.getBricks();
        for (Brick brick : bricks) {
            Elements state = brick.state();
            if (state ==  Elements.DRILL_PIT) {
                set(new PointImpl(brick.getX(), brick.getY() + 1), Elements.DRILL_SPACE);
            }
            set(brick, state);
        }

        Hero hero = game.getHero();
        if (!hero.isAlive()) {
            set(hero, Elements.HERO_DIE);
        } else if (hero.isDrilled()) {
            if (hero.getDirection().equals(Direction.LEFT)) {
                set(hero, Elements.HERO_DRILL_LEFT);
            } else {
                set(hero, Elements.HERO_DRILL_RIGHT);
            }
        } else if (hero.isFall()) {
            if (hero.getDirection().equals(Direction.LEFT)) {
                set(hero, Elements.HERO_FALL_LEFT);
            } else {
                set(hero, Elements.HERO_FALL_RIGHT);
            }
        } else {
            if (hero.getDirection().equals(Direction.LEFT)) {
                set(hero, Elements.HERO_LEFT);
            } else {
                set(hero, Elements.HERO_RIGHT);
            }
        }

        List<Point> gold = game.getGold();
        for (Point g : gold) {
            set(g, Elements.GOLD);
        }

        List<Point> ladder = game.getLadder();
        for (Point l : ladder) {
            if (game.getHero().itsMe(l)) {
                set(l, Elements.HERO_LADDER);
            } else {
                set(l, Elements.LADDER);
            }
        }

        List<Point> pipe = game.getPipe();
        for (Point p : pipe) {
            if (game.getHero().itsMe(p)) {
                if (hero.getDirection().equals(Direction.LEFT)) {
                    set(p, Elements.HERO_PIPE_LEFT);
                } else {
                    set(p, Elements.HERO_PIPE_RIGHT);
                }
            } else {
                set(p, Elements.PIPE);
            }
        }
    }

    private void set(Point pt, Elements element) {
        if (pt.getY() == -1 || pt.getX() == -1) {
            return;
        }

        field[size - 1 - pt.getY()][pt.getX()] = element;
    }
}
