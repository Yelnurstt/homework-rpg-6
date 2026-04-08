package com.narxoz.rpg.chain;

import com.narxoz.rpg.arena.ArenaFighter;

public class ArmorHandler extends DefenseHandler {
    private final int armorValue;

    public ArmorHandler(int armorValue) {
        this.armorValue = armorValue;
    }

    @Override
    public void handle(int incomingDamage, ArenaFighter target) {
        int reducedDamage = Math.max(0, incomingDamage - armorValue);
        int absorbed = incomingDamage - reducedDamage;

        System.out.println("Armor броня поглотила " + absorbed + " урона");
        passToNext(reducedDamage, target);
    }
}