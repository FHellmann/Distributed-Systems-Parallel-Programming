package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.TableMaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Fabio on 22.03.2016.
 */
public class TableMasterMealObserver implements TableMaster, Philosopher.OnStandUpListener {
    private final List<Philosopher> philosopherList = Collections.synchronizedList(new ArrayList<>());
    private volatile int maxMealCount;

    @Override
    public void register(Philosopher philosopher) {
        philosopherList.add(philosopher);
        philosopher.setOnStandUpListener(this);
    }

    @Override
    public void unregister(Philosopher philosopher) {
        philosopherList.remove(philosopher);
        philosopher.setOnStandUpListener(null);
    }

    @Override
    public boolean isAllowedToTakeSeat(Philosopher philosopher) {
        final boolean isAllowedToTakeSeat = philosopher.getMealCount() <= maxMealCount;
        if(isAllowedToTakeSeat) {
            philosopher.unbanned();
        } else {
            philosopher.banned();
        }
        return isAllowedToTakeSeat;
    }

    @Override
    public void onStandUp(Philosopher philosopher) {
        maxMealCount = philosopherList.parallelStream().mapToInt(Philosopher::getMealCount).min().orElse(0) + MAX_DEVIATION;
    }
}
