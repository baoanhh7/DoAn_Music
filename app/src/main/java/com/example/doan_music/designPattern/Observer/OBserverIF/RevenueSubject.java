package com.example.doan_music.designPattern.Observer.OBserverIF;

public interface RevenueSubject {
    void registerObserver(RevenueObserver observer);
    void removeObserver(RevenueObserver observer);
    void notifyObservers();
}
