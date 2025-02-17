/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javolution.util.FastMap;

import com.ne.gs.model.templates.world.WorldMapTemplate;

/**
 * This object is representing one in-game map and can have instances.
 *
 * @author -Nemesiss-
 */
public class WorldMap {

    private final WorldMapTemplate worldMapTemplate;

    private final AtomicInteger nextInstanceId = new AtomicInteger(0);
    /**
     * List of instances.
     */
    private final Map<Integer, WorldMapInstance> instances = new FastMap<Integer, WorldMapInstance>().shared();

    /**
     * World to which belongs this WorldMap
     */
    private final World world;

    public WorldMap(WorldMapTemplate worldMapTemplate, World world) {
        this.world = world;
        this.worldMapTemplate = worldMapTemplate;

        if (worldMapTemplate.getTwinCount() != 0) {
            for (int i = 1; i <= worldMapTemplate.getTwinCount(); i++) {
                int nextId = getNextInstanceId();
                addInstance(nextId, WorldMapInstanceFactory.createWorldMapInstance(this, nextId));
            }
        } else {
            int nextId = getNextInstanceId();
            addInstance(nextId, WorldMapInstanceFactory.createWorldMapInstance(this, nextId));
        }
    }

    public String getName() {
        return worldMapTemplate.getName();
    }

    public int getWaterLevel() {
        return worldMapTemplate.getWaterLevel();
    }

    public int getDeathLevel() {
        return worldMapTemplate.getDeathLevel();
    }

    public WorldType getWorldType() {
        return worldMapTemplate.getWorldType();
    }

    public int getWorldSize() {
        return worldMapTemplate.getWorldSize();
    }

    public Integer getMapId() {
        return worldMapTemplate.getMapId();
    }

    public boolean isPossibleFly() {
        return worldMapTemplate.isFly();
    }

    public boolean isExceptBuff() {
        return worldMapTemplate.isExceptBuff();
    }

    public int getInstanceCount() {
        int twinCount = worldMapTemplate.getTwinCount();
        return twinCount > 0 ? twinCount : 1;
    }

    /**
     * Return a WorldMapInstance - depends on map configuration one map may have twins instances to balance player. This method will return WorldMapInstance by
     * server chose.
     *
     * @return WorldMapInstance.
     */
    public WorldMapInstance getWorldMapInstance() {
        // TODO Balance players into instances.
        return getWorldMapInstance(1);
    }

    /**
     * This method return WorldMapInstance by specified instanceId
     *
     * @param instanceId
     *
     * @return WorldMapInstance
     */
    public WorldMapInstance getWorldMapInstanceById(int instanceId) {
        if (worldMapTemplate.getTwinCount() != 0) {
            if (instanceId > worldMapTemplate.getTwinCount()) {
                throw new IllegalArgumentException("WorldMapInstance " + getMapId() + " has lower instances count than " + instanceId);
            }
        }
        return getWorldMapInstance(instanceId);
    }

    /**
     * Returns WorldMapInstance by instanceId.
     *
     * @param instanceId
     *
     * @return WorldMapInstance/
     */
    private WorldMapInstance getWorldMapInstance(int instanceId) {
        return instances.get(instanceId);
    }

    /**
     * Remove WorldMapInstance by instanceId.
     *
     * @param instanceId
     */
    public void removeWorldMapInstance(int instanceId) {
        instances.remove(instanceId);
    }

    /**
     * Add instance to map
     *
     * @param instanceId
     * @param instance
     */
    public void addInstance(int instanceId, WorldMapInstance instance) {
        instances.put(instanceId, instance);
    }

    /**
     * Returns the World containing this WorldMap.
     */
    public World getWorld() {
        return world;
    }

    public final WorldMapTemplate getTemplate() {
        return worldMapTemplate;
    }

    /**
     * @return the nextInstanceId
     */
    public int getNextInstanceId() {
        return nextInstanceId.incrementAndGet();
    }

    /**
     * Whether this world map is instance type
     *
     * @return
     */
    public boolean isInstanceType() {
        return worldMapTemplate.isInstance();
    }

    /**
     * @return
     */
    public Iterator<WorldMapInstance> iterator() {
        return instances.values().iterator();
    }

    /**
     * All instance ids of this map
     */
    public Collection<Integer> getAvailableInstanceIds() {
        return instances.keySet();
    }
}
