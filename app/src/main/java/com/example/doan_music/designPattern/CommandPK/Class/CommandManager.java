package com.example.doan_music.designPattern.CommandPK.Class;

import com.example.doan_music.designPattern.CommandPK.ICommand.Command;

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
