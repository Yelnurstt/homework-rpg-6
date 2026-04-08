package com.narxoz.rpg;

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
import com.narxoz.rpg.tournament.TournamentEngine;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Homework 6 Demo: Chain of Responsibility + Command ===\n");

        ArenaFighter hero = new ArenaFighter("Странник", 100, 0.20, 25, 5, 18, 3);
        ArenaOpponent opponent = new ArenaOpponent("Гладиатор", 90, 14);

        // -----------------------------------------------------------------------
        // Часть 1 Демо очереди команд (Command Pattern)
        // -----------------------------------------------------------------------
        System.out.println("--- Command Queue Demo ---");

        ActionQueue queue = new ActionQueue();

        queue.enqueue(new AttackCommand(opponent, hero.getAttackPower()));
        queue.enqueue(new HealCommand(hero, 20));
        queue.enqueue(new DefendCommand(hero, 0.15));

        System.out.println("Запланированные действия:");
        for (String desc : queue.getCommandDescriptions()) {
            System.out.println("  " + desc);
        }

        System.out.println("\nОтменяем последнее действие...");
        queue.undoLast();

        System.out.println("Очередь после отмены:");
        for (String desc : queue.getCommandDescriptions()) {
            System.out.println("  " + desc);
        }

        queue.enqueue(new DefendCommand(hero, 0.20));
        System.out.println("\nВыполняем все действия из очереди...");
        queue.executeAll();
        System.out.println("HP противника после атаки: " + opponent.getHealth());

        // -----------------------------------------------------------------------
        // Часть 2 — Демо цепочки защиты (Chain of Responsibility)
        // -----------------------------------------------------------------------
        System.out.println("\n--- Defense Chain Demo ---");

        DefenseHandler dodge = new DodgeHandler(0.50, 99L);
        DefenseHandler block = new BlockHandler(0.30);
        DefenseHandler armor = new ArmorHandler(5);
        DefenseHandler hp    = new HpHandler();

        dodge.setNext(block).setNext(armor).setNext(hp);

        System.out.println("Пропускаем 20 урона через цепочку защиты...");
        System.out.println("HP героя ДО: " + hero.getHealth());
        dodge.handle(20, hero);
        System.out.println("HP героя ПОСЛЕ: " + hero.getHealth());

        // -----------------------------------------------------------------------
        // Часть 3 — Полный турнир (Интеграция паттернов)
        // -----------------------------------------------------------------------
        System.out.println("\n--- Full Arena Tournament ---");

        ArenaFighter tournamentHero = new ArenaFighter("Артур", 120, 0.25, 25, 8, 22, 3);
        ArenaOpponent tournamentOpponent = new ArenaOpponent("Железный Клык", 100, 16);

        TournamentResult result = new TournamentEngine(tournamentHero, tournamentOpponent)
                .setRandomSeed(42L)
                .runTournament();

        System.out.println("Победитель : " + result.getWinner());
        System.out.println("Количество раундов : " + result.getRounds());
        System.out.println("Лог боя:");
        for (String line : result.getLog()) {
            System.out.println("  " + line);
        }

        System.out.println("\n=== Demo Complete ===");
    }
}