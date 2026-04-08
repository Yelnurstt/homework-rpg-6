package com.narxoz.rpg.command;

import java.util.ArrayList;
import java.util.List;

public class ActionQueue {
    private final List<ActionCommand> queue = new ArrayList<>();

    public void enqueue(ActionCommand cmd) {
        queue.add(cmd);
    }

    public void undoLast() {
        if (!queue.isEmpty()) {
            // Удаляем последнюю добавленную команду
            queue.remove(queue.size() - 1);
            System.out.println("Последнее действие отменено удалено из очереди");
        }
    }

    public void executeAll() {
        if (queue.isEmpty()) {
            System.out.println("Очередь действий пуста.");
            return;
        }

        for (ActionCommand cmd : queue) {
            cmd.execute();
        }

        queue.clear();
    }

    public List<String> getCommandDescriptions() {
        List<String> descriptions = new ArrayList<>();
        for (ActionCommand cmd : queue) {
            descriptions.add(cmd.getDescription());
        }
        return descriptions;
    }
}