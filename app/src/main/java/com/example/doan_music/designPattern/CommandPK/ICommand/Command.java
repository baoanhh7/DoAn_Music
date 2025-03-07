package com.example.doan_music.designPattern.CommandPK.ICommand;

public interface Command {
    void execute();  // Thực hiện lệnh
    void undo();     // Hoàn tác lệnh
}
