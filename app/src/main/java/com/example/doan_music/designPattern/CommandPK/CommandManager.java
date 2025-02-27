package com.example.doan_music.designPattern.CommandPK;

import com.example.doan_music.designPattern.Command;

import java.util.Stack;

public class CommandManager{
    private static CommandManager instance;
    private Stack<Command> commandStack = new Stack<>();

    private CommandManager() {}

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    public void executeCommand(Command command) {
        command.execute();
        commandStack.push(command);
    }

    public void undoLastCommand() {
        if (!commandStack.isEmpty()) {
            Command command = commandStack.pop();
            command.undo();
        }
    }
}
