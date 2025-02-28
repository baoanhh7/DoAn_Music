package com.example.doan_music.designPattern.DependencyInjectionPK.IF;

import com.example.doan_music.designPattern.DependencyInjectionPK.Model.User;

public interface UserRepository {
    boolean saveUser(User user);
}
