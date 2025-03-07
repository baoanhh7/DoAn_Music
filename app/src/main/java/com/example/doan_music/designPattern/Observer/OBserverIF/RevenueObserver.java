package com.example.doan_music.designPattern.Observer.OBserverIF;

import com.example.doan_music.designPattern.Observer.Model.RevenueStatistics;

public interface RevenueObserver {
    void onRevenueUpdate(RevenueStatistics statistics);
}
