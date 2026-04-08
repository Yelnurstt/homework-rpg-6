package com.narxoz.rpg.tournament;

import com.narxoz.rpg.arena.ArenaFighter;
import com.narxoz.rpg.arena.ArenaOpponent;
import com.narxoz.rpg.arena.TournamentResult;
import com.narxoz.rpg.chain.ArmorHandler;
import com.narxoz.rpg.chain.BlockHandler;
import com.narxoz.rpg.chain.DefenseHandler;
import com.narxoz.rpg.chain.DodgeHandler;
import com.narxoz.rpg.chain.HpHandler;
import com.narxoz.rpg.command.ActionQueue;
import com.narxoz.rpg.command.AttackCommand;
import com.narxoz.rpg.command.DefendCommand;
import com.narxoz.rpg.command.HealCommand;
import java.util.Random;

public class TournamentEngine {
    private final ArenaFighter hero;
    private final ArenaOpponent opponent;
    private Random random = new Random(1L);

    public TournamentEngine(ArenaFighter hero, ArenaOpponent opponent) {
        this.hero = hero;
        this.opponent = opponent;
    }

    public TournamentEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public TournamentResult runTournament() {
        TournamentResult result = new TournamentResult();
        int round = 0;
        final int maxRounds = 20;

        // Dodge -> Block -> Armor -> Hp
        DefenseHandler dodge = new DodgeHandler(hero.getDodgeChance(), random.nextLong());
        DefenseHandler block = new BlockHandler(hero.getBlockRating() / 100.0);
        DefenseHandler armor = new ArmorHandler(hero.getArmorValue());
        DefenseHandler hp = new HpHandler();

        dodge.setNext(block).setNext(armor).setNext(hp);

        // очередь команд
        ActionQueue actionQueue = new ActionQueue();

        // симулируем раунды
        while (hero.isAlive() && opponent.isAlive() && round < maxRounds) {
            round++;
            result.addLine("--- Раунд " + round + " ---");

            // Герой добавляет действия в очередь: Атака, Лечение (на 15 HP), Защита (шанс уклонения +0.10)
            actionQueue.enqueue(new AttackCommand(opponent, hero.getAttackPower()));
            actionQueue.enqueue(new HealCommand(hero, 15));
            actionQueue.enqueue(new DefendCommand(hero, 0.10));

            result.addLine("Очередь действий героя:");
            for (String desc : actionQueue.getCommandDescriptions()) {
                result.addLine("  - " + desc);
            }

            actionQueue.executeAll();

            if (opponent.isAlive()) {
                result.addLine("Противник (" + opponent.getName() + ") атакует на " + opponent.getAttackPower() + " урона!");
                dodge.handle(opponent.getAttackPower(), hero);
            }

            String logLine = String.format("[Итог Раунда %d] %s HP: %d | %s HP: %d",
                    round, opponent.getName(), opponent.getHealth(), hero.getName(), hero.getHealth());
            result.addLine(logLine);
        }

        if (hero.isAlive() && !opponent.isAlive()) {
            result.setWinner(hero.getName());
        } else if (!hero.isAlive() && opponent.isAlive()) {
            result.setWinner(opponent.getName());
        } else {
            result.setWinner("Ничья (Draw/Max Rounds)");
        }

        result.setRounds(round);
        return result;
    }
}